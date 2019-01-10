package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.rpc.RpcContext;
import com.baomidou.mybatisplus.plugins.Page;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.stylefeng.guns.alipay.AlipayAPI;
import com.stylefeng.guns.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.alipay.vo.AliPayResultVO;
import com.stylefeng.guns.core.util.TokenBucket;
import com.stylefeng.guns.order.OrderAPI;
import com.stylefeng.guns.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.VO.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName OrderController
 * @Description 订单模块controller层
 * @Author lxd
 * @Date 2019/1/2 9:37
 **/
@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {

    private static TokenBucket tokenBucket = new TokenBucket();
    private static final String IMG_PNG = "http://www.maoyan.com";
    @Reference(interfaceClass = OrderAPI.class,check = false,group = "order2018",filter = "tracing")
    OrderAPI orderAPI;
    @Reference(interfaceClass = OrderAPI.class,check = false,group = "order2017",filter = "tracing")
    OrderAPI orderAPI2017;
    @Reference(interfaceClass = AlipayAPI.class,check = false,filter = "tracing")
    AlipayAPI alipayAPI;


    public ResponseEntity error(Integer fieldId,String soldSeats,String seatsName){
        return ResponseEntity.serviceFail("抱歉，下单的人太多了，请稍后重试");
    }

    /**
        购票模块
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     * @return
     */
    @HystrixCommand(fallbackMethod = "error", commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD"),
            @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "4000"),
            @HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "10"),
            @HystrixProperty(name = "circuitBreaker.errorThresholdPercentage", value = "50")},
            threadPoolProperties = {
                    @HystrixProperty(name = "coreSize", value = "1"),
                    @HystrixProperty(name = "maxQueueSize", value = "10"),
                    @HystrixProperty(name = "keepAliveTimeMinutes", value = "1000"),
                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "8"),
                    @HystrixProperty(name = "metrics.rollingStats.numBuckets", value = "12"),
                    @HystrixProperty(name = "metrics.rollingStats.timeInMilliseconds", value = "1500")
            })
    @PostMapping("buyTickets")
    public ResponseEntity buyTickets(Integer fieldId,String soldSeats,String seatsName) {

        //判断人数是否过多
        if (tokenBucket.getToken()) {
            //验证售出的票是否为真
            boolean isTrue = orderAPI.isTrueSeats(fieldId + "", soldSeats);
            if (!isTrue) {
                log.error("售出的票不为真");
                return ResponseEntity.serviceFail("售出的票不为真");
            }
            //已经销售的座位里，有没有这些座位
            boolean isNotSold = orderAPI.isNotSoldSeats(fieldId + "", soldSeats);
            if (!isNotSold) {
                log.error("售出的票已经存在");
                return ResponseEntity.serviceFail("售出的票已经存在");
            }
            //创建订单，注意获取登陆人
            if (isTrue && isNotSold) {
                //只有都为true，才创建订单
                String userId = CurrentUser.getUserInfo();
                if (userId == null && userId.trim().length() <= 0) {
                    log.error("用户未登陆");
                    return ResponseEntity.serviceFail("用户未登陆");
                }

                OrderVO orderVO = orderAPI.saveOrderInfo(fieldId, soldSeats, seatsName, Integer.parseInt(userId));
                if (orderVO == null) {
                    log.error("购票失败");
                    return ResponseEntity.serviceFail("购票业务错误");
                } else {
                    return ResponseEntity.success(orderVO);
                }
            } else {
                return ResponseEntity.serviceFail("订单中的座位编号有问题");
            }
        }else {
            return ResponseEntity.serviceFail("购买人数过多，请稍后再试！！！");
        }
    }

    /**
     * 获取订单信息
     * @param nowPage
     * @param pageSize
     * @return
     */
    @PostMapping("getOrderInfo")
    public ResponseEntity getOrderInfo(@RequestParam(name = "nowPage",required = false,defaultValue = "1") Integer nowPage,
                                       @RequestParam(name = "pageSize",required = false,defaultValue = "5") Integer pageSize) {

        String userId = CurrentUser.getUserInfo();

        Page<OrderVO> page = new Page<>(nowPage,pageSize);
        if (userId != null && userId.trim().length() > 0) {
            Page<OrderVO> result = orderAPI.getOrderByUserId(Integer.parseInt(userId), page);
            Page<OrderVO> result2017 = orderAPI2017.getOrderByUserId(Integer.parseInt(userId), page);
            int totalPages = (int) (result.getPages()+result2017.getPages());

            //整合两个结果
            List<OrderVO> resultList = new ArrayList<>();
            resultList.addAll(result.getRecords());
            resultList.addAll(result2017.getRecords());

            return ResponseEntity.success(nowPage,totalPages,"",resultList);
        }else {
           // log.error("用户未登陆");
            return ResponseEntity.serviceFail("用户未登陆");
        }
    }

    /**
     * 获取订单二维码
     * @param orderId
     * @return
     */
    @PostMapping("getQRCode")
    public ResponseEntity getQRCode(@RequestParam("orderId") String orderId) {
        String userInfo = CurrentUser.getUserInfo();
        if (userInfo == null || userInfo.trim().length() == 0) {
            return ResponseEntity.serviceFail("用户未登陆，请登陆后再试");
        }

        AliPayInfoVO qrCode = alipayAPI.getQRCode(orderId);
        return ResponseEntity.success(IMG_PNG,qrCode);
    }

    /**
     * 获取订单状态
     * @param orderId
     * @param tryNums
     * @return
     */
    @PostMapping("getPayResult")
    public ResponseEntity getPayResult(@RequestParam("orderId") String orderId,@RequestParam("tryNums") Integer tryNums) {
        String userInfo = CurrentUser.getUserInfo();
        if (userInfo == null || userInfo.trim().length() == 0) {
            return ResponseEntity.serviceFail("用户未登陆，请登陆后再试");
        }

        //dubbo的隐式传递
        RpcContext.getContext().setAttachment("userId",userInfo);

        if (tryNums > 3) {
            return ResponseEntity.serviceFail("支付超时失败");
        }else {
            AliPayResultVO orderStatus = alipayAPI.getOrderStatus(orderId);
            return ResponseEntity.success(orderStatus);
        }
    }

}
