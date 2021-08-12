package cn.ecpark.registry;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
        EtcdRegistry etcdRegistry = new EtcdRegistry(registryProperties);
        etcdRegistry.register();
        return etcdRegistry;
    }
}
