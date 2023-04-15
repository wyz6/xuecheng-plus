package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;

import java.util.List;

/**
 * 课程计划 Mapper 接口
 *
 * @author itcast
 */
public interface TeachplanMapper extends BaseMapper<Teachplan> {

    /**
     * 根据课程id查询课程计划树形结构
     *
     * @param courseId
     * @return com.xuecheng.content.model.dto.TeachplanDto
     * @date 2023/4/15 11:29
     * @author wyz
     */
    List<TeachplanDto> selectTreeNodes(long courseId);
}
