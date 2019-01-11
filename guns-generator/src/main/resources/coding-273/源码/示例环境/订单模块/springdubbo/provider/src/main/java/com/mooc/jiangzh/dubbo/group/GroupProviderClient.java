package com.mooc.jiangzh.dubbo.group;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class GroupProviderClient {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext(
                        "applicationContext-group-provider.xml");

        context.start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
