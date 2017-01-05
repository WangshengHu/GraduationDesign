package apiSearch.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import apiSearch.api.JDK;
import apiSearch.intermediate.InterRep;
import apiSearch.intermediate.Invocation;
import apiSearch.intermediate.Method;
import apiSearch.staticClass.Setting;
import apiSearch.tool.Input;
import apiSearch.tool.Project;

public class Format extends Output {

	public String saveDir;
	public String saveFile;
	public PrintWriter writer;

	public Format(String path) {
		this.saveDir = path;
		this.saveFile = path + "/database.txt";

		try {
			File dir = new File(this.saveDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			this.writer = new PrintWriter(new BufferedWriter(new FileWriter(this.saveFile, Input.append)));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getProjId(Project now, String src) {
		String id = "";

		switch (src) {
		case "github":
			String name = getProjName(now, src);
			int idx = name.indexOf("_");
			id = name.substring(0, idx);
			break;
		default:
			id = Integer.toString(Integer.MAX_VALUE);
			break;
		}

		return id;
	}

	public String getProjName(Project now, String src) {
		String name = "";

		switch (src) {
		case "github":
			int idx = now.path.lastIndexOf("\\");
			name = now.path.substring(idx + 1, now.path.length());
			// String[] nameArray = tmp.split("_");
			// for (int i = 2; i < nameArray.length; i++) {
			// name += nameArray[i];
			// }
			break;
		default:
			idx = now.path.lastIndexOf("\\");
			name = now.path.substring(idx + 1, now.path.length());
			break;
		}

		return name;
	}

	public int getRank(int length) {
		int rank = Math.abs(length - 8) / 3;

		return rank;
	}

	@Override
	public void output(Project now, Map<String, InterRep> result) {
	}

	@Override
	public void outputSingleFile(Project now, String file, InterRep result) {
		long writeBegin = System.currentTimeMillis();

		if (result.getData().isEmpty()) {
			return;
		}

		this.writer.println("******" + file + "******");

		for (Method j : result.getData()) {
			String data = j.getData();

			this.writer.println(data);
			// System.out.println(apiUsage);

			// if (!result.isEmpty()) {
			// System.out.println("" + result);
			// }
		}

		this.writer.flush();

		float interval = (System.currentTimeMillis() - writeBegin) / Setting.TimeUnit;
		// System.out.println("[consuming time] writing project [" + file + "] :
		// " + interval + " minutes");
	}

	public void close() {
		this.writer.close();
	}

	@Override
	public void setSrc(String path) {
		// TODO Auto-generated method stub

	}

}
