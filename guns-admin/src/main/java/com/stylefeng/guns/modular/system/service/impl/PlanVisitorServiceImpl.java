package com.stylefeng.guns.modular.system.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.modular.system.form.DdEvaluationForm;
import com.stylefeng.guns.modular.system.model.PlanVisitor;
import com.stylefeng.guns.modular.system.dao.PlanVisitorMapper;
import com.stylefeng.guns.modular.system.service.IPlanVisitorService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 计划活动参与督导表 服务实现类
 * </p>
 *
 * @author stylefeng
 * @since 2018-07-18
 */
@Service
public class PlanVisitorServiceImpl extends ServiceImpl<PlanVisitorMapper, PlanVisitor> implements IPlanVisitorService {
    @Override
    public List<PlanVisitor> queryPlanVisitorList(Map<String, Object> param) {
        return this.baseMapper.queryPlanVisitorList(param);
    }
    //将form对象封装为xml文本
    @Override
    public String getXmlFromExtInfo(DdEvaluationForm form){
        String xmlBody = null;
        if(form!=null){
            Document doc = DocumentHelper.createDocument();
            Element root = doc.addElement("evaluationForm");
            Map<String,String> filedMap = getClassFieldMap(form);
            if(filedMap!=null && filedMap.size()>0){
                for(String key : filedMap.keySet()){
                    Element item = root.addElement(key);
                    item.setText(filedMap.get(key));
                }
            }
            xmlBody=doc.asXML();
        }
        return xmlBody;
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
                        filedMap.put(attrName, StringUtils.defaultString(value));
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return filedMap;
    }

    //将xml文本封装form
    @Override
    public DdEvaluationForm parseExtInfoXml(String extInfoXml) {
        DdEvaluationForm extInfo = null;
        if(StringUtils.isNotBlank(extInfoXml)){
            try{
                extInfo = new DdEvaluationForm();
                Document doc = DocumentHelper.parseText(extInfoXml);
                Element root = doc.getRootElement();
                if(root!=null){
                    List<Element> extInfoAttrEles = root.elements();
                    if(extInfoAttrEles!=null && extInfoAttrEles.size()>0){
                        for(Element attr : extInfoAttrEles){
                            String attrName = attr.getName();
                            String attrValue = attr.getText();
                            setValue(extInfo,attrName,attrValue);
                        }
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return extInfo;
    }
    //为对象自动复制
    private void setValue(Object obj,String attrName,String attrValue) {
        try {
            Class<?> objClass = obj.getClass();
            String firstLetter = attrName.substring(0, 1).toUpperCase();
            String methedName = "set" + firstLetter + attrName.substring(1);
            Method setMethod = objClass.getMethod(methedName, new Class[]{String.class});
            setMethod.invoke(obj, new Object[]{attrValue});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Map<String, Object>> getVisitorPJList(Page<Map<String, Object>> page, Map<String, Object> param) {
        if(page==null){
            return this.baseMapper.getVisitorPJList(param);
        }
        return this.baseMapper.getVisitorPJList(page, param);
    }

    @Override
    public List<Map<String, String>> getPlanCount() {
        return this.baseMapper.getPlanCount();
    }

    @Override
    public List<Map<String, String>> getPlanCountByDate() {
        return this.baseMapper.getPlanCountByDate();
    }
}
