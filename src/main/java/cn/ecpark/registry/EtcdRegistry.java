package cn.ecpark.registry;

import io.etcd.jetcd.*;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseTimeToLiveResponse;
import io.etcd.jetcd.options.LeaseOption;
import io.etcd.jetcd.options.PutOption;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * @Description etcd注册类
 * @Author luocong
 * @Date 2021-08-11 10:04
 * @Version 1.0
 */
@Data
@Slf4j
public class EtcdRegistry implements Registry {
    @Value("${spring.application.name}")
    private String serviceName;

    private RegistryProperties registryProperties;

    private Client client;

    private String prefix = "";

    private String[] endpoints = new String[]{"http://127.0.0.1:2379"};

    private Node node;

    private long leaseId;

    private ScheduledFuture scheduledFuture;

    public EtcdRegistry(RegistryProperties registryProperties){
        if (registryProperties == null) {
            throw new RuntimeException("registryProperties can not be null");
        }
        this.registryProperties = registryProperties;
        configure();
    }

    private void configure(){
        if(StringUtils.hasLength(this.registryProperties.getAddresses())) {
            this.endpoints = this.registryProperties.getAddresses().split(",");
        }
        ClientBuilder builder = Client.builder().endpoints(this.endpoints);

        if(StringUtils.hasLength(this.registryProperties.getUsername())){
            builder.user(ByteSequence.from(this.registryProperties.getUsername(), StandardCharsets.UTF_8));
        }
        if(StringUtils.hasLength(this.registryProperties.getPassword())){
            builder.password(ByteSequence.from(this.registryProperties.getPassword(), StandardCharsets.UTF_8));
        }

        if(this.registryProperties.getTimeout() > 0){
            builder.connectTimeout(Duration.ofMillis(this.registryProperties.getTimeout()));
        }
        this.client = builder.build();
        if(StringUtils.hasLength(this.registryProperties.getServiceName())){
            this.serviceName = this.registryProperties.getServiceName();
        }
        this.node = genNode();
    }

    private Node genNode(){
        Node node = new Node();
        node.id = UUID.randomUUID().toString();
        node.address = AddressUtils.getLocalAddress();
        return node;
    }


    @Override
    public void register() {
        log.info("=== register service to etcd ===");
        try {
            ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
            this.scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        doRegister();
                    } catch (Exception e) {
                        log.error("do register service to etcd error",e);
                    }
                }
            }, 0L, registryProperties.getRegistryInterval(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("register service to etcd error",e);
        }
    }

    private void doRegister() {
        Lease leaseClient = this.client.getLeaseClient();
        boolean needNewLease = true;
        if(this.leaseId > 0 ){
            CompletableFuture<LeaseTimeToLiveResponse> leaseFuture = leaseClient.timeToLive(this.leaseId, LeaseOption.DEFAULT);
            try {
                if(leaseFuture != null && leaseFuture.get().getTTl() > 0){
                    leaseClient.keepAliveOnce(this.leaseId);
                    needNewLease = false;
                }
            } catch (Exception e) {
                log.error("check lease ttl error",e);
            }
        }
        if(needNewLease) {
            CompletableFuture<LeaseGrantResponse> grantFuture = leaseClient.grant(registryProperties.getRegistryTTL(), registryProperties.getRegistryInterval()/2, TimeUnit.SECONDS);
            try {
                this.leaseId = grantFuture.get().getID();
            } catch (Exception e) {
                log.error("grant lease error",e);
                throw new RuntimeException("grant lease fail");
            }
        }

        KV kvClient = client.getKVClient();
        ByteSequence key = ByteSequence.from(getPath(), StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(this.node.getAddress(), StandardCharsets.UTF_8);
        PutOption putOption = PutOption.newBuilder().withLeaseId(this.leaseId).build();
        kvClient.put(key,value,putOption);
        if(log.isDebugEnabled()){
            log.debug("register {}:{} to etcd",getPath(),this.node.getAddress());
        }
    }

    private String getPath(){
        return String.format("%s%s_%s",this.prefix, this.serviceName,this.node.getId());
    }

    @Override
    public void unregister() {
        log.info("===start unregister service from etcd ===");
        //取消定时任务
        if(this.scheduledFuture != null && !this.scheduledFuture.isCancelled()) {
            this.scheduledFuture.cancel(true);
        }
        //释放租约
        Lease leaseClient = this.client.getLeaseClient();
        leaseClient.revoke(this.leaseId);
        //删除key
        KV kvClient = this.client.getKVClient();
        ByteSequence key = ByteSequence.from(getPath(), StandardCharsets.UTF_8);
        kvClient.delete(key);
        log.info("delete key:{} from etcd",getPath());
        log.info("===unregister service from etcd end===");
    }
}
