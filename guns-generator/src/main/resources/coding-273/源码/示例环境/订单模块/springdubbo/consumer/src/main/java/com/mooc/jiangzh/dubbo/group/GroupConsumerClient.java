package com.mooc.jiangzh.dubbo.group;

import com.alibaba.dubbo.rpc.RpcContext;
import com.mooc.jiangzh.dubbo.ServiceAPI;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class GroupConsumerClient {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("" +
                        "applicationContext-group-consumer.xml");

        context.start();

        while (true){
            Scanner scanner = new Scanner(System.in);
            String message = scanner.next();

            // 获取接口
            ServiceAPI serviceAPI =
                    (ServiceAPI)context.getBean("consumerService");

            /*
                2017_order_t
                2018_order_t
             */
            List<String> strings = serviceAPI.mergeTest(message);
            for(String str : strings){
                System.out.println(str);
            }
        }

    }

}
