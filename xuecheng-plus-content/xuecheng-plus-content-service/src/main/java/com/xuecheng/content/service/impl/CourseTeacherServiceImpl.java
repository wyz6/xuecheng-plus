package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.content.mapper.CourseTeacherMapper;
import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {

    @Resource
    private CourseTeacherMapper courseTeacherMapper;

    /**
     * 查询课程的师资信息
     *
     * @param courseId 课程id
     * @return List<CourseTeacher>
     */
    @Override
    public List<CourseTeacher> listCourseTeacher(Long courseId) {
        // 构建查询条件
        LambdaQueryWrapper<CourseTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CourseTeacher::getCourseId, courseId);
        // 查询
        return courseTeacherMapper.selectList(lqw);
    }

    /**
     * 添加老师
     *
     * @param courseTeacher 需要添加的老师信息
     * @return CourseTeacher
     */
    @Override
    public CourseTeacher addOrUpdateCourseTeacher(CourseTeacher courseTeacher) {
        // 新增
        if (courseTeacher.getId() == null) {
            courseTeacher.setCreateDate(LocalDateTime.now());
            int insert = courseTeacherMapper.insert(courseTeacher);
            if (insert <= 0) {
                XueChengPlusException.cast("新增失败");
            }
        } else {
            // 修改
            int update = courseTeacherMapper.updateById(courseTeacher);
            if (update <= 0) {
                XueChengPlusException.cast("修改失败");
            }
        }
        Long id = courseTeacher.getId();
        return courseTeacherMapper.selectById(id);

    }

    /**
     * 删除老师信息
     *
     * @param courseId  课程id
     * @param teacherId 老师id
     */
    @Override
    public void deleteCourseTeacher(Long courseId, Long teacherId) {
        LambdaQueryWrapper<CourseTeacher> lqw = new LambdaQueryWrapper<>();
        lqw.eq(CourseTeacher::getCourseId, courseId).eq(CourseTeacher::getId, teacherId);
        int delete = courseTeacherMapper.delete(lqw);
        if (delete <= 0) {
            XueChengPlusException.cast("删除失败");
        }
    }
}