package com.scnu.gpt.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * <p>
 * 
 * </p>
 *
 * @author ldw
 * @since 2025-06-03
 */
@Getter
@Setter
@TableName("t_video")
public class Video {

    /**
     * 视频
     */
    @TableId(value = "video_id", type = IdType.AUTO)
    private Integer videoId;

    /**
     * 视频名
     */
    private String videoName;

    /**
     * 视频相对路径
     */
    private String videoPath;

    /**
     * 封面相对路径
     */
    private String coverPath;

    /**
     * 创建日期
     */
    private LocalDate createdDate;

    /**
     * 所属教师Id
     */
    private Integer userId;
}
