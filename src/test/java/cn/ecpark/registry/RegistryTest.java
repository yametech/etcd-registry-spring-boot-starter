package cn.ecpark.registry;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description etcd registry test
 * @Author luocong
 * @Date 2021-08-11 14:44
 * @Version 1.0
 */

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = EtcdRegistry.class)
@EnableConfigurationProperties(RegistryProperties.class)
public class RegistryTest {

    @Autowired
    private EtcdRegistry etcdRegistry;

    @Test
    public void testRegistry(){
//        etcdRegistry.register();
//        System.out.println("");

        etcdRegistry.setServerPort(8080);
        new Thread(new Runnable() {
            @Override
            public void run() {
                etcdRegistry.register();
            }
        }).start();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        etcdRegistry.unregister();

    }
}
