package com.mooc.jiangzh.dubbo.stub;


import com.mooc.jiangzh.dubbo.ServiceAPI;

import java.util.Arrays;
import java.util.List;

public class StubServiceImpl implements ServiceAPI {

    @Override
    public String sendMessage(String message) {
        System.out.println("message stub ="+message);

        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "quickstart-provider-message-stub="+message;
    }

    @Override
    public String sendMessage02(String message) {
        System.out.println("message02="+message);

        return "quickstart-provider-message02 ="+message;
    }

    @Override
    public List<String> mergeTest(String message) {
        String str = "groupA = "+message;
        return Arrays.asList(str);
    }

}
