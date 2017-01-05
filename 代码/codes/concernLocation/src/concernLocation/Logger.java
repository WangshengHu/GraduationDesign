package concernLocation;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Logger {

	private static String dir = "D:/SSwords/evaluation/content_java/compWithBaseline/";
	private static PrintWriter writer;

	public static void setDest(String project, String query) {
		query = query.replaceAll("\\s+", "_");
		String file = dir + project + "_" + query + ".txt";
		try {
			if (writer != null) {
				close();
			}
			writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void log(String data) {
		writer.println(data);
	}

	public static void close() {
		writer.flush();
		writer.close();
	}
}
