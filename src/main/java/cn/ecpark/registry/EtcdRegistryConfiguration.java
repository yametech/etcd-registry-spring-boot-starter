package cn.ecpark.registry;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

/**
 * @Description etcd注册者配置类
 * @Author luocong
 * @Date 2021-08-12 10:58
 * @Version 1.0
 */
@Configuration
@EnableConfigurationProperties(RegistryProperties.class)
public class EtcdRegistryConfiguration {

    @Bean(destroyMethod = "unregister")
    public EtcdRegistry etcdRegistry(RegistryProperties registryProperties){
        return new EtcdRegistry(registryProperties);
    }

    @EventListener(WebServerInitializedEvent.class)
    @ConditionalOnWebApplication
    public void onApplicationEvent(WebServerInitializedEvent event) {
        int localPort = event.getWebServer().getPort();
        EtcdRegistry etcdRegistry = event.getApplicationContext().getBean(EtcdRegistry.class);
        etcdRegistry.setServerPort(localPort);
        etcdRegistry.register();
    }
}
