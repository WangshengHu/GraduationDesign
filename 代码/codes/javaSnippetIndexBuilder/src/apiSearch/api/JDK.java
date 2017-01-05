package apiSearch.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import apiSearch.staticClass.Setting;

/**
 * 用于寻找JDK官方API集合的类
 * 
 * @author barry
 *
 */
public class JDK extends Official {

	Map<String, ArrayList<String>> libs = new HashMap<String, ArrayList<String>>();
	String path;

	public JDK() {
		// TODO Auto-generated constructor stub
		this.path = "./java";
	}

	public JDK(String path) {
		// TODO Auto-generated constructor stub
		this.path = path;
	}

	public void readTop(String path, String libName) {
		File f = new File(path);
		File[] files = f.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					findPackage(file.getAbsolutePath(), file.getName(), libName);
				}
			}
		}
		if (Setting.Debug) {
			System.out.println(toString());
		}
	}

	private void findPackage(String path, String packageName, String libName) {
		// TODO Auto-generated method stub
		File f = new File(path);
		File[] files = f.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					String nextPackageName = packageName + "." + file.getName();

					// Check if the file is truly a package
					File[] subFiles = file.listFiles();
					boolean isPackage = false;
					for (File subFile : subFiles) {
						if (subFile.isFile()) {
							isPackage = true;
							break;
						}
					}

					if (isPackage) {
						apis.add(nextPackageName);
						libs.get(libName).add(nextPackageName);
					}

					findPackage(file.getAbsolutePath(), nextPackageName, libName);

				}
			}
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		// String result = "JDK API listing:\n";
		String result = "";
		for (String pacakgeName : apis) {
			result += pacakgeName + "\n";
		}
		return result;
	}

	public void writeDisk(String filePath) {
		try {
			// write all packages found
			Writer writer = new BufferedWriter(new FileWriter(filePath + "/all.txt"));
			writer.write(this.toString());
			writer.close();

			// write packages in every single lib source code
			for (Map.Entry<String, ArrayList<String>> lib : libs.entrySet()) {
				writer = new PrintWriter(new FileWriter(filePath + "/" + lib.getKey() + ".txt"));
				for (String packageName : lib.getValue()) {
					((PrintWriter) writer).println(packageName);
				}
				
				writer.close();
			}
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean isInJDK(String api) {

		String[] line = api.split("\\.");
		ArrayList<String> names = new ArrayList<>();
		for (int i = 0; i < line.length; i++) {
			names.add(line[i]);
		}
		String name = names.get(names.size() - 1);
		String type = "";
		for (int i = 0; i < names.size() - 2; i++) {
			type += names.get(i) + ".";
		}
		type += names.get(names.size() - 2);

		if (apis.contains(type)) {
			return true;
		} else if (type.contains(".")) {
			return isInJDK(type);
		}

		return false;
	}

	public void readPackages(String filePath) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			String packageName;
			while ((packageName = reader.readLine()) != null) {
				apis.add(packageName);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setSingleApi(String api) {
		
	}

	@Override
	public void findOfficialAPI() {
		File f = new File(this.path);
		File[] files = f.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				libs.put(file.getName(), new ArrayList<String>());
				readTop(file.getAbsolutePath(), file.getName());
			}
		}
	}

	@Override
	public boolean isInOfficialAPI(String api) {
		// TODO Auto-generated method stub
		return isInJDK(api);
	}

	public static boolean containsAPI(String apiType) {
		if (apis.contains(apiType)) {
			return true;
		} else if (apiType.contains(".")) {
			return containsAPI(apiType.substring(0, apiType.lastIndexOf('.')));
		}

		return false;
	}

}
