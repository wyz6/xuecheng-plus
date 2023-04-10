package com.xuecheng.content.model.dto;

import com.xuecheng.content.model.po.CourseCategory;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wyz
 * @version 1.0
 * @data 2023/4/10:15:30
 * @deprecated 课程分类树形节点dto
 */
@Data
public class CourseCategoryTreeDto extends CourseCategory implements Serializable {
    List<CourseCategoryTreeDto> childrenTreeNodes;
}
