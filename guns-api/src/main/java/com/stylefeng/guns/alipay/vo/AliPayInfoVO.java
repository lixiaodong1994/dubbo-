package com.stylefeng.guns.alipay.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AliPayInfoVO implements Serializable {

    private String orderId;
    private String QRCodeAddress;

}
