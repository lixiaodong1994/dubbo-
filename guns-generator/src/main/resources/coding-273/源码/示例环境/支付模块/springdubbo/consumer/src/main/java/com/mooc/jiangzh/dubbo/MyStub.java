package com.mooc.jiangzh.dubbo;


import java.util.List;

public class MyStub implements ServiceAPI{

    // 注入Proxy的构造函数
    private final ServiceAPI serviceAPI;
    public MyStub(ServiceAPI serviceAPI){
        this.serviceAPI = serviceAPI;
    }

    @Override
    public String sendMessage(String message) {
        System.out.println("stub sendMessage");
        if(message.equals("123")){
            return "抱歉，该值不能被接受";
        }else{
            message = "stub message ->"+message;
            return this.serviceAPI.sendMessage(message);
        }

    }

    @Override
    public String sendMessage02(String message) {
        System.out.println("stub sendMessage2");
        return this.serviceAPI.sendMessage02(message);
    }

    @Override
    public List<String> mergeTest(String message) {
        System.out.println("stub mergeTest");
        return this.serviceAPI.mergeTest(message);
    }
}
