package apiSearch.tool;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Log {

	public static String logFile;
	public static String logCurrent;
	public static String logCounter;

	public static void setLog(String file, String current) {
		logFile = file;
		logCurrent = current;

		File f = new File(file);
		File fc = new File(current);

		if (Input.append) {
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			if (f.exists()) {
				f.delete();
			}

			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (fc.exists()) {
			fc.delete();
		}

		try {
			fc.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void log(String data) {
		try {
			Writer writer = new FileWriter(logFile, true);
			writer.write(data + "\n\n");

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void logCurrent(String data) {
		try {
			Writer writer = new FileWriter(logCurrent, false);
			writer.write(data + "\n");

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void logCounter(long counter) {
		try {
			Writer writer = new FileWriter(logCounter, false);
			writer.write(counter + "\n");

			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
