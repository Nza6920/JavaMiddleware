package com.niu.middleware.fight.one.server.controller;

import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import com.niu.middleware.fight.one.model.entity.User;
import com.niu.middleware.fight.one.model.mapper.UserMapper;
import com.niu.middleware.fight.one.server.service.log.LogApplicationEvent;
import com.niu.middleware.fight.one.server.utils.ValidatorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    // 用户操作-新增用户
    @RequestMapping("user/add")
    public BaseResponse addUser(@RequestBody @Validated User user, BindingResult result) {

        String error = ValidatorUtil.checkResult(result);

        if (StrUtil.isNotEmpty(error)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), error);
        }

        BaseResponse response = new BaseResponse(StatusCode.Success);
        try {
            int res = userMapper.insertSelective(user);
            if (res > 0) {
                // 记录日志
                LogApplicationEvent event = new LogApplicationEvent(this, "niu", "新增用户", new Gson().toJson(user),"addUser");
                applicationEventPublisher.publishEvent(event);
            }
        } catch (Exception e) {
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }
        return response;
    }
}
