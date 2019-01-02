package com.stylefeng.guns.rest.modular.order.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.cinema.CinemaAPI;
import com.stylefeng.guns.cinema.vo.FilmInfoVO;
import com.stylefeng.guns.cinema.vo.OrderQueryVO;
import com.stylefeng.guns.core.util.UUIDUtil;
import com.stylefeng.guns.order.OrderAPI;
import com.stylefeng.guns.order.vo.OrderVO;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.util.FTPUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DefaultCinemaServiceImpl
 * @Description
 * @Author
 * @Date 2018/12/28 20:44
 **/
@Slf4j
@Component
@Service(interfaceClass = OrderAPI.class,executes = 10)
public class DefaultOrderServiceImpl implements OrderAPI {

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    @Autowired
    FTPUtil ftpUtil;
    @Reference(interfaceClass = CinemaAPI.class,check = false)
    CinemaAPI cinemaAPI;
    /**
     * 是否为真实的座位编号
     * @param fieldId
     * @param seats
     * @return
     */
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        //根据fieldId获取对应的座位位置图
        String seatsPath = moocOrderTMapper.getSeatsByFieldId(fieldId);
        //根据文件地址获取文件内容字符串
        String fileContentByAddress = ftpUtil.getFileContentByAddress(seatsPath);
        // 将fileStrByAddress转换为JSON对象
        JSONObject object = JSON.parseObject(fileContentByAddress);
        // seats=1,2,3   ids="1,3,4,5,6,7,88"
        String ids = object.get("ids").toString();

        // 每一次匹配上的，都给isTrue+1
        String[] seatsArr = seats.split(",");
        String[] idsArr = ids.split(",");

        int isTrue = 0;
        for (String id : idsArr) {
            for (String seat : seatsArr) {
                if (seat.equalsIgnoreCase(id)) {
                    isTrue++ ;
                }
            }
        }

        if (seatsArr.length == isTrue) {
            return true;
        }else {
            return false;
        }
    }

    /**
     * 判断是否为已售座位
     * @param fieldId
     * @param seats
     * @return
     */
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("field_id",fieldId);

        /** 判断座位编号有一个匹配上，则返回false*/
        List<MoocOrderT> moocOrderTList = moocOrderTMapper.selectList(entityWrapper);
        String[] ids = seats.split(",");
        for (MoocOrderT moocOrderT : moocOrderTList) {
            String[] seatIds = moocOrderT.getSeatsIds().split(",");
            for (String seatId : seatIds) {
                for (String id : ids) {
                    if (id.equalsIgnoreCase(seatId)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 生成订单
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     * @param userId
     * @return
     */
    @Override
    public OrderVO saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        //编号
        String uuid = UUIDUtil.genUuid();

        //影片信息
        FilmInfoVO filmInfoVO = cinemaAPI.getFilmInfoByFieldId(fieldId);
        int filmId = Integer.parseInt(filmInfoVO.getFilmId());

        //影院信息
        OrderQueryVO orderNeeds = cinemaAPI.getOrderNeeds(fieldId);
        int cinemaId = Integer.parseInt(orderNeeds.getCinemaId());
        double filmPrice = Double.parseDouble(orderNeeds.getFilmPrice());

        //求订单总金额
        int solds = soldSeats.split(",").length;
        double totalPrice = coutPrice(solds, filmPrice);

        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(uuid);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderPrice(totalPrice);
        moocOrderT.setFilmPrice(filmPrice);
        moocOrderT.setFilmId(filmId);
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setCinemaId(cinemaId);

        Integer insert = moocOrderTMapper.insert(moocOrderT);
        if(insert>0){
            // 返回查询结果
            OrderVO orderVO = moocOrderTMapper.getOrderInfoById(uuid);
            if(orderVO == null || orderVO.getOrderId() == null){
                log.error("订单信息查询失败,订单编号为{}",uuid);
                return null;
            }else {
                return orderVO;
            }
        }else{
            // 插入出错
            log.error("订单插入失败");
            return null;
        }

    }
    /**
     * 获取订单总金额
     * @param sourts
     * @param price
     * @return
     */
    private static double coutPrice(int sourts,double price) {
        BigDecimal sourtsDeci = new BigDecimal(sourts);
        BigDecimal priceDeci = new BigDecimal(price);

        BigDecimal result = sourtsDeci.multiply(priceDeci);
        double finalResult = result.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return finalResult;
    }


    /**
     * 根据用户id获取订单信息
     * @param userId
     * @param page
     * @return
     */
    @Override
    public Page<OrderVO> getOrderByUserId(Integer userId,Page<OrderVO> page) {
        Page<OrderVO> result = new Page<>();
        if(userId == null){
            log.error("订单查询业务失败，用户编号未传入");
            return null;
        }else{
            List<OrderVO> ordersByUserId = moocOrderTMapper.getOrdersByUserId(userId,page);
            if(ordersByUserId==null && ordersByUserId.size()==0){
                result.setTotal(0);
                result.setRecords(new ArrayList<>());
                return result;
            }else{
                // 获取订单总数
                EntityWrapper<MoocOrderT> entityWrapper = new EntityWrapper<>();
                entityWrapper.eq("order_user",userId);
                Integer counts = moocOrderTMapper.selectCount(entityWrapper);
                // 将结果放入Page
                result.setTotal(counts);
                result.setRecords(ordersByUserId);

                return result;
            }
        }
    }

    /**
     * 根据fieldId 获取所有已经销售的座位编号
     * @param fieldId
     * @return
     */
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId) {
        if (fieldId == null) {
            log.error("查询已售座位错误");
            return "";
        }else {
            String soldSeatsByFieldId = moocOrderTMapper.getSoldSeatsByFieldId(fieldId);
            return soldSeatsByFieldId;
        }
    }
}
