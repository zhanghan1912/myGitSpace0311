package com.stylefeng.guns.core.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLDecoder;
import java.util.Map;

public class WordUtil {

	/**
	 * 生成word文件
	 * @param dataMap word中需要展示的动态数据，用map集合来保存
	 * @param templateName word模板名称，例如：test.ftl
	 * @param filePath 文件生成的目标路径，例如：D:/wordFile/
	 * @param fileName 生成的文件名称，例如：test.doc
	 */
	@SuppressWarnings("unchecked")
	public static void createWord(Map dataMap, String templateName, String filePath, String fileName) {
		try {
			//创建配置实例
			Configuration configuration = new Configuration();
			configuration.setClassicCompatible(true);
			//设置编码
			configuration.setDefaultEncoding("UTF-8");
			//ftl模板文件
			configuration.setClassForTemplateLoading(WordUtil.class, "/");
			//获取模板
			Template template = configuration.getTemplate(templateName);
			//输出文件
			File outFile = new File(filePath + File.separator + fileName);
			//如果输出目标文件夹不存在，则创建
			if (!outFile.getParentFile().exists()) {
				outFile.getParentFile().mkdirs();
			}
			//将模板和数据模型合并生成文件
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
			//生成文件
			template.process(dataMap, out);
			//关闭流
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 下载word并删除临时文件文件
	 */
	@SuppressWarnings("unchecked")
	public static void downloadWord(String filePath, String fileOnlyName, String fileName,HttpServletResponse response,HttpServletRequest request) {
		try {
			filePath = URLDecoder.decode(filePath, "UTF-8");
			fileOnlyName = URLDecoder.decode(fileOnlyName, "UTF-8");
			File file = new File(filePath + File.separator + fileOnlyName);
			if (file.exists()) {
				response.setContentType("application/force-download;charset=utf-8");// 设置强制下载不打开
				// 设置文件名
				if(request.getHeader("User-Agent").toUpperCase().indexOf("MSIE") > 0) {
					response.addHeader("Content-Disposition","attachment;"+ "filename="+ new String(fileName.getBytes("GBK"),"ISO8859-1"));
				}else{//firefox、chrome、safari、opera 
					response.addHeader("Content-Disposition","attachment;"+"filename="+ new String(fileName.getBytes("UTF8"), "ISO8859-1") );
				}
				byte[] buffer = new byte[1024];
				FileInputStream fis = null;
				BufferedInputStream bis = null;
				try {
					fis = new FileInputStream(file);
					bis = new BufferedInputStream(fis);
					OutputStream os = response.getOutputStream();
					int i = bis.read(buffer);
					while (i != -1) {
						os.write(buffer, 0, i);
						i = bis.read(buffer);
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (bis != null) {
						try {
							bis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (fis != null) {
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//下载完成后删除文件
					file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 该方法用来将指定的文件转换成base64编码
	 * @param path:图片路径
	 * **/
	public static String getImageStr(String path){
		//1、校验是否为空
		if(path==null || path.trim().length()<=0){return "";}
		//2、校验文件是否为目录或者是否存在
		File picFile = new File(path);
		if(picFile.isDirectory() || (!picFile.exists())) return "";
		//3、校验是否为图片
		try {
			BufferedImage image = ImageIO.read(picFile);
			if (image == null) {
				return "";
			}
		} catch(IOException ex) {
			ex.printStackTrace();
			return "";
		}
		//4、转换成base64编码
		String imageStr = "";
		try {
			byte[] data = null;
			InputStream in = new FileInputStream(path);
			data = new byte[in.available()];
			in.read(data);
			BASE64Encoder encoder = new BASE64Encoder();
			imageStr = encoder.encode(data);
		} catch (Exception e) {
			imageStr="";
			e.printStackTrace();
		}
		return imageStr;
	}
}
