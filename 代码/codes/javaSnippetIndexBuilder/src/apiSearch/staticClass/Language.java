package apiSearch.staticClass;

import java.util.HashMap;
import java.util.Map;



/**
 * 存储语言对应的文件扩展名
 * 若新增语言，则需要修改：
 * 	1.set()方法中增加对应的扩展名键值对
 * @author barry
 *
 */
public class Language {
	
	
	static Map<String, String> languages = new HashMap<String, String>();

	public Language() {
		// TODO Auto-generated constructor stub	
	}

	/**
	 * 待添加其他语言的扩展名
	 */
	public static void set() {
		// TODO Auto-generated method stub
		languages.put("java", ".java");
	}

	public static String getExtension(String language) {
		// TODO Auto-generated method stub
		return languages.get(language);
	}

}
