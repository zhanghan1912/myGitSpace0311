package com.stylefeng.guns.modular.system.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.modular.system.form.DdEvaluationForm;
import com.stylefeng.guns.modular.system.model.PlanVisitor;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 计划活动参与督导表 服务类
 * </p>
 * @since 2018-07-18
 */
public interface IPlanVisitorService extends IService<PlanVisitor> {
    /**
     * 获取活动参与督导列表
     */
    List<PlanVisitor> queryPlanVisitorList(Map<String, Object> param);
    /**
     * 将form对象封装为xml文本
     */
    String getXmlFromExtInfo(DdEvaluationForm form);
    /**
     * 将xml文本封装form
     */
    DdEvaluationForm parseExtInfoXml(String extInfoXml);
    /**
     * 获取督导评价列表
     */
    List<Map<String,Object>> getVisitorPJList(Page<Map<String,Object>> page, Map<String, Object> param);
    /**
     * 统计图表
     */
    List<Map<String,String>> getPlanCount();
    List<Map<String,String>> getPlanCountByDate();
}
