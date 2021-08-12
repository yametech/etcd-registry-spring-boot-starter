package cn.ecpark.registry;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description 注册类属性
 * @Author luocong
 * @Date 2021-08-11 10:05
 * @Version 1.0
 */
@Data
@ConfigurationProperties(prefix = "etcd.registry")
public class RegistryProperties {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 注册地址
     */
    private String addresses;

    /**
     * 注册前缀
     */
    private String prefix;

    /**
     * 注册间隔,单位:秒
     */
    private long registryInterval = 10;

    /**
     * 注册有效期,单位:秒
     */
    private long registryTTL = 30;

    /**
     * 超时时间,单位:秒
     */
    private long timeout = 300;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;


}
