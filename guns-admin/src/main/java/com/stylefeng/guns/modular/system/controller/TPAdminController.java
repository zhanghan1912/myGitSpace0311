package com.stylefeng.guns.modular.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.common.annotion.Permission;
import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.common.constant.factory.PageFactory;
import com.stylefeng.guns.core.common.constant.state.PlanType;
import com.stylefeng.guns.core.common.constant.state.SystemIdEnum;
import com.stylefeng.guns.core.shiro.ShiroKit;
import com.stylefeng.guns.core.util.*;
import com.stylefeng.guns.modular.system.form.DdEvaluationForm;
import com.stylefeng.guns.modular.system.model.Cfg;
import com.stylefeng.guns.modular.system.model.Dict;
import com.stylefeng.guns.modular.system.model.PlanVisitor;
import com.stylefeng.guns.modular.system.model.TeachingPlan;
import com.stylefeng.guns.modular.system.service.ICfgService;
import com.stylefeng.guns.modular.system.service.IPlanVisitorService;
import com.stylefeng.guns.modular.system.service.ITeachingPlanService;
import org.docx4j.openpackaging.io.SaveToZipFile;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipOutputStream;

/**
 * 控制器
 *
 */
@Controller
@RequestMapping("/admin")
public class TPAdminController extends BaseController {

    private String PREFIX = "/system/teachingPlan/";

    @Autowired
    private ITeachingPlanService teachingPlanService;
    @Autowired
    private IPlanVisitorService planVisitorService;
    @Autowired
    private ICfgService cfgService;

    /**
     * 跳转到计划活动列表页面
     */
    @RequestMapping("/lljxjh")
    public String lljxjhIndex() {
        return PREFIX + "lljxjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/lljxjh/list")
    @Permission
    @ResponseBody
    public Object lljxjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.THEORY.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("systemId",systemId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        Page<TeachingPlan> page = new PageFactory<TeachingPlan>().defaultPage();
        List<TeachingPlan> result = teachingPlanService.getTeachignPlanList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 跳转到添加
     */
    @RequestMapping("/lljxjh_add")
    public String teachingPlanAdd() {
        return PREFIX + "lljxjh_add.html";
    }
    /**
     * 新增
     */
    @RequestMapping(value = "/lljxjh/add")
    @ResponseBody
    public Object add(TeachingPlan teachingPlan) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        teachingPlan.setSystemId(systemId);
        teachingPlan.setSystemName(SystemIdEnum.getNameById(systemId));
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("theory_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlan.setCreateUserId(ShiroKit.getUser().getId());
        teachingPlan.setCreateTime(DateUtil.getAllTime());
        teachingPlanService.insert(teachingPlan);
        return SUCCESS_TIP;
    }
    /**
     * 跳转到导入
     */
    @RequestMapping("/lljxjh_import")
    public String teachingPlanImport() {
        return PREFIX + "lljxjh_import.html";
    }
    /**
     * 导入
     */
    @RequestMapping(value = "/lljxjh/import")
    @ResponseBody
    public Map<String, Object> imp(MultipartFile file) {
        Map<String, Object> returnDataMap = new HashMap<>();
        if(file.getSize() > 0){
            returnDataMap = teachingPlanService.importLljxjhExcel(file);
        }
        return returnDataMap;
    }
    /**
     * 跳转到修改
     */
    @RequestMapping("/lljxjh_update/{id}")
    public String lljxjhUpdate(@PathVariable Integer id, Model model) {
        TeachingPlan teachingPlan = teachingPlanService.selectById(id);
        model.addAttribute("item",teachingPlan);
        return PREFIX + "lljxjh_edit.html";
    }
    /**
     * 修改
     */
    @RequestMapping(value = "/lljxjh/update")
    @ResponseBody
    public Object update(TeachingPlan teachingPlan) {
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("theory_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlanService.updateById(teachingPlan);
        return SUCCESS_TIP;
    }
    /**
     * 删除
     */
    @RequestMapping(value = "/lljxjh/delete")
    @ResponseBody
    public Object delete(@RequestParam Integer teachingPlanId) {
        teachingPlanService.deleteById(teachingPlanId);
        return SUCCESS_TIP;
    }
    /*************临床教学计划************/
    /**
     * 跳转到计划活动列表页面
     */
    @RequestMapping("/lcjxjh")
    public String lcjxjhIndex() {
        return PREFIX + "lcjxjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/lcjxjh/list")
    @Permission
    @ResponseBody
    public Object lcjxjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.CLINIC.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("systemId",systemId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        Page<TeachingPlan> page = new PageFactory<TeachingPlan>().defaultPage();
        List<TeachingPlan> result = teachingPlanService.getTeachignPlanList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 跳转到添加
     */
    @RequestMapping("/lcjxjh_add")
    public String lcjxjhAdd() {
        return PREFIX + "lcjxjh_add.html";
    }
    /**
     * 新增
     */
    @RequestMapping(value = "/lcjxjh/add")
    @ResponseBody
    public Object lcjxjhAdd(TeachingPlan teachingPlan) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        teachingPlan.setSystemId(systemId);
        teachingPlan.setSystemName(SystemIdEnum.getNameById(systemId));
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("clinic_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlan.setCreateUserId(ShiroKit.getUser().getId());
        teachingPlan.setCreateTime(DateUtil.getAllTime());
        teachingPlanService.insert(teachingPlan);
        return SUCCESS_TIP;
    }
    /**
     * 跳转到修改
     */
    @RequestMapping("/lcjxjh_update/{id}")
    public String lcjxjhUpdate(@PathVariable Integer id, Model model) {
        TeachingPlan teachingPlan = teachingPlanService.selectById(id);
        model.addAttribute("item",teachingPlan);
        return PREFIX + "lcjxjh_edit.html";
    }
    /**
     * 跳转到导入
     */
    @RequestMapping("/lcjxjh_import")
    public String lcjxjhImport() {
        return PREFIX + "lcjxjh_import.html";
    }
    /**
     * 修改
     */
    @RequestMapping(value = "/lcjxjh/update")
    @ResponseBody
    public Object lcjxjhUpdate(TeachingPlan teachingPlan) {
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("clinic_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlanService.updateById(teachingPlan);
        return SUCCESS_TIP;
    }
    /**
     * 导入
     */
    @RequestMapping(value = "/importPlanType")
    @ResponseBody
    public Map<String, Object> importPlanType(MultipartFile file,String planType) {
        Map<String, Object> returnDataMap = new HashMap<>();
        if(file.getSize() > 0){
            returnDataMap = teachingPlanService.importJxjhExcel(file,planType);
        }
        return returnDataMap;
    }
    /*************考核督导************/
    /**
     * 跳转到考核督导列表页面
     */
    @RequestMapping("/jkjh")
    public String jkjhIndex() {
        return PREFIX + "jkjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/jkjh/list")
    @Permission
    @ResponseBody
    public Object jkjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.INVIGILATE.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("systemId",systemId);
        Page<TeachingPlan> page = new PageFactory<TeachingPlan>().defaultPage();
        List<TeachingPlan> result = teachingPlanService.getTeachignPlanList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 跳转到添加
     */
    @RequestMapping("/jkjh_add")
    public String jkjhAdd() {
        return PREFIX + "jkjh_add.html";
    }
    /**
     * 新增
     */
    @RequestMapping(value = "/jkjh/add")
    @ResponseBody
    public Object jkjhAdd(TeachingPlan teachingPlan) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        teachingPlan.setSystemId(systemId);
        teachingPlan.setSystemName(SystemIdEnum.getNameById(systemId));
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("invigilate_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlan.setCreateUserId(ShiroKit.getUser().getId());
        teachingPlan.setCreateTime(DateUtil.getAllTime());
        teachingPlanService.insert(teachingPlan);
        return SUCCESS_TIP;
    }
    /**
     * 跳转到修改
     */
    @RequestMapping("/jkjh_update/{id}")
    public String jkjhUpdate(@PathVariable Integer id, Model model) {
        TeachingPlan teachingPlan = teachingPlanService.selectById(id);
        model.addAttribute("item",teachingPlan);
        return PREFIX + "jkjh_edit.html";
    }
    /**
     * 跳转到导入
     */
    @RequestMapping("/jkjh_import")
    public String jkjhImport() {
        return PREFIX + "jkjh_import.html";
    }
    /**
     * 修改
     */
    @RequestMapping(value = "/jkjh/update")
    @ResponseBody
    public Object jkjhUpdate(TeachingPlan teachingPlan) {
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("invigilate_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlanService.updateById(teachingPlan);
        return SUCCESS_TIP;
    }
    /*************其他活动************/
    /**
     * 跳转到其他活动列表页面
     */
    @RequestMapping("/qthdjh")
    public String qthdjhIndex() {
        return PREFIX + "qthdjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/qthdjh/list")
    @Permission
    @ResponseBody
    public Object qthdjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.ACTIVITY.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("systemId",systemId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        Page<TeachingPlan> page = new PageFactory<TeachingPlan>().defaultPage();
        List<TeachingPlan> result = teachingPlanService.getTeachignPlanList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 跳转到添加
     */
    @RequestMapping("/qthdjh_add")
    public String qthdjhAdd() {
        return PREFIX + "qthdjh_add.html";
    }
    /**
     * 新增
     */
    @RequestMapping(value = "/qthdjh/add")
    @ResponseBody
    public Object qthdjhAdd(TeachingPlan teachingPlan) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        teachingPlan.setSystemId(systemId);
        teachingPlan.setSystemName(SystemIdEnum.getNameById(systemId));
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("activity_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlan.setCreateUserId(ShiroKit.getUser().getId());
        teachingPlan.setCreateTime(DateUtil.getAllTime());
        teachingPlanService.insert(teachingPlan);
        return SUCCESS_TIP;
    }
    /**
     * 跳转到修改
     */
    @RequestMapping("/qthdjh_update/{id}")
    public String qthdjhUpdate(@PathVariable Integer id, Model model) {
        TeachingPlan teachingPlan = teachingPlanService.selectById(id);
        model.addAttribute("item",teachingPlan);
        return PREFIX + "qthdjh_edit.html";
    }
    /**
     * 跳转到导入
     */
    @RequestMapping("/qthdjh_import")
    public String qthdjhImport() {
        return PREFIX + "qthdjh_import.html";
    }
    /**
     * 修改
     */
    @RequestMapping(value = "/qthdjh/update")
    @ResponseBody
    public Object qthdjhUpdate(TeachingPlan teachingPlan) {
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("activity_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlanService.updateById(teachingPlan);
        return SUCCESS_TIP;
    }
    /*************轮转督导************/
    /**
     * 跳转到轮转督导列表页面
     */
    @RequestMapping("/lzjh")
    public String lzjhIndex() {
        return PREFIX + "lzjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/lzjh/list")
    @Permission
    @ResponseBody
    public Object lzjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.CIRCLE.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("systemId",systemId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        Page<TeachingPlan> page = new PageFactory<TeachingPlan>().defaultPage();
        List<TeachingPlan> result = teachingPlanService.getTeachignPlanList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 跳转到添加
     */
    @RequestMapping("/lzjh_add")
    public String lzjhAdd() {
        return PREFIX + "lzjh_add.html";
    }
    /**
     * 新增
     */
    @RequestMapping(value = "/lzjh/add")
    @ResponseBody
    public Object lzjhAdd(TeachingPlan teachingPlan) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        teachingPlan.setSystemId(systemId);
        teachingPlan.setSystemName(SystemIdEnum.getNameById(systemId));
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("circle_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlan.setCreateUserId(ShiroKit.getUser().getId());
        teachingPlan.setCreateTime(DateUtil.getAllTime());
        teachingPlanService.insert(teachingPlan);
        return SUCCESS_TIP;
    }
    /**
     * 跳转到修改
     */
    @RequestMapping("/lzjh_update/{id}")
    public String lzjhUpdate(@PathVariable Integer id, Model model) {
        TeachingPlan teachingPlan = teachingPlanService.selectById(id);
        model.addAttribute("item",teachingPlan);
        return PREFIX + "lzjh_edit.html";
    }
    /**
     * 跳转到导入
     */
    @RequestMapping("/lzjh_import")
    public String lzjhImport() {
        return PREFIX + "lzjh_import.html";
    }
    /**
     * 修改
     */
    @RequestMapping(value = "/lzjh/update")
    @ResponseBody
    public Object lzjhUpdate(TeachingPlan teachingPlan) {
        teachingPlan.setActivityName(ConstantFactory.me().getDictNameByCode("circle_type",teachingPlan.getActivityType()));
        teachingPlan.setDeptName(ConstantFactory.me().getDeptName(teachingPlan.getDeptId()));
        teachingPlanService.updateById(teachingPlan);
        return SUCCESS_TIP;
    }
    /*********督导计划查询***********/
    /**
     * 跳转到督导计划列表页面
     */
    @RequestMapping("/ddjhcx")
    public String ddjhcxIndex(Model model) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        model.addAttribute("systemId",systemId);
        return PREFIX + "ddjhcx.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/ddjhcx/list")
    @Permission
    @ResponseBody
    public Object ddjhcxList(@RequestParam(required = false) String planType,@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String teachers, @RequestParam(required = false) String classPlace, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        Map<String, Object> param = new HashMap<>();
        param.put("planType", planType);
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("teachers",teachers);
        param.put("classPlace",classPlace);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("systemId",systemId);
        Page<TeachingPlan> page = new PageFactory<TeachingPlan>().defaultPage();
        List<TeachingPlan> result = teachingPlanService.getTeachignPlanList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 跳转到查看
     */
    @RequestMapping("/ddjhcx_info/{id}/{planType}")
    public String ddjhcxInfo(@PathVariable Integer id,@PathVariable String planType, Model model) {
        TeachingPlan teachingPlan = teachingPlanService.selectById(id);
        model.addAttribute("item",teachingPlan);
        List<String> visitors=new ArrayList<>();
        if(teachingPlan!=null){
            List<PlanVisitor> planVisitors=planVisitorService.selectList(new EntityWrapper<PlanVisitor>().eq("plan_id", id));
            if(planVisitors!=null&&planVisitors.size()>0){
                for (PlanVisitor pv:planVisitors) {
                    visitors.add(pv.getVisitorName());
                }
                model.addAttribute("visitors",visitors.toString());
            }else{
                model.addAttribute("visitors","-");
            }
        }
        return PREFIX + "ddjhcx_detail.html";
    }
    /*********督导评价查询***********/
    /**
     * 跳转到督导评价列表页面
     */
    @RequestMapping("/ddpjcx")
    public String ddpjcxIndex(Model model) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        model.addAttribute("systemId",systemId);
        return PREFIX + "ddpjcx.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/ddpjcx/list")
    @Permission
    @ResponseBody
    public Object ddjhcxList(@RequestParam(required = false) String planType,@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String teachers, @RequestParam(required = false) String classPlace,@RequestParam(required = false) String visitorName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        Map<String, Object> param = new HashMap<>();
        param.put("planType", planType);
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("teachers",teachers);
        param.put("visitorName",visitorName);
        param.put("classPlace",classPlace);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("systemId",systemId);
        Page<Map<String,Object>> page = new PageFactory<Map<String,Object>>().defaultPage();
        List<Map<String,Object>> result = planVisitorService.getVisitorPJList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 跳转到查看
     */
    @RequestMapping("/ddpjcx_info/{id}/{planType}")
    public String ddpjcxInfo(@PathVariable Integer id,@PathVariable String planType, Model model) {
        TeachingPlan teachingPlan = teachingPlanService.selectById(id);
        model.addAttribute("item",teachingPlan);
        List<String> visitors=new ArrayList<>();
        List<Integer> visitorIds=new ArrayList<>();
        if(teachingPlan!=null){
            List<PlanVisitor> planVisitors=planVisitorService.selectList(new EntityWrapper<PlanVisitor>().eq("plan_id", id));
            if(planVisitors!=null&&planVisitors.size()>0){
                for (PlanVisitor pv:planVisitors) {
                    visitors.add(pv.getVisitorName());
                    visitorIds.add(pv.getVisitorId());
                }
                model.addAttribute("visitors",visitors.toString());
                model.addAttribute("visitorIds",visitorIds.toString());
            }else{
                model.addAttribute("visitors","-");
                model.addAttribute("visitorIds","-");
            }
            model.addAttribute("planVisitors",planVisitors);
        }
        model.addAttribute("visitors",visitors);
        return PREFIX + "ddpjcx_detail.html";
    }
    /**
     * 督导评价导出操作
     */
    @RequestMapping(value="/exportDdpjList")
    public void exportDdpjList(@RequestParam(required = false) String planType,@RequestParam(required = false) String activityType,
                               @RequestParam(required = false) String courseName, @RequestParam(required = false) String teachers,
                               @RequestParam(required = false) String classPlace,@RequestParam(required = false) String visitorName,
                               @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime,
                               @RequestParam(required = false) String roleFlag,HttpServletResponse response) throws Exception {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        Map<String, Object> param = new HashMap<>();
        param.put("planType", planType);
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("teachers",teachers);
        param.put("visitorName",visitorName);
        param.put("classPlace",classPlace);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("systemId",systemId);
        if("ddls".equals(roleFlag)){
            param.put("visitorId",ShiroKit.getUser().getId());//标识督导列表显示本人
        }
        List<Map<String,Object>> result = planVisitorService.getVisitorPJList(null, param);
        String[] titles = new String[]{
                "course_name:课程名称",
                "activity_name:课程类型",
                "teachers:任课老师",
                "visitors:督导老师",
                "comments:老师评语",
                "class_time:时间",
                "class_place:地点"
        };
        String fileName = "督导评价信息.xls";
        fileName = URLEncoder.encode(fileName, "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setContentType("application/octet-stream;charset=UTF-8");
        ExcleUtil.exportSimpleExcleByObjsAllString(titles,result,response.getOutputStream());
    }
    /**
     * 评价详情打印-WPS打开
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/printToWPS")
    public void printToWPS(String planId, String id, HttpServletResponse response, HttpServletRequest request) throws Exception{
        //文件名称
        String fileName = "";
        Map<String, Object> dataMap = new HashMap<String, Object>();
        String UPLOAD_BASE_DIR = "";//文件上传访问根地址
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_dir"));
        if(cfg!=null){
            UPLOAD_BASE_DIR=cfg.getCfgValue();
        }
        String DOWNLOAD_TEMP_DIR = "C:/testWord";//临时文件存放地址
        Cfg cfg1=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "download_temp_dir"));
        if(cfg1!=null){
            DOWNLOAD_TEMP_DIR=cfg.getCfgValue();
        }
        List<String> imgList=new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        param.put("visitorId",id);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        TeachingPlan teachingPlan =new TeachingPlan();
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            PlanVisitor planVisitor=tempVisitorList.get(0);
            teachingPlan = teachingPlanService.selectById(planId);
            fileName = (teachingPlan!=null?teachingPlan.getCourseName():"");
            dataMap.put("planVisitor",planVisitor);
            DdEvaluationForm form=new DdEvaluationForm();
            String imgUrlTemp="";
            if(planVisitor!=null&&StringUtils.checkValNotNull(planVisitor.getEvaluateScore())){
                fileName = fileName+"_"+planVisitor.getVisitorName();
                form=planVisitorService.parseExtInfoXml(planVisitor.getEvaluateScore());
                if(form!=null){
                    dataMap.put("total",countScore(form));
                    if(StringUtils.checkValNotNull(form.getImgUrl1())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl1().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl1",imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl2())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl2().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl2",imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl3())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl3().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl3",imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl4())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl4().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl4",imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl5())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl5().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl5",imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl6())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl6().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl6",imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl7())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl7().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl7",imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl8())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl8().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl8",imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl9())){
                        imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl9().replaceAll("\\\\","/");
                        imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                        dataMap.put("imgUrl9",imgUrlTemp);
                    }
                }
            }
            dataMap.put("form",form);
            dataMap.put("imgList",imgList);
        }
        dataMap.put("item",teachingPlan);
        /** 文件名称，唯一字符串 */
        Random r = new Random();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        StringBuffer sb = new StringBuffer();
        sb.append(sdf1.format(new Date()));
        sb.append("_");
        sb.append(r.nextInt(100));
        //文件路径
        String filePath = DOWNLOAD_TEMP_DIR;
        //文件唯一名称
        String fileOnlyName = "评价详情_" + sb + ".doc";
        fileName=fileName+".doc";
        String templatePath="print/tongyong1.ftl";
        if(PlanType.INVIGILATE.getId().equals(teachingPlan.getPlanType())){
            templatePath= "print/tongyong2.ftl";
        }
        if(StringUtils.checkValNotNull(teachingPlan.getActivityType())){
            if("xjk".equals(teachingPlan.getActivityType())){
                templatePath="print/xjkForm.ftl";
            }
            if("jnk".equals(teachingPlan.getActivityType())){
                templatePath="print/jnkForm.ftl";
            }
            if("gpcf".equals(teachingPlan.getActivityType())){
                templatePath="print/jxcfForm.ftl";
            }
            if("batl".equals(teachingPlan.getActivityType())){
                templatePath="print/batlForm.ftl";
            }
            if("zyqkysxxjx".equals(teachingPlan.getActivityType())){
                templatePath="print/lzjxForm.ftl";
            }
            if("zyqkysxxjn".equals(teachingPlan.getActivityType())){
                templatePath="print/lzjnForm.ftl";
            }
//            if("djt".equals(teachingPlan.getActivityType())){
//                templatePath="/print/tkpjLcForm.ftl";
//            }
//            if("jcyx".equals(teachingPlan.getActivityType())||"jcyx_jbjn".equals(teachingPlan.getActivityType())
//                    ||"zyjck".equals(teachingPlan.getActivityType())||"syysj".equals(teachingPlan.getActivityType())
//                    ||"zyzsyll".equals(teachingPlan.getActivityType())||"zyjd".equals(teachingPlan.getActivityType())
//                    ||"zllc".equals(teachingPlan.getActivityType())||"lcyx".equals(teachingPlan.getActivityType())
//                    ||"rwsy".equals(teachingPlan.getActivityType())
//                    ){
//                templatePath="/print/tkpjSyForm.ftl";
//            }
        }
        /** 生成word */
        WordUtil.createWord(dataMap, templatePath, filePath, fileOnlyName);
        /** 下载word并删除临时文件 */
        WordUtil.downloadWord(filePath,fileOnlyName,fileName,response,request);
    }
    /**
     * 评价详情批量打印
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/batchPrintToWPS")
    public void batchPrintToWPS(String planId,HttpServletResponse response, HttpServletRequest request) throws Exception{
        TeachingPlan teachingPlan = teachingPlanService.selectById(planId);
        List<File> fileList=new ArrayList<>();
        String UPLOAD_BASE_DIR = "";//文件上传访问根地址
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_dir"));
        if(cfg!=null){
            UPLOAD_BASE_DIR=cfg.getCfgValue();
        }
        String DOWNLOAD_TEMP_DIR = "C:/testWord";//临时文件存放地址
        Cfg cfg1=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "download_temp_dir"));
        if(cfg1!=null){
            DOWNLOAD_TEMP_DIR=cfg.getCfgValue();
        }
        List<String> imgList=new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            for (PlanVisitor planVisitor:tempVisitorList) {
                //文件名称
                String fileName = "";
                Map<String, Object> dataMap = new HashMap<String, Object>();
                fileName = (teachingPlan!=null?teachingPlan.getCourseName():"");
                dataMap.put("planVisitor",planVisitor);
                DdEvaluationForm form=new DdEvaluationForm();
                String imgUrlTemp="";
                if(planVisitor!=null&&StringUtils.checkValNotNull(planVisitor.getEvaluateScore())){
                    fileName = fileName+"_"+planVisitor.getVisitorName();
                    form=planVisitorService.parseExtInfoXml(planVisitor.getEvaluateScore());
                    if(form!=null){
                        dataMap.put("total",countScore(form));
                        if(StringUtils.checkValNotNull(form.getImgUrl1())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl1().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl1",imgUrlTemp);
                        }
                        if(StringUtils.checkValNotNull(form.getImgUrl2())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl2().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl2",imgUrlTemp);
                        }
                        if(StringUtils.checkValNotNull(form.getImgUrl3())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl3().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl3",imgUrlTemp);
                        }
                        if(StringUtils.checkValNotNull(form.getImgUrl4())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl4().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl4",imgUrlTemp);
                        }
                        if(StringUtils.checkValNotNull(form.getImgUrl5())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl5().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl5",imgUrlTemp);
                        }
                        if(StringUtils.checkValNotNull(form.getImgUrl6())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl6().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl6",imgUrlTemp);
                        }
                        if(StringUtils.checkValNotNull(form.getImgUrl7())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl7().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl7",imgUrlTemp);
                        }
                        if(StringUtils.checkValNotNull(form.getImgUrl8())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl8().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl8",imgUrlTemp);
                        }
                        if(StringUtils.checkValNotNull(form.getImgUrl9())){
                            imgUrlTemp=UPLOAD_BASE_DIR+form.getImgUrl9().replaceAll("\\\\","/");
                            imgUrlTemp=WordUtil.getImageStr(imgUrlTemp);
                            dataMap.put("imgUrl9",imgUrlTemp);
                        }
                        dataMap.put("form",form);
                        dataMap.put("imgList",imgList);
                        dataMap.put("item",teachingPlan);
                        /** 文件名称，唯一字符串 */
                        Random r = new Random();
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
                        StringBuffer sb = new StringBuffer();
                        sb.append(sdf1.format(new Date()));
                        sb.append("_");
                        sb.append(r.nextInt(100));
                        //文件路径
                        String filePath = DOWNLOAD_TEMP_DIR;
                        //文件唯一名称
                        String fileOnlyName = planVisitor.getVisitorName() + "_" + sb + ".doc";
                        fileName=fileName+".doc";
                        String templatePath="print/tongyong1.ftl";
                        if(PlanType.INVIGILATE.getId().equals(teachingPlan.getPlanType())){
                            templatePath= "print/tongyong2.ftl";
                        }
                        if(StringUtils.checkValNotNull(teachingPlan.getActivityType())){
                            if("xjk".equals(teachingPlan.getActivityType())){
                                templatePath="print/xjkForm.ftl";
                            }
                            if("jnk".equals(teachingPlan.getActivityType())){
                                templatePath="print/jnkForm.ftl";
                            }
                            if("gpcf".equals(teachingPlan.getActivityType())){
                                templatePath="print/jxcfForm.ftl";
                            }
                            if("batl".equals(teachingPlan.getActivityType())){
                                templatePath="print/batlForm.ftl";
                            }
                            if("zyqkysxxjx".equals(teachingPlan.getActivityType())){
                                templatePath="print/lzjxForm.ftl";
                            }
                            if("zyqkysxxjn".equals(teachingPlan.getActivityType())){
                                templatePath="print/lzjnForm.ftl";
                            }
//                            if("djt".equals(teachingPlan.getActivityType())){
//                                templatePath="/print/tkpjLcForm.ftl";
//                            }
//                            if("jcyx".equals(teachingPlan.getActivityType())||"jcyx_jbjn".equals(teachingPlan.getActivityType())
//                                    ||"zyjck".equals(teachingPlan.getActivityType())||"syysj".equals(teachingPlan.getActivityType())
//                                    ||"zyzsyll".equals(teachingPlan.getActivityType())||"zyjd".equals(teachingPlan.getActivityType())
//                                    ||"zllc".equals(teachingPlan.getActivityType())||"lcyx".equals(teachingPlan.getActivityType())
//                                    ||"rwsy".equals(teachingPlan.getActivityType())
//                                    ){
//                                templatePath="/print/tkpjLlForm.ftl";
//                            }
                        }
                        /** 生成word */
                        WordUtil.createWord(dataMap, templatePath, filePath, fileOnlyName);
                        fileList.add(new File(filePath + File.separator + fileOnlyName));
                    }
                }
            }
            if(fileList.size()>0){
                /** 下载zip并删除临时文件 */
                String zipName = teachingPlan.getCourseName()+"_评价详情.zip";
                String outFilePath = DOWNLOAD_TEMP_DIR;
                File fileZip = new File(outFilePath+File.separator +zipName);
                FileOutputStream outStream = new FileOutputStream(fileZip);
                ZipOutputStream toClient = new ZipOutputStream(outStream);
                ZipUtil.zipFile(fileList,toClient);
                toClient.close();
                outStream.close();
                ZipUtil.downloadFile(fileZip,response,true);
                for(File f:fileList){
                    f.delete();
                }
            }
        }
    }

    /**
     * 评价详情打印
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/print")
    public void print(String planId, String id, HttpServletResponse response, HttpServletRequest request) throws Exception{
        String defultImg=this.getClass().getClassLoader().getResource("print/noImg.png").getPath();
        //文件名称
        String fileName = "";
        Map<String, Object> dataMap = new HashMap<String, Object>();
        String UPLOAD_BASE_DIR = "";//文件上传物理路径
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_dir"));
        if(cfg!=null){
            UPLOAD_BASE_DIR=cfg.getCfgValue();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        param.put("visitorId",id);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        String templatePath2="";
        TeachingPlan teachingPlan =new TeachingPlan();
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            PlanVisitor planVisitor=tempVisitorList.get(0);
            if("Sy".equals(planVisitor.getEvaluateType())){
                templatePath2="/print/tkpjLlForm.docx";
            }
            if("Kt".equals(planVisitor.getEvaluateType())){
                templatePath2="/print/tkpjKtForm.docx";
            }
            teachingPlan = teachingPlanService.selectById(planId);
            fileName = (teachingPlan!=null?teachingPlan.getCourseName():"");
            dataMap.put("visitorName",planVisitor.getVisitorName());
            DdEvaluationForm form=new DdEvaluationForm();
            String imgUrlTemp="";
            if(planVisitor!=null&&StringUtils.checkValNotNull(planVisitor.getEvaluateScore())){
                fileName = fileName+"_"+planVisitor.getVisitorName();
                form=planVisitorService.parseExtInfoXml(planVisitor.getEvaluateScore());
                if(form!=null){
                    dataMap.put("total",countScore(form));
                    Map<String, String> formMap = new HashMap<String, String>();
                    formMap=getClassFieldMap(form);
                    dataMap.putAll(formMap);
                    if(StringUtils.checkValNotNull(form.getImgUrl1())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl1().replaceAll("\\\\","/");
                        dataMap.put("imgUrl1","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl1","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl2())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl2().replaceAll("\\\\","/");
                        dataMap.put("imgUrl2","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl2","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl3())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl3().replaceAll("\\\\","/");
                        dataMap.put("imgUrl3","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl3","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl4())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl4().replaceAll("\\\\","/");
                        dataMap.put("imgUrl4","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl4","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl5())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl5().replaceAll("\\\\","/");
                        dataMap.put("imgUrl5","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl5","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl6())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl6().replaceAll("\\\\","/");
                        dataMap.put("imgUrl6","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl6","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl7())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl7().replaceAll("\\\\","/");
                        dataMap.put("imgUrl7","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl7","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl8())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl8().replaceAll("\\\\","/");
                        dataMap.put("imgUrl8","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl8","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl9())){
                        imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl9().replaceAll("\\\\","/");
                        dataMap.put("imgUrl9","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                    }else{
                        dataMap.put("imgUrl9","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                    }
                }
            }
        }
        Map<String, String> itemMap = new HashMap<String, String>();
        itemMap=getClassFieldMap(teachingPlan);
        dataMap.putAll(itemMap);
        fileName=fileName+".docx";
        String templatePath="/print/tongyong1.docx";
        if(PlanType.INVIGILATE.getId().equals(teachingPlan.getPlanType())){
            templatePath= "/print/tongyong2.docx";
        }
        if(StringUtils.checkValNotNull(teachingPlan.getActivityType())){
            if("xjk".equals(teachingPlan.getActivityType())){
                templatePath="/print/xjkForm.docx";
            }
            if("jnk".equals(teachingPlan.getActivityType())){
                templatePath="/print/jnkForm.docx";
            }
            if("gpcf".equals(teachingPlan.getActivityType())){
                templatePath="/print/jxcfForm.docx";
            }
            if("batl".equals(teachingPlan.getActivityType())){
                templatePath="/print/batlForm.docx";
            }
            if("zyqkysxxjx".equals(teachingPlan.getActivityType())){
                templatePath="/print/lzjxForm.docx";
            }
            if("zyqkysxxjn".equals(teachingPlan.getActivityType())){
                templatePath="/print/lzjnForm.docx";
            }
            if("lcyx".equals(teachingPlan.getActivityType())
                    ||"djt".equals(teachingPlan.getActivityType())){
                templatePath= "/print/tkpjLcForm.docx";
            }
            if("jcyx".equals(teachingPlan.getActivityType())||"jcyx_jbjn".equals(teachingPlan.getActivityType())
                    ||"zyjck".equals(teachingPlan.getActivityType())||"syysj".equals(teachingPlan.getActivityType())
                    ||"zyzsyll".equals(teachingPlan.getActivityType())||"zyjd".equals(teachingPlan.getActivityType())
                    ||"zllc".equals(teachingPlan.getActivityType())||"rwsy".equals(teachingPlan.getActivityType())
                    ){
                templatePath=templatePath2;
            }
        }
        InputStream input = this.getClass().getResourceAsStream(templatePath);
        WordprocessingMLPackage temeplete = new WordprocessingMLPackage();
        temeplete = Docx4jUtil.convert3(input, dataMap, null, true);
        if (temeplete != null) {
            response.setHeader("Content-disposition", "attachment; filename=" + new String(fileName.getBytes("gbk"), "ISO8859-1") + "");
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            ServletOutputStream out = response.getOutputStream();
            (new SaveToZipFile(temeplete)).save(out);
            out.flush();
        }
    }

    /**
     * 评价详情批量打印
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value="/batchPrint")
    public void batchPrint(String planId,HttpServletResponse response, HttpServletRequest request) throws Exception{
        String defultImg=this.getClass().getClassLoader().getResource("print/noImg.png").getPath();
        String folder=System.getProperty("java.io.tmpdir")+ File.separator;
        String zipFile = UUID.randomUUID().toString().replaceAll("-", "");
        String dir = folder+zipFile;
        File dirFile = new File(dir);
        if(dirFile.exists()==false){
            dirFile.mkdirs();
        }
        List<WordprocessingMLPackage> addTemplates = new ArrayList<WordprocessingMLPackage>();
        TeachingPlan teachingPlan = teachingPlanService.selectById(planId);
        String UPLOAD_BASE_DIR = "";//文件上传物理地址
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_dir"));
        if(cfg!=null){
            UPLOAD_BASE_DIR=cfg.getCfgValue();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            String templatePath="/print/tongyong1.docx";
            if(PlanType.INVIGILATE.getId().equals(teachingPlan.getPlanType())){
                templatePath= "/print/tongyong2.docx";
            }
            if(StringUtils.checkValNotNull(teachingPlan.getActivityType())){
                if("xjk".equals(teachingPlan.getActivityType())){
                    templatePath="/print/xjkForm.docx";
                }
                if("jnk".equals(teachingPlan.getActivityType())){
                    templatePath="/print/jnkForm.docx";
                }
                if("gpcf".equals(teachingPlan.getActivityType())){
                    templatePath="/print/jxcfForm.docx";
                }
                if("batl".equals(teachingPlan.getActivityType())){
                    templatePath="/print/batlForm.docx";
                }
                if("zyqkysxxjx".equals(teachingPlan.getActivityType())){
                    templatePath="/print/lzjxForm.docx";
                }
                if("zyqkysxxjn".equals(teachingPlan.getActivityType())){
                    templatePath="/print/lzjnForm.docx";
                }
                if("lcyx".equals(teachingPlan.getActivityType())
                        ||"djt".equals(teachingPlan.getActivityType())){
                    templatePath= "/print/tkpjLcForm.docx";
                }
                if("jcyx".equals(teachingPlan.getActivityType())||"jcyx_jbjn".equals(teachingPlan.getActivityType())
                        ||"zyjck".equals(teachingPlan.getActivityType())||"syysj".equals(teachingPlan.getActivityType())
                        ||"zyzsyll".equals(teachingPlan.getActivityType())||"zyjd".equals(teachingPlan.getActivityType())
                        ||"zllc".equals(teachingPlan.getActivityType())||"rwsy".equals(teachingPlan.getActivityType())
                        ){

                    templatePath="/print/tkpjLlForm.docx";
                }
            }
            for (PlanVisitor planVisitor:tempVisitorList) {
                if("Sy".equals(planVisitor.getEvaluateType())){
                    templatePath="/print/tkpjLlForm.docx";
                }
                if("Kt".equals(planVisitor.getEvaluateType())){
                    templatePath="/print/tkpjKtForm.docx";
                }
                //文件名称
                String fileName = "";
                Map<String, Object> dataMap = new HashMap<String, Object>();
                if(planVisitor!=null){
                    fileName = (teachingPlan!=null?teachingPlan.getCourseName():"");
                    dataMap.put("visitorName",planVisitor.getVisitorName());
                    DdEvaluationForm form=new DdEvaluationForm();
                    String imgUrlTemp="";
                    if(planVisitor!=null&&StringUtils.checkValNotNull(planVisitor.getEvaluateScore())){
                        fileName = fileName+"_"+planVisitor.getVisitorName();
                        form=planVisitorService.parseExtInfoXml(planVisitor.getEvaluateScore());
                        if(form!=null){
                            dataMap.put("total",countScore(form));
                            Map<String, String> formMap = new HashMap<String, String>();
                            formMap=getClassFieldMap(form);
                            dataMap.putAll(formMap);
                            if(StringUtils.checkValNotNull(form.getImgUrl1())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl1().replaceAll("\\\\","/");
                                dataMap.put("imgUrl1","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl1","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                            if(StringUtils.checkValNotNull(form.getImgUrl2())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl2().replaceAll("\\\\","/");
                                dataMap.put("imgUrl2","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl2","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                            if(StringUtils.checkValNotNull(form.getImgUrl3())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl3().replaceAll("\\\\","/");
                                dataMap.put("imgUrl3","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl3","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                            if(StringUtils.checkValNotNull(form.getImgUrl4())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl4().replaceAll("\\\\","/");
                                dataMap.put("imgUrl4","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl4","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                            if(StringUtils.checkValNotNull(form.getImgUrl5())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl5().replaceAll("\\\\","/");
                                dataMap.put("imgUrl5","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl5","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                            if(StringUtils.checkValNotNull(form.getImgUrl6())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl6().replaceAll("\\\\","/");
                                dataMap.put("imgUrl6","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl6","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                            if(StringUtils.checkValNotNull(form.getImgUrl7())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl7().replaceAll("\\\\","/");
                                dataMap.put("imgUrl7","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl7","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                            if(StringUtils.checkValNotNull(form.getImgUrl8())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl8().replaceAll("\\\\","/");
                                dataMap.put("imgUrl8","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl8","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                            if(StringUtils.checkValNotNull(form.getImgUrl9())){
                                imgUrlTemp="file:/"+UPLOAD_BASE_DIR+form.getImgUrl9().replaceAll("\\\\","/");
                                dataMap.put("imgUrl9","<img src='"+imgUrlTemp+"' width='160' height='180'  alt='照片'/>");
                            }else{
                                dataMap.put("imgUrl9","<img src='"+defultImg+"' width='20' height='20'  alt='照片'/>");
                            }
                        }
                    }
                }
                Map<String, String> itemMap = new HashMap<String, String>();
                itemMap=getClassFieldMap(teachingPlan);
                dataMap.putAll(itemMap);
                fileName=fileName+".docx";
                InputStream input = this.getClass().getResourceAsStream(templatePath);
                WordprocessingMLPackage temeplete = new WordprocessingMLPackage();
                temeplete = Docx4jUtil.convert3(input, dataMap, null, true);
                addTemplates.add(temeplete);
                File f = new File(dir+File.separator+fileName);
                temeplete.save(f);
            }
            ZipUtil.makeDirectoryToZip(dirFile, new File(folder+zipFile+".zip"), "", 7);
            String zipFileName = (teachingPlan!=null?teachingPlan.getCourseName():"")+".zip";
            zipFileName = URLEncoder.encode(zipFileName, "UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFileName + "\"");
            response.setContentType("application/octet-stream;charset=UTF-8");
            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
            byte[] data = getByte(new File(folder+zipFile+".zip"));
            if (data != null) {
                outputStream.write(data);
                new File(folder+zipFile+".zip").delete();
            }
            outputStream.flush();
            outputStream.close();
        }
    }

    /**
     * 字典项列表加载
     */
    @RequestMapping(value = "/dictLoad")
    @ResponseBody
    public Object dictLoad(String dictType) {
        List<Dict> dicts=new ArrayList<>();
        dicts=ConstantFactory.me().findByDictType(dictType);
        return dicts;
    }




    public static byte[] getByte(File file) throws Exception {
        if (file == null) {
            return null;
        }
        try {
            int length = (int) file.length();
            if (length > Integer.MAX_VALUE) {    //当文件的长度超过了int的最大值
                System.out.println("this file is max ");
            }
            FileInputStream stream = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(length);
            byte[] b = new byte[length];
            int n;
            while ((n = stream.read(b)) != -1)
                out.write(b, 0, n);
            stream.close();
            out.close();
            return out.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    private String countScore(DdEvaluationForm form){
        double score1= StringUtils.checkValNotNull(form.getScoreItem1())?Double.parseDouble(form.getScoreItem1()):0;
        double score2= StringUtils.checkValNotNull(form.getScoreItem2())?Double.parseDouble(form.getScoreItem2()):0;
        double score3= StringUtils.checkValNotNull(form.getScoreItem3())?Double.parseDouble(form.getScoreItem3()):0;
        double score4= StringUtils.checkValNotNull(form.getScoreItem4())?Double.parseDouble(form.getScoreItem4()):0;
        double score5= StringUtils.checkValNotNull(form.getScoreItem5())?Double.parseDouble(form.getScoreItem5()):0;
        double score6= StringUtils.checkValNotNull(form.getScoreItem6())?Double.parseDouble(form.getScoreItem6()):0;
        double score7= StringUtils.checkValNotNull(form.getScoreItem7())?Double.parseDouble(form.getScoreItem7()):0;
        double score8= StringUtils.checkValNotNull(form.getScoreItem8())?Double.parseDouble(form.getScoreItem8()):0;
        double score9= StringUtils.checkValNotNull(form.getScoreItem9())?Double.parseDouble(form.getScoreItem9()):0;
        double score10= StringUtils.checkValNotNull(form.getScoreItem10())?Double.parseDouble(form.getScoreItem10()):0;
        double score11= StringUtils.checkValNotNull(form.getScoreItem11())?Double.parseDouble(form.getScoreItem11()):0;
        double score12= StringUtils.checkValNotNull(form.getScoreItem12())?Double.parseDouble(form.getScoreItem12()):0;
        double score13= StringUtils.checkValNotNull(form.getScoreItem13())?Double.parseDouble(form.getScoreItem13()):0;
        double score14= StringUtils.checkValNotNull(form.getScoreItem14())?Double.parseDouble(form.getScoreItem14()):0;
        double score15= StringUtils.checkValNotNull(form.getScoreItem15())?Double.parseDouble(form.getScoreItem15()):0;
        double total=score1+score2+score3+score4+score5+score6+score7+score8+score9+score10+score11+score12+score13+score14+score15;
        return total+"";
    }
    //获取属性名和值
    private Map<String,String> getClassFieldMap(Object obj){
        Map<String,String> filedMap = null;
        if(obj!=null){
            try{
                filedMap = new HashMap<String, String>();
                String stringClassName = String.class.getSimpleName();
                Class<?> objClass = obj.getClass();
                Field[] fileds = objClass.getDeclaredFields();
                for(Field f : fileds){
                    String typeName = f.getType().getSimpleName();
                    if(stringClassName.equals(typeName)){
                        String attrName = f.getName();
                        String firstLetter = attrName.substring(0,1).toUpperCase();
                        String methedName = "get"+firstLetter+attrName.substring(1);
                        Method getMethod = objClass.getMethod(methedName);
                        String value = (String)getMethod.invoke(obj);
                        filedMap.put(attrName, org.apache.commons.lang.StringUtils.defaultString(value));
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return filedMap;
    }
}
