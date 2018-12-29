package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.cinema.CinemaAPI;
import com.stylefeng.guns.cinema.vo.*;
import com.stylefeng.guns.rest.modular.VO.ResponseEntity;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaConditionResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldResponseVO;
import com.stylefeng.guns.rest.modular.cinema.vo.CinemaFieldsResponseVO;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName CinemaController
 * @Description
 * @Author lxd
 * @Date 2018/12/28 17:18
 **/
@Log4j
@RestController
@RequestMapping("cinema")
public class CinemaController {
    private static final String IMG = "http://img:maoyan.com";

    @Reference(interfaceClass = CinemaAPI.class,cache = "lru",connections = 10,check = false)
    CinemaAPI cinemaAPI;

    @GetMapping("getCinemas")
    public ResponseEntity getCinemas(CinemaQueryVO cinemaQueryVO) {
        try {
            Page<CinemaVO> cinemas = cinemaAPI.getCinemas(cinemaQueryVO);
            if (cinemas == null) {
                return ResponseEntity.success("没有影院可查");
            }else {
                return ResponseEntity.success(cinemas.getCurrent(),(int)cinemas.getPages(),IMG,cinemas.getRecords());
            }

        }catch (Exception ex) {
            log.error("获取影院异常----",ex);
            return ResponseEntity.serviceFail("查询影院列表失败");
        }
    }

    /**
     * 获取品牌列表
     * @param cinemaQueryVO
     * @return
     */
    @GetMapping("getCondition")
    public ResponseEntity getCondition(CinemaQueryVO cinemaQueryVO) {
        try{
            List<BrandVO> brands = cinemaAPI.getBrands(cinemaQueryVO.getBrandId());
            List<AreaVO> areas = cinemaAPI.getAreas(cinemaQueryVO.getDistrictId());
            List<HallTypeVO> hallTypes = cinemaAPI.getHallTypes(cinemaQueryVO.getHallType());

            CinemaConditionResponseVO cinemaConditionResponseVO = new CinemaConditionResponseVO();
            cinemaConditionResponseVO.setBrandList(brands);
            cinemaConditionResponseVO.setHalltypeList(hallTypes);
            cinemaConditionResponseVO.setAreaList(areas);
            return ResponseEntity.success(cinemaConditionResponseVO);
        }catch (Exception ex) {
            log.error("获取列表异常"+ex);
            return ResponseEntity.appFail("获取列表异常");
        }
    }

    /**
     * 获取行政区域列表
     * @param cinemaId
     * @return
     */
    @PostMapping("getAreas")
    public ResponseEntity getAreas(Integer cinemaId) {
        try{
            CinemaInfoVO cinemaInfoById = cinemaAPI.getCinemaInfoById(cinemaId);
            List<FilmInfoVO> filmInfoByCinemaId = cinemaAPI.getFilmInfoByCinemaId(cinemaId);

            CinemaFieldsResponseVO cinemaFieldsResponseVO = new CinemaFieldsResponseVO();
            cinemaFieldsResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldsResponseVO.setFilmList(filmInfoByCinemaId);

            return ResponseEntity.success(IMG,cinemaFieldsResponseVO);
        }catch (Exception ex) {
            log.error("获取播放场次异常"+ex);
            return ResponseEntity.appFail("获取播放场次异常");
        }
    }

    /**
     * 获取影厅信息
     * @param cinemaId
     * @param fieldId
     * @return
     */
    @PostMapping("getFieldInfo")
    public ResponseEntity getFieldInfo(Integer cinemaId,Integer fieldId){
        try{

            CinemaInfoVO cinemaInfoById = cinemaAPI.getCinemaInfoById(cinemaId);
            FilmInfoVO filmInfoByFieldId = cinemaAPI.getFilmInfoByFieldId(fieldId);
            HallInfoVO filmFieldInfo = cinemaAPI.getFilmFieldInfo(fieldId);

            //TODO: 造几个销售的假数据，后续会对接订单接口
            filmFieldInfo.setSoldSeats("1,2,3");

            CinemaFieldResponseVO cinemaFieldResponseVO = new CinemaFieldResponseVO();
            cinemaFieldResponseVO.setCinemaInfo(cinemaInfoById);
            cinemaFieldResponseVO.setFilmInfo(filmInfoByFieldId);
            cinemaFieldResponseVO.setHallInfo(filmFieldInfo);

            return ResponseEntity.success(IMG,cinemaFieldResponseVO);
        }catch (Exception e){
            log.error("获取选座信息失败",e);
            return ResponseEntity.serviceFail("获取选座信息失败");
        }
    }

}
