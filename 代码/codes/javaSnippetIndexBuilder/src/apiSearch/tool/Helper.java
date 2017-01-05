package apiSearch.tool;

public class Helper {
	
	public static String splitByCamelCase(String str) {
		str = str.replaceAll("[\\d_]", " ");
		for (int i = 0; i < str.length(); ++i) {
			if (str.charAt(i) >= 'A' && str.charAt(i) <= 'Z') {
				str = str.substring(0, i) + " " + str.substring(i, str.length());
				++i;
			}
		}
		
		return str.toLowerCase();
	}

}
