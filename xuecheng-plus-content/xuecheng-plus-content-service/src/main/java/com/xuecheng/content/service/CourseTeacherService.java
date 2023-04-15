package com.xuecheng.content.service;

import com.xuecheng.content.model.po.CourseTeacher;

import java.util.List;

/**
 * 师资管理相关服务
 */
public interface CourseTeacherService {
    /**
     * 查询课程的师资信息
     *
     * @param courseId 课程id
     * @return List<CourseTeacher>
     */
    List<CourseTeacher> listCourseTeacher(Long courseId);

    /**
     * 添加老师
     *
     * @param courseTeacher 需要添加的老师信息
     * @return CourseTeacher
     */
    CourseTeacher addOrUpdateCourseTeacher(CourseTeacher courseTeacher);

    /**
     * 删除老师信息
     *
     * @param courseId  课程id
     * @param teacherId 老师id
     */
    void deleteCourseTeacher(Long courseId, Long teacherId);
}
