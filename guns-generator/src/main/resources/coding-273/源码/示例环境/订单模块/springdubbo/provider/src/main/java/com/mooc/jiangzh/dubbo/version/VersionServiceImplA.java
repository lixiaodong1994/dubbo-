package com.mooc.jiangzh.dubbo.version;


import com.mooc.jiangzh.dubbo.ServiceAPI;

import java.util.Arrays;
import java.util.List;

public class VersionServiceImplA implements ServiceAPI {

    @Override
    public String sendMessage(String message) {
        System.out.println("message group a version0.1="+message);

        return "quickstart-provider-message-group-a version0.1="+message;
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
