package com.mooc.jiangzh.dubbo.controlller;

import com.alibaba.dubbo.rpc.RpcContext;
import com.mooc.jiangzh.dubbo.ServiceAPI;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ConsumerControllerClient02 {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-hello-consumer.xml");

        context.start();

        while (true){
            Scanner scanner = new Scanner(System.in);
            String message = scanner.next();

            // 获取接口
            ServiceAPI serviceAPI = (ServiceAPI)context.getBean("consumerService");

            // 测试负载均衡使用
            for(int i=0;i<10;i++){
                System.out.println(serviceAPI.sendMessage(message+i));
            }

        }

    }

}
