package com.stylefeng.guns.modular.system.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.modular.system.model.PlanVisitor;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 计划活动参与督导表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2018-07-18
 */
public interface PlanVisitorMapper extends BaseMapper<PlanVisitor> {
    /**
     * 获取计划活动数据列表
     */
    List<PlanVisitor> queryPlanVisitorList(Map<String, Object> param);/**
     * 获取督导评价列表
     */
    List<Map<String,Object>> getVisitorPJList(Page<Map<String,Object>> page, Map<String, Object> param);
    List<Map<String,Object>> getVisitorPJList(Map<String, Object> param);
    //统计图表
    List<Map<String,String>> getPlanCount();
    //统计图表(近七天)
    List<Map<String,String>> getPlanCountByDate();
}
