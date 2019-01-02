package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.order.OrderAPI;
import com.stylefeng.guns.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.VO.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @Reference(interfaceClass = OrderAPI.class,check = false)
    OrderAPI orderAPI;

    /**
        购票模块
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     * @return
     */
    @PostMapping("buyTickets")
    public ResponseEntity buyTickets(Integer fieldId,String soldSeats,String seatsName) {

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
            }else {
                return ResponseEntity.success(orderVO);
            }
        }else{
            return ResponseEntity.serviceFail("订单中的座位编号有问题");
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
            return ResponseEntity.success(nowPage,(int)result.getPages(),"",result.getRecords());
        }else {
           // log.error("用户未登陆");
            return ResponseEntity.serviceFail("用户未登陆");
        }
    }
}
