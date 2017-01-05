package apiSearch.intermediate;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 中间表示形式的类，该类中存储了中间表示形式所需要的信息。支持最基本的序列化形式保存方法。每个InterRep类保存了单个文件中的所有找到的API信息。
 * 
 * @author barry
 *
 */
public class InterRep implements Serializable {
	// String path; //中间表示形式的文件保存位置

	// String project;
	// String file;
	ArrayList<Method> data;

	public InterRep() {
		// TODO Auto-generated constructor stub
		data = new ArrayList<Method>();
	}

	public ArrayList<Method> getData() {
		return this.data;
	}

	public void setData(ArrayList<Method> data) {
		this.data = data;
	}

	public void clearData() {
		// TODO Auto-generated method stub
		this.data.clear();
	}
}
