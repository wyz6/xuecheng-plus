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

    /**
     * 查询某个父课程计划的子计划的个数
     *
     * @param id 课程计划id
     * @return 子计划的个数
     */
    int selectSonPlanById(Long id);


    /**
     * 修改课程计划的排序值
     *
     * @param id      课程计划id
     * @param orderBy 排序值
     */
    void updateOrderBy(Long id, Integer orderBy);
}
