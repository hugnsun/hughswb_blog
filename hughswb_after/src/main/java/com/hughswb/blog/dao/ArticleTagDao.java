package com.hughswb.blog.dao;

import com.hughswb.blog.entity.ArticleTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;


/**
 * 文章标签
 *
 * @author swb
 * @date 2021/08/10
 */
@Repository
public interface ArticleTagDao extends BaseMapper<ArticleTag> {

}
