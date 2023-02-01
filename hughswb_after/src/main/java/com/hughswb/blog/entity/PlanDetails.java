package com.hughswb.blog.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

/**
 * 计划明细表
 *
 * @author 变成派大星
 * @date 2023-02-01 21:13:25
 */
@Data
@TableName("tb_plan_details")
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "计划明细表")
public class PlanDetails extends Model<PlanDetails> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    @ApiModelProperty(value="id")
    private Integer id;

    /**
     * 分类ID
     */
    @ApiModelProperty(value="分类ID")
    private Integer categoryId;

    /**
     * 计划名称
     */
    @ApiModelProperty(value="计划名称")
    private String planName;

    /**
     * 开始时间
     */
    @ApiModelProperty(value="开始时间")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDateTime startTime;

    /**
     * 规划结束时间
     */
    @ApiModelProperty(value="规划结束时间")
    @JsonFormat(pattern="yyyy-MM-dd", timezone="GMT+8")
    private LocalDateTime plannedEndTime;

    /**
     * 是否延期
     */
    @ApiModelProperty(value="是否延期")
    private Integer delayOrNot;

    /**
     * 是否上传笔记
     */
    @ApiModelProperty(value="是否上传笔记")
    private Integer whetherToUploadNotes;

    /**
     * 笔记路径
     */
    @ApiModelProperty(value="笔记路径")
    private String notePath;

    /**
     * 是否完成任务
     */
    @ApiModelProperty(value="是否完成任务")
    private Integer whetherComplete;

    /**
     * 学习路径
     */
    @ApiModelProperty(value="学习路径")
    private String learningPath;

    /**
     * 父节点
     */
    @ApiModelProperty(value="父节点")
    private Integer pid;

    /**
     * 逻辑删除
     */
    @ApiModelProperty(value="逻辑删除")
    private Integer delFlag;

    /**
     * 当前进度
     */
    @ApiModelProperty(value="当前进度")
    private String currentProgress;

    /**
     * 备注
     */
    @ApiModelProperty(value="备注")
    private String note;

    /**
     * 当前状态
     */
    @ApiModelProperty(value="当前状态")
    private Integer status;

}
