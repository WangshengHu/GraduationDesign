package apiSearch.search;

import java.util.ArrayList;

import apiSearch.intermediate.Invocation;
import apiSearch.intermediate.Method;


/**
 * 搜索算法的抽象基类，拆分API为类型+方法名，并在某棵AST上进行搜索
 * @author barry
 *
 */
public abstract class Search {

	public String type;
	public String name;
	
	public Search() {
		// TODO Auto-generated constructor stub
		type = new String();
		name = new String();
	}
	
	//拆分API = ClassType + MethodName
	public void setAPI(String api) {
		String[] line = api.split("\\.");
		ArrayList<String> names = new ArrayList<>();
		for (int i = 0; i < line.length; i++) {
			names.add(line[i]);
		}
		name = names.get(names.size() - 1);
		for (int i = 0; i < names.size() - 2; i++) {
			type += names.get(i) + ".";
		}
		type += names.get(names.size() - 2);
		
	}
	
	public String getType() {
		return type;
	}
	public abstract ArrayList<Method> search(Object root);

}
