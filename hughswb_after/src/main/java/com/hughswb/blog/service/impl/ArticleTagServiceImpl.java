package com.hughswb.blog.service.impl;

import com.hughswb.blog.entity.ArticleTag;
import com.hughswb.blog.dao.ArticleTagDao;
import com.hughswb.blog.service.ArticleTagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * 文章标签服务
 *
 * @author swb
 * @date 2021/08/10
 */
@Service
public class ArticleTagServiceImpl extends ServiceImpl<ArticleTagDao, ArticleTag> implements ArticleTagService {

}
