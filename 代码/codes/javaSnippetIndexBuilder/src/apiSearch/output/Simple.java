package apiSearch.output;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import apiSearch.intermediate.InterRep;
import apiSearch.intermediate.Invocation;
import apiSearch.tool.Input;
import apiSearch.tool.Project;

/**
 * 简单的信息输出格式，输出信息包括： 1.该项目的相关信息 2.该项目中有哪些文件中找到了该API的使用情况
 * 
 * @author barry
 *
 */

public class Simple extends Output {

	public Simple() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void output(Project now, Map<String, InterRep> result) {
		// TODO Auto-generated method stub
		if (now.isEmpty()) {
			return;
		}

		// System.out.println("----------------project output in simple
		// mode-----------------------");
		// System.out.println("project name: " + now.name);
		// System.out.println("project path: " + now.path);
		// System.out.println("\nmsg:found that following files have used this
		// API...\n");
		//
		// Iterator iterator = now.result.entrySet().iterator();
		// while (iterator.hasNext()) {
		// Entry entry = (Entry) iterator.next();
		// String file = (String)entry.getKey();
		// InterRep inter = (InterRep)entry.getValue();
		//
		//
		// if (inter.getData().isEmpty()) {
		// continue;
		// }
		//
		// for (Var j : inter.getData()) {
		// if (j.hasAPI(Input.api)) {
		// System.out.println("\nfile:" + file);
		// String result = j.toString(Input.api);
		//
		// if (!result.isEmpty()) {
		// System.out.println("" + result);
		// }
		// }
		// }
		//
		// }
		System.out.println("Done.");
	}

	@Override
	public void outputSingleFile(Project now, String file, InterRep result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setSrc(String path) {
		// TODO Auto-generated method stub
		
	}

}
