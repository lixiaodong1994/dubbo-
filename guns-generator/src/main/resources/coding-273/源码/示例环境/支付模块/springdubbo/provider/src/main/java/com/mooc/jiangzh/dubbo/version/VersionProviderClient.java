package com.mooc.jiangzh.dubbo.version;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class VersionProviderClient {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext(
                        "applicationContext-version-provider.xml");

        context.start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
