package com.stylefeng.guns.modular.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.shiro.ShiroKit;
import com.stylefeng.guns.core.shiro.ShiroUser;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.modular.system.form.DdEvaluationForm;
import com.stylefeng.guns.modular.system.model.*;
import com.stylefeng.guns.modular.system.service.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.util.*;

/**
 * 微信端小程序接口 控制器
 * @author
 * @Date 2018-07-11 17:37:12
 */
@Controller
@RequestMapping("/supervisor")
public class InterfaceController extends BaseController {

    @Autowired
    private ITeachingPlanService teachingPlanService;
    @Autowired
    private IDeptService deptService;
    @Autowired
    private IPlanVisitorService planVisitorService;
    @Autowired
    private ICfgService cfgService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IRoleService roleService;
    @Autowired
    private IRelationService relationService;

    private static Logger logger = LoggerFactory.getLogger(InterfaceController.class);

    @ApiOperation("登录接口")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "account", value = "账号", required = true, dataType = "String"),
            @ApiImplicitParam(name = "password", value = "密码", required = true, dataType = "String")
    })
    @ResponseBody
    public Object login(String account, String password) {
        Map<String,String> resultMap=new HashMap<>();
        String code = "200";
        String desc = "登录成功";
        String userId = "";
        String roleName = "";
        try {
            Subject currentUser = ShiroKit.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(account, password.toCharArray());
            token.setRememberMe(false);
            currentUser.login(token);
            List<Integer> roleList = ShiroKit.getUser().getRoleList();
            if (roleList == null || roleList.size() == 0) {
                code = "201";
                desc = "用户未赋权";
            }else{
                Role role=roleService.selectById(roleList.get(0));
                if(role!=null){
                    roleName=role.getTips();
                    List<Relation> relations=relationService.selectList(new EntityWrapper<Relation>().eq("roleid", role.getId()));
                    if(relations!=null&&relations.size()>0){
                        for (Relation re:relations) {//督导角色权限对应小程序首页菜单显示
                            if(re.getMenuid()==175){
                                resultMap.put("menu1",ConstantFactory.FLAG_Y);
                            }
                            if(re.getMenuid()==181){
                                resultMap.put("menu2",ConstantFactory.FLAG_Y);
                            }
                            if(re.getMenuid()==187){
                                resultMap.put("menu3",ConstantFactory.FLAG_Y);
                            }
                            if(re.getMenuid()==197){
                                resultMap.put("menu4",ConstantFactory.FLAG_Y);
                            }
                        }
                    }
                }
            }
            userId=ShiroKit.getUser().getId()+"";
        } catch (AuthenticationException e) {
            code = "202";
            desc = "账号或密码错误";
        }
        resultMap.put("code",code);
        resultMap.put("desc",desc);
        resultMap.put("userId",userId);
        resultMap.put("roleName",roleName);
//        String result = "{\"code\":"+code+",\"desc\":"+desc+",\"userId\":"+userId+",\"roleName\":"+roleName+"}";
        return JSON.toJSON(resultMap);
    }

    @ApiOperation("教学列表接口")
    @RequestMapping(value = "/jxjhList", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "roleName", value = "用户角色Name", required = true, dataType = "String"),
            @ApiImplicitParam(name = "planType", value = "督导类型", required = false, dataType = "String"),
            @ApiImplicitParam(name = "activityType", value = "活动类型", required = false, dataType = "String"),
            @ApiImplicitParam(name = "deptId", value = "部门", required = false, dataType = "String")
    })
    @ResponseBody
    public Object jxjhList(String userId,String roleName,String planType,String activityType, String deptId) {
        User shiroUser=userService.selectById(userId);
        Map<String, Object> resultMap=new HashMap<>();
        if(StringUtils.checkValNull(deptId)&&shiroUser!=null){
            deptId=shiroUser.getDeptid()+"";
        }
        List<Map<String, Object>> deptList = this.deptService.list(null);
        Map<String, Object> param = new HashMap<>();
        param.put("planType", planType);
        param.put("activityType",activityType);
        param.put("deptId",deptId);
//        param.put("timeFlag","new");//查询当前时间及之后数据
        if(StringUtils.checkValNotNull(roleName)&&"visitor".equals(roleName)){
            param.put("visitorId",userId);//标识督导列表显示本人
        }
        List<Map<String,Object>> result = teachingPlanService.getVisitorTPList(null, param);
        resultMap.put("deptList",deptList);
        resultMap.put("result",result);
        return JSON.toJSON(resultMap);
    }

    @ApiOperation("计划详情接口")
    @RequestMapping(value = "/jxjhInfo", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "teachingPlanId", value = "计划ID", required = true, dataType = "String")
    })
    @ResponseBody
    public Object jxjhInfo(String teachingPlanId) {
        Map<String,Object> resultMap=new HashMap<>();
        TeachingPlan teachingPlan=new TeachingPlan();
        List<PlanVisitor> planVisitors=new ArrayList<>();
        if(StringUtils.checkValNotNull(teachingPlanId)){
            teachingPlan=teachingPlanService.selectById(teachingPlanId);
        }
        if(teachingPlan!=null){
            planVisitors=planVisitorService.selectList(new EntityWrapper<PlanVisitor>().eq("plan_id", teachingPlanId));
        }
        resultMap.put("teachingPlan",teachingPlan);
        resultMap.put("planVisitors",planVisitors);
        return JSON.toJSON(resultMap);
    }

    @ApiOperation("参与督导计划")
    @RequestMapping(value = "/joinPlan", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "teachingPlanId", value = "计划ID", required = true, dataType = "String")
    })
    @ResponseBody
    public Object joinPlan(String teachingPlanId,String userId) {
        Map<String,String> resultMap=new HashMap<>();
        String code = "200";
        String desc = "参与成功";
        User shiroUser=userService.selectById(userId);
        TeachingPlan tp=teachingPlanService.selectById(teachingPlanId);
        Integer num=null;
        Map<String, Object> param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visitorId",shiroUser.getId());
        param.put("visiteFlag", ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            code = "201";
            desc = "已经参与过该计划";
            resultMap.put("code",code);
            resultMap.put("desc",desc);
            return JSON.toJSON(resultMap);
        }
        if(tp!=null){
            //督导计划参加人数配置
            Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", tp.getPlanType()));
            if(cfg!=null&&StringUtils.checkValNotNull(cfg.getCfgValue())){
                num=Integer.parseInt(cfg.getCfgValue());
            }
        }
        param = new HashMap<>();
        param.put("planId", teachingPlanId);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> visitorList=planVisitorService.queryPlanVisitorList(param);
        if(num==null||(visitorList!=null&&num>visitorList.size())){
            PlanVisitor visitor=new PlanVisitor();
            visitor.setPlanId(Integer.parseInt(teachingPlanId));
            visitor.setVisiteFlag(ConstantFactory.FLAG_Y);
            visitor.setVisitorId(shiroUser.getId());
            visitor.setVisitorName(shiroUser.getName());
            visitor.setCreateTime(DateUtil.getAllTime());
            visitor.setCreateUserId(shiroUser.getId());
            planVisitorService.insert(visitor);
        }else{
            code = "202";
            desc = "当前参与人数已满，无法参与";
            resultMap.put("code",code);
            resultMap.put("desc",desc);
            return JSON.toJSON(resultMap);
        }
        resultMap.put("code",code);
        resultMap.put("desc",desc);
        return JSON.toJSON(resultMap);
    }

    @ApiOperation("保存督导评价")
    @RequestMapping(value = "/saveFormScore", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "参与督导表id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "evaluateType", value = "理论督导中评价类型", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem1", value = "评分项1", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem2", value = "评分项2", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem3", value = "评分项3", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem4", value = "评分项4", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem5", value = "评分项5", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem6", value = "评分项6", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem7", value = "评分项7", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem8", value = "评分项8", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem9", value = "评分项9", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem10", value = "评分项10", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem11", value = "评分项11", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem12", value = "评分项12", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem13", value = "评分项13", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem14", value = "评分项14", required = false, dataType = "String"),
            @ApiImplicitParam(name = "scoreItem15", value = "评分项15", required = false, dataType = "String"),
            @ApiImplicitParam(name = "grade", value = "等级、是否通过", required = false, dataType = "String"),
            @ApiImplicitParam(name = "comment", value = "评语", required = false, dataType = "String"),
            @ApiImplicitParam(name = "proposal", value = "需要补充的意见和建议", required = false, dataType = "String"),
            @ApiImplicitParam(name = "better", value = "改进意见", required = false, dataType = "String"),
            @ApiImplicitParam(name = "pjbsjyj", value = "评价表设计意见", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl1", value = "照片上传1", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl2", value = "照片上传2", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl3", value = "照片上传3", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl4", value = "照片上传4", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl5", value = "照片上传5", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl6", value = "照片上传6", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl7", value = "照片上传7", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl8", value = "照片上传8", required = false, dataType = "String"),
            @ApiImplicitParam(name = "imgUrl9", value = "照片上传9", required = false, dataType = "String")
        })
    @ResponseBody
    public Object saveFormScore(String id,String evaluateType,String scoreItem1,String scoreItem2,String scoreItem3,String scoreItem4,String scoreItem5,String scoreItem6,String scoreItem7,String scoreItem8,String scoreItem9,String scoreItem10,String scoreItem11
            ,String scoreItem12,String scoreItem13,String scoreItem14,String scoreItem15,String grade,String comment,String proposal,String better,String pjbsjyj
            ,String imgUrl1,String imgUrl2,String imgUrl3,String imgUrl4,String imgUrl5,String imgUrl6,String imgUrl7,String imgUrl8,String imgUrl9) throws Exception{
        DdEvaluationForm evaluationForm=new DdEvaluationForm();
        evaluationForm.setScoreItem1(scoreItem1);
        evaluationForm.setScoreItem2(scoreItem2);
        evaluationForm.setScoreItem3(scoreItem3);
        evaluationForm.setScoreItem4(scoreItem4);
        evaluationForm.setScoreItem5(scoreItem5);
        evaluationForm.setScoreItem6(scoreItem6);
        evaluationForm.setScoreItem7(scoreItem7);
        evaluationForm.setScoreItem8(scoreItem8);
        evaluationForm.setScoreItem9(scoreItem9);
        evaluationForm.setScoreItem10(scoreItem10);
        evaluationForm.setScoreItem11(scoreItem11);
        evaluationForm.setScoreItem12(scoreItem12);
        evaluationForm.setScoreItem13(scoreItem13);
        evaluationForm.setScoreItem14(scoreItem14);
        evaluationForm.setScoreItem15(scoreItem15);
        evaluationForm.setGrade(grade);
        evaluationForm.setComment(comment);
        evaluationForm.setProposal(proposal);
        evaluationForm.setBetter(better);
        evaluationForm.setPjbsjyj(pjbsjyj);
        evaluationForm.setImgUrl1(imgUrl1);
        evaluationForm.setImgUrl2(imgUrl2);
        evaluationForm.setImgUrl3(imgUrl3);
        evaluationForm.setImgUrl4(imgUrl4);
        evaluationForm.setImgUrl5(imgUrl5);
        evaluationForm.setImgUrl6(imgUrl6);
        evaluationForm.setImgUrl7(imgUrl7);
        evaluationForm.setImgUrl8(imgUrl8);
        evaluationForm.setImgUrl9(imgUrl9);
        Map<String,String> resultMap=new HashMap<>();
        String code = "201";
        String desc = "保存失败";
        PlanVisitor planVisitor = new PlanVisitor();
        if(StringUtils.checkValNotNull(id)){
            planVisitor = planVisitorService.selectById(id);
            evaluationForm.setDdsj(DateUtil.getMmTime());
            if (evaluateType!=null){
                planVisitor.setEvaluateType(evaluateType);
            }
            String conent=planVisitorService.getXmlFromExtInfo(evaluationForm);
            conent = URLDecoder.decode(conent, "UTF-8");
            planVisitor.setEvaluateScore(conent);
            planVisitorService.updateById(planVisitor);
            code = "200";
            desc = "保存成功";
            resultMap.put("code",code);
            resultMap.put("desc",desc);
            return JSON.toJSON(resultMap);
        }
        resultMap.put("code",code);
        resultMap.put("desc",desc);
        return JSON.toJSON(resultMap);
    }
    /**
     * 跳转到评价详情
     */

    @ApiOperation("督导评价详情")
    @RequestMapping(value = "/evaluationInfo", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "督导id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "planId", value = "督导计划id", required = true, dataType = "String")
    })
    @ResponseBody
    public Object evaluationInfo(@RequestParam String planId,@RequestParam String id) {
        String UPLOAD_BASE_URL_XCX = "";//文件上传访问根地址（小程序）
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_url_xcx"));
        if(cfg!=null){
            UPLOAD_BASE_URL_XCX=cfg.getCfgValue();
        }
        Map<String, Object> resultMap=new HashMap<>();
        Map<String, Object> param = new HashMap<>();
        param.put("planId", planId);
        param.put("visitorId",id);
        param.put("visiteFlag",ConstantFactory.FLAG_Y);
        List<PlanVisitor> tempVisitorList=planVisitorService.queryPlanVisitorList(param);
        TeachingPlan teachingPlan =new TeachingPlan();
        if(tempVisitorList!=null&&tempVisitorList.size()>0){
            PlanVisitor planVisitor=tempVisitorList.get(0);
            teachingPlan = teachingPlanService.selectById(planId);
            resultMap.put("visitorName",planVisitor.getVisitorName());
            resultMap.put("planVisitorId",planVisitor.getId());
            resultMap.put("evaluateType",planVisitor.getEvaluateType());
            DdEvaluationForm form=new DdEvaluationForm();
            if(planVisitor!=null&&StringUtils.checkValNotNull(planVisitor.getEvaluateScore())){
                form=planVisitorService.parseExtInfoXml(planVisitor.getEvaluateScore());
            }
            resultMap.put("form",form);
        }
        resultMap.put("item",teachingPlan);
        resultMap.put("UPLOAD_BASE_URL_XCX",UPLOAD_BASE_URL_XCX);
        return JSON.toJSON(resultMap);
    }

    @ApiOperation("督导计划分配列表接口")
    @RequestMapping(value = "/jxfpList", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "roleName", value = "用户角色Name", required = true, dataType = "String"),
            @ApiImplicitParam(name = "planType", value = "督导类型", required = false, dataType = "String"),
            @ApiImplicitParam(name = "activityType", value = "活动类型", required = false, dataType = "String"),
            @ApiImplicitParam(name = "visitorName", value = "督导老师", required = false, dataType = "String"),
            @ApiImplicitParam(name = "beginTime", value = "开始时间", required = false, dataType = "String"),
            @ApiImplicitParam(name = "endTime", value = "结束时间", required = false, dataType = "String")
    })
    @ResponseBody
    public Object jxfpList(String userId,String planType,String activityType,String roleName,String visitorName, String beginTime,String endTime) {
        Map<String, Object> param = new HashMap<>();
        param.put("planType", planType);
        param.put("activityType",activityType);
        param.put("visitorName",visitorName);
        param.put("beginTime",beginTime);
        param.put("endTime",endTime);
        if(StringUtils.checkValNotNull(roleName)&&"visitor".equals(roleName)){
            param.put("visitorId",userId);//标识督导列表显示本人
        }
        List<Map<String,Object>> result = planVisitorService.getVisitorPJList(null, param);
        return JSON.toJSON(result);
    }

    @ApiOperation("图片上传接口")
    @RequestMapping(value = "/picture", method = RequestMethod.POST)
    public void uploadPicture(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String UPLOAD_BASE_DIR = "";//文件上传物理路径
        Cfg cfg=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_dir"));
        if(cfg!=null){
            UPLOAD_BASE_DIR=cfg.getCfgValue();
        }
        Map<String, Object> resultMap=new HashMap<>();
        //获取文件需要上传到的路径
        File directory = new File(UPLOAD_BASE_DIR);
        String returnPath = "\\upload\\uploadPicture\\"+DateUtil.getDays()+"\\";//返回路径值
        String realPath = directory.getCanonicalPath()+returnPath;
        // 判断存放上传文件的目录是否存在（不存在则创建）
        File dir = new File(realPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        logger.debug("realPath=" + realPath);
        request.setCharacterEncoding("utf-8"); //设置编码
        JSONArray jsonArray = new JSONArray();
        try {
            StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
            Iterator<String> iterator = req.getFileNames();
            while (iterator.hasNext()) {
                HashMap<String, Object> res = new HashMap<String, Object>();
                MultipartFile file = req.getFile(iterator.next());
                // 获取文件名
                String fileNames = file.getOriginalFilename();
                int split = fileNames.lastIndexOf(".");
                //获取上传文件的后缀
                String extName = fileNames.substring(split + 1, fileNames.length());
                //申明UUID
                String uuid = UUID.randomUUID().toString().replace("-", "");
                //组成新的图片名称
                String newName = uuid + "." + extName;
                String destPath = realPath + newName;
                returnPath = returnPath + newName;
                logger.debug("destPath=" + destPath);
                //真正写到磁盘上
                File file1 = new File(destPath);
                OutputStream out = new FileOutputStream(file1);
                out.write(file.getBytes());
                res.put("url", returnPath);//返回访问路径
                jsonArray.add(res);
                out.close();
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        resultMap.put("jsonArray",jsonArray);
        PrintWriter printWriter = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        printWriter.write(JSON.toJSONString(resultMap));
        printWriter.flush();
    }

}
