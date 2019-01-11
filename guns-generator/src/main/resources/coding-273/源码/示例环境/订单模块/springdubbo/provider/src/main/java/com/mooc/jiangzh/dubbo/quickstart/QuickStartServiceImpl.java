package com.mooc.jiangzh.dubbo.quickstart;


import com.mooc.jiangzh.dubbo.ServiceAPI;

import java.util.List;

public class QuickStartServiceImpl implements ServiceAPI {

    @Override
    public String sendMessage(String message) {
        System.out.println("message="+message);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "quickstart-provider-message="+message;
    }

    @Override
    public String sendMessage02(String message) {
        System.out.println("message02="+message);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "quickstart-provider-message02 ="+message;
    }

    @Override
    public List<String> mergeTest(String message) {
        return null;
    }

}
