package com.mooc.jiangzh.dubbo;

import java.util.List;

public interface ServiceAPI {

    String sendMessage(String message);

    String sendMessage02(String message);

    List<String> mergeTest(String message);
}
