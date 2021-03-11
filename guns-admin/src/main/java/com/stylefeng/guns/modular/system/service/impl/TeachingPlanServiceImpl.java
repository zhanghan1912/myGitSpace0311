package com.stylefeng.guns.modular.system.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.common.constant.state.PlanType;
import com.stylefeng.guns.core.common.constant.state.SystemIdEnum;
import com.stylefeng.guns.core.shiro.ShiroKit;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.modular.system.model.TeachingPlan;
import com.stylefeng.guns.modular.system.dao.TeachingPlanMapper;
import com.stylefeng.guns.modular.system.service.ITeachingPlanService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 计划活动表 服务实现类
 * </p>
 *
 * @author suncg
 * @since 2018-07-11
 */
@Service
public class TeachingPlanServiceImpl extends ServiceImpl<TeachingPlanMapper, TeachingPlan> implements ITeachingPlanService {

    @Override
    public List<TeachingPlan> getTeachignPlanList(Page<TeachingPlan> page, Map<String, Object> param) {
        return this.baseMapper.getTeachignPlanList(page, param);
    }

    @Override
    public Map<String, Object> importLljxjhExcel(MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        InputStream is = null;
        try {
            is =  file.getInputStream();
            byte[] fileData = new byte[(int)file.getSize()];
            is.read(fileData);
            Workbook wb = createCommonWorkbook(new ByteInputStream(fileData, (int)file.getSize()));
            return parseExcel(wb);
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return map;
    }
    private Workbook createCommonWorkbook(InputStream inS) throws Exception {
        // 首先判断流是否支持mark和reset方法，最后两个if分支中的方法才能支持
        if (!inS.markSupported()) {
            // 还原流信息
            inS = new PushbackInputStream(inS);
        }
        // EXCEL2003使用的是微软的文件系统
        if (POIFSFileSystem.hasPOIFSHeader(inS)) {
            return new HSSFWorkbook(inS);
        }
        throw new IOException("不能解析的excel版本");
    }
    private Map<String, Object> parseExcel(Workbook wb) {
        int succCount = 0, loseCount = 0;
        Map<String, Object> returnDataMap = new HashMap<>();
        List<Integer> loseList = new ArrayList<>();
        Map<Integer,String> problemsMap = new HashMap<>();
        int sheetNum = wb.getNumberOfSheets();
        if (sheetNum > 0) {
            Sheet sheet = wb.getSheetAt(0);
            //解决空行问题
            int row_num = sheet.getLastRowNum();
            if (row_num < 2) {
                returnDataMap.put("succCount", succCount);
//                throw new RuntimeException("没有数据");
            }
            //获取表头
            Row titleR = sheet.getRow(0);
            //获取表头单元格数
            int cell_num = titleR.getLastCellNum();
            List<String> colnames = new ArrayList<>();
            for (int i = 0; i < cell_num; i++) {
                colnames.add(titleR.getCell(i).getStringCellValue());
            }
            for (int i = 1; i <= row_num; i++) {
                Row r = sheet.getRow(i);
                String p1 = "部门不存在！！";
                String p2 = "教学类型不存在！";
                String p3 = "标红列为必填项！！";
                String p4 = "请填写正确的时间格式！";
                String flag = ConstantFactory.FLAG_Y;
                TeachingPlan tp = new TeachingPlan();
                tp.setId(null);
                tp.setPlanType(PlanType.THEORY.getId());
                tp.setCreateUserId(ShiroKit.getUser().getId());
                tp.setCreateTime(DateUtil.getAllTime());
                for (int j = 0; j < colnames.size(); j++) {
                    String value = "";
                    Cell cell = r.getCell(j);
                    if (null == cell || "" == cell.toString().trim()) {
                        continue;
                    }
                    if (cell.getCellType() == 1) {
                        value = cell.getStringCellValue().trim();
                    } else {
                        value = _doubleTrans(cell.getNumericCellValue()).trim();
                    }
                    if ("部门".equals(colnames.get(j))) {
                        tp.setDeptName(value);
                        tp.setDeptId(ConstantFactory.me().getDeptId(value));
                    }else if ("课程名称".equals(colnames.get(j))) {
                        tp.setCourseName(value);
                    }else if ("教学类型".equals(colnames.get(j))) {
                        tp.setActivityName(value);
                        tp.setActivityType(ConstantFactory.me().getDictCodeByName("theory_type",value));
                    }else if ("任课老师".equals(colnames.get(j))) {
                        tp.setTeachers(value);
                    }else if ("班级名称".equals(colnames.get(j))) {
                        tp.setClassName(value);
                    }else if ("班级构成".equals(colnames.get(j))) {
                        tp.setClassTarget(value);
                    }else if ("上课班号".equals(colnames.get(j))) {
                        tp.setClassNo(value);
                    }else if ("上课人数".equals(colnames.get(j))) {
                        tp.setClassNumber(value);
                    }else if ("节次".equals(colnames.get(j))) {
                        tp.setClassOrder(value);
                    }else if ("时间".equals(colnames.get(j))) {
                        Pattern pattern = Pattern.compile("^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s([0-1][0-9]|2[0-3]):([0-5][0-9])$");
                        Matcher matcher = pattern.matcher(value);
                        if(!matcher.matches()){
                            flag = ConstantFactory.FLAG_N;
                            problemsMap.put(i+1,p4);
                        }else{
                            tp.setClassTime(value);
                        }
                    }else if ("地点".equals(colnames.get(j))) {
                        tp.setClassPlace(value);
                    }else if ("备注".equals(colnames.get(j))) {
                        tp.setMemo(value);
                    }else if ("教师职称".equals(colnames.get(j))) {
                        tp.setTeacherTitle(value);
                    }else if ("章节".equals(colnames.get(j))) {
                        tp.setChapter(value);
                    }else if ("实验内容（项目）".equals(colnames.get(j))) {
                        tp.setChapter(value);
                    }else if ("开课单位".equals(colnames.get(j))) {
                        tp.setCourseUnit(value);
                    }else if ("学时".equals(colnames.get(j))) {
                        tp.setSchoolHours(value);
                    }
                }
                String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
                tp.setSystemId(systemId);
                tp.setSystemName(SystemIdEnum.getNameById(systemId));
                if(ToolUtil.isEmpty(tp.getDeptName()) || ToolUtil.isEmpty(tp.getCourseName()) || ToolUtil.isEmpty(tp.getActivityName())){
                    flag = ConstantFactory.FLAG_N;
                    problemsMap.put(i+1,p3);
                }
                if(tp.getDeptId() == -1){
                    flag = ConstantFactory.FLAG_N;
                    problemsMap.put(i+1,p1);
                }
                if(ToolUtil.isEmpty(tp.getActivityType())){
                    flag = ConstantFactory.FLAG_N;
                    problemsMap.put(i+1,p2);
                }
                if(ConstantFactory.FLAG_Y.equals(flag)){
                    this.insert(tp);
                    succCount ++;
                }
                if (ConstantFactory.FLAG_N.equals(flag)) {
                    loseCount++;
                    loseList.add(i + 1);
                }
            }
            returnDataMap.put("succCount", succCount);
            returnDataMap.put("loseCount", loseCount);
            returnDataMap.put("loseList", loseList);
            returnDataMap.put("problemsMap", problemsMap);
        }
        return returnDataMap;
    }
    private static String _doubleTrans(double d) {
        if ((double) Math.round(d) - d == 0.0D) {
            return String.valueOf((long) d);
        } else {
            return String.valueOf(d);
        }
    }

    @Override
    public List<Map<String,Object>> getVisitorTPList(Page<Map<String,Object>> page, Map<String, Object> param) {
        if(page==null){
            return this.baseMapper.getVisitorTPList(param);
        }
        return this.baseMapper.getVisitorTPList(page, param);
    }
    @Override
    public Map<String, Object> importJxjhExcel(MultipartFile file,String planType) {
        Map<String, Object> map = new HashMap<>();
        InputStream is = null;
        try {
            is =  file.getInputStream();
            byte[] fileData = new byte[(int)file.getSize()];
            is.read(fileData);
            Workbook wb = createCommonWorkbook(new ByteInputStream(fileData, (int)file.getSize()));
            return parseExcel1(wb,planType);
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return map;
    }

    private Map<String, Object> parseExcel1(Workbook wb,String planType) {
        int succCount = 0, loseCount = 0;
        Map<String, Object> returnDataMap = new HashMap<>();
        List<Integer> loseList = new ArrayList<>();
        Map<Integer,String> problemsMap = new HashMap<>();
        int sheetNum = wb.getNumberOfSheets();
        if (sheetNum > 0) {
            Sheet sheet = wb.getSheetAt(0);
            //解决空行问题
            int row_num = sheet.getLastRowNum();
            if (row_num < 2) {
                returnDataMap.put("succCount", succCount);
//                throw new RuntimeException("没有数据");
            }
            //获取表头
            Row titleR = sheet.getRow(0);
            //获取表头单元格数
            int cell_num = titleR.getLastCellNum();
            List<String> colnames = new ArrayList<>();
            for (int i = 0; i < cell_num; i++) {
                colnames.add(titleR.getCell(i).getStringCellValue());
            }
            for (int i = 1; i <= row_num; i++) {
                Row r = sheet.getRow(i);
                String p1 = "部门不存在！！";
                String p2 = "教学类型不存在！";
                String p3 = "标红列为必填项！！";
                String p4 = "请填写正确的时间格式！";
                String flag = ConstantFactory.FLAG_Y;
                TeachingPlan tp = new TeachingPlan();
                tp.setId(null);
                tp.setPlanType(planType);
                tp.setCreateUserId(ShiroKit.getUser().getId());
                tp.setCreateTime(DateUtil.getAllTime());
                for (int j = 0; j < colnames.size(); j++) {
                    String value = "";
                    Cell cell = r.getCell(j);
                    if (null == cell || "" == cell.toString().trim()) {
                        continue;
                    }
                    if (cell.getCellType() == 1) {
                        value = cell.getStringCellValue().trim();
                    } else {
                        value = _doubleTrans(cell.getNumericCellValue()).trim();
                    }
                    if ("部门".equals(colnames.get(j))) {
                        tp.setDeptName(value);
                        tp.setDeptId(ConstantFactory.me().getDeptId(value));
                    }else if ("课程名称".equals(colnames.get(j))||"主题名称".equals(colnames.get(j))) {
                        tp.setCourseName(value);
                    }else if ("教学类型".equals(colnames.get(j))||"考核类型".equals(colnames.get(j))||"活动类型".equals(colnames.get(j))||"轮转类型".equals(colnames.get(j))) {
                        tp.setActivityName(value);
                        String dictType="";
                        if(PlanType.THEORY.getId().equals(planType)){
                            dictType="theory_type";
                        }else if(PlanType.CLINIC.getId().equals(planType)){
                            dictType="clinic_type";
                        }else if(PlanType.INVIGILATE.getId().equals(planType)){
                            dictType="invigilate_type";
                            p2 = "考核类型不存在！";
                        }else if(PlanType.ACTIVITY.getId().equals(planType)){
                            dictType="activity_type";
                            p2 = "活动类型不存在！";
                        }else if(PlanType.CIRCLE.getId().equals(planType)){
                            dictType="circle_type";
                            p2 = "轮转类型不存在！";
                        }
                        tp.setActivityType(ConstantFactory.me().getDictCodeByName(dictType,value));
                    }else if ("任课老师".equals(colnames.get(j))) {
                        tp.setTeachers(value);
                    }else if ("班级名称".equals(colnames.get(j))) {
                        tp.setClassName(value);
                    }else if ("班级构成".equals(colnames.get(j))) {
                        tp.setClassTarget(value);
                    }else if ("上课班号".equals(colnames.get(j))) {
                        tp.setClassNo(value);
                    }else if ("上课人数".equals(colnames.get(j))) {
                        tp.setClassNumber(value);
                    }else if ("节次".equals(colnames.get(j))) {
                        tp.setClassOrder(value);
                    }else if ("时间".equals(colnames.get(j))) {
                        Pattern pattern = Pattern.compile("^((([0-9]{3}[1-9]|[0-9]{2}[1-9][0-9]{1}|[0-9]{1}[1-9][0-9]{2}|[1-9][0-9]{3})-(((0[13578]|1[02])-(0[1-9]|[12][0-9]|3[01]))|((0[469]|11)-(0[1-9]|[12][0-9]|30))|(02-(0[1-9]|[1][0-9]|2[0-8]))))|((([0-9]{2})(0[48]|[2468][048]|[13579][26])|((0[48]|[2468][048]|[3579][26])00))-02-29))\\s([0-1][0-9]|2[0-3]):([0-5][0-9])$");
                        Matcher matcher = pattern.matcher(value);
                        if(!matcher.matches()){
                            flag = ConstantFactory.FLAG_N;
                            problemsMap.put(i+1,p4);
                        }else{
                            tp.setClassTime(value);
                        }
                    }else if ("地点".equals(colnames.get(j))) {
                        tp.setClassPlace(value);
                    }else if ("内容详情".equals(colnames.get(j))) {
                        tp.setDetail(value);
                    }else if ("备注".equals(colnames.get(j))) {
                        tp.setMemo(value);
                    }else if ("教师职称".equals(colnames.get(j))) {
                        tp.setTeacherTitle(value);
                    }else if ("章节".equals(colnames.get(j))) {
                        tp.setChapter(value);
                    }else if ("开课单位".equals(colnames.get(j))) {
                        tp.setCourseUnit(value);
                    }else if ("学时".equals(colnames.get(j))) {
                        tp.setSchoolHours(value);
                    }else if ("查房病种".equals(colnames.get(j))) {
                        tp.setDisease(value);
                    }else if ("床号".equals(colnames.get(j))) {
                        tp.setBedNumber(value);
                    }else if ("住院号".equals(colnames.get(j))) {
                        tp.setInpatientNumber(value);
                    }else if ("病例编号".equals(colnames.get(j))) {
                        tp.setCaseNumber(value);
                    }else if ("记录人".equals(colnames.get(j))) {
                        tp.setNoteTaker(value);
                    }else if ("参与人员".equals(colnames.get(j))) {
                        tp.setParticipants(value);
                    }
                }
                if(PlanType.THEORY.getId().equals(planType) || PlanType.CLINIC.getId().equals(planType)){
                    if(ToolUtil.isEmpty(tp.getDeptName()) || ToolUtil.isEmpty(tp.getCourseName()) || ToolUtil.isEmpty(tp.getActivityName())){
                        flag = ConstantFactory.FLAG_N;
                        problemsMap.put(i+1,p3);
                    }
                }
                if(PlanType.INVIGILATE.getId().equals(planType)||PlanType.ACTIVITY.getId().equals(planType)||PlanType.CIRCLE.getId().equals(planType)){
                    if(ToolUtil.isEmpty(tp.getDeptName()) || ToolUtil.isEmpty(tp.getCourseName()) || ToolUtil.isEmpty(tp.getActivityName())
                            || ToolUtil.isEmpty(tp.getClassTime()) || ToolUtil.isEmpty(tp.getClassPlace()) || ToolUtil.isEmpty(tp.getDetail())){
                        flag = ConstantFactory.FLAG_N;
                        problemsMap.put(i+1,p3);
                    }
                }
                String systemId=(String)ShiroKit.getSession().getAttribute("systemId");
                tp.setSystemId(systemId);
                tp.setSystemName(SystemIdEnum.getNameById(systemId));
                if(tp.getDeptId() == -1){
                    flag = ConstantFactory.FLAG_N;
                    problemsMap.put(i+1,p1);
                }
                if(ToolUtil.isEmpty(tp.getActivityType())){
                    flag = ConstantFactory.FLAG_N;
                    problemsMap.put(i+1,p2);
                }
                if(ConstantFactory.FLAG_Y.equals(flag)){
                    this.insert(tp);
                    succCount ++;
                }
                if (ConstantFactory.FLAG_N.equals(flag)) {
                    loseCount++;
                    loseList.add(i + 1);
                }
            }
            returnDataMap.put("succCount", succCount);
            returnDataMap.put("loseCount", loseCount);
            returnDataMap.put("loseList", loseList);
            returnDataMap.put("problemsMap", problemsMap);
        }
        return returnDataMap;
    }
}
