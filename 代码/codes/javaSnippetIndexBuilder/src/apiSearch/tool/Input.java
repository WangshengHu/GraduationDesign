package apiSearch.tool;

import java.io.File;

/**
 * 读取输入参数
 * 
 * @author barry
 *
 */
public class Input {

	public static String inter = new String();
	public static String output = new String();

	public static String srcPath = new String();// 整个代码库的存放目录路径
	public static String savePath = new String();// 整个结果的保存位置
	public static String apiFile = new String();// 需要查找的所有API存放文件路徑
	public static String logFile = new String();
	public static String logCurrent = new String();

	public static boolean debug = false;
	public static boolean append = false;

	public static int startProj = 1;
	public static int startFile = 1;
	public static long counter = 1;

	public static void read(String[] args) throws Exception {

		for (int i = 0; i < args.length; i++) {
			String now = args[i];
			if (now.startsWith("-intermediate")) {
				if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
					inter = args[i + 1];
					i++;
				} else {
					inter = "default";
				}
			} else if (now.startsWith("-output")) {
				output = args[i + 1];
				i++;
			} else if (now.startsWith("-codePath")) {
				srcPath = args[i + 1];
				i++;
			} else if (now.startsWith("-savePath")) {
				savePath = args[i + 1];
				i++;
			} else if (now.startsWith("-apiFile")) {
				apiFile = args[i + 1];
				i++;
			} else if (now.startsWith("-logFile")) {
				logFile = args[i + 1];
				i++;
			} else if (now.startsWith("-debug")) {
				debug = true;
			} else if (now.startsWith("-append")) {
				append = true;
			} else if (now.startsWith("-startProject")) {
				startProj = Integer.parseInt(args[i + 1]);
				i++;
			} else if (now.startsWith("-startFile")) {
				startFile = Integer.parseInt(args[i + 1]);
				i++;
			} else if (now.startsWith("-logCurrent")) {
				logCurrent = args[i + 1];
				i++;
			} else if (now.startsWith("-counter")) {
				counter = Long.parseLong(args[i + 1]);
				i++;
			} else {
				System.err.println(now);
				throw new Exception("invalid option exist!");
			}
		}

		File f = new File(savePath);
		File[] files = f.listFiles();

		// if (output.isEmpty() || srcPath.isEmpty() || savePath.isEmpty()) {
		// throw new Exception("not enough option given! at least need:
		// -output,-codePath,-savePath");
		// }
	}

}
