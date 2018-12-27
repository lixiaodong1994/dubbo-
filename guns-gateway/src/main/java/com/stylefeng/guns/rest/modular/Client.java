package com.stylefeng.guns.rest.modular;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @ClassName Client
 * @Description TODO
 * @Author admin
 * @Date 2018/12/25 14:31
 **/
@Component
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    @Reference(interfaceClass = UserService.class)
    UserService userService;

    public void run(String username,String password) {

    }

}
