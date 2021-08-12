package cn.ecpark.registry;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @Description 地址工具类
 * @Author luocong
 * @Date 2021-08-11 14:19
 * @Version 1.0
 */
public class AddressUtils {

    public static String getLocalAddress(){
        String localAddress = "";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            localAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return localAddress;
    }

    public static void main(String[] args) {
        String localAddress = getLocalAddress();
        System.out.println("localAddress:" + localAddress);
    }

}
