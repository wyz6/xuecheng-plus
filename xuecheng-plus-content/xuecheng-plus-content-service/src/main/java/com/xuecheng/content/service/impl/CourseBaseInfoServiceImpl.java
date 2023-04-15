package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.base.model.PageParams;
import com.xuecheng.base.model.PageResult;
import com.xuecheng.content.mapper.*;
import com.xuecheng.content.model.dto.AddCourseDto;
import com.xuecheng.content.model.dto.CourseBaseInfoDto;
import com.xuecheng.content.model.dto.EditCourseDto;
import com.xuecheng.content.model.dto.QueryCourseParamsDto;
import com.xuecheng.content.model.po.*;
import com.xuecheng.content.service.CourseBaseInfoService;
import com.xuecheng.content.service.CourseTeacherService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @description: 课程基本信息管理业务接口实现类
 * @date 2023/4/10 11:11
 */
@Service
@Slf4j
public class CourseBaseInfoServiceImpl implements CourseBaseInfoService {
    @Resource
    private CourseBaseMapper courseBaseMapper;

    @Resource
    private CourseMarketMapper courseMarketMapper;

    @Resource
    private CourseCategoryMapper courseCategoryMapper;

    @Resource
    private TeachplanMapper teachplanMapper;

    @Autowired
    CourseTeacherService courseTeacherService;

    @Resource
    private TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public PageResult<CourseBase> queryCourseBaseList(PageParams pageParams, QueryCourseParamsDto queryCourseParamsDto) {

        //构建查询条件对象
        LambdaQueryWrapper<CourseBase> queryWrapper = new LambdaQueryWrapper<>();
        //构建查询条件，根据课程名称查询
        queryWrapper.like(StringUtils.isNotEmpty(queryCourseParamsDto.getCourseName()), CourseBase::getName, queryCourseParamsDto.getCourseName());
        //构建查询条件，根据课程审核状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getAuditStatus()), CourseBase::getAuditStatus, queryCourseParamsDto.getAuditStatus());
        //构建查询条件，根据课程发布状态查询
        //todo:根据课程发布状态查询
        queryWrapper.eq(StringUtils.isNotEmpty(queryCourseParamsDto.getPublishStatus()), CourseBase::getStatus, queryCourseParamsDto.getPublishStatus());

        //分页对象
        Page<CourseBase> page = new Page<>(pageParams.getPageNo(), pageParams.getPageSize());
        // 查询数据内容获得结果
        Page<CourseBase> pageResult = courseBaseMapper.selectPage(page, queryWrapper);
        // 获取数据列表
        List<CourseBase> list = pageResult.getRecords();
        // 获取数据总数
        long total = pageResult.getTotal();
        // 构建结果集
        return new PageResult<>(list, total, pageParams.getPageNo(), pageParams.getPageSize());

    }

    @Transactional
    @Override
    public CourseBaseInfoDto createCourseBase(Long companyId, AddCourseDto dto) {

        //合法性校验
        if (StringUtils.isBlank(dto.getName())) {
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getMt())) {
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getSt())) {
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getGrade())) {
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getTeachmode())) {
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getUsers())) {
            XueChengPlusException.cast("课程名称为空");
        }

        if (StringUtils.isBlank(dto.getCharge())) {
            XueChengPlusException.cast("课程名称为空");
        }

        // 向课程基本信息表course_base中添加课程基本信息
        //新增对象
        CourseBase courseBaseNew = new CourseBase();
        //将填写的课程信息赋值给新增对象,属性名一样就能拷贝
        BeanUtils.copyProperties(dto, courseBaseNew);
        //设置审核状态，默认未提交
        courseBaseNew.setAuditStatus("202002");
        //设置发布状态，默认未发布
        courseBaseNew.setStatus("203001");
        //机构id
        courseBaseNew.setCompanyId(companyId);
        //添加创建时间
        courseBaseNew.setCreateDate(LocalDateTime.now());
        //插入课程基本信息表
        int insert = courseBaseMapper.insert(courseBaseNew);
        if (insert <= 0) {
            XueChengPlusException.cast("新增课程基本信息失败");
        }

        // 向课程营销表course_market保存课程营销信息
        CourseMarket courseMarketNew = new CourseMarket();
        // 将页面输入的数据拷贝到courseMarketNew对象中
        BeanUtils.copyProperties(dto, courseMarketNew);
        // 课程id
        Long courseId = courseBaseNew.getId();
        courseMarketNew.setId(courseId);
        // 保存营销信息
        saveCourseMarket(courseMarketNew);
        // 查询课程基本信息及营销信息并返回

        return getCourseBaseInfo(courseId);
    }

    // 单独写一个方法，保存课程营销信息，逻辑：存在则更新，不存在则新增
    private int saveCourseMarket(CourseMarket courseMarketNew) {
        // 参数合法性校验
        String charge = courseMarketNew.getCharge();
        if (StringUtils.isEmpty(charge)) {
            XueChengPlusException.cast("收费规则为空");
        }
        // 如果课程收费，价格没填写，抛出异常
        if (charge.equals("201001")) {
            if (courseMarketNew.getPrice() == null || courseMarketNew.getPrice() <= 0) {
                XueChengPlusException.cast("价格不能为空且大于0");
            }
        }
        // 从数据库查询课程营销信息，存在则更新，不存在则新增
        Long id = courseMarketNew.getId();
        CourseMarket courseMarket = courseMarketMapper.selectById(id);
        if (courseMarket == null) {
            // 不存在，新增
            return courseMarketMapper.insert(courseMarketNew);
        } else {
            // 将courseMarketNew中的数据拷贝到courseMarket中
            BeanUtils.copyProperties(courseMarketNew, courseMarket);
            courseMarket.setId(courseMarketNew.getId());
            // 存在，更新
            return courseMarketMapper.updateById(courseMarket);
        }
    }

    // 根据课程id查询课程基本信息，包括基本信息和营销信息
    public CourseBaseInfoDto getCourseBaseInfo(long courseId) {
        // 查询课程基本信息
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程基本信息不存在");
        }
        // 查询课程营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        if (courseMarket == null) {
            XueChengPlusException.cast("课程营销信息不存在");
        }
        // 将课程基本信息和课程营销信息封装到CourseBaseInfoDto中
        CourseBaseInfoDto courseBaseInfoDto = new CourseBaseInfoDto();
        BeanUtils.copyProperties(courseBase, courseBaseInfoDto);
        BeanUtils.copyProperties(courseMarket, courseBaseInfoDto);
        // 通过courseCategoryMapper查询课程分类信息，将分类信息封装到CourseBaseInfoDto中
        CourseCategory courseCategoryBySt = courseCategoryMapper.selectById(courseBase.getSt());
        courseBaseInfoDto.setStName(courseCategoryBySt.getName());
        CourseCategory courseCategoryByMt = courseCategoryMapper.selectById(courseBase.getMt());
        courseBaseInfoDto.setMtName(courseCategoryByMt.getName());

        return courseBaseInfoDto;
    }

    @Transactional
    @Override
    public CourseBaseInfoDto updateCourseBase(Long companyId, EditCourseDto dto) {
        //课程id
        Long courseId = dto.getId();
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        if (courseBase == null) {
            XueChengPlusException.cast("课程不存在");
        }

        //校验本机构只能修改本机构的课程
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("本机构只能修改本机构的课程");
        }

        //封装基本信息的数据
        BeanUtils.copyProperties(dto, courseBase);
        courseBase.setChangeDate(LocalDateTime.now());

        //更新课程基本信息
        int i = courseBaseMapper.updateById(courseBase);

        //封装营销信息的数据
        CourseMarket courseMarket = new CourseMarket();
        BeanUtils.copyProperties(dto, courseMarket);
        saveCourseMarket(courseMarket);
        //查询课程信息
        return this.getCourseBaseInfo(courseId);

    }

    /**
     * 删除课程
     *
     * @param companyId 机构id
     * @param courseId  课程id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourseBaseById(Long companyId, Long courseId) {
        CourseBase courseBase = courseBaseMapper.selectById(courseId);
        // 校验课程是否存在
        if (courseBase == null) {
            XueChengPlusException.cast("要删除的课程不存在");
        }
        // 校验机构id
        if (!courseBase.getCompanyId().equals(companyId)) {
            XueChengPlusException.cast("不能删除其他机构的课程");
        }
        int delete1 = courseBaseMapper.deleteById(courseId);
        // 校验是否处于可删除状态
        if (!"202002".equals(courseBase.getAuditStatus())) {
            XueChengPlusException.cast("只能删除未提交的课程");
        }
        // 根据课程id删除营销信息
        CourseMarket courseMarket = courseMarketMapper.selectById(courseId);
        int delete2 = 2;
        if (courseMarket != null) {
            delete2 = courseMarketMapper.deleteById(courseId);
        }
        if (delete1 <= 0 || delete2 <= 0) {
            XueChengPlusException.cast("删除课程失败");
        }
        // 根据课程id删除 课程计划信息 和 媒资信息
        LambdaQueryWrapper<Teachplan> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Teachplan::getCourseId, courseBase.getId());
        List<Teachplan> teachplans = teachplanMapper.selectList(lqw);
        for (Teachplan teachplan : teachplans) {
            Long teachplanId = teachplan.getId();
            teachplanMapper.deleteById(teachplanId);
            teachplanMediaMapper.delByTeachplanId(teachplanId);
        }
        // 根据课程id删除师资信息
        List<CourseTeacher> teachers = courseTeacherService.listCourseTeacher(courseId);
        for (CourseTeacher teacher : teachers) {
            courseTeacherService.deleteCourseTeacher(courseId, teacher.getId());
        }
    }
}
