package com.minzheng.blog.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleTimelineVo {

    /**
     * 文章Id
     */
    private Integer id;
    /**
     * 内容
     */
    private String content;


    /**
     * 时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;


    /**
     * 尺寸
     */
    private String size;


    /**
     * 颜色
     */
    private String color;

    /**
     * 作者
     */
    private String auth;
}
