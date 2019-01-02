package com.stylefeng.guns.cinema.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName OrderQueryVO
 * @Description
 * @Author
 * @Date 2019/1/2 14:19
 **/
@Data
public class OrderQueryVO implements Serializable{

    private String cinemaId;
    private String filmPrice;

}
