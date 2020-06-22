package com.niu.middleware.fight.one.server.controller;

import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @Author nza
 * @Date 2020/6/22
 **/
@RestController
@RequestMapping("log")
public class LogController extends AbstractController {

    // 用户操作-新增用户
    @RequestMapping("user/add")
    public BaseResponse addUser() {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            //TODO:写真正的核心业务逻辑

        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }
}
