package strategy;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import apiSearch.intermediate.FileIO;
import apiSearch.output.Format;
import apiSearch.output.Index;
import apiSearch.output.Output;
import apiSearch.output.Simple;
import apiSearch.parser.JDT;
import apiSearch.parser.Parser;
import apiSearch.search.JDTSearch;
import apiSearch.search.Search;
import apiSearch.staticClass.Setting;
import apiSearch.tool.Input;
import apiSearch.tool.Project;

/**
 * 根据工具中读取的输入，存储并设置相应的API搜索策略 若需要增加新的支持，需要修改： 1.增加setXXX()方法中的相应判断和对象创建；
 * 2.增加setValidXXX()方法中的相应内容； 3.若要增加新的语言支持，则继承该类并实现所需的抽象方法，作为新的搜索策略；
 * 
 * @author barry
 *
 */

public abstract class Strategy {

	Output out;

	FileIO interIO;
	Search search;
	Parser parser;

	Set<String> validParser;
	Set<String> validSearch;
	Set<String> validInter;
	Set<String> validOutput;

	public ArrayList<Project> projects;

	String readPath;

	public Strategy() {
		// TODO Auto-generated constructor stub
		setvalidParameter();
		setStrategy();
		projects = new ArrayList<Project>();
		// mkSaveDir();
	}

	// private void mkSaveDir() {
	// // TODO Auto-generated method stub
	// File save = new File(Input.savePath);
	// if (!save.exists()) {
	// save.mkdirs();
	// }
	// }

	private void setvalidParameter() {
		// TODO Auto-generated method stub
		setvalidPaser();
		setvalidSearch();
		setvalidInter();
		setvalidOutput();

	}

	private void setDebug() {
		// TODO Auto-generated method stub
		if (Input.debug) {
			Setting.Debug = true;
		} else {
			Setting.Debug = false;
		}
	}

	private void setvalidOutput() {
		// TODO Auto-generated method stub
		validOutput = new HashSet<>();
		validOutput.add("simple");
		validOutput.add("format");
		validOutput.add("index");
	}

	private void setvalidInter() {
		// TODO Auto-generated method stub
		validInter = new HashSet<>();
		validInter.add("default");
	}

	private void setvalidSearch() {
		// TODO Auto-generated method stub
		validSearch = new HashSet<>();
		validSearch.add("jdt");
	}

	private void setvalidPaser() {
		// TODO Auto-generated method stub
		validParser = new HashSet<>();
		validParser.add("jdt");
	}

	private void setStrategy() {
		// TODO Auto-generated method stub
		setInter();
		setSearch();
		setInter();
		setOutput();
		setParser();
		setDebug();
	}

	private void setParser() {
		parser = new JDT("java");
	}

	private void setOutput() {
		// TODO Auto-generated method stub
		if (validOutput.contains(Input.output)) {
			if (Input.output.contains("simple")) {
				out = new Simple();
			} else if (Input.output.contains("format")) {
				out = new Format(Input.savePath);
			} else if (Input.output.contains("index")) {
				out = new Index(Input.counter);
			}
		} else {
			try {
				throw new Exception("no such output format");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void setSearch() {
		search = new JDTSearch();
	}

	private void setInter() {
		// TODO Auto-generated method stub
		if (validInter.contains(Input.inter) || Input.inter.isEmpty()) {
			if (Input.inter.isEmpty() || Input.inter.contains("default")) {
				interIO = new FileIO();
			}
		} else {
			try {
				throw new Exception("no such intermediate representation");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public ArrayList<Project> findProject(String extension) {
		// TODO Auto-generated method stub
		File codehouse = new File(Input.srcPath);
		File[] projectDirectories = codehouse.listFiles();
		ArrayList<Project> projects = new ArrayList<Project>();

		for (File project : projectDirectories) {
			if (project.isDirectory()) {
				String projectName = project.getName();
				String projectPath = project.getAbsolutePath();
				if (Input.debug) {
					System.out.println("---------------");
					System.out.println(projectName);
				}
				Project now = new Project(projectName, projectPath, extension);
				projects.add(now);
			}
		}

		return projects;
	}

	public abstract void strategy(String extension);

	public abstract void createSearch(String extension);

	public abstract void readSearch();

	public abstract void DoSomething();

}
