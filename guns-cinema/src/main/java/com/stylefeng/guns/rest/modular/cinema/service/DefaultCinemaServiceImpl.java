package com.stylefeng.guns.rest.modular.cinema.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.cinema.CinemaAPI;
import com.stylefeng.guns.cinema.vo.*;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.sun.org.apache.xpath.internal.operations.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DefaultCinemaServiceImpl
 * @Description
 * @Author
 * @Date 2018/12/28 20:44
 **/
@Component
@Service(interfaceClass = CinemaAPI.class,executes = 10)
public class DefaultCinemaServiceImpl implements CinemaAPI {

    @Autowired
    MoocCinemaTMapper moocCinemaTMapper;
    @Autowired
    MoocBrandDictTMapper moocBrandDictTMapper;
    @Autowired
    MoocAreaDictTMapper moocAreaDictTMapper;
    @Autowired
    MoocHallDictTMapper moocHallDictTMapper;
    @Autowired
    MoocFieldTMapper moocFieldTMapper;

    /**
     * 影院查询
     * @param cinemaQueryVO
     * @return
     */
    @Override
    public Page<CinemaVO> getCinemas(CinemaQueryVO cinemaQueryVO) {

        //1. 根据CinemaQueryVO，查询影院列表
        List<CinemaVO> cinemas = new ArrayList<>();
        Page<MoocCinemaT> page = new Page<>(cinemaQueryVO.getNowPage(),cinemaQueryVO.getPageSize());
        EntityWrapper<MoocCinemaT> entityWrapper = new EntityWrapper<>();
        if (cinemaQueryVO.getBrandId() != 99) {
            entityWrapper.eq("brand_id",cinemaQueryVO.getBrandId());
        }
        if (cinemaQueryVO.getDistrictId() != 99) {
            entityWrapper.eq("area_id",cinemaQueryVO.getDistrictId());
        }
        if (cinemaQueryVO.getHallType() != 99) {
            entityWrapper.like("hall_ids","%#"+cinemaQueryVO.getHallType()+"#%");
        }


        //2. 将数据实体转换为业务实体
        List<MoocCinemaT> moocCinemaTS = moocCinemaTMapper.selectPage(page, entityWrapper);
        for (MoocCinemaT moocCinemaT : moocCinemaTS) {
            CinemaVO cinemaVO = new CinemaVO();
            cinemaVO.setUuid(moocCinemaT.getUuid()+"");
            cinemaVO.setMinimumPrice(moocCinemaT.getMinimumPrice()+"");
            cinemaVO.setCinemaName(moocCinemaT.getCinemaName());
            cinemaVO.setAddress(moocCinemaT.getCinemaAddress());

            cinemas.add(cinemaVO);
        }

        //根据条件，得到影院列表总数
        long counts = moocCinemaTMapper.selectCount(entityWrapper);

        //3. 封装返回结果
        Page<CinemaVO> result = new Page<>();
        result.setRecords(cinemas);
        result.setSize(cinemaQueryVO.getPageSize());
        result.setTotal(counts);

        return result;
    }

    /**
     * 2、根据条件获取品牌列表[除了就99以外，其他的数字为isActive]
     * @param brandId
     * @return
     */
    @Override
    public List<BrandVO> getBrands(int brandId) {
        boolean flag = false;
        List<BrandVO> brandVOS = new ArrayList<>();

        //判断brandId是否存在
        MoocBrandDictT moocBrandDictT = moocBrandDictTMapper.selectById(brandId);
        if (brandId == 99 || moocBrandDictT == null || moocBrandDictT.getUuid() == null) {
            flag = true;
        }

        //查询所有列表
        List<MoocBrandDictT> moocList = moocBrandDictTMapper.selectList(null);
        for (MoocBrandDictT mooc : moocList) {
            BrandVO brandVO = new BrandVO();
            brandVO.setBrandId(mooc.getUuid()+"");
            brandVO.setBrandName(mooc.getShowName());

            //判断flag是否为true，为true，表明为全部
            if (flag) {
                if (mooc.getUuid() == 99) {
                    brandVO.setActive(true);
                }
            }else {
                if (mooc.getUuid() == brandId) {
                    brandVO.setActive(true);
                }
            }
            brandVOS.add(brandVO);
        }
        return brandVOS;
    }

    /**
     *  //3、获取行政区域列表
     * @param areaId
     * @return
     */
    @Override
    public List<AreaVO> getAreas(int areaId) {
        boolean flag = false;
        List<AreaVO> areaVOS = new ArrayList<>();

        //判断brandId是否存在
        MoocAreaDictT moocAreaDictT = moocAreaDictTMapper.selectById(areaId);
        if (areaId == 99 || moocAreaDictT == null || moocAreaDictT.getUuid() == null) {
            flag = true;
        }

        //查询所有列表
        List<MoocAreaDictT> moocAreaDictTS = moocAreaDictTMapper.selectList(null);
        for (MoocAreaDictT mooc : moocAreaDictTS) {
            AreaVO areaVO = new AreaVO();
            areaVO.setAreaId(mooc.getUuid()+"");
            areaVO.setAreaName(mooc.getShowName());

            //判断flag是否为true，为true，表明为全部
            if (flag) {
                if (mooc.getUuid() == 99) {
                    areaVO.setActive(true);
                }
            }else {
                if (mooc.getUuid() == areaId) {
                    areaVO.setActive(true);
                }
            }
            areaVOS.add(areaVO);
        }
        return areaVOS;
    }

    /**
     * 4、获取影厅类型列表
     * @param hallType
     * @return
     */
    @Override
    public List<HallTypeVO> getHallTypes(int hallType) {
        boolean flag = false;
        List<HallTypeVO> hallTypeVOS = new ArrayList<>();
        // 判断brandId是否存在
        MoocHallDictT moocHallDictT = moocHallDictTMapper.selectById(hallType);
        // 判断brandId 是否等于 99
        if(hallType == 99 || moocHallDictT==null || moocHallDictT.getUuid() == null){
            flag = true;
        }
        // 查询所有列表
        List<MoocHallDictT> moocHallDictTS = moocHallDictTMapper.selectList(null);
        // 判断flag如果为true，则将99置为isActive
        for(MoocHallDictT hall : moocHallDictTS){
            HallTypeVO hallTypeVO = new HallTypeVO();
            hallTypeVO.setHalltypeName(hall.getShowName());
            hallTypeVO.setHalltypeId(hall.getUuid()+"");
            // 如果flag为true，则需要99，如为false，则匹配上的内容为active
            if(flag){
                if(hall.getUuid() == 99){
                    hallTypeVO.setActive(true);
                }
            }else{
                if(hall.getUuid() == hallType){
                    hallTypeVO.setActive(true);
                }
            }

            hallTypeVOS.add(hallTypeVO);
        }

        return hallTypeVOS;
    }

    /**
     * 5、根据影院编号，获取影院信息
     * @param cinemaId
     * @return
     */
    @Override
    public CinemaInfoVO getCinemaInfoById(int cinemaId) {
        MoocCinemaT moocCinemaT = moocCinemaTMapper.selectById(cinemaId);
        if(moocCinemaT == null){
            return new CinemaInfoVO();
        }
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        cinemaInfoVO.setImgUrl(moocCinemaT.getImgAddress());
        cinemaInfoVO.setCinemaPhone(moocCinemaT.getCinemaPhone());
        cinemaInfoVO.setCinemaName(moocCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaId(moocCinemaT.getUuid()+"");
        cinemaInfoVO.setCinemaId(moocCinemaT.getCinemaAddress());

        return cinemaInfoVO;
    }

    /**
     * //6、获取所有电影的信息和对应的放映场次信息，根据影院编号
     * @param cinemaId
     * @return
     */
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(int cinemaId) {
        List<FilmInfoVO> filmInfos = moocFieldTMapper.getFilmInfos(cinemaId);
        return filmInfos;
    }

    /**
     * //7、根据放映场次ID获取放映信息
     * @param fieldId
     * @return
     */
    @Override
    public HallInfoVO getFilmFieldInfo(int fieldId) {

        HallInfoVO hallInfoVO = moocFieldTMapper.getHallInfo(fieldId);
        return hallInfoVO;
    }

    /**
     * //8、根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
     * @param fieldId
     * @return
     */
    @Override
    public FilmInfoVO getFilmInfoByFieldId(int fieldId) {

        FilmInfoVO filmInfoVO = moocFieldTMapper.getFilmInfoById(fieldId);
        return filmInfoVO;
    }

    /**
     * 获取订单需要的电影信息
     * @param fieldId
     * @return
     */
    @Override
    public OrderQueryVO getOrderNeeds(int fieldId) {
        OrderQueryVO orderQueryVO = new OrderQueryVO();
        MoocFieldT moocFieldT = moocFieldTMapper.selectById(fieldId);
        orderQueryVO.setCinemaId(moocFieldT.getCinemaId() + "");
        orderQueryVO.setFilmPrice(moocFieldT.getPrice() + "");
        return orderQueryVO;
    }
}
