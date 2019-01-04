package com.stylefeng.guns.rest.common;

/**
 * @ClassName CurrentUser
 * @Description 使用threadlocal存取用户信息
 * @Author lxd
 * @Date 2018/12/25 15:41
 **/
public class CurrentUser {
    private static final InheritableThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    public static void setUserInfo(String userId) {
        threadLocal.set(userId);
    }

    public static String getUserInfo() {
        return threadLocal.get();
    }


}
