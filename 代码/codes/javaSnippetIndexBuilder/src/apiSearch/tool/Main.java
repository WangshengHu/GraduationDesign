package apiSearch.tool;

import strategy.JavaStrategy;
import strategy.Strategy;
import apiSearch.api.JDK;
import apiSearch.intermediate.FileIO;
import apiSearch.staticClass.Language;

/**
 * API搜索的入口，若要增加新的支持，需要： 1.增加isOfficalAPI(...)中对应的语言官方API检测
 * 2.增加makeStrategy(...)方法中对应的新搜索策略的对象创建
 * 
 * @author barry
 *
 */
public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
		Language.set();
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Main main = new Main();
		main.begin(args);
	}

	private void begin(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Input.read(args);

		// process api
		if (processApi()) {
			return;
		}

		Log.setLog(Input.logFile, Input.logCurrent);

		Strategy strategy = makeStrategy();
		String extension = Language.getExtension("java");
		strategy.strategy(extension);

		// System.out.println("-------------intermediate data
		// saving---------------");
		//
		// for (int i = 0; i < strategy.projects.size(); i++) {
		//
		// Project now = strategy.projects.get(i);
		//
		// FileIO io = new FileIO(makePath(Input.savePath, now.name));
		// io.write(now);
		//
		// }

	}

	private boolean processApi() {
		JDK jdk = new JDK();
		jdk.readPackages(Input.apiFile);

		System.out.println("****************************************************");
		System.out.println("*******************search Api mode******************");
		System.out.println("****************************************************");

		return false;
	}

	private Strategy makeStrategy() {
		JavaStrategy strategy = new JavaStrategy();
		return strategy;
	}

	private String makePath(String path, String name) {
		return path + "/" + name;
	}

}
