

package com.hughswb.blog.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hughswb.blog.entity.TbPlanClassification;
import com.hughswb.blog.service.TbPlanClassificationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * 计划分类表
 *
 * @author 变成派大星
 * @date 2023-01-31 17:42:26
 */
@RestController
@AllArgsConstructor
@RequestMapping("/oversimplification" )
@Api(value = "oversimplification", tags = "计划分类表管理")
public class TbPlanClassificationController {

    private final TbPlanClassificationService tbPlanClassificationService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param tbPlanClassification 计划分类表
     * @return 返回所有的查询分类数据
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page" )
    public R getTbPlanClassificationPage(Page<TbPlanClassification> page, TbPlanClassification tbPlanClassification) {
        return R.ok(tbPlanClassificationService.page(page, Wrappers.query(tbPlanClassification)));
    }


    /**
     * 通过id查询计划分类表
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}" )
    public R<TbPlanClassification> getById(@PathVariable("id" ) Integer id) {
        return R.ok(tbPlanClassificationService.getById(id));
    }

    /**
     * 新增计划分类表
     * @param tbPlanClassification 计划分类表
     * @return R
     */
    @ApiOperation(value = "新增计划分类表", notes = "新增计划分类表")
    @PostMapping
    public R<Boolean> save(@RequestBody TbPlanClassification tbPlanClassification) {
        return R.ok(tbPlanClassificationService.save(tbPlanClassification));
    }

    /**
     * 修改计划分类表
     * @param tbPlanClassification 计划分类表
     * @return R
     */
    @ApiOperation(value = "修改计划分类表", notes = "修改计划分类表")
    @PutMapping
    public R<Boolean> updateById(@RequestBody TbPlanClassification tbPlanClassification) {
        return R.ok(tbPlanClassificationService.updateById(tbPlanClassification));
    }

    /**
     * 通过id删除计划分类表
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除计划分类表", notes = "通过id删除计划分类表")
    @DeleteMapping("/{id}" )
    public R<Boolean> removeById(@PathVariable Integer id) {
        return R.ok(tbPlanClassificationService.removeById(id));
    }
    /**
     * 查询所有的分类
     * @return R
     */
    @ApiOperation(value = "查询所有的分类", notes = "查询所有的分类")
    @GetMapping("/allCategory" )
    public R<List<TbPlanClassification>> allCategory() {
        return R.ok(tbPlanClassificationService.list(new LambdaQueryWrapper<TbPlanClassification>()
                .select(TbPlanClassification::getId,TbPlanClassification::getCategoryName,TbPlanClassification::getIcon)
                .orderByAsc(TbPlanClassification::getSort)));
    }

}
