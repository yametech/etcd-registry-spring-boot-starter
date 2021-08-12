package cn.ecpark.registry;

/**
 * @Description 注册
 * @Author luocong
 * @Date 2021-08-10 17:30
 * @Version 1.0
 */
public interface Registry {
    /**
     * 服务注册
     * @return
     */
    void register();

    /**
     * 取消注册
     * @return
     */
    void unregister();

}
