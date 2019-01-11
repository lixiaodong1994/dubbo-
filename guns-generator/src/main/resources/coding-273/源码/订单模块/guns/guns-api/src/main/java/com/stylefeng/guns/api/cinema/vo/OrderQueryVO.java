package com.stylefeng.guns.api.cinema.vo;

import lombok.Data;
import sun.plugin2.message.Serializer;

import java.io.Serializable;

@Data
public class OrderQueryVO implements Serializable{

    private String cinemaId;
    private String filmPrice;

}
