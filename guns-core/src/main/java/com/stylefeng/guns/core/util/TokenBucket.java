package com.stylefeng.guns.core.util;

/**
 * @ClassName TokenBucket
 * @Description 令牌桶
 * @Author lxd
 * @Date 2019/1/4 13:52
 **/
public class TokenBucket {

    private int bucketNums = 100; //桶的容量
    private int rate = 1;           //流入速度
    private int nowTokens;          //当前令牌数量
    private long timestamp = getNowTime();     //当前时间

    //获取当前时间
    private long getNowTime() {
        return System.currentTimeMillis();
    }

    private int min(int tokens) {
        if (bucketNums > tokens) {
            //令牌数量还没有超过桶
            return tokens;
        }else {
            //令牌数量超过桶，则返回桶
            return bucketNums;
        }
    }

    public boolean getToken() {
        //记录拿令牌的时间
        long nowTime = getNowTime();
        //添加令牌【判断有多少个令牌】
        nowTokens = nowTokens + (int)((nowTime - timestamp)*rate);
        //添加以后的令牌数量与桶的容量哪个小
        nowTokens = min(nowTokens);
        //修改拿令牌的时间
        timestamp = nowTime;
        //判断令牌是否足够
        if (nowTokens < 1) {
            return false;
        }else {
            return true;
        }
    }




}
