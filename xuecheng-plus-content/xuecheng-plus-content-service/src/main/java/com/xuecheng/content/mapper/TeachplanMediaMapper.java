package com.xuecheng.content.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xuecheng.content.model.po.TeachplanMedia;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author itcast
 */
public interface TeachplanMediaMapper extends BaseMapper<TeachplanMedia> {
    /**
     * 根据课程计划id删除课程视频
     *
     * @return 删除成功与否
     */
    int delByTeachplanId(Long teachplanId);
}
