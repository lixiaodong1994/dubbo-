package com.stylefeng.guns.film.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ActorRequestVO implements Serializable {

    private ActorVO director;
    private List<ActorVO> actors;

}
