package com.mooc.jiangzh.dubbo.group;


import com.mooc.jiangzh.dubbo.ServiceAPI;

import java.util.Arrays;
import java.util.List;

public class GroupServiceImplB implements ServiceAPI {

    @Override
    public String sendMessage(String message) {
        System.out.println("message group b="+message);

        return "quickstart-provider-message-group-b="+message;
    }

    @Override
    public String sendMessage02(String message) {
        System.out.println("message02="+message);

        return "quickstart-provider-message02 ="+message;
    }

    public List<String> mergeTest(String message) {
        String str = "groupB = "+message;
        return Arrays.asList(str);
    }

}
