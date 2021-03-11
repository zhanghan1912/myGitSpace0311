package com.stylefeng.guns.modular.system.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.modular.system.model.TeachingPlan;
import com.baomidou.mybatisplus.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 计划活动表 服务类
 * </p>
 *
 * @author suncg
 * @since 2018-07-11
 */
public interface ITeachingPlanService extends IService<TeachingPlan> {

    /**
     * 获取计划活动数据列表
     */
    List<TeachingPlan> getTeachignPlanList(Page<TeachingPlan> page, Map<String, Object> param);
    /**
     * 理论教学计划导入
     */
    Map<String, Object> importLljxjhExcel(MultipartFile file);
    /**
     * 获取计划督导数据列表
     */
    List<Map<String,Object>> getVisitorTPList(Page<Map<String,Object>> page, Map<String, Object> param);
    /**
     * 教学计划导入
     */
    Map<String, Object> importJxjhExcel(MultipartFile file,String planType);
}
