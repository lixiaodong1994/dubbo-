package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.film.FilmAPI;
import com.stylefeng.guns.film.vo.BannerVO;
import com.stylefeng.guns.rest.modular.VO.ResponseEntity;
import com.stylefeng.guns.rest.modular.film.vo.FilmIndexVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Reference(interfaceClass = FilmAPI.class,check = false)
    FilmAPI filmAPI;


    @GetMapping("/getIndex")
    public ResponseEntity getIndex() {
        FilmIndexVO filmIndexVO = new FilmIndexVO();
        filmIndexVO.setBanners(filmAPI.getBanners());
        filmIndexVO.setHotFilms(filmAPI.getHotFilms(true,8));
        filmIndexVO.setSoonFilms(filmAPI.getSoonFilms(true,8));
        filmIndexVO.setBoxRanking(filmAPI.getBoxRanking());
        filmIndexVO.setExpectRanking(filmAPI.getExpectRanking());
        filmIndexVO.setTop100(filmAPI.getTop());
        return ResponseEntity.success(IMG,filmIndexVO);
    }

}
