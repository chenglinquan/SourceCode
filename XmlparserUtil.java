package com.cheng.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * @className XmlparserUtil
 * @todo 工具类 将xml造型成实体类打印输出
 * @author chenglinquan
 * @data 2016年7月20日
 */
public class XmlparserUtil {

	public static final String END_WITH = "VO";
	public static Map<String, Set<String>> classMap = new HashMap<String, Set<String>>();
	public static String outputPath = "g:" + File.separator + "output";

	public static void main(String[] args) {

		SAXReader sr = new SAXReader();
		PrintWriter pw = null;

		try {
			Document document = sr.read(new File("src/com/cheng/xml/6992W09.xml"));
			Element root = document.getRootElement();
			System.out.println(root.getName());
			traversal(root);
			Set<String> keyset = classMap.keySet();
			// resultToFile("test.txt");
			for (String key : keyset) {
				File file = new File(outputPath + File.separator + key + ".java");
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();
				pw = new PrintWriter(file);
				pw.println("public class " + key + "{\n");
				Set<String> sets = classMap.get(key);
				Iterator<String> attrset = sets.iterator();
				while (attrset.hasNext()) {
					pw.println(attrset.next().toString());

				}
				pw.println("\n}");
				pw.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (!(pw == null)) {
				pw.close();
			}
		}
	}

	public static void traversal(Element element) {
		// 获取节点名称
		String voName = upperFirstChar(element.getName());
		// System.out.println(voName);
		// 如果集合中已放入了该类
		if (classMap.containsKey(voName)) {
			// 已存入的属性
			Set<String> existAttrs = classMap.get(voName);
			// 此节点的属性
			Set<String> thisAttrs = attrlist(element);
			Set<String> thisChildren = childrenlist(element);
			existAttrs.addAll(thisAttrs);
			existAttrs.addAll(thisChildren);

		} else {// 未放入该类
			Set<String> attrs = new HashSet<String>();
			Set<String> children = new HashSet<String>();
			// 获取节点属性
			attrs = attrlist(element);
			// 获取子节点
			children = childrenlist(element);
			attrs.addAll(children);
			classMap.put(voName, attrs);
		}

		List<Element> childrenit = element.elements();
		// 如果存在子节点 递归调用该方法
		for (Element item : childrenit) {
			traversal(item);
		}

	}

	// 首字母大写 末尾加VO
	public static String upperFirstChar(String name) {
		String firstupper = name.substring(0, 1).toUpperCase();
		name = firstupper + name.substring(1) + END_WITH;
		return name;
	}

	// 遍历某节点的属性 返回属性集合
	public static Set<String> attrlist(Element element) {
		Set<String> items = new HashSet<String>();
		Iterator<Attribute> ait = element.attributeIterator();
		while (ait.hasNext()) {
			Attribute attr = ait.next();
			if ("ture".equals(attr.getValue().trim()) || "false".equals(attr.getValue().trim())) {
				String e = "private boolean " + attr.getName() + ";";
				items.add(e);
			} else {
				String e = "private String " + attr.getName() + ";";
				items.add(e);
			}
		}
		return items;
	}

	// 遍历子节点
	public static Set<String> childrenlist(Element element) {
		Iterator<Element> eit = element.elementIterator();
		Set<String> items = new HashSet<String>();
		while (eit.hasNext()) {
			Element item = eit.next();
			String name = upperFirstChar(item.getName());
			String e = "private List<" + name + "> " + item.getName() + ";";
			items.add(e);
		}
		return items;
	}

	public static void resultToFile(String path) throws FileNotFoundException {
		File file = new File(path);
		PrintStream printStream = new PrintStream(new FileOutputStream(file));
		PrintStream out = printStream;
		System.setOut(out);
	}
}
