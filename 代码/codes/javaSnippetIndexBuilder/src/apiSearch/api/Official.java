package apiSearch.api;

import java.util.HashSet;
import java.util.Set;


/**
 * 用于找到某种语言的官方API集合的抽象基类，若要添加对其他语言的支持，则需要：
 * 	1.继承该类
 * 	2.实现findOfficialAPI()方法，填充成员apis
 * @author barry
 *
 */
public abstract class Official {

	static Set<String> apis;
	
	public Official() {
		// TODO Auto-generated constructor stub
		apis = new HashSet<>();
	}
	public abstract void findOfficialAPI();
	public abstract boolean isInOfficialAPI(String api);

}
