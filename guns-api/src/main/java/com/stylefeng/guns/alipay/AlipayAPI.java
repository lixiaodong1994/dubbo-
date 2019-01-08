package com.stylefeng.guns.alipay;

import com.stylefeng.guns.alipay.vo.AliPayInfoVO;
import com.stylefeng.guns.alipay.vo.AliPayResultVO;

/**
 * @ClassName AlipayAPI
 * @Description 支付接口
 * @Author lxd
 * @Date 2019/1/4 16:30
 **/
public interface AlipayAPI {
    //获取订单二维码
    AliPayInfoVO getQRCode(String orderId);
    //获取订单状态
    AliPayResultVO getOrderStatus(String orderId);
}
