package strategy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import apiSearch.intermediate.FileIO;
import apiSearch.intermediate.InterRep;
import apiSearch.intermediate.Invocation;
import apiSearch.parser.JDT;
import apiSearch.search.JDTSearch;
import apiSearch.staticClass.Setting;
import apiSearch.tool.Helper;
import apiSearch.tool.Input;
import apiSearch.tool.Log;
import apiSearch.tool.Project;

public class JavaStrategy extends Strategy {

	long findprojectBegin;
	JDT jdt = (JDT) parser;
	JDTSearch searcher = (JDTSearch) search;
	InterRep inter = new InterRep();

	public JavaStrategy() {
		super();

		this.jdt = (JDT) parser;
		this.searcher = (JDTSearch) search;
		this.inter = new InterRep();
	}

	@Override
	public void strategy(String extension) {
		// TODO Auto-generated method stub

		System.out.println("**********searching java files**********");
		createSearch(extension);
	}

	public void readSearch() {
		// TODO Auto-generated method stub
		findprojectBegin = System.currentTimeMillis();

		File results = new File(readPath);
		File[] files = results.listFiles();
		if (files != null && files.length > 1) {

			System.out.println("-------------intermediate data reading---------------");
			for (File project : files) {

				if (project.getName().startsWith(".")) {
					continue;
				}

				FileIO reader = new FileIO(project.getAbsolutePath());

				try {
					Project now = reader.read();
					projects.add(now);
					// out.output(now);
				} catch (ClassNotFoundException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		long findprojectEnd = System.currentTimeMillis();

		System.out.println("[consuming time] finding projects : "
				+ (findprojectEnd - findprojectBegin) / Setting.TimeUnit + " minutes");
		searchAPI();

		DoSomething();
	}

	// 读取上次搜索的结果后可以继续做一些事儿...
	public void DoSomething() {
		// TODO Auto-generated method stub
		searchAPI();
	}

	public void createSearch(String extension) {
		// TODO Auto-generated method stub

		findprojectBegin = System.currentTimeMillis();

		// System.out.println("-------begin to find project-----");
		projects = findProject(extension);
		// System.out.println("-------end to find project-----");

		float interval = (System.currentTimeMillis() - findprojectBegin) / Setting.TimeUnit;

		System.out.println("[consuming time] finding projects : " + interval + " minutes");

		parse();
	}

	private void searchAPI() {
		// TODO Auto-generated method stub
		for (int i = 0; i < projects.size(); i++) {
			Project now = projects.get(i);
			out.output(now, null);
		}
	}

	private void parse() {
		int numOfProj = projects.size();

		System.out.println("Search will start from project [" + Input.startProj + "/" + numOfProj + "] file ["
				+ Input.startFile + "]");

		int count = 0;

		for (int i = Input.startProj - 1; i < numOfProj; i++) {
			Project now = projects.get(i);

			Map<String, InterRep> result = new HashMap<String, InterRep>();

			jdt.setSrc(now.path);

			// System.out.println("-----project time test begin-----");
			System.out.println(
					"**********processing [" + (i + 1) + "/" + numOfProj + "] project [" + now.path + "]**********");
			long projectBegin = System.currentTimeMillis();

			int numOfFile = now.codes.size();

			int j = 0;
			if (i == Input.startProj - 1) {
				j = Input.startFile - 1;
			}
			for (; j < numOfFile; j++) {

				long fileBegin = System.currentTimeMillis();

				String codePath = now.codes.get(j);
				
				// filter test files
				String fileName = codePath.substring(codePath.lastIndexOf("\\") + 1, codePath.lastIndexOf("."));
				if (Helper.splitByCamelCase(fileName).contains("test")) {
					continue;
				}

				// System.out.println(
				// "**********processing [" + (j + 1) + "/" + numOfFile + "]
				// file [" + codePath + "]**********");
				Log.logCurrent("processing [" + (i + 1) + "/" + numOfProj + "] project [" + (j + 1) + "/" + numOfFile
						+ "] file [" + codePath + "]");

				jdt.SetFile(codePath);

				long parseBegin = System.currentTimeMillis();

				jdt.parse(searcher.name);

				long parseEnd = System.currentTimeMillis();
				// System.out.println("parse last: " + (parseEnd - parseBegin) +
				// "Millis");

				if (Setting.Debug) {
					System.out.println("--------search begin------------");
					System.out.println("search: " + codePath);
				}

				inter.clearData();

				long searchBegin = System.currentTimeMillis();

				if (!jdt.isEmpty()) {

					count++;

					SingleFileSearch singleFileSearch = new SingleFileSearch(2);

					try {
						singleFileSearch.start();
						singleFileSearch.join(Setting.SingleFileIntervalLevel2);
						singleFileSearch.stop();

						if (!singleFileSearch.isDone()) {
							if (singleFileSearch.isError()) {
								Log.log("project [" + now.path + "]\nfile [" + codePath
										+ "] search AST(search level 2):\n" + singleFileSearch.getErrorMessage());
							} else {
								Log.log("project [" + now.path + "]\nfile [" + codePath
										+ "] search AST(search level 2):\n" + "timeout ("
										+ Setting.SingleFileIntervalLevel2 / 1000 + "s)");
							}

							singleFileSearch = new SingleFileSearch(1);
							singleFileSearch.start();
							singleFileSearch.join(Setting.SingleFileIntervalLevel1);
							singleFileSearch.stop();

							if (!singleFileSearch.isDone()) {
								if (singleFileSearch.isError()) {
									Log.log("project [" + now.path + "]\nfile [" + codePath
											+ "] search AST(search level 1):\n" + singleFileSearch.getErrorMessage());
								} else {
									Log.log("project [" + now.path + "]\nfile [" + codePath
											+ "] search AST(search level 1):\n" + "timeout ("
											+ Setting.SingleFileIntervalLevel1 / 1000 + "s)");
								}

								singleFileSearch = new SingleFileSearch(0);
								singleFileSearch.start();
								singleFileSearch.join(Setting.SingleFileIntervalLevel0);
								singleFileSearch.stop();

								if (!singleFileSearch.isDone()) {
									if (singleFileSearch.isError()) {
										Log.log("project [" + now.path + "]\nfile [" + codePath
												+ "] search AST(search level 0):\n"
												+ singleFileSearch.getErrorMessage());
									} else {
										Log.log("project [" + now.path + "]\nfile [" + codePath
												+ "] search AST(search level 0):\n" + "timeout ("
												+ Setting.SingleFileIntervalLevel0 / 1000 + "s)");
									}
								}
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}

					boolean empty = false;

					if (inter.getData().isEmpty()) {
						empty = true;
					}

					if (!empty) {
						// now.result.put(codePath, inter);
						// Due to the memory resource limit, split the parsing
						// result by single .java file
						String[] pathArray = codePath.split("\\\\");
						String filePath = "D:\\Code\\Repos\\r10k_java";
						for (int k = 2; k < pathArray.length; k++) {
							filePath += "\\" + pathArray[k];
						}
						out.setSrc(now.path);
						out.outputSingleFile(now, filePath, inter);
						// out.outputSingleFile(now, codePath, inter);
						// result.put(codePath, inter);
						now.isEmpty = false;
					}
				}

				float interval = (System.currentTimeMillis() - fileBegin) / Setting.TimeUnit;
				// System.out.println("[consuming time] processing file [" +
				// codePath + "] : " + interval + " minutes");

				// long searchEnd = System.currentTimeMillis();
				//
				// System.out.println("search last: " + (searchEnd -
				// searchBegin) + "Millis");

				if (Setting.Debug) {
					System.out.println("--------search end------------");
				}

			}

			// out.output(now, result);

			// projects.set(i, now);
			// long last = System.currentTimeMillis() - projectBegin;
			// System.out.println("!!!this project use: " + last + "
			// Millis!!!");
			// System.out.println("-----project time test end-----");

			float interval = (System.currentTimeMillis() - projectBegin) / Setting.TimeUnit;
			System.out.println("[consuming time] processing project [" + now.path + "] : " + interval + " minutes");
		}

		out.close();

		System.out.println("[consuming time] All projects process : "
				+ (System.currentTimeMillis() - findprojectBegin) / Setting.TimeUnit + " minutes");
		System.out.println("search files : " + count);
	}

	class SingleFileSearch extends Thread {
		private int level = 0; // 0 for only method invocation, 1 for method
								// invocation & field
								// access
		private boolean done = false;
		private boolean error = false;
		private String errorMessage = "runtime exception";

		public SingleFileSearch(int level) {
			this.level = level;
		}

		public boolean isDone() {
			return this.done;
		}

		public boolean isError() {
			return this.error;
		}

		public String getErrorMessage() {
			return this.errorMessage;
		}

		public void run() {
			try {
				inter.setData(searcher.search(jdt.getRoot(), this.level));
				done = true;
			} catch (Exception e) {
				e.printStackTrace();
				done = false;
				error = true;
				if (e.getMessage() != null) {
					this.errorMessage = e.getMessage();
				}
			}

		}
	}

}
