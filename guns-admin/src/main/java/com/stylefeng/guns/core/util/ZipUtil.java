package com.stylefeng.guns.core.util;

import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipFile;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

	static final int BUFFER = 2048;
	/**
	 * The buffer.
	 */
	protected static byte buf[] = new byte[1024];

	public static void toZip(File[] files, File zipFile) {

	}

	/**
	 * 遍历目录并添加文件.
	 *
	 * @param jos
	 *            - JAR 输出流
	 * @param file
	 *            - 目录文件名
	 * @param pathName
	 *            - ZIP中的目录名
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private static void recurseFiles(org.apache.tools.zip.ZipOutputStream jos, File file,
									 String pathName) throws IOException, FileNotFoundException {
		if (file.isDirectory()) {

			pathName = pathName + file.getName() + "/";
			jos.putNextEntry(new org.apache.tools.zip.ZipEntry(pathName));
			String fileNames[] = file.list();
			if (fileNames != null) {
				for (int i = 0; i < fileNames.length; i++)
					recurseFiles(jos, new File(file, fileNames[i]), pathName);

			}
		} else {
			org.apache.tools.zip.ZipEntry jarEntry = new org.apache.tools.zip.ZipEntry(pathName + file.getName());
			// System.out.println(pathName + "  " + file.getName());
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fin);
			jos.putNextEntry(jarEntry);
			jos.setEncoding("gbk");
			int len;
			while ((len = in.read(buf)) >= 0)
				jos.write(buf, 0, len);
			in.close();
			jos.closeEntry();
		}
	}

	private static void recurseNjmueduFiles(org.apache.tools.zip.ZipOutputStream jos, File file,
											String pathName) throws IOException, FileNotFoundException {
		if (file.isDirectory()) {

			pathName = pathName + "/";
			jos.putNextEntry(new org.apache.tools.zip.ZipEntry(pathName));
			String fileNames[] = file.list();
			if (fileNames != null) {
				for (int i = 0; i < fileNames.length; i++)
					recurseFiles(jos, new File(file, fileNames[i]), pathName);

			}
		} else {
			org.apache.tools.zip.ZipEntry jarEntry = new org.apache.tools.zip.ZipEntry(pathName);
			// System.out.println(pathName + "  " + file.getName());
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream in = new BufferedInputStream(fin);
			jos.putNextEntry(jarEntry);
			jos.setEncoding("GBK");
			int len;
			while ((len = in.read(buf)) >= 0)
				jos.write(buf, 0, len);
			in.close();
			jos.closeEntry();
		}
	}

	public static void toZip(List<File> files, File zipFile,
							 String zipFolderName, int level) throws IOException,
			FileNotFoundException {
		level = checkZipLevel(level);

		if (zipFolderName == null) {
			zipFolderName = "";
		}

		org.apache.tools.zip.ZipOutputStream jos = new org.apache.tools.zip.ZipOutputStream(new FileOutputStream(zipFile));
		jos.setLevel(level);

		for (int i = 0; i < files.size(); i++) {
			recurseFiles(jos, files.get(i), zipFolderName);
		}

		jos.close();

	}

	/**
	 * 创建 ZIP/JAR 文件.
	 *
	 * @param directory
	 *            - 要添加的目录
	 * @param zipFile
	 *            - 保存的 ZIP 文件名
	 * @param zipFolderName
	 *            - ZIP 中的路径名
	 * @param level
	 *            - 压缩级别(0~9)
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public static void makeDirectoryToZip(File directory, File zipFile,
										  String zipFolderName, int level) throws IOException,
			FileNotFoundException {
		level = checkZipLevel(level);

		if (zipFolderName == null) {
			zipFolderName = "";
		}

		org.apache.tools.zip.ZipOutputStream jos = new org.apache.tools.zip.ZipOutputStream(new FileOutputStream(zipFile));
		jos.setLevel(level);

		String fileNames[] = directory.list();
		if (fileNames != null) {
			for (int i = 0; i < fileNames.length; i++)
				recurseFiles(jos, new File(directory, fileNames[i]),
						zipFolderName);

		}
		if(directory.exists()){
			deleteDir(directory);
		}
		jos.close();
	}
	private static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			//递归删除目录中的子目录下
			for (int i=0; i<children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// 目录此时为空，可以删除
		return dir.delete();
	}

	public static void makeNjmueduStuWorkDirectoryToZip(File directory, Map<String, String> userMap, File zipFile,
														String zipFolderName, int level) throws IOException,
			FileNotFoundException {
		level = checkZipLevel(level);

		if (zipFolderName == null) {
			zipFolderName = "";
		}

		org.apache.tools.zip.ZipOutputStream jos = new org.apache.tools.zip.ZipOutputStream(new FileOutputStream(zipFile));
		jos.setLevel(level);

		String fileNames[] = directory.list();
		if (fileNames != null) {
			for (int i = 0; i < fileNames.length; i++){
				String oldFileName = fileNames[i];
				int endIndex = oldFileName.lastIndexOf(".");
				String key = oldFileName.substring(0, endIndex);
				String value = userMap.get(key);
				if(StringUtils.isNotBlank(value)){
					String newName = value + oldFileName.substring(endIndex);
					recurseNjmueduFiles(jos, new File(directory, fileNames[i]), newName);
				}
			}
		}
		jos.close();
	}

	/**
	 * 检查并设置有效的压缩级别.
	 *
	 * @param level
	 *            - 压缩级别
	 * @return 有效的压缩级别或者默认压缩级别
	 */
	public static int checkZipLevel(int level) {
		if (level < 0 || level > 9)
			level = 7;
		return level;
	}

	public static void unZip(File zipFile,String zipFolderName) throws IOException {
		ZipFile zis = new ZipFile(zipFile);
		Enumeration<org.apache.tools.zip.ZipEntry> e = zis.getEntries();
		while(e.hasMoreElements()){
			org.apache.tools.zip.ZipEntry entry = (org.apache.tools.zip.ZipEntry) e.nextElement();
			File file = new File(zipFolderName+entry.getName());

			if(entry.isDirectory()){
				file.mkdirs();
				continue;
			}
			InputStream inputStream = zis.getInputStream(entry);
			File parent = file.getParentFile();
			if(parent!=null&&!parent.exists()){
				parent.mkdirs();
			}
			byte[] buf = new byte[BUFFER];
			int readedBytes = 0;
			FileOutputStream fileOut = new FileOutputStream(file);
			while((readedBytes = inputStream.read(buf) ) > 0){
				fileOut.write(buf , 0 , readedBytes );
			}
			fileOut.close();
			inputStream.close();
		}
		zis.close();
	}

	//下载directory目录下且存在list文件名的的文件
	public static void makeDirectoryToZip(File directory, List<String> list, File zipFile, int level) throws IOException{
		level = checkZipLevel(level);
		org.apache.tools.zip.ZipOutputStream jos = new org.apache.tools.zip.ZipOutputStream(new FileOutputStream(zipFile));
		jos.setLevel(level);
		String fileNames[] = directory.list();
		if (null != fileNames && fileNames.length > 0) {
			for (int i = 0; i < fileNames.length; i++){
				String fileName = fileNames[i];
				if(null != list && list.contains(fileName)){
					recurseNjmueduFiles(jos, new File(directory, fileNames[i]), fileName);
				}
			}
		}
		jos.close();
	}
	/**
	 * 将服务器文件存到压缩包中
	 */
	public static void zipFile(List<File> files, ZipOutputStream outputStream) throws IOException, ServletException {
		try {
			int size = files.size();
			// 压缩列表中的文件
			for (int i = 0; i < size; i++) {
				File file = (File) files.get(i);
				zipFile(file, outputStream);
			}
		} catch (IOException e) {
			throw e;
		}
	}
	public static void zipFile(File inputFile, ZipOutputStream outputstream) throws IOException, ServletException {
		try {
			if (inputFile.exists()) {
				if (inputFile.isFile()) {
					FileInputStream inStream = new FileInputStream(inputFile);
					BufferedInputStream bInStream = new BufferedInputStream(inStream);
					ZipEntry entry = new ZipEntry(inputFile.getName());
					outputstream.putNextEntry(entry);

					final int MAX_BYTE = 10 * 1024 * 1024; // 最大的流为10M
					long streamTotal = 0; // 接受流的容量
					int streamNum = 0; // 流需要分开的数量
					int leaveByte = 0; // 文件剩下的字符数
					byte[] inOutbyte; // byte数组接受文件的数据

					streamTotal = bInStream.available(); // 通过available方法取得流的最大字符数
					streamNum = (int) Math.floor(streamTotal / MAX_BYTE); // 取得流文件需要分开的数量
					leaveByte = (int) streamTotal % MAX_BYTE; // 分开文件之后,剩余的数量

					if (streamNum > 0) {
						for (int j = 0; j < streamNum; ++j) {
							inOutbyte = new byte[MAX_BYTE];
							// 读入流,保存在byte数组
							bInStream.read(inOutbyte, 0, MAX_BYTE);
							outputstream.write(inOutbyte, 0, MAX_BYTE); // 写出流
						}
					}
					// 写出剩下的流数据
					inOutbyte = new byte[leaveByte];
					bInStream.read(inOutbyte, 0, leaveByte);
					outputstream.write(inOutbyte);
					outputstream.closeEntry(); // Closes the current ZIP entry
					// and positions the stream for
					// writing the next entry
					bInStream.close(); // 关闭
					inStream.close();
				}
			} else {
				throw new ServletException("文件不存在！");
			}
		} catch (IOException e) {
			throw e;
		}
	}

	public static void downloadFile(File file, HttpServletResponse response, boolean isDelete) {
		try {
			// 以流的形式下载文件。
			BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			response.reset();
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment;filename=" + new String(file.getName().getBytes("UTF-8"),"ISO-8859-1"));
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
			if(isDelete)
			{
				file.delete();        //是否将生成的服务器端文件删除
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
