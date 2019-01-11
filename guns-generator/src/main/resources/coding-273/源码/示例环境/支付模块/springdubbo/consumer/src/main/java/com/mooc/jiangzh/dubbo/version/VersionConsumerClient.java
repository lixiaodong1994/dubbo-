package com.mooc.jiangzh.dubbo.version;

import com.mooc.jiangzh.dubbo.ServiceAPI;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class VersionConsumerClient {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext("" +
                        "applicationContext-version-consumer.xml");

        context.start();

        while (true){
            Scanner scanner = new Scanner(System.in);
            String message = scanner.next();

            // 获取接口
            ServiceAPI serviceAPI =
                    (ServiceAPI)context.getBean("consumerService");

            System.out.println(serviceAPI.sendMessage(message));

        }

    }

}
