package com.stylefeng.guns.rest.common;

/**
 * @ClassName CurrentUser
 * @Description TODO
 * @Author admin
 * @Date 2018/12/25 15:41
 **/
public class CurrentUser {
    private static final ThreadLocal<String> threadLocal = new ThreadLocal<>();

    public static void setUserInfo(String userId) {
        threadLocal.set(userId);
    }

    public static String getUserInfo() {
        return threadLocal.get();
    }


}
