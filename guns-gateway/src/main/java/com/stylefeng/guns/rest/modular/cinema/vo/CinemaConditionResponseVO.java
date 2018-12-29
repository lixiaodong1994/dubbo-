package com.stylefeng.guns.rest.modular.cinema.vo;

import com.stylefeng.guns.cinema.vo.AreaVO;
import com.stylefeng.guns.cinema.vo.BrandVO;
import com.stylefeng.guns.cinema.vo.HallTypeVO;
import lombok.Data;

import java.util.List;

@Data
public class CinemaConditionResponseVO {

    private List<BrandVO> brandList;
    private List<AreaVO> areaList;
    private List<HallTypeVO> halltypeList;

}
