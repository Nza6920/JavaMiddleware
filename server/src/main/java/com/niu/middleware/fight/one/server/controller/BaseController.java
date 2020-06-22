package com.niu.middleware.fight.one.server.controller;/**
 * Created by Administrator on 2020/3/16.
 */

import com.niu.middleware.fight.one.api.response.BaseResponse;
import com.niu.middleware.fight.one.api.response.StatusCode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2020/3/16 18:40
 **/
@RestController
@RequestMapping("base")
public class BaseController {

    private static final Logger log = LoggerFactory.getLogger(BaseController.class);

    @RequestMapping("info")
    public BaseResponse info(String name){
        BaseResponse response=new BaseResponse(StatusCode.Success);
        try {
            //TODO:写真正的核心业务逻辑
            if (StringUtils.isBlank(name)){
                name="Java分布式中间件大汇聚实战一";
            }
            response.setData(name);

        }catch (Exception e){
            response=new BaseResponse(StatusCode.Fail.getCode(),e.getMessage());
        }
        return response;
    }

}






















