# etcd-registry-spring-boot-starter
# 使用方法

## 1.引入依赖

```
<dependency>
   <groupId>cn.ecpark</groupId>
   <artifactId>etcd-registry-spring-boot-starter</artifactId>
   <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 2.增加配置

```
etcd.registry.addresses=http://10.200.100.200:42379
```

备注：更多配置项，看RegistryProperties.class