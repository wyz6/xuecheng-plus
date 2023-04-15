package com.xuecheng.content.api;

import com.xuecheng.content.model.po.CourseTeacher;
import com.xuecheng.content.service.CourseTeacherService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "师资管理相关接口")
@RestController
public class CourseTeacherController {

    private final CourseTeacherService courseTeacherService;

    @Autowired
    public CourseTeacherController(CourseTeacherService courseTeacherService) {
        this.courseTeacherService = courseTeacherService;
    }


    @ApiOperation(value = "查询老师")
    @GetMapping("/courseTeacher/list/{courseId}")
    public List<CourseTeacher> listCourseTeacher(@PathVariable Long courseId) {
        Long companyId = 1232141425L;
        return courseTeacherService.listCourseTeacher(courseId);
    }

    @ApiOperation(value = "新增或修改老师信息")
    @PostMapping("/courseTeacher")
    public CourseTeacher addOrUpdateCourseTeacher(@RequestBody CourseTeacher courseTeacher) {
        return courseTeacherService.addOrUpdateCourseTeacher(courseTeacher);
    }


    @ApiOperation(value = "新增或修改老师信息")
    @DeleteMapping("/courseTeacher/course/{courseId}/{teacherId}")
    public void deleteCourseTeacher(@PathVariable("courseId") Long courseId, @PathVariable("teacherId") Long teacherId) {
        Long companyId = 1232141425L;
        courseTeacherService.deleteCourseTeacher(courseId, teacherId);
    }
}