package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.film.FilmAPI;
import com.stylefeng.guns.film.vo.CatVO;
import com.stylefeng.guns.film.vo.FilmVO;
import com.stylefeng.guns.film.vo.SourceVO;
import com.stylefeng.guns.film.vo.YearVO;
import com.stylefeng.guns.rest.modular.VO.ResponseEntity;
import com.stylefeng.guns.rest.modular.film.vo.FilmConditionVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import com.stylefeng.guns.rest.modular.film.vo.FilmRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


/**
 * @ClassName FilmController
 * @Description
 * @Author lxd
 * @Date 2018/12/26 15:11
 **/
@RestController
@RequestMapping("film")
public class FilmController {

    private static final String IMG = "http://img.maoyan.com";

    private static final Logger logger = LoggerFactory.getLogger(FilmController.class);

    @Reference(interfaceClass = FilmAPI.class,check = false)
    FilmAPI filmAPI;


    /**
     * 电影首页
     * @return
     */
    @GetMapping("/getIndex")
    public ResponseEntity getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        filmIndexVO.setBanners(filmAPI.getBanners());
        filmIndexVO.setHotFilms(filmAPI.getHotFilms(true,8,1,1,99,99,99));
        filmIndexVO.setSoonFilms(filmAPI.getSoonFilms(true,8,1,1,99,99,99));
        filmIndexVO.setBoxRanking(filmAPI.getBoxRanking());
        filmIndexVO.setExpectRanking(filmAPI.getExpectRanking());
        filmIndexVO.setTop100(filmAPI.getTop());
        return ResponseEntity.success(IMG,filmIndexVO);
    }

    /**
     * 获取电影列表
     * @param catId
     * @param sourceId
     * @param yearId
     * @return
     */
    @GetMapping("/getConditionList")
    public ResponseEntity getConditionList(@RequestParam(name = "catId",required = false,defaultValue ="99")String catId,
                                           @RequestParam(name = "sourceId",required = false,defaultValue ="99")String sourceId,
                                           @RequestParam(name = "yearId",required = false,defaultValue ="99")String yearId) {

        FilmConditionVO filmConditionVO = new FilmConditionVO();

        boolean flag;

        flag = false;
        List<CatVO> cats = filmAPI.getCats();
        List<CatVO> catVOList = new ArrayList<>();
        CatVO cat = null;
        // 1,2,4,99,5,8
        if (cats.size() >= 1) {
            for (CatVO catVO : cats) {
                if (catVO.getCatId().equals("99")) {
                    //是全部选项的时候，跳过，继续往下执行
                    cat = catVO;
                    continue;
                }
                //当不是全部选项的时候，判断选中的标签的id是那个，然后将状态变为active
                if (catVO.getCatId().equals(catId)) {
                    flag = true;
                    catVO.setActive(true);
                }else {
                    catVO.setActive(false);
                }
                catVOList.add(catVO);
                //如果都没有选，flag还是false，也就是说还是默认的全部选项的时候，将
                //该全部选项变为active然后再放到集合中
            }
            if (!flag) {
                cat.setActive(true);
                catVOList.add(cat);
            }else {
                cat.setActive(false);
                catVOList.add(cat);
            }
        }else {
            logger.info("cats：从数据库中查询到的cats为空！！！！");
        }
        //片源
        flag = false;
        List<SourceVO> sources = filmAPI.getSources();
        List<SourceVO> sourceVOList = new ArrayList<>();
        SourceVO source = null;
        // 1,2,4,99,5,8
        if (sources.size() >= 1) {
            for (SourceVO sourceVO : sources) {
                if (sourceVO.getSourceId().equals("99")) {
                    //是全部选项的时候，跳过，继续往下执行
                    source = sourceVO;
                    continue;
                }
                //当不是全部选项的时候，判断选中的标签的id是那个，然后将状态变为active
                if (sourceVO.getSourceId().equals(catId)) {
                    flag = true;
                    sourceVO.setActive(true);
                }else {
                    sourceVO.setActive(false);
                }
                sourceVOList.add(sourceVO);
            }
            //如果都没有选，flag还是false，也就是说还是默认的全部选项的时候，将
            //该全部选项变为active然后再放到集合中
            if (!flag) {
                source.setActive(true);
                sourceVOList.add(source);
            }else {
                source.setActive(false);
                sourceVOList.add(source);
            }
        }else {
            logger.info("sources：从数据库中查询到的sources为空！！！！");
        }
        //年代集合
        flag = false;
        List<YearVO> years = filmAPI.getYears();
        List<YearVO> yearVOList = new ArrayList<>();
        YearVO year = null;
        // 1,2,4,99,5,8
        if (years.size() >= 1) {
            for (YearVO yearVO : years) {
                if (yearVO.getYearId().equals("99")) {
                    //是全部选项的时候，跳过，继续往下执行
                    year = yearVO;
                    continue;
                }
                //当不是全部选项的时候，判断选中的标签的id是那个，然后将状态变为active
                if (yearVO.getYearId().equals(catId)) {
                    flag = true;
                    yearVO.setActive(true);
                }else {
                    yearVO.setActive(false);
                }
                yearVOList.add(yearVO);
            }
            //如果都没有选，flag还是false，也就是说还是默认的全部选项的时候，将
            //该全部选项变为active然后再放到集合中
            if (!flag) {
                year.setActive(true);
                yearVOList.add(year);
            }else {
                year.setActive(false);
                yearVOList.add(year);
            }
        }else {
            logger.info("years：从数据库中查询到的years为空！！！！");
        }

        filmConditionVO.setCatInfo(catVOList);
        filmConditionVO.setSourceInfo(sourceVOList);
        filmConditionVO.setYearInfo(yearVOList);

        return ResponseEntity.success(filmConditionVO);
    }


    /**
     * 查询电影
     * @param filmRequestVO
     * @return
     */
    @GetMapping("getFilms")
    public ResponseEntity getFilms(FilmRequestVO filmRequestVO) {
        String IMG = "http://img:maoyan.com";
        FilmVO filmVO = null;
        //判断showType判断影片查询类型
        //根据sortId排序
        //添加各种条件查询
        //判断当前是第几页
        switch (filmRequestVO.getShowType()) {
            case 1:
                filmVO = filmAPI.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                        filmRequestVO.getSortId(), filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            case 2:
                filmVO = filmAPI.getSoonFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                    filmRequestVO.getSortId(), filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            case 3:
                filmVO = filmAPI.getClassicFilms(filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                    filmRequestVO.getSortId(), filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
            default:
                filmVO = filmAPI.getHotFilms(false,filmRequestVO.getPageSize(),filmRequestVO.getNowPage(),
                    filmRequestVO.getSortId(), filmRequestVO.getSourceId(),filmRequestVO.getYearId(),filmRequestVO.getCatId());
                break;
        }

        return ResponseEntity.success(filmVO.getNowPage(),filmVO.getTotalPage(),IMG,filmVO.getFilmInfo());
    }



}
