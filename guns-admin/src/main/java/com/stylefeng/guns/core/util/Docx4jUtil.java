package com.stylefeng.guns.core.util;

import cn.afterturn.easypoi.word.WordExportUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.docx4j.Docx4J;
import org.docx4j.XmlUtils;
import org.docx4j.convert.in.xhtml.DivToSdt;
import org.docx4j.convert.in.xhtml.XHTMLImporterImpl;
import org.docx4j.convert.out.FOSettings;
import org.docx4j.fonts.IdentityPlusMapper;
import org.docx4j.fonts.Mapper;
import org.docx4j.fonts.PhysicalFont;
import org.docx4j.fonts.PhysicalFonts;
import org.docx4j.jaxb.Context;
import org.docx4j.model.structure.SectionWrapper;
import org.docx4j.openpackaging.contenttype.ContentType;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.OpcPackage;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.PartName;
import org.docx4j.openpackaging.parts.WordprocessingML.AlternativeFormatInputPart;
import org.docx4j.openpackaging.parts.WordprocessingML.HeaderPart;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.docx4j.relationships.Relationship;
import org.docx4j.wml.*;
import org.springframework.util.Assert;
import org.w3c.tidy.Tidy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Docx4jUtil {

	private static final String CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
	static ObjectFactory factory = Context.getWmlObjectFactory();

	private static WordprocessingMLPackage getTemplate(File name) throws Docx4JException, FileNotFoundException {
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(name));
		return template;
	}

	private static WordprocessingMLPackage getTemplate(String name) throws Docx4JException, FileNotFoundException {
		WordprocessingMLPackage template = WordprocessingMLPackage.load(new FileInputStream(new File(name)));
		return template;
	}

	private static List<Object> getAllElementFromObject(Object obj, Class<?> toSearch) {
		List<Object> result = new ArrayList<Object>();
		if (obj instanceof JAXBElement) obj = ((JAXBElement<?>) obj).getValue();

		if (obj.getClass().equals(toSearch))
			result.add(obj);
		else if (obj instanceof ContentAccessor) {
			List<?> children = ((ContentAccessor) obj).getContent();
			for (Object child : children) {
				result.addAll(getAllElementFromObject(child, toSearch));
			}
		}
		return result;
	}

	private static void replacePlaceholder(WordprocessingMLPackage template, String value, String placeholder ) {
		value = StringUtils.defaultString(value);
		List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

		for (Object text : texts) {
			Text textElement = (Text) text;
			if(textElement.getValue().startsWith("$")){
//				System.err.println(textElement.getValue());
			}
			if (textElement.getValue().contains(placeholder)) {
				String temp2 = textElement.getValue();
				String temp = temp2.replace(placeholder, value);
				textElement.setValue(temp);
			}
			String placeholderHtml = placeholder.replace("}","^html}");
			if (textElement.getValue().equals(placeholderHtml)) {
				XHTMLImporterImpl importer = new XHTMLImporterImpl(template);
				importer.setDivHandler(new DivToSdt());
				try {
					Tidy tidy = new Tidy();
					tidy.setPrintBodyOnly(true);
					tidy.setInputEncoding("UTF-8");
					tidy.setOutputEncoding("GBK");
					tidy.setXHTML(true);
//					tidy.setLogicalEmphasis(true);
					InputStream in = new ByteArrayInputStream(value.getBytes("UTF-8"));
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					tidy.parse(in, baos);
					in.close();
					value = baos.toString();
					baos.close();

				    value =  value.replace("&lt;", "〈");
				    value =  value.replace("&gt;", "〉");
//					System.out.println("before:///"+value+"/////////////");
					value = value.replace("&quot;","'");//双引号改为单引号
//					value = value.replaceAll("&amp;quot;","&amp;apos;");//双引号改为单引号
//				    System.out.println("before:///"+value+"/////////////");
					value = value.replaceAll("[>]\\s+", ">");
					value = value.replaceAll("\\s+[<]", "<");
					value = value.replaceAll("\\s+", " ");

				    value = StringEscapeUtils.unescapeHtml(value);
				    value =  value.replaceAll("&", "＆");

//					String ref = "^width:\\.*px;$";
//					value = value.replaceAll(ref,"width:10cm;");

//				    value =  value.replaceAll("width", "width1");
//				    value =  value.replaceAll("hight", "hight1");
//				    System.out.println("after:///"+value+"/////////////");
				    P p = findParagraph(template, textElement);
				    if(p!=null){
						textElement.setValue(textElement.getValue().replace(placeholderHtml, ""));
                        ((ContentAccessor)p.getParent()).getContent().addAll(importer.convert("<div>" + value + "</div>", null));
					}else{
				    	Tbl tbl = findTbl(template, textElement);
				    	if(tbl!=null){
							textElement.setValue(textElement.getValue().replace(placeholderHtml, ""));
                            ((ContentAccessor)tbl.getParent()).getContent().addAll(importer.convert("<div>" + value + "</div>", null));
						}
				    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {

		String value = "<span\n" +
				"  style=\"font-size: 14px;width:458px; line-height: 150%; font-family: 宋体;\">\n" +
				"    <img\n" +
				"    src=\"http://zyykj.jswst.gov.cn/upload/images/20180319/89081521440717643.jpg\"\n" +
				"     title=\"图片1435.jpg\"\n" +
				"    style=\"width: 458px; height: 240px;\" />\n" +
				"  </span>";

		value = value.replaceAll("width:\\d*px;|width:\\s\\d*px;","width:10cm;");

		System.out.println(value);



		String input = "<img class=\"entImg\" k1=\"ZHA455544942terttte\" k2=\".png\" label=\"FjYBs45643455xCbc234Me\" src=\"h-p://73442dc.c344.20.34.clo4434ddn.com/Z4554520438534.png?e=6839&token=42342344423joSfxMyIRMCqpdWoN0v:3242346I342343SBr8mw=\" style=\"width:300px;height:300px;\" />";
		String regex = "(?i)(k1|k2|label|src)[=\"\'\\s]+([^\"\']*)(?=[\"\'\\s>]+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		while(matcher.find())
		{
			System.out.println(matcher.group(1) + ": " + matcher.group(2));
		}
		input = input.replaceAll("(?i)(src[=\"\'\\s]+)[^\"\']*(?=[\"\'\\s>]+)", "$1\\$hash\\$");
		System.out.println(input);

	}

	private static void replaceNullPlaceholder(WordprocessingMLPackage template) {
		List<Object> texts = getAllElementFromObject(template.getMainDocumentPart(), Text.class);

		for (Object text : texts) {
			Text textElement = (Text) text;
			if (textElement.getValue().contains("${")) {
				textElement.setValue(textElement.getValue().replaceAll("\\$\\{(.[\\s\\S]*?)\\}", ""));
			}else if(textElement.getValue().contains("#{")){
				textElement.setValue(textElement.getValue().replaceAll("\\#\\{([\\s\\S]*)\\}", ""));
			}
		}
	}

	private static P findParagraph(WordprocessingMLPackage template,Text textElement) {
		// 1. get the paragraph
		List<Object> paragraphs = getAllElementFromObject(template.getMainDocumentPart(), P.class);

		P toFind = null;
		for (Object p : paragraphs) {
			List<Object> texts = getAllElementFromObject(p, Text.class);
			for (Object t : texts) {
				Text content = (Text) t;
				if (content.equals(textElement)) {
					toFind = (P) p;
					return toFind;
				}
			}
		}
		return null;
	}

	private static Tbl findTbl(WordprocessingMLPackage template,Text textElement) {
		// 2. get the tbl
		List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

		for (Iterator<Object> iterator = tables.iterator(); iterator.hasNext();) {
			Object tbl = iterator.next();
			List<?> textElements = getAllElementFromObject(tbl, Text.class);
			for (Object text : textElements) {
				Text content = (Text) text;
				if (content.equals(textElement)) {
					return (Tbl) tbl;
				}
			}
		}
		return null;
	}

//	private static void replaceParagraph(String placeholder, String textToAdd, WordprocessingMLPackage template, ContentAccessor addTo) {
	private static void replaceParagraph(String placeholder, String textToAdd, WordprocessingMLPackage template) {
		// 1. 从模版中找到要替换的段落
		List<Object> paragraphs = getAllElementFromObject(template.getMainDocumentPart(), P.class);

		P toReplace = null;
		for (Object p : paragraphs) {
			List<Object> texts = getAllElementFromObject(p, Text.class);
			for (Object t : texts) {
				Text content = (Text) t;
				if (content.getValue().equals(placeholder)) {
					toReplace = (P) p;
					break;
				}
			}
		}
		if(toReplace==null){
			replacePlaceholder(template, textToAdd, placeholder);
			return;
		}
		P toReplaceN = null;
		for (Object p : paragraphs) {
			List<Object> texts = getAllElementFromObject(p, Text.class);
			for (Object t : texts) {
				Text content = (Text) t;
				if (content.getValue().equals(placeholder.replace("}","^p}"))) {
					toReplaceN = (P) p;
					content.setValue("");
					break;
				}
			}
		}
		if(toReplaceN==null){
            replacePlaceholder(template, textToAdd, placeholder);
			return;
		}
		// we now have the paragraph that contains our placeholder: toReplace
		// 2. 将输入文本拆分成单独的行
		String as[] = StringUtils.splitPreserveAllTokens(textToAdd, "\n");

		for (int i = 0; i < as.length; i++) {
			String ptext = as[i];
			if (StringUtils.isBlank(ptext) && i==as.length-1) {
				continue;
			}

			// 3. 每一行基于模版中的段落创建一个新的段落
			P copy = (P) XmlUtils.deepCopy(toReplace);

			// replace the text elements from the copy
			List<?> texts = getAllElementFromObject(copy, Text.class);
			if (texts.size() > 0) {
				Text textToReplace = (Text) texts.get(0);
				textToReplace.setValue(ptext);
			}

			// add the paragraph to the document
//			addTo.getContent().add(copy);
			if(toReplaceN!=null)
             ((ContentAccessor)toReplaceN.getParent()).getContent().add(copy);
		}

		// 4. 移除原来的段落
		((ContentAccessor)toReplace.getParent()).getContent().remove(toReplace);

	}

	private static void replaceTable(String[] placeholders, List<Map<String, String>> textToAdd,
			WordprocessingMLPackage template) throws Docx4JException, JAXBException {
		List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);

		// 1. find the table
		Tbl tempTable = null;
		for(int i = 0;i<placeholders.length;i++){

			tempTable = getTemplateTable(tables, placeholders[i]);
			if(tempTable==null) {
				tempTable = getTemplateTable(tables, placeholders[i].replace("}", "^p}"));
			}
			if(tempTable != null){
				break;
			}

		}
		if(tempTable==null) return;
		List<Object> rows = getAllElementFromObject(tempTable, Tr.class);

		// first row is header, second row is content
		if (rows.size() == 2) {
			// this is our template row
			Tr templateRow = (Tr) rows.get(1);

			for (Map<String, String> replacements : textToAdd) {
				// 2 and 3 are done in this method
				addRowToTable(tempTable, templateRow, replacements);
			}

			// 4. remove the template row
			tempTable.getContent().remove(templateRow);
		}
	}

	private static Tbl getTemplateTable(List<Object> tables, String templateKey) throws Docx4JException, JAXBException {
		for (Iterator<Object> iterator = tables.iterator(); iterator.hasNext();) {
			Object tbl = iterator.next();
			List<?> textElements = getAllElementFromObject(tbl, Text.class);
			for (Object text : textElements) {
				Text textElement = (Text) text;
				if (textElement.getValue() != null && textElement.getValue().equals(templateKey))
					return (Tbl) tbl;
			}
		}
		return null;
	}

	private static void addRowToTable(Tbl reviewtable, Tr templateRow, Map<String, String> replacements) {
		Tr workingRow = (Tr) XmlUtils.deepCopy(templateRow);
		List<?> textElements = getAllElementFromObject(workingRow, R.class);
		for (Object object : textElements) {
			R run = (R) object;
			List<?> texts = getAllElementFromObject(run, Text.class);
			Text text = null;
			if (texts.size() > 0) {
				text = (Text) texts.get(0);
			}
			String placeholder = text.getValue();
			String placeKey = placeholder.replace("#{", "").replace("}", "");
			if(placeholder.contains("^p}")){
				placeKey = placeholder.replace("#{", "").replace("^p}", "");
				String replacementValue = (String) replacements.get(placeKey);
				if (replacementValue != null) {

					String[] contentArr = replacementValue.split("\n");
					/*Text t = new Text();
					t.setSpace("preserve");
					t.setValue(contentArr[0]);
					//run.setRPr(runProperties);
					run.getContent().add(text);*/

					for (int i = 0, len = contentArr.length; i < len; i++) {
						Br br = new Br();
						run.getContent().add(br);// 换行
						text = new Text();
						text.setSpace("preserve");
						text.setValue(contentArr[i]);
						//run.setRPr(runProperties);
						run.getContent().add(text);
					}
				}
			}else{
				String replacementValue = (String) replacements.get(placeKey);
				if (replacementValue != null)
					text.setValue(replacementValue);
			}
		}

		reviewtable.getContent().add(workingRow);
	}

	private static void writeDocxToStream(WordprocessingMLPackage template, String target) throws IOException, Docx4JException {
		File f = new File(target);
		template.save(f);
	}

	public static WordprocessingMLPackage mergeDocx(List<WordprocessingMLPackage> wmlPkgList) throws Docx4JException, IOException {
		WordprocessingMLPackage target = null;
		int chunkId = 0;
		Iterator<WordprocessingMLPackage> it = wmlPkgList.iterator();
		while (it.hasNext() ){
			WordprocessingMLPackage insert = it.next();
			if (insert != null) {
				ByteArrayOutputStream os = new ByteArrayOutputStream ();
				insert.save(os);
				byte bytes[] = os.toByteArray();
		        if (target == null){   //Copy first (master) document
//		            OutputStream os = new ByteArrayOutputStream ();
//		        	os.write (IOUtils.toByteArray (is) );
//		        	os.close();
					target = insert;
		        }
		        else{     //Attach the others (Alternative input parts)
		            insertDocx (target.getMainDocumentPart(), bytes, chunkId++);
		    	}
		        os.close();
			}
		}
//		if (target != null){
//			target.save (generated);
//			return new FileInputStream (generated);
//		}
//		else{
//			return null;
//		}
		return target;
	}

//	public static WordprocessingMLPackage merge(List<WordprocessingMLPackage> wmlPkgList){
//		try {
//			// Use reflection, so docx4j can be built
//			// by users who don't have the MergeDocx utility
//			Class<?> documentBuilder = Class.forName("com.plutext.merge.DocumentBuilder");			
//			//Method method = documentBuilder.getMethod("merge", wmlPkgList.getClass());			
//			Method[] methods = documentBuilder.getMethods(); 
//			Method method = null;
//			for (int j=0; j<methods.length; j++) {
//				System.out.println(methods[j].getName());
//				if (methods[j].getName().equals("merge")) {
//					method = methods[j];
//					break;
//				}
//			}			
//			if (method==null) throw new NoSuchMethodException();
//			
//			WordprocessingMLPackage resultPkg = (WordprocessingMLPackage)method.invoke(null, wmlPkgList);
//			return resultPkg;	
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} 
//		return null;
//	}

	//插入文档
	private static void insertDocx(MainDocumentPart main, byte[] bytes, int chunkId) {
		try {
			AlternativeFormatInputPart afiPart = new AlternativeFormatInputPart(new PartName("/part" + chunkId + ".docx"));
			afiPart.setContentType(new ContentType(CONTENT_TYPE));
			afiPart.setBinaryData(bytes);
			Relationship altChunkRel = main.addTargetPart(afiPart);
			CTAltChunk chunk = Context.getWmlObjectFactory().createCTAltChunk();
			chunk.setId(altChunkRel.getId());
			main.addObject(chunk);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void toPdf(WordprocessingMLPackage wordMLPackage,OutputStream os) throws Exception{
		if (wordMLPackage != null) {
			try {
				//pdf准备工作
				// Font regex (optional)
				// Set regex if you want to restrict to some defined subset of fonts
				// Here we have to do this before calling createContent,
				// since that discovers fonts
				String regex = null;
				// Windows:
				// String  字体类型
				// regex=".*(calibri|camb|cour|arial|symb|times|Times|zapf).*";
				regex = ".*(mingliu|mingliub|simsunb|simsun|calibri|camb|cour|arial|times|comic|georgia|impact|LSANS|pala|tahoma|trebuc|verdana|symbol|webdings|wingding).*";
				// Mac
				// String
				// regex=".*(Courier New|Arial|Times New Roman|Comic Sans|Georgia|Impact|Lucida Console|Lucida Sans Unicode|Palatino Linotype|Tahoma|Trebuchet|Verdana|Symbol|Webdings|Wingdings|MS Sans Serif|MS Serif).*";
				PhysicalFonts.setRegex(regex);

				Mapper fontMapper = new IdentityPlusMapper();
				fontMapper.put("隶书", PhysicalFonts.get("LiSu"));
				fontMapper.put("宋体",PhysicalFonts.get("SimSun"));
				fontMapper.put("微软雅黑",PhysicalFonts.get("Microsoft Yahei"));
				fontMapper.put("黑体",PhysicalFonts.get("SimHei"));
				fontMapper.put("楷体",PhysicalFonts.get("KaiTi"));
				fontMapper.put("新宋体",PhysicalFonts.get("NSimSun"));
				fontMapper.put("华文行楷", PhysicalFonts.get("STXingkai"));
				fontMapper.put("华文仿宋", PhysicalFonts.get("STFangsong"));
				fontMapper.put("宋体扩展",PhysicalFonts.get("simsun-extB"));
				fontMapper.put("仿宋",PhysicalFonts.get("FangSong"));
				fontMapper.put("仿宋_GB2312",PhysicalFonts.get("FangSong_GB2312"));
				fontMapper.put("幼圆",PhysicalFonts.get("YouYuan"));
				fontMapper.put("华文宋体",PhysicalFonts.get("STSong"));
				fontMapper.put("华文中宋",PhysicalFonts.get("STZhongsong"));
				wordMLPackage.setFontMapper(fontMapper);

				// .. example of mapping font Times New Roman which doesn't have certain Arabic glyphs
				// eg Glyph "賷" (0x64a, afii57450) not available in font "TimesNewRomanPS-ItalicMT".
				// eg Glyph "噩" (0x62c, afii57420) not available in font "TimesNewRomanPS-ItalicMT".
				// to a font which does
				PhysicalFont font = PhysicalFonts.getPhysicalFonts().get("calibri");
				// make sure this is in your regex (if any)!!!
				if (font != null) {
					fontMapper.getFontMappings().put("Times New Roman", font);
				}
				fontMapper.getFontMappings().put("Libian SC Regular", PhysicalFonts.getPhysicalFonts().get("SimSun"));


				// FO exporter setup (required)
				// .. the FOSettings object
				FOSettings foSettings = Docx4J.createFOSettings();
//			if (saveFO) {
//				foSettings.setFoDumpFile(new java.io.File(inputfilepath + ".fo"));
//			}
				foSettings.setWmlPackage(wordMLPackage);

				// Document format:
				// The default implementation of the FORenderer that uses Apache Fop will output
				// a PDF document if nothing is passed via
				// foSettings.setApacheFopMime(apacheFopMime)
				// apacheFopMime can be any of the output formats defined in org.apache.fop.apps.MimeConstants eg org.apache.fop.apps.MimeConstants.MIME_FOP_IF or
				// FOSettings.INTERNAL_FO_MIME if you want the fo document as the result.
				//foSettings.setApacheFopMime(FOSettings.INTERNAL_FO_MIME);

				// exporter writes to an OutputStream.
//			String outputfilepath;
//			if (inputfilepath==null) {
//				outputfilepath = System.getProperty("user.dir") + "/OUT_FontContent.pdf";
//			} else {
//				outputfilepath = inputfilepath + ".pdf";
//			}
//			OutputStream os = new java.io.FileOutputStream(outputfilepath);


				//Don't care what type of exporter you use
			Docx4J.toFO(foSettings, os, Docx4J.FLAG_NONE);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
	}

	public static WordprocessingMLPackage convert(File templateFile, Map<String,Object> templateParams,String watermark,boolean replaceParagraph) throws Exception {
		if (templateFile != null) {
			WordprocessingMLPackage template = getTemplate(templateFile);
			for (Map.Entry<String, Object> m : templateParams.entrySet()) {
				if(m.getValue()!=null&&m.getValue() instanceof List){
					List dataList = (List) m.getValue();
					if(dataList.size()==0){
						continue;
					}
					Map<String,String> objMap = new HashMap<String,String>();
					if(dataList.get(0)==null){
						continue;
					}
					if(dataList.get(0).getClass().getSimpleName().equals("ItemGroupData")){
						objMap = (Map<String,String>)BeanUtil.getProperty(dataList.get(0), "objMap");
					}else if(dataList.get(0) instanceof Map){
						objMap = (Map<String,String>)dataList.get(0);
					}

					if(objMap!=null&&objMap.keySet().size()>0){
						String[] placeholders = new String[objMap.keySet().size()];
						Iterator<String> it = objMap.keySet().iterator();
						int i=0;
						while (it.hasNext()) {
							placeholders[i++] =  "#{"+it.next()+"}";
						}
						List<Map<String,String>> objMapList = new ArrayList<Map<String,String>>();
						for(Object obj : dataList){
							if(dataList.get(0).getClass().getSimpleName().equals("ItemGroupData")){
								objMap = (Map<String,String>)BeanUtil.getProperty(obj, "objMap");
							}else{
								objMap = (Map<String,String>)obj;
							}
//							System.err.println(objMap);
							objMapList.add(objMap);
						}
						replaceTable(placeholders, objMapList, template);
					}
				}else{
					String mValue = StringUtils.defaultString((String) m.getValue());
					if(replaceParagraph){
						if(mValue.indexOf("\n")>-1){
							replaceParagraph("${" + m.getKey() + "}", (String) m.getValue(), template);
						}else{
							replacePlaceholder(template, (String) m.getValue(), "${" + m.getKey() + "}");
						}
					}else{
						replacePlaceholder(template, (String) m.getValue(), "${" + m.getKey() + "}");
					}

				}

			}
			replaceNullPlaceholder(template);

			if(StringUtils.isNotBlank(watermark)){
				Relationship relationship = createHeaderPart(template,watermark);
				createHeaderReference(template, relationship);
			}
			return template;
		}
		return null;
	}

	private static Relationship createHeaderPart(WordprocessingMLPackage wordprocessingMLPackage,String watermark)
			throws Exception {
		HeaderPart headerPart = new HeaderPart();
		Relationship rel =  wordprocessingMLPackage.getMainDocumentPart().addTargetPart(headerPart);
		headerPart.setJaxbElement(getHdr(watermark));
		return rel;
	}

	private static void createHeaderReference(
			WordprocessingMLPackage wordprocessingMLPackage,
			Relationship relationship )
			throws InvalidFormatException {

		List<SectionWrapper> sections = wordprocessingMLPackage.getDocumentModel().getSections();
		for(SectionWrapper sectionWrapper : sections){
			SectPr sectPr = sectionWrapper.getSectPr();
			// There is always a section wrapper, but it might not contain a sectPr
			if (sectPr==null ) {
				sectPr = factory.createSectPr();
				wordprocessingMLPackage.getMainDocumentPart().addObject(sectPr);
				sections.get(sections.size() - 1).setSectPr(sectPr);
			}

			HeaderReference headerReference = factory.createHeaderReference();
			headerReference.setId(relationship.getId());
			headerReference.setType(HdrFtrRef.DEFAULT);
			sectPr.getEGHdrFtrReferences().add(headerReference);
		}


	}

	private static Hdr getHdr(String watermark) throws Exception {

		Hdr hdr = factory.createHdr();

		String openXML = "<w:p xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\" xmlns:o=\"urn:schemas-microsoft-com:office:office\" xmlns:w10=\"urn:schemas-microsoft-com:office:word\">"
                + "<w:pPr>"
                      + "<w:pStyle w:val=\"Header\"/>"

                +"</w:pPr>"
                + "<w:sdt>"
                +   "<w:sdtPr>"
                      + "<w:id w:val=\"-1589924921\"/>"

                      + "<w:lock w:val=\"sdtContentLocked\"/>"

                      + "<w:docPartObj>"
                            + "<w:docPartGallery w:val=\"Watermarks\"/>"

                            + "<w:docPartUnique/>"

                      +"</w:docPartObj>"

                +"</w:sdtPr>"

                + "<w:sdtEndPr/>"

                + "<w:sdtContent>"
                      + "<w:r>"
                            + "<w:rPr>"
                                  + "<w:noProof/>"

                                  + "<w:lang w:eastAsia=\"zh-TW\"/>"

                            +"</w:rPr>"

                            + "<w:pict>"
                                  + "<v:shapetype adj=\"10800\" coordsize=\"21600,21600\" id=\"_x0000_t136\" o:spt=\"136\" path=\"m@7,l@8,m@5,21600l@6,21600e\">"
                                        + "<v:formulas>"
                                              + "<v:f eqn=\"sum #0 0 10800\"/>"

                                              + "<v:f eqn=\"prod #0 2 1\"/>"

                                              + "<v:f eqn=\"sum 21600 0 @1\"/>"

                                              + "<v:f eqn=\"sum 0 0 @2\"/>"

                                              + "<v:f eqn=\"sum 21600 0 @3\"/>"

                                              + "<v:f eqn=\"if @0 @3 0\"/>"

                                              + "<v:f eqn=\"if @0 21600 @1\"/>"

                                              + "<v:f eqn=\"if @0 0 @2\"/>"

                                              + "<v:f eqn=\"if @0 @4 21600\"/>"

                                              + "<v:f eqn=\"mid @5 @6\"/>"

                                              + "<v:f eqn=\"mid @8 @5\"/>"

                                              + "<v:f eqn=\"mid @7 @8\"/>"

                                              + "<v:f eqn=\"mid @6 @7\"/>"

                                              + "<v:f eqn=\"sum @6 0 @5\"/>"

                                        +"</v:formulas>"

                                        + "<v:path o:connectangles=\"270,180,90,0\" o:connectlocs=\"@9,0;@10,10800;@11,21600;@12,10800\" o:connecttype=\"custom\" textpathok=\"t\"/>"

                                        + "<v:textpath fitshape=\"t\" on=\"t\"/>"

                                        + "<v:handles>"
                                              + "<v:h position=\"#0,bottomRight\" xrange=\"6629,14971\"/>"

                                        +"</v:handles>"

                                        + "<o:lock shapetype=\"t\" text=\"t\" v:ext=\"edit\"/>"

                                  +"</v:shapetype>"

                                  + "<v:shape fillcolor=\"silver\" id=\"PowerPlusWaterMarkObject357476642\" o:allowincell=\"f\" o:spid=\"_x0000_s2049\" stroked=\"f\" style=\"position:absolute;margin-left:0;margin-top:0;width:527.85pt;height:131.95pt;rotation:315;z-index:-251658752;mso-position-horizontal:center;mso-position-horizontal-relative:margin;mso-position-vertical:center;mso-position-vertical-relative:margin\" type=\"#_x0000_t136\">"
                                        + "<v:fill opacity=\".5\"/>"

                                        + "<v:textpath string=\""+watermark+"\" style=\"font-family:&quot;Calibri&quot;;font-size:1pt\"/>"

                                        + "<w10:wrap anchorx=\"margin\" anchory=\"margin\"/>"

                                  +"</v:shape>"

                            +"</w:pict>"

                      +"</w:r>"

                +"</w:sdtContent>"

          +"</w:sdt>"
          + "</w:p>";

			P p = (P)XmlUtils.unmarshalString(openXML);

	        hdr.getContent().add(p);
			return hdr;

		}

	public static WordprocessingMLPackage convert2(File templateFile, Map<String,Object> templateParams,String watermark,boolean replaceParagraph) throws Exception {
		if (templateFile != null) {
			WordprocessingMLPackage template = getTemplate(templateFile);
			for (Map.Entry<String, Object> m : templateParams.entrySet()) {
				if(m.getValue()!=null&&m.getValue() instanceof List){
					List dataList = (List) m.getValue();
					if(dataList.size()==0){
						continue;
					}
					Map<String,String> objMap = new HashMap<String,String>();
					if(dataList.get(0)==null){
						continue;
					}
					String className = dataList.get(0).getClass().getSimpleName();
					if(className.equals("ItemGroupData")){
						objMap = (Map<String,String>)BeanUtil.getProperty(dataList.get(0), "objMap");
					}else if(dataList.get(0) instanceof Map){
						objMap = (Map<String,String>)dataList.get(0);
					}

					if(objMap!=null&&objMap.keySet().size()>0){
						String[] placeholders = new String[objMap.keySet().size()];
						Iterator<String> it = objMap.keySet().iterator();
						int i=0;
						while (it.hasNext()) {
							placeholders[i++] =  "#{"+it.next()+"}";
						}
						List<Map<String,String>> objMapList = new ArrayList<Map<String,String>>();
						for(Object obj : dataList){
							if(className.equals("ItemGroupData")){
								objMap = (Map<String,String>)BeanUtil.getProperty(obj, "objMap");
							}else{
								objMap = (Map<String,String>)obj;
							}
							objMapList.add(objMap);
						}
						replaceTable2(placeholders, objMapList, template);
					}
				}else{
					String mValue = StringUtils.defaultString((String) m.getValue());
					if(replaceParagraph && mValue.indexOf("\n")>-1) {
						replaceParagraph("${" + m.getKey() + "}", (String) m.getValue(), template);
					}else{
						replacePlaceholder(template, (String) m.getValue(), "${" + m.getKey() + "}");
					}
				}
			}
			replaceNullPlaceholder(template);
			if(StringUtils.isNotBlank(watermark)){
				Relationship relationship = createHeaderPart(template,watermark);
				createHeaderReference(template, relationship);
			}
			return template;
		}
		return null;
	}
	private static void replaceTable2(String[] placeholders, List<Map<String, String>> textToAdd, WordprocessingMLPackage template) throws Docx4JException, JAXBException {
		List<Object> tables = getAllElementFromObject(template.getMainDocumentPart(), Tbl.class);
		// 1. find the table
		Tbl tempTable = getTemplateTable(tables, placeholders[0]);
		if(tempTable==null) return;
		List<Object> rows = getAllElementFromObject(tempTable, Tr.class);
		Tr templateRow = (Tr) rows.get(0);
		for (Map<String, String> replacements : textToAdd) {
			// 2 and 3 are done in this method
			addRowToTable2(tempTable, templateRow, replacements);
		}
		// 4. remove the template row
		tempTable.getContent().remove(templateRow);
	}
	private static void addRowToTable2(Tbl reviewtable, Tr templateRow, Map<String, String> replacements) {
		Tr workingRow = (Tr) XmlUtils.deepCopy(templateRow);
		List<?> textElements = getAllElementFromObject(workingRow, R.class);
		for (Object object : textElements) {
			R run = (R) object;
			List<?> texts = getAllElementFromObject(run, Text.class);
			Text text = null;
			if (texts.size() > 0) {
				text = (Text) texts.get(0);
			}
			String placeholder = text.getValue();
			String placeKey = placeholder.replace("#{", "").replace("}", "");
			String replacementValue = (String) replacements.get(placeKey);
			String[] contentArr = replacementValue.split("\n");
			for (int i = 0, len = contentArr.length; i < len; i++) {
				Br br = new Br();
				text = new Text();
				text.setSpace("preserve");
				text.setValue(contentArr[i]);
				run.getContent().add(text);
				if(i < len-1){
					run.getContent().add(br);// 换行
				}
			}
		}
		reviewtable.getContent().add(workingRow);
	}
	/**
	 * 导出word
	 * <p>第一步生成替换后的word文件，只支持docx</p>
	 * <p>第二步下载生成的文件</p>
	 * <p>第三步删除生成的临时文件</p>
	 * 模版变量中变量格式：{{foo}}
	 * @param templatePath word模板地址
	 * @param temDir 生成临时文件存放地址
	 * @param fileName 文件名
	 * @param params 替换的参数
	 * @param request HttpServletRequest
	 * @param response HttpServletResponse
	 */
	public static void exportWord(String templatePath, String temDir, String fileName, Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
		Assert.notNull(templatePath,"模板路径不能为空");
		Assert.notNull(temDir,"临时文件路径不能为空");
		Assert.notNull(fileName,"导出文件名不能为空");
		Assert.isTrue(fileName.endsWith(".docx"),"word导出请使用docx格式");
		if (!temDir.endsWith("/")){
			temDir = temDir + File.separator;
		}
		File dir = new File(temDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		try {
			String userAgent = request.getHeader("user-agent").toLowerCase();
			if (userAgent.contains("msie") || userAgent.contains("like gecko")) {
				fileName = URLEncoder.encode(fileName, "UTF-8");
			} else {
				fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
			}
			XWPFDocument doc = WordExportUtil.exportWord07(templatePath, params);
			String tmpPath = temDir + fileName;
			FileOutputStream fos = new FileOutputStream(tmpPath);
			doc.write(fos);
			// 设置强制下载不打开
			response.setContentType("application/force-download");
			// 设置文件名
			response.addHeader("Content-Disposition", "attachment;fileName=" + fileName);
			OutputStream out = response.getOutputStream();
			doc.write(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
//			delAllFile(temDir);//这一步看具体需求，要不要删
		}
	}
	public static void build(File tmpFile, Map<String, String> contentMap, String exportFile) throws Exception {
		FileInputStream tempFileInputStream = new FileInputStream(tmpFile);
		HWPFDocument document = new HWPFDocument(tempFileInputStream);
		// 读取文本内容
		Range bodyRange = document.getRange();
		// 替换内容
		for (Map.Entry<String, String> entry : contentMap.entrySet()) {
			bodyRange.replaceText("${" + entry.getKey() + "}", entry.getValue());
		}
		//导出到文件
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		document.write(byteArrayOutputStream);
		OutputStream outputStream = new FileOutputStream(exportFile);
		outputStream.write(byteArrayOutputStream.toByteArray());
		outputStream.close();
	}

	public static WordprocessingMLPackage convert3(InputStream input, Map<String,Object> templateParams,String watermark,boolean replaceParagraph) throws Exception {
		if (input != null) {
			WordprocessingMLPackage template = WordprocessingMLPackage.load(input);
			for (Map.Entry<String, Object> m : templateParams.entrySet()) {
				if(m.getValue()!=null&&m.getValue() instanceof List){
					List dataList = (List) m.getValue();
					if(dataList.size()==0){
						continue;
					}
					Map<String,String> objMap = new HashMap<String,String>();
					if(dataList.get(0)==null){
						continue;
					}
					if(dataList.get(0).getClass().getSimpleName().equals("ItemGroupData")){
						objMap = (Map<String,String>)BeanUtil.getProperty(dataList.get(0), "objMap");
					}else if(dataList.get(0) instanceof Map){
						objMap = (Map<String,String>)dataList.get(0);
					}

					if(objMap!=null&&objMap.keySet().size()>0){
						String[] placeholders = new String[objMap.keySet().size()];
						Iterator<String> it = objMap.keySet().iterator();
						int i=0;
						while (it.hasNext()) {
							placeholders[i++] =  "#{"+it.next()+"}";
						}
						List<Map<String,String>> objMapList = new ArrayList<Map<String,String>>();
						for(Object obj : dataList){
							if(dataList.get(0).getClass().getSimpleName().equals("ItemGroupData")){
								objMap = (Map<String,String>)BeanUtil.getProperty(obj, "objMap");
							}else{
								objMap = (Map<String,String>)obj;
							}
//							System.err.println(objMap);
							objMapList.add(objMap);
						}
						replaceTable(placeholders, objMapList, template);
					}
				}else{
					String mValue = StringUtils.defaultString((String) m.getValue());
					if(replaceParagraph){
						if(mValue.indexOf("\n")>-1){
							replaceParagraph("${" + m.getKey() + "}", (String) m.getValue(), template);
						}else{
							replacePlaceholder(template, (String) m.getValue(), "${" + m.getKey() + "}");
						}
					}else{
						replacePlaceholder(template, (String) m.getValue(), "${" + m.getKey() + "}");
					}

				}

			}
			replaceNullPlaceholder(template);

			if(StringUtils.isNotBlank(watermark)){
				Relationship relationship = createHeaderPart(template,watermark);
				createHeaderReference(template, relationship);
			}
			return template;
		}
		return null;
	}

}
