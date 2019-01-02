package com.stylefeng.guns.cinema;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.cinema.vo.*;

import java.util.List;

/**
 * @ClassName CinemaAPI
 * @Description 影院接口
 * @Author lxd
 * @Date 2018/12/28 17:19
 **/
public interface CinemaAPI {

    //1.根据CinemaQueryVO，查询影院列表
    Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO);
    //2.根据条件获取品牌列表
    List<BrandVO> getBrands(int brandId);
    //3.获取行政区域列表
    List<AreaVO> getAreas(int areaId);
    //4.获取影厅类型列表
    List<HallTypeVO> getHallTypes(int hallType);
    //5.根据影院编号，获取影院信息
    CinemaInfoVO getCinemaInfoById(int cinemaId);
    //6.获取所有电影的信息和对应的放映场次信息，根据影院编号
    List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId);
    //7.根据放映场次id获取放映信息
    HallInfoVO getFilmFieldInfo(int fieldId);
    //8.根据反攻场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
    FilmInfoVO getFilmInfoByFieldId(int fieldId);

    //获取订单需要的电影信息
    OrderQueryVO getOrderNeeds(int fieldId);


}
