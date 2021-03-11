package com.stylefeng.guns.modular.system.controller;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.modular.system.service.INoticeService;
import com.stylefeng.guns.modular.system.service.IPlanVisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 总览信息
 *
 * @author fengshuonan
 * @Date 2017年3月4日23:05:54
 */
@Controller
@RequestMapping("/blackboard")
public class BlackboardController extends BaseController {

    @Autowired
    private INoticeService noticeService;
    @Autowired
    private IPlanVisitorService planVisitorService;

    /**
     * 跳转到黑板
     */
    @RequestMapping("")
    public String blackboard(Model model) {
        //通知展示
//        List<Map<String, Object>> notices = noticeService.list(null);
//        model.addAttribute("noticeList", notices);
        //首页图表信息展示
        List<Map<String, String>> chartsData = planVisitorService.getPlanCount();
        model.addAttribute("chartsData", JSON.toJSON(chartsData));
        Map<String, Object> chartsMap=new HashMap<>();
        List<String> dateList=new ArrayList<>();//日期
        String date1= DateUtil.getAfterDayDate("-6");
        date1=date1.substring(8,10);
        dateList.add(date1);
        String date2= DateUtil.getAfterDayDate("-5");
        date2=date2.substring(8,10);
        dateList.add(date2);
        String date3= DateUtil.getAfterDayDate("-4");
        date3=date3.substring(8,10);
        dateList.add(date3);
        String date4= DateUtil.getAfterDayDate("-3");
        date4=date4.substring(8,10);
        dateList.add(date4);
        String date5= DateUtil.getAfterDayDate("-2");
        date5=date5.substring(8,10);
        dateList.add(date5);
        String date6= DateUtil.getAfterDayDate("-1");
        date6=date6.substring(8,10);
        dateList.add(date6);
        String date7= DateUtil.getTime();
        date7=date7.substring(8,10);
        dateList.add(date7);
        List<Map<String, String>> chartsData4DateList = planVisitorService.getPlanCountByDate();
        if(chartsData4DateList!=null){
            for (Map<String, String> dd:chartsData4DateList) {
                chartsMap.put(dd.get("PLAN_TYPE")+dd.get("DATENUM"),dd.get("COUNTNUM"));
            }
        }
        model.addAttribute("chartsMap", JSON.toJSON(chartsMap));
        model.addAttribute("dateList", JSON.toJSON(dateList));
        return "/blackboard.html";
    }
}
