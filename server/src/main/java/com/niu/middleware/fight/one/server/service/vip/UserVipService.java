package com.niu.middleware.fight.one.server.service.vip;

import com.niu.middleware.fight.one.model.entity.UserVip;
import com.niu.middleware.fight.one.model.mapper.UserVipMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @Description: redisson - vip过期提醒
 * @Author nza
 * @Date 2020/7/19
 **/
@Service
@Slf4j
public class UserVipService {

    @Autowired
    private UserVipMapper userVipMapper;

    @Autowired
    private RedissonClient redissonClient;

    @Transactional(rollbackFor = Exception.class)
    public void addVip(UserVip userVip) {

        userVip.setVipTime(DateTime.now().toDate());
        int res = userVipMapper.insertSelective(userVip);
        // 充值成功之后
        if (res > 0) {

            // vipDay 代表失效时间 : 第一次提醒 vipDay - x 第二次提醒 vipDay
            RMapCache<String, Integer> mapCache = redissonClient.getMapCache(Constant.RedissonUserVIPKey);

            String keyFirst = userVip.getId() + Constant.SplitCharUserVip + Constant.VipExpireFlg.First.getType();

            // 第一次提醒
            long firstTTL = Long.parseLong(String.valueOf(userVip.getVipDay() - Constant.x));
            if (firstTTL > 0) {
                mapCache.put(keyFirst, userVip.getId(), firstTTL, TimeUnit.MINUTES);

                // 第二次提醒
                String keySecond = userVip.getId() + Constant.SplitCharUserVip + Constant.VipExpireFlg.End.getType();
                long secondTTL = Long.valueOf(userVip.getVipDay());
                mapCache.put(keySecond, userVip.getId(), secondTTL, TimeUnit.MINUTES);
            }
        }
    }
}
