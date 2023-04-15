package com.xuecheng.content.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xuecheng.base.execption.XueChengPlusException;
import com.xuecheng.content.mapper.TeachplanMapper;
import com.xuecheng.content.mapper.TeachplanMediaMapper;
import com.xuecheng.content.model.dto.SaveTeachplanDto;
import com.xuecheng.content.model.dto.TeachplanDto;
import com.xuecheng.content.model.po.Teachplan;
import com.xuecheng.content.service.TeachplanService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @description 课程计划service接口实现类
 * @date 2023/4/15 11:42
 */
@Service
public class TeachplanServiceImpl implements TeachplanService {

    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanMediaMapper teachplanMediaMapper;

    @Override
    public List<TeachplanDto> findTeachplanTree(long courseId) {
        return teachplanMapper.selectTreeNodes(courseId);
    }

    @Override
    @Transactional
    public void saveTeachplan(SaveTeachplanDto teachplanDto) {
        // 课程计划id
        Long id = teachplanDto.getId();
        // 修改课程计划
        if (id != null) {
            Teachplan teachplan = teachplanMapper.selectById(id);
            BeanUtils.copyProperties(teachplanDto, teachplan);
            teachplanMapper.updateById(teachplan);
        } else {
            //取出同父同级别的课程计划数量
            int count = getTeachplanCount(teachplanDto.getCourseId(), teachplanDto.getParentid());
            Teachplan teachplanNew = new Teachplan();
            //设置排序号
            teachplanNew.setOrderby(count + 1);
            BeanUtils.copyProperties(teachplanDto, teachplanNew);
            teachplanMapper.insert(teachplanNew);
        }
    }

    /**
     * @param courseId 课程id
     * @param parentId 父课程计划id
     * @return int 最新排序号
     * @description 获取最新的排序号
     * @author Mr.M
     * @date 2022/9/9 13:43
     */
    private int getTeachplanCount(long courseId, long parentId) {
        LambdaQueryWrapper<Teachplan> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Teachplan::getCourseId, courseId);
        queryWrapper.eq(Teachplan::getParentid, parentId);
        Integer count = teachplanMapper.selectCount(queryWrapper);
        return count;
    }

    /**
     * 删除课程计划
     *
     * @param teachplanId 课程计划id
     */
    @Override
    @Transactional
    public void deleteTeachplan(Long teachplanId) {
        Teachplan teachplan = teachplanMapper.selectById(teachplanId);
        // 校验课程是否存在
        if (teachplan == null) {
            XueChengPlusException.cast("要删除的课程计划不存在");
        }
        if (teachplan.getGrade() == 1) {
            // 删除第一级别的章时要求章下边没有小节方可删除。
            int num = teachplanMapper.selectSonPlanById(teachplanId);
            if (num != 0) {
                //抛出异常：{"errCode":"120409","errMessage":"课程计划信息还有子级信息，无法操作"}
                XueChengPlusException.cast("课程计划信息还有子级信息，无法操作");
            } else {
                // 删除该第一级别课程计划
                int i = teachplanMapper.deleteById(teachplanId);
                // 判断删除成功与否
                if (i <= 0) {
                    XueChengPlusException.cast("删除课程计划失败");
                }
            }
        } else {
            // 删除第二级别的小节的同时需要将其它关联的视频信息也删除。
            int i = teachplanMapper.deleteById(teachplanId);
            // 删除关联的视频信息
            teachplanMediaMapper.delByTeachplanId(teachplanId);
            // 判断删除成功与否
            if (i <= 0) {
                XueChengPlusException.cast("删除课程计划失败");
            }
        }
    }

    /**
     * 课程计划排序
     *
     * @param move 上移还是下移
     * @param id   需要移动的课程的id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveTeachplan(String move, Long id) {
        Teachplan teachplan = teachplanMapper.selectById(id);
        // 1、检查课程是否存在
        if (teachplan == null) {
            XueChengPlusException.cast("课程计划不存在");
        }
        // 2、检查上移或者下移是否可行，即上面或者下面是否还有
        // 2.1 获得当前课程的排序号
        Integer orderBy = teachplan.getOrderby();
        // 2.2 根据上面或者下面的排序号获得另一个待交换的课程计划
        // courseId和parentId相同
        Long courseId = teachplan.getCourseId();
        Long parentId = teachplan.getParentid();
        int orderByAnother;
        if ("movedown".equals(move)) {
            orderByAnother = orderBy + 1;
        } else if ("moveup".equals(move)) {
            orderByAnother = orderBy - 1;
        } else {
            orderByAnother = 0;
            XueChengPlusException.cast("非法参数");
        }
        // 2.3 判断是否存在待交换的课程计划
        LambdaQueryWrapper<Teachplan> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Teachplan::getCourseId, courseId)
                .eq(Teachplan::getParentid, parentId)
                .eq(Teachplan::getOrderby, orderByAnother);
        List<Teachplan> list = teachplanMapper.selectList(lqw);
        // 不存在待交换的课程计划或者存在多个
        if (list.size() == 0) {
            XueChengPlusException.cast("已经无法再移动了");
        } else if (list.size() > 1) {
            XueChengPlusException.cast("出错了");
        }
        // 待交换的课程计划
        Teachplan teachplan2 = list.get(0);
        // 3、执行上移或者下移的逻辑
        // 设置新的orderBy并且更新数据库
        teachplan.setOrderby(orderByAnother);
        teachplan2.setOrderby(orderBy);
        teachplanMapper.updateById(teachplan);
        teachplanMapper.updateById(teachplan2);
    }
}
