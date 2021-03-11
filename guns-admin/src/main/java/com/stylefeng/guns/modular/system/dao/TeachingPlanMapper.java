package com.stylefeng.guns.modular.system.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.modular.system.model.TeachingPlan;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 计划活动表 Mapper 接口
 * </p>
 *
 * @author suncg
 * @since 2018-07-11
 */
public interface TeachingPlanMapper extends BaseMapper<TeachingPlan> {

    /**
     * 获取计划活动数据列表
     */
    List<TeachingPlan> getTeachignPlanList(Page<TeachingPlan> page, Map<String, Object> param);
    /**
     * 获取计划督导数据列表
     */
    List<Map<String,Object>> getVisitorTPList(Page<Map<String, Object>> page, Map<String, Object> param);
    List<Map<String,Object>> getVisitorTPList(Map<String, Object> param);

}
