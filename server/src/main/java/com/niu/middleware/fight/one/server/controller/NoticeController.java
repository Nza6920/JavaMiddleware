package com.niu.middleware.fight.one.server.controller;

import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import com.niu.middleware.fight.one.model.entity.Notice;
import com.niu.middleware.fight.one.model.mapper.NoticeMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import com.niu.middleware.fight.one.server.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 发布订阅控制器
 * @Author nza
 * @Date 2020/7/13
 **/
@RestController
@RequestMapping("notice")
public class NoticeController extends AbstractController {

    @Autowired
    private NoticeMapper noticeMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @RequestMapping("add")
    public BaseResponse addNotice(@RequestBody @Validated Notice notice, BindingResult result) {
        String checkRes = ValidatorUtil.checkResult(result);
        if (StringUtils.isNotBlank(checkRes)) {
            return new BaseResponse(StatusCode.InvalidParams.getCode(), checkRes);
        }

        BaseResponse response = new BaseResponse(StatusCode.Success);

        try {

            int res = noticeMapper.insertSelective(notice);
            if (res > 0) {
                // 生产者发布消息- pub
                redisTemplate.convertAndSend(Constant.RedisTopicNameEmail, notice);
            }


        } catch (Exception e) {
            log.error("redis订阅发布机制-发生异常: ", e);
            response = new BaseResponse(StatusCode.Fail.getCode(), e.getMessage());
        }

        return response;
    }
}
