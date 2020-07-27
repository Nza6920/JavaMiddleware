package com.niu.middleware.fight.one.server.service.praise;

import cn.hutool.core.date.DateTime;
import com.niu.middleware.fight.one.model.dto.PraiseDto;
import com.niu.middleware.fight.one.model.entity.Article;
import com.niu.middleware.fight.one.model.entity.ArticlePraise;
import com.niu.middleware.fight.one.model.mapper.ArticleMapper;
import com.niu.middleware.fight.one.model.mapper.ArticlePraiseMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: 点赞业务类
 * @Author nza
 * @Date 2020/7/26
 **/
@Slf4j
@Service
public class PraiseService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticlePraiseMapper articlePraiseMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    // 获取文章列表
    public List<Article> getAll() throws Exception {

        return articleMapper.selectAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean praiseOn(PraiseDto dto) {
        final String recordKey = Constant.RedisArticleUserPraiseKey + dto.getUserId() + dto.getArticleId();
        // 判断是否已点赞, 基于redis的缓存判断(原子操作)
        // 当开启事务支持或管道通信后会返回null
        Boolean canPraise = redisTemplate.opsForValue().setIfAbsent(recordKey, 1);
        if (canPraise) {
            // 将点赞的数据插入数据库
            ArticlePraise entity = new ArticlePraise(dto.getArticleId(), dto.getUserId(), DateTime.now());
            int res = articlePraiseMapper.insertSelective(entity);
            if (res > 0) {
                // 叠加文章的点赞总数
                articleMapper.updatePraiseTotal(dto.getArticleId(), 1);

                // 缓存点赞相关数据
            }
        }

        return true;
    }
}
