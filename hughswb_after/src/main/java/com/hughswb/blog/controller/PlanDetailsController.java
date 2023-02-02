package com.hughswb.blog.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.hughswb.blog.entity.PlanDetails;
import com.hughswb.blog.service.PlanDetailsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;


/**
 * 计划明细表
 *
 * @author 变成派大星
 * @date 2023-02-01 21:13:25
 */
@RestController
@AllArgsConstructor
@RequestMapping("/plan-details" )
@Api(value = "plan-details", tags = "计划明细表管理")
public class PlanDetailsController {

    private final  PlanDetailsService planDetailsService;

    /**
     * 分页查询
     * @param page 分页对象
     * @param planDetails 计划明细表
     * @return
     */
    @ApiOperation(value = "分页查询", notes = "分页查询")
    @GetMapping("/page" )
    public R getPlanDetailsPage(Page<PlanDetails> page, PlanDetails planDetails) {
        return R.ok(planDetailsService.page(page, Wrappers.query(planDetails)));
    }


    /**
     * 通过id查询计划明细表
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id查询", notes = "通过id查询")
    @GetMapping("/{id}" )
    public R<PlanDetails> getById(@PathVariable("id" ) Integer id) {
        return R.ok(planDetailsService.getById(id));
    }

    /**
     * 新增计划明细表
     * @param planDetails 计划明细表
     * @return R
     */
    @ApiOperation(value = "新增计划明细表", notes = "新增计划明细表")
    @PostMapping
    public R<Boolean> save(@RequestBody PlanDetails planDetails) {
        return R.ok(planDetailsService.save(planDetails));
    }

    /**
     * 修改计划明细表
     * @param planDetails 计划明细表
     * @return R
     */
    @ApiOperation(value = "修改计划明细表", notes = "修改计划明细表")
    @PutMapping
    public R<Boolean> updateById(@RequestBody PlanDetails planDetails) {
        return R.ok(planDetailsService.updateById(planDetails));
    }

    /**
     * 通过id删除计划明细表
     * @param id id
     * @return R
     */
    @ApiOperation(value = "通过id删除计划明细表", notes = "通过id删除计划明细表")
    @DeleteMapping("/{id}" )
    public R<Boolean> removeById(@PathVariable Integer id) {
        return R.ok(planDetailsService.removeById(id));
    }

}
