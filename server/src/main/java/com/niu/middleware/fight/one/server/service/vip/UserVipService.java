package com.niu.middleware.fight.one.server.service.vip;

import com.niu.middleware.fight.one.model.entity.UserVip;
import com.niu.middleware.fight.one.model.mapper.UserVipMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RDelayedQueue;
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

    // 基于 redisson mapCache
    @Transactional(rollbackFor = Exception.class)
    public void addVip1(UserVip userVip) {

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

    // 基于 Redisson 的延时队列
    public void addVip2(UserVip userVip) {

        userVip.setVipTime(DateTime.now().toDate());
        int res = userVipMapper.insertSelective(userVip);
        // 充值成功之后
        if (res > 0) {
            // 获取 Blocking Queue
            RBlockingQueue<String> blockingQueue = redissonClient.getBlockingQueue(Constant.RedissonUserVipQueue);

            // 获取延时队列
            RDelayedQueue<String> delayedQueue = redissonClient.getDelayedQueue(blockingQueue);

            // 第一次提醒
            String firstQueue = userVip.getId() + Constant.SplitCharUserVip + Constant.VipExpireFlg.First.getType();
            long firstTTL = Long.parseLong(String.valueOf(userVip.getVipDay() - Constant.x2));
            if (firstTTL > 0) {
                delayedQueue.offer(firstQueue, firstTTL, TimeUnit.SECONDS);
            }

            // 第二次提醒
            String secondQueue = userVip.getId() + Constant.SplitCharUserVip + Constant.VipExpireFlg.End.getType();
            long secondTTL = Long.valueOf(userVip.getVipDay());
            if (firstTTL > 0) {
                delayedQueue.offer(secondQueue, secondTTL, TimeUnit.SECONDS);
            }
        }

    }
}
