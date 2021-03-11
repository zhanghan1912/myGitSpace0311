package com.stylefeng.guns.core.util;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ExcleUtil {

	 public static <T> void exportSimpleExcle(String[] titles , List<T> dataList , Class<T> cless , OutputStream os) throws Exception{
		 HSSFWorkbook wb = new HSSFWorkbook();
	     // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
	     HSSFSheet sheet = wb.createSheet("sheet1");
	     // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
	     HSSFRow row = sheet.createRow((int) 0);
	     // 第四步，创建单元格，并设置值表头 设置表头居中  
	     HSSFCellStyle style = wb.createCellStyle();
	     style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
	  
	     List<String> paramIds = new ArrayList<String>();
	     
	     HSSFCell cell = null;
	     for(int i = 0 ; i<titles.length ; i++){
	    	 String[] title = titles[i].split(":");
	    	 cell = row.createCell(i);
	    	 cell.setCellValue(title[1]);  
		     cell.setCellStyle(style);
		     paramIds.add(title[0]);
	     }
	     if(dataList!=null){
	    	 for(int i=0; i<dataList.size() ; i++){
				 T item = dataList.get(i);
				 row = sheet.createRow(i + 1);
				 Object result = null;
				 for(int j = 0 ; j <paramIds.size();j++){
					 String paramId = paramIds.get(j);
					 String firstStr = paramId.substring(0, 1).toUpperCase();
					 String secondStr = paramId.substring(1);
					 Method mt = cless.getMethod("get"+firstStr+secondStr);
					 result = mt.invoke(item);
					 if(result!=null){
						 row.createCell(j).setCellValue(result.toString());	 
					 }else{
						 row.createCell(j).setCellValue("");
					 }
					   
				 }
				 
			 } 
	     }
         wb.write(os);  
	 }

	 public static void exportSimpleExcleByObjs(String[] titles,List dataList,OutputStream os) throws Exception{
		 HSSFWorkbook wb = new HSSFWorkbook();
	     // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
	     HSSFSheet sheet = wb.createSheet("sheet1");
	     // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
	     HSSFRow row = sheet.createRow((int) 0);
	     // 第四步，创建单元格，并设置值表头 设置表头居中  
	     HSSFCellStyle style = wb.createCellStyle();

		 //添加设置列为文本格式
		 HSSFDataFormat format = wb.createDataFormat();
		 style.setDataFormat(format.getFormat("@"));
         // 创建一个居中格式
	     style.setAlignment(HorizontalAlignment.CENTER);

		 Font font = wb.createFont();
		 font.setFontHeightInPoints((short) 12);
		 font.setBold(true);
		 style.setFont(font);

	     List<String> paramIds = new ArrayList<String>();
	     
	     HSSFCell cell = null;
	     for(int i = 0 ; i<titles.length ; i++){
	    	 String[] title = titles[i].split(":");
	    	 cell = row.createCell(i);
	    	 cell.setCellValue(title[1]);  
		     cell.setCellStyle(style);
			 cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		     paramIds.add(title[0]);
		     int length = title[1].length();
			 sheet.setColumnWidth(i, length*800);
	     }
	     if(dataList!=null){
	    	 for(int i=0; i<dataList.size() ; i++){
				 Object item = dataList.get(i);
				 row = sheet.createRow(i + 1);
				 Object result = null;
				 for(int j = 0 ; j <paramIds.size();j++){
					 String paramId = paramIds.get(j);
					 result = getValueByAttrs(paramId,item);
					 Boolean isNum = false;//data是否为数值型
					 Boolean isInteger=false;//data是否为整数
					 Boolean isPercent=false;//data是否为百分数
					 if (result != null || !"".equals(result)) {
						 //判断data是否为数值型
						 isNum = result.toString().matches("^(-?\\d+)(\\.\\d+)?$");
						 //判断data是否为整数（小数部分是否为0）
						 isInteger=result.toString().matches("^[-\\+]?[\\d]*$");
						 //判断data是否为百分数（是否包含“%”）
						 isPercent=result.toString().contains("%");
					 }
					 //如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
					 if (isNum && !isPercent) {
//						 HSSFCellStyle contextstyle =wb.createCellStyle();
//						 HSSFDataFormat df = wb.createDataFormat(); // 此处设置数据格式
						 /*if (isInteger) {
							 contextstyle.setDataFormat(df.getBuiltinFormat("#,#0"));//数据格式只显示整数
						 }else{
							 contextstyle.setDataFormat(df.getBuiltinFormat("#,##0.00"));//保留两位小数点
						 }*/
						 // 设置单元格格式
//						 contentCell.setCellStyle(contextstyle);
						 // 设置单元格内容为double类型
//						 contentCell.setCellValue();
						 if(result.toString().length() < 15) {
							 row.createCell(j).setCellValue(Double.parseDouble(result.toString()));
						 }else{
							 row.createCell(j).setCellValue(result.toString().length()>32767?result.toString().substring(0,32767):result.toString());
						 }
					 } else {
//						 // 设置单元格内容为字符型
						 row.createCell(j).setCellValue(result.toString());
//						 row.createCell(j).setCellValue(result.toString().length()>32767?result.toString().substring(0,32767):result.toString());
					 }

				 }
				 
			 } 
	     }
         wb.write(os);
	 }

	//column为设置文本格式数组（处理首字母为0的数字字符串）
	public static void exportSimpleExcleByObjs(String[] titles,List dataList,OutputStream os,String [] column) throws Exception{
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet1");
		HSSFRow row = sheet.createRow((int) 0);
		HSSFCellStyle style = wb.createCellStyle();
		//添加设置列为文本格式
		HSSFDataFormat format = wb.createDataFormat();
		style.setDataFormat(format.getFormat("@"));
		// 创建一个居中格式
		style.setAlignment(HorizontalAlignment.CENTER);
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		style.setFont(font);
		List<String> paramIds = new ArrayList<String>();
		HSSFCell cell = null;
		for(int i = 0 ; i<titles.length ; i++){
			String[] title = titles[i].split(":");
			cell = row.createCell(i);
			cell.setCellValue(title[1]);
			cell.setCellStyle(style);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			paramIds.add(title[0]);
			int length = title[1].length();
			sheet.setColumnWidth(i, length*800);
		}
		if(dataList!=null){
			for(int i=0; i<dataList.size() ; i++){
				Object item = dataList.get(i);
				row = sheet.createRow(i + 1);
				Object result = null;
				for(int j = 0 ; j <paramIds.size();j++){
					String paramId = paramIds.get(j);
					result = getValueByAttrs(paramId,item);
					Boolean isNum = false;//data是否为数值型
					Boolean isInteger=false;//data是否为整数
					Boolean isPercent=false;//data是否为百分数
					if (result != null || !"".equals(result)) {
						//判断data是否为数值型
						isNum = result.toString().matches("^(-?\\d+)(\\.\\d+)?$");
						//判断data是否为整数（小数部分是否为0）
						isInteger=result.toString().matches("^[-\\+]?[\\d]*$");
						//判断data是否为百分数（是否包含“%”）
						isPercent=result.toString().contains("%");
					}
					//如果单元格内容是数值类型，涉及到金钱（金额、本、利），则设置cell的类型为数值型，设置data的类型为数值类型
					if (isNum && !isPercent) {
						if(result.toString().length() < 15) {
							if(null != column && Arrays.asList(column).contains(j+"")){
								row.createCell(j).setCellValue(result.toString());
							}else{
								row.createCell(j).setCellValue(Double.parseDouble(result.toString()));
							}
						}else{
							row.createCell(j).setCellValue(result.toString().length()>32767?result.toString().substring(0,32767):result.toString());
						}
					} else {
						// 设置单元格内容为字符型
						 row.createCell(j).setCellValue(result.toString().length()>32767?result.toString().substring(0,32767):result.toString());
					}
				}
			}
		}
		wb.write(os);
	}

	public static void exportSimpleExcleByObjsAllString(String[] titles,List dataList,OutputStream os) throws Exception{
		HSSFWorkbook wb = new HSSFWorkbook();
		// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
		HSSFSheet sheet = wb.createSheet("sheet1");
		// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
		HSSFRow row = sheet.createRow((int) 0);
		// 第四步，创建单元格，并设置值表头 设置表头居中
		HSSFCellStyle style = wb.createCellStyle();

		//添加设置列为文本格式
		HSSFDataFormat format = wb.createDataFormat();
		style.setDataFormat(format.getFormat("@"));
		// 创建一个居中格式
		style.setAlignment(HorizontalAlignment.CENTER);

		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 12);
		font.setBold(true);
		style.setFont(font);


		List<String> paramIds = new ArrayList<String>();

		HSSFCell cell = null;
		for(int i = 0 ; i<titles.length ; i++){
			String[] title = titles[i].split(":");
			cell = row.createCell(i);
			cell.setCellValue(title[1]);
			cell.setCellStyle(style);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			paramIds.add(title[0]);
			int length = title[1].length();
			sheet.setColumnWidth(i, length*800);
		}
		if(dataList!=null){
			for(int i=0; i<dataList.size() ; i++){
				Object item = dataList.get(i);
				row = sheet.createRow(i + 1);
				Object result = null;
				for(int j = 0 ; j <paramIds.size();j++){
					String paramId = paramIds.get(j);
					result = getValueByAttrs(paramId,item);
					// 设置单元格内容为字符型
					row.createCell(j).setCellValue(result.toString());
				}
			}
		}
		wb.write(os);
	}


    public static void exportSimpleExcleByObjsAndCustom(String[] titles,List dataList,OutputStream os) throws Exception{
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("sheet1");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        // 创建一个居中格式
        style.setAlignment(HorizontalAlignment.CENTER);

        Font font = wb.createFont();
        font.setFontHeightInPoints((short) 12);
        font.setBold(true);
        style.setFont(font);

        List<String> paramIds = new ArrayList<String>();

        HSSFCell cell = null;
        for(int i = 0 ; i<titles.length ; i++){
            String[] title = titles[i].split(":");
            cell = row.createCell(i);
            cell.setCellValue(title[1]);
            cell.setCellStyle(style);
            paramIds.add(title[0]);
            int length = title[1].length();
            sheet.setColumnWidth(i, length*800);
        }
        if(dataList!=null){
            for(int i=0; i<dataList.size() ; i++){
                Object item = dataList.get(i);
                row = sheet.createRow(i + 1);
                Object result = null;
                for(int j = 0 ; j <paramIds.size();j++){
                    String paramId = paramIds.get(j);
                    result = getValueByAttrs(paramId,item);
                    row.createCell(j).setCellValue(result.toString().length()>32767?result.toString().substring(0,32767):result.toString());
                }

            }
        }

        HSSFRow row2 = sheet.createRow(dataList.size()+3);
        HSSFCell cell2 = null;
        cell2 = row2.createCell(3);
        cell2.setCellValue("*说明：考核项目内容按照各专业实际考核内容填写。");
        cell2.setCellStyle(style);

        HSSFRow row3 = sheet.createRow(dataList.size()+5);
        HSSFCell cell3 = null;
        cell3 = row3.createCell(5);
        cell3.setCellValue("规培秘书签名：");
        cell3 = row3.createCell(8);
        cell3.setCellValue("考核小组签名：");
        wb.write(os);
    }


    public static void setColumnWidth(int length, int key, Map<Integer, Integer> columnWidth) {
		if(columnWidth.containsKey(key)) {
			Integer ol = columnWidth.get(key);
			if(ol<length){
                columnWidth.put(key,length);
            }
		}else{
			columnWidth.put(key,length);
		}
	}
	 public static void exportSimpleExcleByObjsWithWitdh(String[] titles,List dataList,OutputStream os) throws Exception{
		 HSSFWorkbook wb = new HSSFWorkbook();
	     // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
	     HSSFSheet sheet = wb.createSheet("sheet1");
	     // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
	     HSSFRow row = sheet.createRow((int) 0);
	     // 第四步，创建单元格，并设置值表头 设置表头居中
	     HSSFCellStyle style = wb.createCellStyle();
	     style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

		 Map<Integer, Integer> columnWidth = new HashMap<>();
	     List<String> paramIds = new ArrayList<String>();

	     HSSFCell cell = null;
	     for(int i = 0 ; i<titles.length ; i++){
	    	 String[] title = titles[i].split(":");
	    	 cell = row.createCell(i);
	    	 cell.setCellValue(title[1]);
		     cell.setCellStyle(style);
		     paramIds.add(title[0]);
		     int length = title[1].length();
			 sheet.setColumnWidth(i, length*800);
			 setColumnWidth(title[1].toString().getBytes().length, i, columnWidth);
	     }
	     if(dataList!=null){
	    	 for(int i=0; i<dataList.size() ; i++){
				 Object item = dataList.get(i);
				 row = sheet.createRow(i + 1);
				 Object result = null;
				 for(int j = 0 ; j <paramIds.size();j++){
					 String paramId = paramIds.get(j);
					 result = getValueByAttrs(paramId,item);
					 row.createCell(j).setCellValue(result.toString().length()>32767?result.toString().substring(0,32767):result.toString());

					 setColumnWidth(result.toString().getBytes().length, j, columnWidth);
				 }

			 }
	     }
		 Set<Integer> keys = columnWidth.keySet();
		 for (Integer key : keys) {
			 int width = columnWidth.get(key);
			 if(width * 2 * 256>155*256)
			 {
				 width=155*256;
			 }else{
				 width=width * 2 * 256;
			 }
			 sheet.setColumnWidth(key, width);
		 }
         wb.write(os);
	 }


	 
	 public static void exportSimpleExcleWithHeadlineByObjs(String[] headLines, String[] titles,List dataList,File file) throws Exception{
		 HSSFWorkbook wb = new HSSFWorkbook();
		 // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
		 HSSFSheet sheet = wb.createSheet("sheet1");
		 // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
		 //HSSFRow row = sheet.createRow((int) 0);  
		 // 第四步，创建单元格，并设置值表头 设置表头居中  
		 HSSFCellStyle style = wb.createCellStyle();
		 style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		 //
		 int headLength = headLines.length;
		 if(headLines!=null && headLength > 0){
			 for(int i=0; i<headLength ; i++){
				 HSSFRow row = sheet.createRow(i);
				 HSSFCell hSSFCell0 = row.createCell(0);
				 hSSFCell0.setCellValue(headLines[i]);
				 //sheet.addMergedRegion(new Region(1,(short)1,1,(short)2));
				 //sheet.mergeCells(0, 0, 3, 0);  
				 sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 0, (short)titles.length-1));
			 } 
		 }
		 
		 List<String> paramIds = new ArrayList<String>();
		 HSSFRow row = sheet.createRow((int) headLength);
		 HSSFCell cell = null;
		 for(int i = 0 ; i<titles.length ; i++){
			 String[] title = titles[i].split(":");
			 cell = row.createCell(i);
			 cell.setCellValue(title[1]);  
			 cell.setCellStyle(style);
			 paramIds.add(title[0]);
			 int length = title[1].length();
			 sheet.setColumnWidth(i, length*700);
		 }
		 
		if(dataList!=null){
			for(int i=0; i<dataList.size() ; i++){
				 Object item = dataList.get(i);
				 row = sheet.createRow(headLength + 1 + i);
				 Object result = null;
				 for(int j = 0 ; j <paramIds.size();j++){
					 String paramId = paramIds.get(j);
					 if(StringUtils.isBlank(paramId)){//序号
						 result = i+1;
					 }else{
						 result = getValueByAttrs(paramId,item);
					 }
					 row.createCell(j).setCellValue(result.toString());	 
				 }
				 
			 } 
		 }
		 FileOutputStream os = new FileOutputStream(file);
		 wb.write(os);  
		 os.close();
	 }
	 
	 public static void exportSimpleExcleWithHeadlineByObjs(String[] headLines, String[] titles,List dataList,OutputStream os) throws Exception{
		 HSSFWorkbook wb = new HSSFWorkbook();
		 // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
		 HSSFSheet sheet = wb.createSheet("sheet1");
		 // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
		 //HSSFRow row = sheet.createRow((int) 0);  
		 // 第四步，创建单元格，并设置值表头 设置表头居中  
		 HSSFCellStyle style = wb.createCellStyle();
		 style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		 //
		 int headLength = headLines.length;
		 if(headLines!=null && headLength > 0){
			 for(int i=0; i<headLength ; i++){
				 HSSFRow row = sheet.createRow(i);
				 HSSFCell hSSFCell0 = row.createCell(0);
				 hSSFCell0.setCellValue(headLines[i]);
				 //sheet.addMergedRegion(new Region(1,(short)1,1,(short)2));
				 //sheet.mergeCells(0, 0, 3, 0);  
				 sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 0, (short)titles.length-1));
			 } 
		 }
		 
		 List<String> paramIds = new ArrayList<String>();
		 HSSFRow row = sheet.createRow((int) headLength);
		 HSSFCell cell = null;
		 for(int i = 0 ; i<titles.length ; i++){
			 String[] title = titles[i].split(":");
			 cell = row.createCell(i);
			 cell.setCellValue(title[1]);  
			 cell.setCellStyle(style);
			 paramIds.add(title[0]);
			 int length = title[1].length();
			 sheet.setColumnWidth(i, length*700);
		 }
		 
		if(dataList!=null){
			for(int i=0; i<dataList.size() ; i++){
				 Object item = dataList.get(i);
				 row = sheet.createRow(headLength + 1 + i);
				 Object result = null;
				 for(int j = 0 ; j <paramIds.size();j++){
					 String paramId = paramIds.get(j);
					 if(StringUtils.isBlank(paramId)){//序号
						 result = i+1;
					 }else{
						 result = getValueByAttrs(paramId,item);
					 }
					 row.createCell(j).setCellValue(result.toString());	 
				 }
				 
			 } 
		 }
		 wb.write(os);
	 }
	 
	 public static void exportSimpleExcleWithHeadlin(String[] headLines, String[] titles,List dataList,OutputStream os) throws Exception{
		 HSSFWorkbook wb = new HSSFWorkbook();
		 // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet  
		 HSSFSheet sheet = wb.createSheet("sheet1");
		 // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short  
		 //HSSFRow row = sheet.createRow((int) 0);  
		 // 第四步，创建单元格，并设置值表头 设置表头居中  
		 HSSFCellStyle style = wb.createCellStyle();
		 style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		 HSSFCellStyle styleTwo = wb.createCellStyle();
		 styleTwo.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		 HSSFCellStyle styleThree = wb.createCellStyle();
		 styleThree.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
		 HSSFFont font =wb.createFont();
		 font.setFontHeightInPoints((short)17);
		 HSSFFont fontTwo =wb.createFont();
		 fontTwo.setFontHeightInPoints((short)12);
		 //
		 int headLength = headLines.length;
		 if(headLines!=null && headLength > 0){
			 for(int i=0; i<headLength ; i++){
				 HSSFRow row = sheet.createRow(i);
				 HSSFCell hSSFCell0 = row.createCell(0);
				 hSSFCell0.setCellValue(headLines[i]);
				 if(i==0){
					 styleTwo.setFont(font);
					 hSSFCell0.setCellStyle(styleTwo);
				 }
				 if(i==1){
					 styleThree.setFont(fontTwo);
					 hSSFCell0.setCellStyle(styleThree);
				 }
				 sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 0, (short)titles.length-1));
			 } 
		 }
		 
		 List<String> paramIds = new ArrayList<String>();
		 HSSFRow row = sheet.createRow((int) headLength);
		 HSSFCell cell = null;
		 for(int i = 0 ; i<titles.length ; i++){
			 String[] title = titles[i].split(":");
			 cell = row.createCell(i);
			 cell.setCellValue(title[1]);  
			 cell.setCellStyle(style);
			 paramIds.add(title[0]);
			 int length = title[1].length();
			 sheet.setColumnWidth(i, length*700);
		 }
		 
		 if(dataList!=null){
			 HSSFCell rowCell = null;
			 for(int i=0; i<dataList.size() ; i++){
				 Object item = dataList.get(i);
				 row = sheet.createRow(headLength + 1 + i);
				 Object result = null;
				 for(int j = 0 ; j <paramIds.size();j++){
					 String paramId = paramIds.get(j);
					 if(StringUtils.isBlank(paramId)){//序号
						 result = i+1;
					 }else{
						 result = getValueByAttrs(paramId,item);
					 }
					 rowCell=row.createCell(j);
					 rowCell.setCellStyle(style);
					 rowCell.setCellValue(result.toString());
				 }
				 
			 } 
		 }
		 wb.write(os);
	 }
	 
	 private static Object getValueByAttrs(String attrNames,Object obj) throws Exception{
		 Object value = "";
		 if(StringUtils.isNotBlank(attrNames)){
			 String proptyName = "";
			 int pIndex = attrNames.indexOf(".");
			 
			 if(pIndex>=0){
				 proptyName = attrNames.substring(0,pIndex);
			 }else{
				 proptyName = attrNames;
			 }
			 
			 if(StringUtils.isNotBlank(proptyName) && obj!=null){
				 Class clazz = obj.getClass();
				 String firstStr = proptyName.substring(0, 1).toUpperCase();
				 String secondStr = proptyName.substring(1);
                 Method mt;
                 Object result;
                 if(proptyName.length() >= 4 && "get(".equals(proptyName.substring(0, 4))){
                     int index = Integer.parseInt(proptyName.split("\\(")[1].split("\\)")[0]);
                     result = null;
                     if(((List) obj).size()>index){
                         result = ((List) obj).get(index);
                     }
                 }else if(obj instanceof Map){
                     Map map = (Map) obj;
                     result=((Map) obj).get(proptyName);
                 } else{
                     mt = clazz.getMethod("get"+firstStr+secondStr);
                     result = mt.invoke(obj);
                 }

				 if(result!=null){
					 String stringClassName = String.class.getSimpleName();
					 String inegerClassName = Integer.class.getSimpleName();
                     String bigDecimalClassName = BigDecimal.class.getSimpleName();
					 String valueClassName = result.getClass().getSimpleName();
					 if(stringClassName.equals(valueClassName)||bigDecimalClassName.equals(valueClassName)||inegerClassName.equals(valueClassName)){
						 value = result;
					 }else{
						 String surplusName = attrNames.substring(pIndex+1);
						 value = getValueByAttrs(surplusName,result);
					 }
				 }
			 }
		 }
		 return value;
	 }

    /**
     * 无锡科研评审结果导出
     * @param titles
     * @param titles2
     * @param dataList
     * @param os
     * @throws Exception
     */
    public static void exportExcleExpertProj(String[] titles,String[] titles2,List dataList,OutputStream os) throws Exception{
        HSSFWorkbook wb = new HSSFWorkbook();
        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet("sheet1");
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow((int) 0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

        List<String> paramIds = new ArrayList<String>();

        HSSFCell cell = null;

        for(int i = 0 ; i<titles.length ; i++){
           // String[] title = titles2[i].split(":");
            cell = row.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(style);
           // paramIds.add(titles[0]);
            int length = 1;
            if(titles[i] != null){
                length = titles[i].length();
                sheet.setColumnWidth(i, length*1000);
            }
        }
        row = sheet.createRow(1);
        for(int i = 0 ; i<titles2.length ; i++){
            String[] title = titles2[i].split(":");
            cell = row.createCell(i);
            cell.setCellStyle(style);
            paramIds.add(i,title[0]);
            if(title.length > 1){
                cell.setCellValue(title[1]);
                int length = title[1].length();
                sheet.setColumnWidth(i, length*1000);
            }
        }
        if(dataList!=null){
            for(int i=0; i<dataList.size() ; i++){
                Object item = dataList.get(i);
                row = sheet.createRow(i + 2);
                Object result = null;
                for(int j = 0 ; j <paramIds.size();j++){
                    String paramId = paramIds.get(j);
                    result = getValueByAttrs(paramId,item);
                    row.createCell(j).setCellValue(result.toString());
                }
            }
        }
        wb.write(os);
    }


	/**
	 * 导出多个Excel文件并打包的方法
	 * @param titles 表头
	 * @param dataListList 结果集
	 * @param out 输出流
	 * @throws Exception
	 */
	public static  <T> void exportExcel(String fileName,List<String> names, String[] titles, List<List<T>> dataListList, OutputStream out, HttpServletRequest request) throws Exception{

		File zip = new File(request.getRealPath("/files") + "/" + fileName + ".zip");// 压缩文件


		List<String> fileNames = new ArrayList();// 用于存放生成的文件名称s
		//文件流用于转存文件

		for (int n = 0; n < dataListList.size(); n++) {
			List<T> dataList=dataListList.get(n);
			// 声明一个工作薄
			//Workbook workbook = new HSSFWorkbook();
			// 生成一个表格
			//HSSFSheet sheet = (HSSFSheet) workbook.createSheet("sheet1");
			HSSFWorkbook wb = new HSSFWorkbook();
			// 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
			HSSFSheet sheet = wb.createSheet("sheet1");
			// 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
			HSSFRow row = sheet.createRow((int) 0);
			// 第四步，创建单元格，并设置值表头 设置表头居中
			HSSFCellStyle style = wb.createCellStyle();// 生成一个样式
			style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
			// 指定当单元格内容显示不下时自动换行
			style.setWrapText(true);
			List<String> paramIds = new ArrayList<String>();
			HSSFCell cell = null;
			for(int i = 0 ; i<titles.length ; i++){
				String[] title = titles[i].split(":");
				cell = row.createCell(i);
				cell.setCellValue(title[1]);
				cell.setCellStyle(style);
				paramIds.add(title[0]);
				int length = title[1].length();
				sheet.setColumnWidth(i, length*700);
			}
			if(dataList!=null){
				for(int i=0; i<dataList.size() ; i++){
					Object item = dataList.get(i);
					row = sheet.createRow(i + 1);
					Object result = null;
					for(int j = 0 ; j <paramIds.size();j++){
						String paramId = paramIds.get(j);
						result = getValueByAttrs(paramId,item);
						row.createCell(j).setCellValue(result.toString());
					}

				}
			}
			String file = request.getRealPath("/files") + "/" + names.get(n)+ ".xls";
			File fileTemp = new File(request.getRealPath("/files"));
			if(!fileTemp.exists()){
				fileTemp.mkdir();
			}
			fileNames.add(file);
			FileOutputStream o = new FileOutputStream(file);
			wb.write(o);
			o.close();
		}

		File srcfile[] = new File[fileNames.size()];
		for (int i = 0, n1 = fileNames.size(); i < n1; i++) {
			srcfile[i] = new File(fileNames.get(i));
		}
		ZipFiles(srcfile, zip);//压缩文件
		FileInputStream inStream = new FileInputStream(zip);
		byte[] buf = new byte[4096];
		int readLength;
		while (((readLength = inStream.read(buf)) != -1)) {
			out.write(buf, 0, readLength);
		}
		inStream.close();
		zip.delete();//删除服务器上压缩文件
	}
	//压缩文件
	public static void ZipFiles(File[] srcfile, File zipfile) {
		byte[] buf = new byte[1024];
		ZipOutputStream out=null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipfile));
			for (int i = 0; i < srcfile.length; i++) {
				try {
					FileInputStream in = new FileInputStream(srcfile[i]);
					out.putNextEntry(new ZipEntry(srcfile[i].getName()));
					int len;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					out.closeEntry();
					in.close();
					srcfile[i].delete();//删除服务器上文件
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(out!=null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

    /**
     * 创建一个sheet页
     */
    public static void createSimpleSheetWithHeadline(String[] headLines, String[] titles, List dataList, HSSFWorkbook wb, String sheetName) throws Exception{
        HSSFSheet sheet = wb.createSheet(sheetName);
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        HSSFCellStyle styleTwo = wb.createCellStyle();
        styleTwo.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        HSSFCellStyle styleThree = wb.createCellStyle();
        styleThree.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式
        HSSFFont font =wb.createFont();
        font.setFontHeightInPoints((short)17);
        HSSFFont fontTwo =wb.createFont();
        fontTwo.setFontHeightInPoints((short)12);
        //
        int headLength = headLines.length;
        if(headLines!=null && headLength > 0){
            for(int i=0; i<headLength ; i++){
                HSSFRow row = sheet.createRow(i);
                HSSFCell hSSFCell0 = row.createCell(0);
                hSSFCell0.setCellValue(headLines[i]);
                if(i==0){
                    styleTwo.setFont(font);
                    hSSFCell0.setCellStyle(styleTwo);
                }
                if(i==1){
                    styleThree.setFont(fontTwo);
                    hSSFCell0.setCellStyle(styleThree);
                }
                sheet.addMergedRegion(new CellRangeAddress(i, (short)i, 0, (short)titles.length-1));
            }
        }

        List<String> paramIds = new ArrayList<String>();
        HSSFRow row = sheet.createRow((int) headLength);
        HSSFCell cell = null;
        for(int i = 0 ; i<titles.length ; i++){
            String[] title = titles[i].split(":");
            cell = row.createCell(i);
            cell.setCellValue(title[1]);
            cell.setCellStyle(style);
            paramIds.add(title[0]);
            int length = title[1].length();
            sheet.setColumnWidth(i, length*1200);
        }

        if(dataList!=null){
            HSSFCell rowCell = null;
            for(int i=0; i<dataList.size() ; i++){
                Object item = dataList.get(i);
                row = sheet.createRow(headLength + 1 + i);
                Object result = null;
                for(int j = 0 ; j <paramIds.size();j++){
                    String paramId = paramIds.get(j);
                    if(StringUtils.isBlank(paramId)){//序号
                        result = i+1;
                    }else{
                        result = getValueByAttrs(paramId,item);
                    }
                    rowCell=row.createCell(j);
                    rowCell.setCellStyle(style);
                    rowCell.setCellValue(result.toString());
                }
            }
        }
    }

	public static void setCookie(HttpServletResponse response) {
		//更新完后，设定cookie，用于页面判断更新完成后的标志
		Cookie status = new Cookie("updateStatus","success");
		status.setMaxAge(600);
		status.setPath("/");
		response.addCookie(status);//添加cookie操作必须在写出文件前，如果写在后面，随着数据量增大时cookie无法写入。
	}
}
