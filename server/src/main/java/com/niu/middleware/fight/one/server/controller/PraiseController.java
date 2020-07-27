package com.niu.middleware.fight.one.server.controller;

import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import com.niu.middleware.fight.one.server.service.praise.PraiseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 点赞控制器
 * @Author nza
 * @Date 2020/7/26
 **/
@RestController
@RequestMapping("praise/article")
public class PraiseController extends AbstractController {

    @Autowired
    private PraiseService praiseService;

    // 获取文章列表
    @GetMapping("list")
    public BaseResponse articleList() {
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(praiseService.getAll());
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }

}
