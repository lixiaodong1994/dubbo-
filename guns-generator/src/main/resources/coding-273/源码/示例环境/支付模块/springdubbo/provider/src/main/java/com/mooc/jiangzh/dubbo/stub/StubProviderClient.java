package com.mooc.jiangzh.dubbo.stub;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class StubProviderClient {

    public static void main(String[] args) {
        ClassPathXmlApplicationContext context =
                new ClassPathXmlApplicationContext(
                        "applicationContext-stub-provider.xml");

        context.start();

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
