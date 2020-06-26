package com.niu.middleware.fight.one.server.service.log;

import com.niu.middleware.fight.one.model.entity.SysLog;
import com.niu.middleware.fight.one.model.mapper.SysLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: TODO
 * @Author nza
 * @Date 2020/6/22
 **/
@Service
public class LogService {

    @Autowired
    private SysLogMapper logMapper;

    // 记录日志
    public void recordLog(SysLog log) {
        logMapper.insert(log);
    }
}
