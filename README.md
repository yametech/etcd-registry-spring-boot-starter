# etcd-registry-spring-boot-starter
# 使用方法

## 1.引入依赖

```xml
<dependency>
   <groupId>io.netty</groupId>
   <artifactId>netty-buffer</artifactId>
   <version>4.1.66.Final</version>
   <exclusions>
      <exclusion>
         <groupId>io.netty</groupId>
         <artifactId>netty-common</artifactId>
      </exclusion>
   </exclusions>
</dependency>
<dependency>
   <groupId>io.netty</groupId>
   <artifactId>netty-common</artifactId>
   <version>4.1.66.Final</version>
</dependency>

<dependency>
   <groupId>cn.ecpark</groupId>
   <artifactId>etcd-registry-spring-boot-starter</artifactId>
   <version>1.0.0-SNAPSHOT</version>
   <exclusions>
      <exclusion>
         <groupId>io.netty</groupId>
         <artifactId>netty-buffer</artifactId>
      </exclusion>
      <exclusion>
         <groupId>io.netty</groupId>
         <artifactId>netty-common</artifactId>
      </exclusion>
   </exclusions>
</dependency>
```

## 2.增加配置

```properties
etcd.registry.addresses=http://10.200.100.200:42379
```

备注：更多配置项，看RegistryProperties.class