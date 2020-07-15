package com.niu.middleware.fight.one.server.utils;/**
 * Created by Administrator on 2020/3/14.
 */

import org.apache.commons.lang3.RandomStringUtils;

/**
 * 随机数工具
 * @Author:debug (SteadyJack)
 * @Link: weixin-> debug0868 qq-> 1948831260
 * @Date: 2020/3/14 17:49
 **/
public class RandomUtil {

    //短信验证码
    public static String randomMsgCode(final Integer num){
        /*ThreadLocalRandom random=ThreadLocalRandom.current();
        int num=random.nextInt(1000,9999);
        return String.valueOf(num);*/

        return RandomStringUtils.randomNumeric(num);
    }


//    private static final Integer total=100;
//
//    //多线程压测
//    static class CodePlayer extends Thread {
//        @Override
//        public void run() {
//            System.out.println(getName() + ": " + randomMsgCode(4));
//        }
//    }
//
//    public static void main(String[] args) {
//        for (int i=1;i<=total;i++){
//            new CodePlayer().start();
//        }
//    }
}












