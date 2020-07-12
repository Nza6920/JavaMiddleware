package com.niu.middleware.fight.one.server.enums;

/**
 * 系统常量配置
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2020/3/12 21:10
 **/
public class Constant {

    public static final String RedisStringPrefix="SpringBootRedis:String:V1:";

    public static final String RedisListPrefix="SpringBootRedis:List:User:V1:";

    public static final String RedisListNoticeKey="SpringBootRedis:List:Queue:Notice:V1";

    public static final String RedisSetKey="SpringBoot:Redis:Set:User:Email";

    public static final String RedisSetProblemKey="SpringBoot:Redis:Set:Problem";

    public static final String RedisSetProblemsKey="SpringBoot:Redis:Set:Problems:V1";

    public static final String RedisSortedSetKey1 ="SpringBootRedis:SortedSet:PhoneFare:key1";

    public static final String RedisSortedSetKey2 ="SpringBootRedis:SortedSet:PhoneFare:key:V1";

    public static final String RedisHashKeyConfig ="SpringBootRedis:Hash:Key:SysConfig:V1";

    public static final String RedisExpireKey ="SpringBootRedis:Key:Expire";

    public static final String RedisRepeatKey ="SpringBootRedis:Key:Expire:Repeat:";


    public static final String RedisCacheBeatLockKey="SpringBootRedis:LockKey:";


    public static final String RedisArticlePraiseUser ="SpringBootRedis:Article:Praise:User:V6:";

    public static final String RedisArticlePraiseHashKey ="SpringBootRedis:Hash:Article:Praise:V6";

    public static final String RedisArticlePraiseSortKey ="SpringBootRedis:Hash:Article:Sort:V6";

    public static final String RedisArticleUserPraiseKey ="SpringBootRedis:Hash:Article:User:Praise:V6";


    public static final String RedisTopicNameEmail="SpringBootRedisTopicEmailInfo";

    public static final String logOperateUser="debug";

    public static final String RedisMsgCodeKey="SpringBootRedis:MsgCode:";

    public static final String RedissonMsgCodeKey="SpringBootRedisson:MsgCode";

    public static final String RedissonUserVIPKey="SpringBootRedisson:UserVIP:V2";

    public static final String RedissonUserVipQueue="SpringBootRedisson:UserVip:Queue";

    public static final String SplitCharUserVip="-";

    //vip会员过期前 x 天/小时/分钟/秒 进行提醒...; 即 ttl=vipDay - x;
    public static final Integer x=10;


    //用户会员到期前的多次提醒的标识
    public enum VipExpireFlg{

        First(1),
        End(2),

        ;

        private Integer type;

        VipExpireFlg(Integer type) {
            this.type = type;
        }

        public Integer getType() {
            return type;
        }

        public void setType(Integer type) {
            this.type = type;
        }
    }

}























