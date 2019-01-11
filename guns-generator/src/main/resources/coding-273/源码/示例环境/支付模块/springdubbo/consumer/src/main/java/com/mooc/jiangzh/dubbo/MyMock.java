package com.mooc.jiangzh.dubbo;

import java.util.List;

public class MyMock implements ServiceAPI{
    @Override
    public String sendMessage(String message) {
        return "抱歉，订单人数过多，请稍后重试";
    }

    @Override
    public String sendMessage02(String message) {
        return "抱歉，订单人数过多，请稍后重试";
    }

    @Override
    public List<String> mergeTest(String message) {
        return null;
    }
}
