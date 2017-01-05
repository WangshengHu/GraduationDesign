package apiSearch.intermediate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import apiSearch.staticClass.Setting;

import java.util.Set;
import java.util.TreeMap;

/**
 * 用于保存具体的API信息的类，每个Invocationr类保存了一个成员访问或函数调用
 * 
 * @author barry
 *
 */
public class Invocation {

	private String api; // api full name
	private int start;
	private int length;
	private boolean isConstructor;

	public Invocation(boolean constructor) {
		this.api = new String();
		this.isConstructor = constructor;
	}

	public void setApi(String api) {
		this.api = api;
		if (this.isConstructor) {
			this.api = this.api + "." + this.getShortName(api);
		}
	}

	public void setPos(int start, int length) {
		this.start = start;
		this.length = length;
	}

	public String getApi() {
		return this.api;
	}

	public int getStart() {
		return this.start;
	}

	public int getLength() {
		return this.length;
	}

	private String getShortName(String api) {
		String shortName = "";

		if (this.isConstructor) {
			shortName = api.substring(api.lastIndexOf('.') + 1);
		} else {
			String name = api.substring(0, api.lastIndexOf('.'));

			int idx = name.lastIndexOf('.');
			shortName = api.substring(idx + 1);
		}

		return shortName;
	}

	public String getApiUsageData() {
		String apiUsage = getShortName(this.api) + Setting.Separator + this.api + Setting.Separator + this.start
				+ Setting.Separator + this.length;
		return apiUsage;
	}

	public void sort() {
		// TODO Auto-generated method stub

	}

}
