package com.stylefeng.guns.modular.system.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.toolkit.StringUtils;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.common.constant.state.PlanType;
import com.stylefeng.guns.modular.system.form.CfgForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Autowired;
import com.stylefeng.guns.core.log.LogObjectHolder;
import org.springframework.web.bind.annotation.RequestParam;
import com.stylefeng.guns.modular.system.model.Cfg;
import com.stylefeng.guns.modular.system.service.ICfgService;

/**
 * 控制器
 *
 * @author fengshuonan
 * @Date 2018-07-18 11:29:58
 */
@Controller
@RequestMapping("/syscfg")
public class CfgController extends BaseController {

    private String PREFIX = "/system/cfg/";

    @Autowired
    private ICfgService cfgService;

    /**
     * 跳转到首页
     */
    @RequestMapping("")
    public String index(Model model) {
        CfgForm form=new CfgForm();
        Cfg cfg1=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", PlanType.THEORY.getId()));
        Cfg cfg2=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", PlanType.CLINIC.getId()));
        Cfg cfg3=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", PlanType.INVIGILATE.getId()));
        Cfg cfg4=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", PlanType.CIRCLE.getId()));
        Cfg cfg5=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "ddpjsj"));
        Cfg cfg6=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_dir"));
        Cfg cfg7=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_url"));
        Cfg cfg8=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "upload_base_url_xcx"));
        Cfg cfg9=cfgService.selectOne(new EntityWrapper<Cfg>().eq("cfg_code", "download_temp_dir"));
        if(cfg1!=null){
            form.setTheoryCfgId(cfg1.getId());
            form.setTheoryCfgValue(cfg1.getCfgValue());
        }if(cfg2!=null){
            form.setClinicCfgId(cfg2.getId());
            form.setClinicCfgValue(cfg2.getCfgValue());
        }if(cfg3!=null){
            form.setInvigilateCfgId(cfg3.getId());
            form.setInvigilateCfgValue(cfg3.getCfgValue());
        }if(cfg4!=null){
            form.setCircleCfgId(cfg4.getId());
            form.setCircleCfgValue(cfg4.getCfgValue());
        }if(cfg5!=null){
            form.setDdpjsjCfgId(cfg5.getId());
            form.setDdpjsjCfgValue(cfg5.getCfgValue());
        }if(cfg6!=null){
            form.setUploadBaseDirId(cfg6.getId());
            form.setUploadBaseDirValue(cfg6.getCfgValue());
        }if(cfg7!=null){
            form.setUploadBaseUrlId(cfg7.getId());
            form.setUploadBaseUrlValue(cfg7.getCfgValue());
        }if(cfg8!=null){
            form.setUploadBaseUrlXcxId(cfg8.getId());
            form.setUploadBaseUrlXcxValue(cfg8.getCfgValue());
        }if(cfg9!=null){
            form.setDownloadTempDirId(cfg9.getId());
            form.setDownloadTempDirValue(cfg9.getCfgValue());
        }
        model.addAttribute("cfgForm",form);
        return PREFIX + "cfg.html";
    }

    /**
     * 新增/修改
     */
    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save(CfgForm cfgForm) {
        Cfg theoryCfg=new Cfg();//理论教学
        Cfg clinicCfg=new Cfg();//临床教学
        Cfg invigilateCfg=new Cfg();//监考考核
        Cfg circleCfg=new Cfg();//轮转督导
        Cfg ddpjsjCfg=new Cfg();//督导评价时间
        Cfg dirCfg=new Cfg();//文件上传物理路径
        Cfg urlCfg=new Cfg();//文件上传访问根地址
        Cfg urlXcxCfg=new Cfg();//文件上传访问根地址(小程序)
        Cfg downloadTempCfg=new Cfg();//系统存放临时文件地址
        theoryCfg.setId(cfgForm.getTheoryCfgId());
        theoryCfg.setCfgCode(PlanType.THEORY.getId());
        theoryCfg.setCfgValue(cfgForm.getTheoryCfgValue());
        clinicCfg.setId(cfgForm.getClinicCfgId());
        clinicCfg.setCfgCode(PlanType.CLINIC.getId());
        clinicCfg.setCfgValue(cfgForm.getClinicCfgValue());
        invigilateCfg.setId(cfgForm.getInvigilateCfgId());
        invigilateCfg.setCfgCode(PlanType.INVIGILATE.getId());
        invigilateCfg.setCfgValue(cfgForm.getInvigilateCfgValue());
        circleCfg.setId(cfgForm.getCircleCfgId());
        circleCfg.setCfgCode(PlanType.CIRCLE.getId());
        circleCfg.setCfgValue(cfgForm.getCircleCfgValue());
        ddpjsjCfg.setId(cfgForm.getDdpjsjCfgId());
        ddpjsjCfg.setCfgCode("ddpjsj");
        ddpjsjCfg.setCfgValue(cfgForm.getDdpjsjCfgValue());
        dirCfg.setId(cfgForm.getUploadBaseDirId());
        dirCfg.setCfgCode("upload_base_dir");
        dirCfg.setCfgValue(cfgForm.getUploadBaseDirValue());
        urlCfg.setId(cfgForm.getUploadBaseUrlId());
        urlCfg.setCfgCode("upload_base_url");
        urlCfg.setCfgValue(cfgForm.getUploadBaseUrlValue());
        urlXcxCfg.setId(cfgForm.getUploadBaseUrlXcxId());
        urlXcxCfg.setCfgCode("upload_base_url_xcx");
        urlXcxCfg.setCfgValue(cfgForm.getUploadBaseUrlXcxValue());
        downloadTempCfg.setId(cfgForm.getDownloadTempDirId());
        downloadTempCfg.setCfgCode("download_temp_dir");
        downloadTempCfg.setCfgValue(cfgForm.getDownloadTempDirValue());

        if(StringUtils.checkValNotNull(theoryCfg.getId())){
            cfgService.updateAllColumnById(theoryCfg);
        }else{
            cfgService.insert(theoryCfg);
        }
        if(StringUtils.checkValNotNull(clinicCfg.getId())){
            cfgService.updateAllColumnById(clinicCfg);
        }else{
            cfgService.insert(clinicCfg);
        }
        if(StringUtils.checkValNotNull(invigilateCfg.getId())){
            cfgService.updateAllColumnById(invigilateCfg);
        }else{
            cfgService.insert(invigilateCfg);
        }
        if(StringUtils.checkValNotNull(circleCfg.getId())){
            cfgService.updateAllColumnById(circleCfg);
        }else{
            cfgService.insert(circleCfg);
        }
        if(StringUtils.checkValNotNull(ddpjsjCfg.getId())){
            cfgService.updateAllColumnById(ddpjsjCfg);
        }else{
            cfgService.insert(ddpjsjCfg);
        }
        if(StringUtils.checkValNotNull(dirCfg.getId())){
            cfgService.updateAllColumnById(dirCfg);
        }else{
            cfgService.insert(dirCfg);
        }
        if(StringUtils.checkValNotNull(urlCfg.getId())){
            cfgService.updateAllColumnById(urlCfg);
        }else{
            cfgService.insert(urlCfg);
        }
        if(StringUtils.checkValNotNull(urlXcxCfg.getId())){
            cfgService.updateAllColumnById(urlXcxCfg);
        }else{
            cfgService.insert(urlXcxCfg);
        }
        if(StringUtils.checkValNotNull(downloadTempCfg.getId())){
            cfgService.updateAllColumnById(downloadTempCfg);
        }else{
            cfgService.insert(downloadTempCfg);
        }
        return SUCCESS_TIP;
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Object update(Cfg cfg) {
        cfgService.updateById(cfg);
        return SUCCESS_TIP;
    }
}
