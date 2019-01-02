package com.stylefeng.guns.core.util;

import java.util.Random;
import java.util.UUID;

/**
 * @ClassName UUIDUtil
 * @Description
 * @Author lxd
 * @Date 2019/1/2 14:15
 **/
public class UUIDUtil {

    public static String genUuid() {
        return UUID.randomUUID().toString();
    }

}
