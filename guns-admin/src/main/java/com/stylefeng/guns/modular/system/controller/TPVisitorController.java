package com.stylefeng.guns.modular.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.base.tips.ErrorTip;
import com.stylefeng.guns.core.common.annotion.Permission;
import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.common.constant.factory.PageFactory;
import com.stylefeng.guns.core.common.constant.state.PlanType;
import com.stylefeng.guns.core.shiro.ShiroKit;
import com.stylefeng.guns.core.shiro.ShiroUser;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.modular.system.form.DdEvaluationForm;
import com.stylefeng.guns.modular.system.model.Cfg;
import com.stylefeng.guns.modular.system.model.PlanVisitor;
import com.stylefeng.guns.modular.system.model.TeachingPlan;
import com.stylefeng.guns.modular.system.service.ICfgService;
import com.stylefeng.guns.modular.system.service.IDeptService;
import com.stylefeng.guns.modular.system.service.IPlanVisitorService;
import com.stylefeng.guns.modular.system.service.ITeachingPlanService;
import io.swagger.models.auth.In;
import org.abego.treelayout.internal.util.java.lang.string.StringUtil;
import org.apache.ibatis.annotations.Lang;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.*;

/**
 * 控制器
 *
 */
@Controller
@RequestMapping("/visitor")
public class TPVisitorController extends BaseController {

    private String PREFIX = "/system/teachingPlan/";

    @Autowired
    private ITeachingPlanService teachingPlanService;
    @Autowired
    private IPlanVisitorService planVisitorService;
    @Autowired
    private ICfgService cfgService;
    @Autowired
    private IDeptService deptService;

    /**
     * 跳转到计划活动列表页面
     */
    @RequestMapping("/llddjh")
    public String llddjhIndex() {
        return PREFIX + "visitor/llddjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/llddjh/list")
    @Permission
    @ResponseBody
    public Object llddjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime, @RequestParam(required = false) Integer deptId,Model model) {
        if(StringUtils.checkValNull(deptId)){
            deptId=ShiroKit.getUser().getDeptId();
        }
        List<Map<String, Object>> deptList = this.deptService.list(null);
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.THEORY.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("deptId",deptId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        param.put("visitorId",ShiroKit.getUser().getId());//标识督导列表显示本人
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        param.put("systemId",systemId);
        Page<Map<String,Object>> page = new PageFactory<Map<String,Object>>().defaultPage();
        List<Map<String,Object>> result = teachingPlanService.getVisitorTPList(page, param);
        page.setRecords(result);
        model.addAttribute("deptList",deptList);
        return super.packForBT(page);
    }
    /**
     * 参与督导
     */
    @RequestMapping(value = "/llddjh/joinPlan")
    @ResponseBody
    public Object joinPlan(@RequestParam Integer teachingPlanId) {
        Integer num=null;
        ShiroUser shiroUser = ShiroKit.getUser();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visitorId",shiroUser.getId());
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            return new ErrorTip(-1,"已经参与过该计划");
        }
        //督导计划参加人数配置
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", PlanType.THEORY.getId()));
        if(cfg!=null&&StringUtils.checkValNotNull(cfg.getCfgValue())){
            num=Integer.parseInt(cfg.getCfgValue());
        }
        param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> visitorList=planVisitorService.queryPlanVisitorList(param);
        if(num==null||(visitorList!=null&&num>visitorList.size())){
            PlanVisitor visitor=new PlanVisitor();
            visitor.setPlanId(teachingPlanId);
            visitor.setVisiteFlag(ConstantFactory.FLAG_Y);
            visitor.setVisitorId(shiroUser.getId());
            visitor.setVisitorName(shiroUser.getName());
            visitor.setCreateTime(DateUtil.getAllTime());
            visitor.setCreateUserId(shiroUser.getId());
            planVisitorService.insert(visitor);
        }else{
            return new ErrorTip(-1,"当前参与人数已满，无法参与");
        }
        return SUCCESS_TIP;
    }
    /**
     * 取消督导
     */
    @RequestMapping(value = "/llddjh/cancelPlan")
    @ResponseBody
    public Object cancelPlan(@RequestParam Integer teachingPlanId) {
        ShiroUser shiroUser = ShiroKit.getUser();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visitorId",shiroUser.getId());
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> visitorCheck=planVisitorService.queryPlanVisitorList(param);
        if(visitorCheck!=null&&visitorCheck.size()>0){
            boolean flag=planVisitorService.deleteById(visitorCheck.get(0).getId());
            if(!flag){
                return new ErrorTip(0,"");
            }
        }else{
            return new ErrorTip(-1,"当前没有参与该计划，无法取消");
        }
        return SUCCESS_TIP;
    }
    /**********临床督导计划*************/
    /**
     * 跳转到计划活动列表页面
     */
    @RequestMapping("/lcddjh")
    public String lcddjhIndex() {
        return PREFIX + "visitor/lcddjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/lcddjh/list")
    @Permission
    @ResponseBody
    public Object lcddjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime, @RequestParam(required = false) Integer deptId,Model model) {
        if(StringUtils.checkValNull(deptId)){
            deptId=ShiroKit.getUser().getDeptId();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.CLINIC.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("deptId",deptId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        param.put("visitorId",ShiroKit.getUser().getId());//标识督导列表显示本人
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        param.put("systemId",systemId);
        Page<Map<String,Object>> page = new PageFactory<Map<String,Object>>().defaultPage();
        List<Map<String,Object>> result = teachingPlanService.getVisitorTPList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 参与督导
     */
    @RequestMapping(value = "/lcddjh/joinPlan")
    @ResponseBody
    public Object joinLcddjhPlan(@RequestParam Integer teachingPlanId) {
        Integer num=null;
        ShiroUser shiroUser = ShiroKit.getUser();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visitorId",shiroUser.getId());
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            return new ErrorTip(-1,"已经参与过该计划");
        }
        //督导计划参加人数配置
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", PlanType.CLINIC.getId()));
        if(cfg!=null&&StringUtils.checkValNotNull(cfg.getCfgValue())){
            num=Integer.parseInt(cfg.getCfgValue());
        }
        param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> visitorList=planVisitorService.queryPlanVisitorList(param);
        if(num==null||(visitorList!=null&&num>visitorList.size())){
            PlanVisitor visitor=new PlanVisitor();
            visitor.setPlanId(teachingPlanId);
            visitor.setVisiteFlag(ConstantFactory.FLAG_Y);
            visitor.setVisitorId(shiroUser.getId());
            visitor.setVisitorName(shiroUser.getName());
            visitor.setCreateTime(DateUtil.getAllTime());
            visitor.setCreateUserId(shiroUser.getId());
            planVisitorService.insert(visitor);
        }else{
            return new ErrorTip(-1,"当前参与人数已满，无法参与");
        }
        return SUCCESS_TIP;
    }

    /**********监考督导计划*************/
    /**
     * 跳转到计划活动列表页面
     */
    @RequestMapping("/khddjh")
    public String jkddjhIndex() {
        return PREFIX + "visitor/jkddjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/khddjh/list")
    @Permission
    @ResponseBody
    public Object jkddjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime, @RequestParam(required = false) Integer deptId,Model model) {
        if(StringUtils.checkValNull(deptId)){
            deptId=ShiroKit.getUser().getDeptId();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.INVIGILATE.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("deptId",deptId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        param.put("visitorId",ShiroKit.getUser().getId());//标识督导列表显示本人
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        param.put("systemId",systemId);
        Page<Map<String,Object>> page = new PageFactory<Map<String,Object>>().defaultPage();
        List<Map<String,Object>> result = teachingPlanService.getVisitorTPList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 参与督导
     */
    @RequestMapping(value = "/khddjh/joinPlan")
    @ResponseBody
    public Object joinJkddjhPlan(@RequestParam Integer teachingPlanId) {
        Integer num=null;
        ShiroUser shiroUser = ShiroKit.getUser();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visitorId",shiroUser.getId());
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            return new ErrorTip(-1,"已经参与过该计划");
        }
        //督导计划参加人数配置
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", PlanType.INVIGILATE.getId()));
        if(cfg!=null&&StringUtils.checkValNotNull(cfg.getCfgValue())){
            num=Integer.parseInt(cfg.getCfgValue());
        }
        param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> visitorList=planVisitorService.queryPlanVisitorList(param);
        if(num==null||(visitorList!=null&&num>visitorList.size())){
            PlanVisitor visitor=new PlanVisitor();
            visitor.setPlanId(teachingPlanId);
            visitor.setVisiteFlag(ConstantFactory.FLAG_Y);
            visitor.setVisitorId(shiroUser.getId());
            visitor.setVisitorName(shiroUser.getName());
            visitor.setCreateTime(DateUtil.getAllTime());
            visitor.setCreateUserId(shiroUser.getId());
            planVisitorService.insert(visitor);
        }else{
            return new ErrorTip(-1,"当前参与人数已满，无法参与");
        }
        return SUCCESS_TIP;
    }
    /**********轮转督导计划*************/
    /**
     * 跳转到计划活动列表页面
     */
    @RequestMapping("/lzddjh")
    public String lzddjhIndex() {
        return PREFIX + "visitor/lzddjh.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/lzddjh/list")
    @Permission
    @ResponseBody
    public Object lzddjhList(@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime, @RequestParam(required = false) Integer deptId,Model model) {
        if(StringUtils.checkValNull(deptId)){
            deptId=ShiroKit.getUser().getDeptId();
        }
        Map<String, Object> param = new HashMap<>();
        param.put("planType", PlanType.CIRCLE.getId());
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("deptId",deptId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        param.put("visitorId",ShiroKit.getUser().getId());//标识督导列表显示本人
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        param.put("systemId",systemId);
        Page<Map<String,Object>> page = new PageFactory<Map<String,Object>>().defaultPage();
        List<Map<String,Object>> result = teachingPlanService.getVisitorTPList(page, param);
        page.setRecords(result);
        return super.packForBT(page);
    }
    /**
     * 参与督导
     */
    @RequestMapping(value = "/lzddjh/joinPlan")
    @ResponseBody
    public Object joinLzddjhPlan(@RequestParam Integer teachingPlanId) {
        Integer num=null;
        ShiroUser shiroUser = ShiroKit.getUser();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visitorId",shiroUser.getId());
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            return new ErrorTip(-1,"已经参与过该计划");
        }
        //督导计划参加人数配置
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", PlanType.CIRCLE.getId()));
        if(cfg!=null&&StringUtils.checkValNotNull(cfg.getCfgValue())){
            num=Integer.parseInt(cfg.getCfgValue());
        }
        param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> visitorList=planVisitorService.queryPlanVisitorList(param);
        if(num==null||(visitorList!=null&&num>visitorList.size())){
            PlanVisitor visitor=new PlanVisitor();
            visitor.setPlanId(teachingPlanId);
            visitor.setVisiteFlag(ConstantFactory.FLAG_Y);
            visitor.setVisitorId(shiroUser.getId());
            visitor.setVisitorName(shiroUser.getName());
            visitor.setCreateTime(DateUtil.getAllTime());
            visitor.setCreateUserId(shiroUser.getId());
            planVisitorService.insert(visitor);
        }else{
            return new ErrorTip(-1,"当前参与人数已满，无法参与");
        }
        return SUCCESS_TIP;
    }
    /**
     * 检查是否参与督导
     */
    @RequestMapping(value = "/checkJoin")
    @ResponseBody
    public Object checkJoin(@RequestParam Integer teachingPlanId) {
        ShiroUser shiroUser = ShiroKit.getUser();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visitorId",shiroUser.getId());
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            return SUCCESS_TIP;
        }
        return new ErrorTip(-1,"未参与过该计划，无法评价");
    }
    /**
     * 跳转到活动评价
     */
    @RequestMapping("/evaluationform/{id}")
    public String evaluationform(@PathVariable Integer id,Model model) {
//        Properties prop = new Properties();
//        String UPLOAD_BASE_URL = "";//文件上传访问根地址
//        try {
//            InputStream inputStream = InterfaceController.class.getClassLoader().getResourceAsStream("pdguns.properties");
//            prop.load(inputStream);
//            UPLOAD_BASE_URL = prop.getProperty("upload_base_url");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        String UPLOAD_BASE_URL = "";//文件上传访问根地址
        Cfg urlCfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_url"));
        if(urlCfg!=null){
            UPLOAD_BASE_URL=urlCfg.getCfgValue();
        }
        List<String> imgList=new ArrayList<>();
        String date="";//督导评价时间
        Map<String, Object> param = new HashMap<>();
        param.put("planId", id);
        param.put("visitorId",ShiroKit.getUser().getId());
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        TeachingPlan teachingPlan =new TeachingPlan();
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            PlanVisitor planVisitor=tempVisitorList.get(0);
            teachingPlan = teachingPlanService.selectById(id);
            model.addAttribute("planVisitor",planVisitor);
            DdEvaluationForm form=new DdEvaluationForm();
            String imgUrlTemp="";
            if(planVisitor!=null&&StringUtils.checkValNotNull(planVisitor.getEvaluateScore())){
                form=planVisitorService.parseExtInfoXml(planVisitor.getEvaluateScore());
                if(form!=null){
                    date=form.getDdsj();
                    if(StringUtils.checkValNotNull(form.getImgUrl1())){
                        imgUrlTemp=form.getImgUrl1().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl2())){
                        imgUrlTemp=form.getImgUrl2().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl3())){
                        imgUrlTemp=form.getImgUrl3().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl4())){
                        imgUrlTemp=form.getImgUrl4().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl5())){
                        imgUrlTemp=form.getImgUrl5().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl6())){
                        imgUrlTemp=form.getImgUrl6().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl7())){
                        imgUrlTemp=form.getImgUrl7().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl8())){
                        imgUrlTemp=form.getImgUrl8().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl9())){
                        imgUrlTemp=form.getImgUrl9().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                }
            }
            model.addAttribute("form",form);
            model.addAttribute("imgList",imgList);
        }
        model.addAttribute("item",teachingPlan);
        model.addAttribute("editFlag","");
        model.addAttribute("UPLOAD_BASE_URL",UPLOAD_BASE_URL);
        model.addAttribute("planId", id);
        model.addAttribute("visitorId",ShiroKit.getUser().getId());
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "ddpjsj"));
        if(cfg!=null){//判断督导时间设置
            if(StringUtils.checkValNotNull(cfg.getCfgValue())&&StringUtils.checkValNotNull(date)){
                int num= Integer.parseInt(cfg.getCfgValue());
                int daySub=(int)DateUtil.getDaySub(date,DateUtil.getDay());
                if(daySub>num){
                    model.addAttribute("editFlag","view");
                }
            }
        }
        if(PlanType.INVIGILATE.getId().equals(teachingPlan.getPlanType())){
            return PREFIX + "evaluationForm/tongyong2.html";
        }
        if(StringUtils.checkValNotNull(teachingPlan.getActivityType())){
            if("xjk".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/xjkForm.html";
            }
            if("jnk".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/jnkForm.html";
            }
            if("gpcf".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/jxcfForm.html";
            }
            if("batl".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/batlForm.html";
            }
            if("zyqkysxxjx".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/lzjxForm.html";
            }
            if("zyqkysxxjn".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/lzjnForm.html";
            }
            if("lcyx".equals(teachingPlan.getActivityType())
                    ||"djt".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/tkpjLcForm.html";
            }
            if("jcyx".equals(teachingPlan.getActivityType())||"jcyx_jbjn".equals(teachingPlan.getActivityType())
                    ||"zyjck".equals(teachingPlan.getActivityType())||"syysj".equals(teachingPlan.getActivityType())
                    ||"zyzsyll".equals(teachingPlan.getActivityType())||"zyjd".equals(teachingPlan.getActivityType())
                    ||"zllc".equals(teachingPlan.getActivityType())||"rwsy".equals(teachingPlan.getActivityType())
                    ){
                return PREFIX + "evaluationForm/tkpjLlForm.html";
                //return PREFIX + "evaluationForm/tkpjKtForm.html";

            }
        }
        return PREFIX + "evaluationForm/tongyong1.html";
    }
    /**
     * 保存评价
     */
    @RequestMapping(value = "/saveFormScore")
    @ResponseBody
    public Object saveFormScore(DdEvaluationForm evaluationForm,String id,String evaluateType) throws Exception{
        PlanVisitor planVisitor = new PlanVisitor();
        if(StringUtils.checkValNotNull(id)){
            planVisitor = planVisitorService.selectById(id);
            evaluationForm.setDdsj(DateUtil.getMmTime());
            String conent=planVisitorService.getXmlFromExtInfo(evaluationForm);
            conent = URLDecoder.decode(conent, "UTF-8");
            if(evaluateType!=null){
                planVisitor.setEvaluateType(evaluateType);
            }
            planVisitor.setEvaluateScore(conent);
            planVisitorService.updateById(planVisitor);
            return SUCCESS_TIP;
        }
        return new ErrorTip(-1,"");
    }
    /**
     * 跳转到活动评价详情
     */
    @RequestMapping("/evaluationInfo/{id}/{planId}")
    public String evaluationInfo(@PathVariable Integer planId,@PathVariable Integer id,Model model) {
        String UPLOAD_BASE_URL = "";//文件上传访问根地址
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_url"));
        if(cfg!=null){
            UPLOAD_BASE_URL=cfg.getCfgValue();
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
            model.addAttribute("planVisitor",planVisitor);
            DdEvaluationForm form=new DdEvaluationForm();
            String imgUrlTemp="";
            if(planVisitor!=null&&StringUtils.checkValNotNull(planVisitor.getEvaluateScore())){
                form=planVisitorService.parseExtInfoXml(planVisitor.getEvaluateScore());
                if(form!=null){
                    if(StringUtils.checkValNotNull(form.getImgUrl1())){
                        imgUrlTemp=form.getImgUrl1().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl2())){
                        imgUrlTemp=form.getImgUrl2().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl3())){
                        imgUrlTemp=form.getImgUrl3().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl4())){
                        imgUrlTemp=form.getImgUrl4().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl5())){
                        imgUrlTemp=form.getImgUrl5().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl6())){
                        imgUrlTemp=form.getImgUrl6().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl7())){
                        imgUrlTemp=form.getImgUrl7().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl8())){
                        imgUrlTemp=form.getImgUrl8().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                    if(StringUtils.checkValNotNull(form.getImgUrl9())){
                        imgUrlTemp=form.getImgUrl9().replaceAll("\\\\","/");
                        imgList.add(imgUrlTemp);
                    }
                }
            }
            model.addAttribute("form",form);
            model.addAttribute("imgList",imgList);
        }
        model.addAttribute("item",teachingPlan);
        model.addAttribute("editFlag","view");
        model.addAttribute("UPLOAD_BASE_URL",UPLOAD_BASE_URL);
        if(PlanType.INVIGILATE.getId().equals(teachingPlan.getPlanType())){
            return PREFIX + "evaluationForm/tongyong2.html";
        }
        if(StringUtils.checkValNotNull(teachingPlan.getActivityType())){
            if("xjk".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/xjkForm.html";
            }
            if("jnk".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/jnkForm.html";
            }
            if("gpcf".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/jxcfForm.html";
            }
            if("batl".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/batlForm.html";
            }
            if("zyqkysxxjx".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/lzjxForm.html";
            }
            if("zyqkysxxjn".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/lzjnForm.html";
            }
            if("lcyx".equals(teachingPlan.getActivityType())
                    ||"djt".equals(teachingPlan.getActivityType())){
                return PREFIX + "evaluationForm/tkpjLcForm.html";
            }
            if("jcyx".equals(teachingPlan.getActivityType())||"jcyx_jbjn".equals(teachingPlan.getActivityType())
                    ||"zyjck".equals(teachingPlan.getActivityType())||"syysj".equals(teachingPlan.getActivityType())
                    ||"zyzsyll".equals(teachingPlan.getActivityType())||"zyjd".equals(teachingPlan.getActivityType())
                    ||"zllc".equals(teachingPlan.getActivityType())||"rwsy".equals(teachingPlan.getActivityType())
                    ){
                return PREFIX + "evaluationForm/tkpjLlForm.html";
            }
        }
        return PREFIX + "evaluationForm/tongyong1.html";
    }
    /*********督导评价查询***********/
    /**
     * 跳转到督导评价列表页面
     */
    @RequestMapping("/ddpjcx")
    public String ddpjcxIndex(Model model) {
        String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
        model.addAttribute("systemId",systemId);
        return PREFIX + "visitor/ddpjcxdd.html";
    }
    /**
     * 获取列表
     */
    @RequestMapping(value = "/ddpjcx/list")
    @Permission
    @ResponseBody
    public Object ddjhcxList(@RequestParam(required = false) String planType,@RequestParam(required = false) String activityType, @RequestParam(required = false) String courseName, @RequestParam(required = false) String teachers, @RequestParam(required = false) String classPlace,@RequestParam(required = false) String visitorName, @RequestParam(required = false) String beginTime, @RequestParam(required = false) String endTime) {
        Map<String, Object> param = new HashMap<>();
        param.put("planType", planType);
        param.put("activityType",activityType);
        param.put("courseName",courseName);
        param.put("teachers",teachers);
        param.put("visitorName",visitorName);
        param.put("classPlace",classPlace);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        param.put("visitorId",ShiroKit.getUser().getId());//标识督导列表显示本人
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
        String UPLOAD_BASE_URL = "";//文件上传访问根地址
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_url"));
        if(cfg!=null){
            UPLOAD_BASE_URL=cfg.getCfgValue();
        }
        TeachingPlan teachingPlan = teachingPlanService.selectById(id);
        model.addAttribute("item",teachingPlan);
        List<String> visitors=new ArrayList<>();
        List<Integer> visitorIds=new ArrayList<>();
        if(teachingPlan!=null){
            List<PlanVisitor> planVisitors=planVisitorService.selectList(new EntityWrapper<PlanVisitor>().eq("plan_id", id));
            if(planVisitors!=null&&planVisitors.size()>0){
                for (PlanVisitor pv:planVisitors) {
                    if(ShiroKit.getUser().getId()!=null&&ShiroKit.getUser().getId().equals(pv.getVisitorId())){
                        visitors.add(pv.getVisitorName());
                        visitorIds.add(pv.getVisitorId());
                    }
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
        model.addAttribute("UPLOAD_BASE_URL",UPLOAD_BASE_URL);
        return PREFIX + "ddpjcx_detail.html";
    }

    @RequestMapping("/ddpjchoice/{id}")
    public String ddpjchoice(@PathVariable Integer id,Model model) {
        model.addAttribute("visitors",id);
        return PREFIX + "evaluationForm/ddpjchoice.html";
    }
}
