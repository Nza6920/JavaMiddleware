package com.niu.middleware.fight.one.server.controller;

import cn.hutool.core.util.StrUtil;
import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import com.niu.middleware.fight.one.model.dto.PraiseDto;
import com.niu.middleware.fight.one.server.service.praise.PraiseService;
import com.niu.middleware.fight.one.server.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    // 点赞文章
    @PostMapping(value = "on")
    public BaseResponse praiseOn(@RequestBody @Validated PraiseDto dto, BindingResult result) {
        String checkRes = ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), checkRes);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(praiseService.praiseOn(dto));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }

    // 取消点赞文章
    @PostMapping(value = "cancel")
    public BaseResponse praiseCancel(@RequestBody @Validated PraiseDto dto, BindingResult result) {
        String checkRes = ValidatorUtil.checkResult(result);
        if (StrUtil.isNotBlank(checkRes)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), checkRes);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(praiseService.praiseCancel(dto));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }

    // 获取文章详情
    @GetMapping("info")
    public BaseResponse articleInfo(@RequestParam Integer articleId, @RequestParam Integer currUserId) {
        if (articleId == null || articleId <= 0) {
            return new BaseResponse(StatusCode.Success.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(praiseService.getArticleInfo(articleId, currUserId));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }

    // 获取用户点赞过的历史文章-用户详情
    @GetMapping("user/articles")
    public BaseResponse userArticles(@RequestParam Integer currUserId) {
        if (currUserId == null || currUserId <= 0) {
            return new BaseResponse(StatusCode.Success.InvalidParams);
        }
        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            response.setData(praiseService.getUserArticles(currUserId));
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }
}
