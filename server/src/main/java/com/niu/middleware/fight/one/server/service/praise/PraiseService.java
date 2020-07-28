package com.niu.middleware.fight.one.server.service.praise;

import cn.hutool.core.date.DateTime;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.niu.middleware.fight.one.model.dto.PraiseDto;
import com.niu.middleware.fight.one.model.entity.Article;
import com.niu.middleware.fight.one.model.entity.ArticlePraise;
import com.niu.middleware.fight.one.model.mapper.ArticleMapper;
import com.niu.middleware.fight.one.model.mapper.ArticlePraiseMapper;
import com.niu.middleware.fight.one.model.mapper.UserMapper;
import com.niu.middleware.fight.one.server.enums.Constant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Autowired
    private UserMapper userMapper;

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
                this.cachePraiseOn(dto);
            }
        }

        return true;
    }

    // 缓存点赞相关信息
    private void cachePraiseOn(final PraiseDto dto) {
        // 选择的数据结构是 Hash: key-字符串, 存储到redis的标志符; field-文章id; value-用户id列表
        HashOperations<String, String, Set<Integer>> praiseHash = redisTemplate.opsForHash();

        // 记录点赞过当前文章的用户列表
        Set<Integer> uIds = praiseHash.get(Constant.RedisArticlePraiseHashKey, dto.getArticleId().toString());

        if (uIds == null || uIds.isEmpty()) {
            uIds = Sets.newHashSet();
        }

        uIds.add(dto.getUserId());
        praiseHash.put(Constant.RedisArticlePraiseHashKey, dto.getArticleId().toString(), uIds);

        // 缓存点赞排行榜

        // 缓存用户的点赞过的历史文章
    }

    // 取消点赞
    @Transactional(rollbackFor = Exception.class)
    public Boolean praiseCancel(PraiseDto dto) {

        final String recordKey = Constant.RedisArticleUserPraiseKey + dto.getUserId() + dto.getArticleId();

        Boolean hasPraise = redisTemplate.hasKey(recordKey);
        if (hasPraise) {
            // 移除掉 db 中的记录
            int res = articlePraiseMapper.cancelPraise(dto.getArticleId(), dto.getUserId());
            if (res > 0) {

                // 移除缓存中用户点赞的记录
                redisTemplate.delete(recordKey);


                // 更新文章的点赞总数
                articleMapper.updatePraiseTotal(dto.getArticleId(), -1);

                // 缓存取消点赞相关信息
                this.cachePraiseCancel(dto);
            }
        }

        return true;
    }

    private void cachePraiseCancel(final PraiseDto dto) {
        // 选择的数据结构是 Hash: key-字符串, 存储到redis的标志符; field-文章id; value-用户id列表
        HashOperations<String, String, Set<Integer>> praiseHash = redisTemplate.opsForHash();

        // 记录点赞过当前文章的用户列表
        Set<Integer> uIds = praiseHash.get(Constant.RedisArticlePraiseHashKey, dto.getArticleId().toString());

        if (uIds != null && !uIds.isEmpty() && uIds.contains(dto.getUserId())) {
            uIds.remove(dto.getUserId());
            praiseHash.put(Constant.RedisArticlePraiseHashKey, dto.getArticleId().toString(), uIds);
        }
    }

    // 获取文章详情-点赞过的用户列表-排行榜
    public Map<String, Object> getArticleInfo(Integer articleId, Integer currUserId) {

        Map<String, Object> resMap = Maps.newHashMap();

        // 文章本身的信息
        Article article = articleMapper.selectByPK(articleId);
        resMap.put("articleInfo", article);

        // 获取点赞过当前文章的用户列表 ~ 需要获取用户的昵称
        HashOperations<String, String, Set<Integer>> praiseHash = redisTemplate.opsForHash();
        Set<Integer> uIds = praiseHash.get(Constant.RedisArticlePraiseHashKey, articleId.toString());
        if (uIds != null && !uIds.isEmpty()) {
            String ids = Joiner.on(",").join(uIds);
            resMap.put("userNames", userMapper.selectNamesById(ids));
        }

        return resMap;
    }
}
