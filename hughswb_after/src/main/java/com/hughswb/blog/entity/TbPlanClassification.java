package com.hughswb.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 计划分类表
 *
 * @author 变成派大星
 * @date 2023-01-31 17:42:26
 */
@Data
@TableName("tb_plan_classification")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "计划分类表")
public class TbPlanClassification extends Model<TbPlanClassification> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    @ApiModelProperty(value="id")
    private Integer id;

    /**
     * 计划分类
     */
    @ApiModelProperty(value="计划分类")
    private String categoryName;

    /**
     * 计划描述
     */
    @ApiModelProperty(value="计划描述")
    private String description;

    /**
     * 创建时间
     */
    @ApiModelProperty(value="创建时间")
    private LocalDateTime dateCreated;

    /**
     * 图标
     */
    @ApiModelProperty(value="图标")
    private String icon;

    /**
     * 排序
     */
    @ApiModelProperty(value="排序")
    private Integer sort;
}
