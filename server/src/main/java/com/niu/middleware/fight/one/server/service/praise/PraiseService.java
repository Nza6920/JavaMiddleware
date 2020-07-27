package com.niu.middleware.fight.one.server.service.praise;

import com.niu.middleware.fight.one.model.entity.Article;
import com.niu.middleware.fight.one.model.mapper.ArticleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    // 获取文章列表
    public List<Article> getAll() throws Exception {

        return articleMapper.selectAll();
    }

}
