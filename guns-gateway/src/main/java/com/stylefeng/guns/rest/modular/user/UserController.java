package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.common.CurrentUser;
import com.stylefeng.guns.rest.modular.VO.ResponseEntity;
import com.stylefeng.guns.user.UserService;
import com.stylefeng.guns.user.vo.UserInfoModel;
import com.stylefeng.guns.user.vo.UserModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName UserController
 * @Description
 * @Author admin
 * @Date 2018/12/26 9:39
 **/
@RestController
@RequestMapping("/user")
public class UserController {
    //check=false，只是保证项目启动不会报错，但是服务不能使用，因为没有服务提供者
    @Reference(interfaceClass = UserService.class,check = false)
    UserService userService;

    /**
     * 注册
     * @param userModel
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity register(UserModel userModel) {
        if (userModel.getUsername() == null || userModel.getUsername().trim().length() == 0) {
            return ResponseEntity.serviceFail("用户名不能为空");
        }
        if (userModel.getPassword() == null || userModel.getPassword().trim().length() == 0) {
            return ResponseEntity.serviceFail("密码不能为空");
        }

        boolean isSuccess = userService.register(userModel);
        if (isSuccess) {
            return ResponseEntity.success("注册成功");
        }else {
            return ResponseEntity.serviceFail("注册失败");
        }
    }

    /**
     * 检查用户名是否存在
     * @param username
     * @return
     */
    @PostMapping("/check")
    public ResponseEntity checkUsername(String username) {
        if (username != null && username.trim().length() > 0) {
            boolean noExist = userService.checkUsername(username);
            if (noExist) {
                //false:已经存在 true：不存在
                return ResponseEntity.success("用户名不存在");
            }else {
                return ResponseEntity.serviceFail("用户名已经存在");
            }
        }else {
            return ResponseEntity.serviceFail("用户名不能为空");
        }
    }

    /**
     * 退出
     * @return
     */
    @GetMapping("/logout")
    public ResponseEntity logout() {
         /*
            应用：
                1、前端存储JWT 【七天】 ： JWT的刷新
                2、服务器端会存储活动用户信息【30分钟】
                3、JWT里的userId为key，查找活跃用户
            退出：
                1、前端删除掉JWT
                2、后端服务器删除活跃用户缓存
            现状：
                1、前端删除掉JWT
         */

        return ResponseEntity.success("退出成功");
    }

    /**
     * 获取单个用户信息
     * @return
     */
    @GetMapping("/getUserInfo")
    public ResponseEntity getUserInfo() {
        //获取用户id
        String uuid = CurrentUser.getUserInfo();
        //首先判断用户是否登陆
        if (uuid != null && uuid.trim().length() > 0) {
            int userId = Integer.parseInt(uuid);
            //根据用户id 到数据库中查找对应的用户信息
            UserInfoModel userInfo = userService.getUserInfo(userId);
            if (userInfo != null) {
                //有用户信息
                return ResponseEntity.success(userInfo);
            }else {
                //没有用户信息
                return ResponseEntity.serviceFail("用户查询信息失败");
            }
        }else {
            //用户未登陆
            return ResponseEntity.serviceFail("用户未登陆");
        }
    }


    /**
     * 修改自己的用户信息
     * @return
     */
    @PostMapping("/updateUserInfo")
    public ResponseEntity updateUserInfo(UserInfoModel userInfoModel) {
        //获取用户id
        String uuid = CurrentUser.getUserInfo();
        //首先判断用户是否登陆
        if (uuid != null && uuid.trim().length() > 0) {
            int userId = Integer.parseInt(uuid);
            //只能修改自己的用户信息
            if (userId != userInfoModel.getUuid()) {
                return ResponseEntity.serviceFail("请修改您自己的信息");
            }
            //根据用户id 到数据库中查找对应的用户信息
            UserInfoModel userInfo= userService.updateUserInfo(userInfoModel);
            if (userInfo != null) {
                //有用户信息
                return ResponseEntity.success(userInfo);
            }else {
                //没有用户信息
                return ResponseEntity.serviceFail("用户信息更新失败");
            }
        }else {
            //用户未登陆
            return ResponseEntity.serviceFail("用户未登陆");
        }
    }

}
