package com.stylefeng.guns.rest.modular.VO;

import lombok.Data;

/**
 * @ClassName ResponseEntity
 * @Description TODO
 * @Author admin
 * @Date 2018/12/25 15:22
 **/

@Data
public class ResponseEntity<M> {
    //status:0-成功；1-失败；999-异常
    private int status;
    private String message;
    private M data;
    private String img;

    public ResponseEntity(){}

    public static<M> ResponseEntity success(String img,M m) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStatus(0);
        responseEntity.setData(m);
        responseEntity.setImg(img);
        return responseEntity;
    }

    public static<M> ResponseEntity success(M m) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStatus(0);
        responseEntity.setData(m);
        return responseEntity;
    }

    public static<M> ResponseEntity serviceFail(String message) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStatus(1);
        responseEntity.setData(message);
        return responseEntity;
    }

    public static<M> ResponseEntity appFail(String message) {
        ResponseEntity responseEntity = new ResponseEntity();
        responseEntity.setStatus(999);
        responseEntity.setData(message);
        return responseEntity;
    }

}
