package com.hughswb.blog.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.hughswb.blog.dao.ArticleDao;
import com.hughswb.blog.dto.CategoryBackDTO;
import com.hughswb.blog.dto.CategoryDTO;
import com.hughswb.blog.dto.CategoryOptionDTO;
import com.hughswb.blog.service.ArticleService;
import com.hughswb.blog.util.BeanCopyUtils;
import com.hughswb.blog.util.PageUtils;
import com.hughswb.blog.vo.ConditionVO;
import com.hughswb.blog.vo.PageResult;
import com.hughswb.blog.entity.Article;
import com.hughswb.blog.entity.Category;
import com.hughswb.blog.dao.CategoryDao;
import com.hughswb.blog.exception.BizException;
import com.hughswb.blog.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hughswb.blog.vo.CategoryVO;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * 分类服务
 *
 * @author xiaojie
 * @date 2021/07/29
 */
@Service
@AllArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    private CategoryDao categoryDao;

    private ArticleDao articleDao;

    private ArticleService articleService;

    @Override
    public PageResult<CategoryDTO> listCategories() {
        return new PageResult<>(categoryDao.listCategoryDTO(), categoryDao.selectCount(null));
    }

    @Override
    public PageResult<CategoryBackDTO> listBackCategories(ConditionVO condition) {
        // 查询分类数量
        Integer count = categoryDao.selectCount(new LambdaQueryWrapper<Category>()
                .like(StringUtils.isNotBlank(condition.getKeywords()), Category::getCategoryName, condition.getKeywords()));
        if (count == 0) {
            return new PageResult<>();
        }
        // 分页查询分类列表
        List<CategoryBackDTO> categoryList = categoryDao.listCategoryBackDTO(PageUtils.getLimitCurrent(), PageUtils.getSize(), condition);
        return new PageResult<>(categoryList, count);
    }

    @Override
    public List<CategoryOptionDTO> listCategoriesBySearch(ConditionVO condition) {
        // 搜索分类
        List<Category> categoryList = categoryDao.selectList(new LambdaQueryWrapper<Category>()
                .like(StringUtils.isNotBlank(condition.getKeywords()), Category::getCategoryName, condition.getKeywords())
                .orderByDesc(Category::getId));
        return BeanCopyUtils.copyList(categoryList, CategoryOptionDTO.class);
    }

    @Override
    public void deleteCategory(List<Integer> categoryIdList) {
        // 查询分类id下是否有文章
        Integer count = articleDao.selectCount(new LambdaQueryWrapper<Article>()
                .in(Article::getCategoryId, categoryIdList));
        if (count > 0) {
            throw new BizException("删除失败，该分类下存在文章");
        }
        categoryDao.deleteBatchIds(categoryIdList);
    }

    @Override
    public void saveOrUpdateCategory(CategoryVO categoryVO) {
        // 判断分类名重复
        Category existCategory = categoryDao.selectOne(new LambdaQueryWrapper<Category>()
                .select(Category::getId)
                .eq(Category::getCategoryName, categoryVO.getCategoryName()));
        if (Objects.nonNull(existCategory) && !existCategory.getId().equals(categoryVO.getId())) {
            throw new BizException("分类名已存在");
        }
        Category category = Category.builder()
                .id(categoryVO.getId())
                .categoryName(categoryVO.getCategoryName())
                .encryptionOrNot(categoryVO.getEncryptionOrNot())
                .build();
        this.saveOrUpdate(category);
    }

    /**
     * @return 返回所有分类数据
     */
    @Override
    public List<CategoryDTO> allClassifications() {
        List<Category> categories = categoryDao.selectList(new LambdaQueryWrapper<>());
        List<CategoryDTO> categoryDTOS = new ArrayList<>(categories.size());
        Map<Integer, List<Article>> integerListMap = articleService.classifiedStatistic();
        categories.forEach(ca->{
            CategoryDTO categoryDTO = new CategoryDTO();
            BeanUtils.copyProperties(ca,categoryDTO);
            categoryDTO.setArticleCount(CollUtil.isNotEmpty(integerListMap.get(ca.getId())) ? integerListMap.get(ca.getId()).size() : 0);
            categoryDTOS.add(categoryDTO);
        });
        return categoryDTOS;
    }

}
