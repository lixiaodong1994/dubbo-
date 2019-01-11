package com.mooc.jiangzh.dubbo;

import com.alibaba.dubbo.rpc.RpcContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ConsumerClient {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext-hello-consumer.xml");

        context.start();

        while (true){
            Scanner scanner = new Scanner(System.in);
            String message = scanner.next();

            // 获取接口
            ServiceAPI serviceAPI = (ServiceAPI)context.getBean("consumerService");

            // 测试负载均衡使用
//            for(int i=0;i<10;i++){
//                System.out.println(serviceAPI.sendMessage(message+i));
//            }

            // 测试异步调用
            long beginTime = System.currentTimeMillis();

            serviceAPI.sendMessage(message);
            Future<String> sendFuture = RpcContext.getContext().getFuture();

            long sendEndTime = System.currentTimeMillis();

            String send02 = serviceAPI.sendMessage02(message);
//            Future<String> sendFuture02 = RpcContext.getContext().getFuture();

            long sendEndTime02 = System.currentTimeMillis();

            System.out.println(sendFuture.get()+" , "+send02 +
                    "，send执行时间="+(sendEndTime-beginTime)+" , send2执行时间="+(sendEndTime02-beginTime));

        }

    }

}
