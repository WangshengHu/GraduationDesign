package apiSearch.output;

import java.util.Map;

import apiSearch.intermediate.InterRep;
import apiSearch.tool.Project;

/**
 * 信息输出格式的基类
 * 
 * @author barry
 *
 */
public abstract class Output {

	public Output() {
		// TODO Auto-generated constructor stub
	}

	public abstract void output(Project now, Map<String, InterRep> result);

	public abstract void outputSingleFile(Project now, String file, InterRep result);

	public abstract void close();

	public abstract void setSrc(String path);

}
