package util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SWordNet {

	public static void extractLists(String file, String outFile) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile, false), "utf-8"));

			Map<String, List<String>> relatedWords = new HashMap<String, List<String>>();

			String row;
			while ((row = reader.readLine()) != null) {
				String[] pairs = row.split("\\s+");
				String lword = pairs[0];
				String rword = pairs[1];
				List<String> list = new ArrayList<String>();
				if (relatedWords.containsKey(lword)) {
					list = relatedWords.get(lword);
				}
				if (!list.contains(rword)) {
					list.add(rword);
				}
				relatedWords.put(lword, list);
				list = new ArrayList<String>();
				if (relatedWords.containsKey(rword)) {
					list = relatedWords.get(rword);
				}
				if (!list.contains(lword)) {
					list.add(lword);
				}
				relatedWords.put(rword, list);
			}
			reader.close();

			for (Entry<String, List<String>> entry : relatedWords.entrySet()) {
				String word = entry.getKey();
				List<String> list = entry.getValue();
				String data = word;
				for (String rword : list) {
					data += " " + rword;
				}
				writer.println(data);
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void extractTransitiveLists(String file, String outFile) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile, false), "utf-8"));

			Map<String, List<String>> relatedWords = new HashMap<String, List<String>>();
			Map<String, List<String>> transReWords = new HashMap<String, List<String>>();

			String row;
			while ((row = reader.readLine()) != null) {
				String[] words = row.split("\\s+");
				String word = words[0];
				List<String> list = new ArrayList<String>();
				for (int i = 1; i < words.length; i++) {
					list.add(words[i]);
				}
				relatedWords.put(word, list);
			}
			reader.close();

			for (String term : relatedWords.keySet()) {
				List<String> list = new ArrayList<String>();
				List<String> list0 = relatedWords.get(term);
				list.addAll(list0);
				for (String word0 : list0) {
					List<String> list1 = relatedWords.get(word0);
					for (String word1 : list1) {
						if (word1.equals(term) || list.contains(word1)) {
							continue;
						}
						list.add(word1);
						List<String> list2 = relatedWords.get(word1);
						for (String word2 : list2) {
							if (word2.equals(term) || list.contains(word2)) {
								continue;
							}
							list.add(word2);
						}
					}
				}
				transReWords.put(term, list);
			}

			for (Entry<String, List<String>> entry : transReWords.entrySet()) {
				String word = entry.getKey();
				List<String> list = entry.getValue();
				String data = word;
				for (String rword : list) {
					data += " " + rword;
				}
				writer.println(data);
			}
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void combine(String file1, String file2, String outFile) {
		try {
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "utf-8"));
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2), "utf-8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile, false), "utf-8"));

			String row;
			while ((row = reader1.readLine()) != null) {
				String[] pairs = row.split(",");
				String data = pairs[0] + " " + pairs[1];
				writer.println(data);
			}
			reader1.close();
			while ((row = reader2.readLine()) != null) {
				String[] pairs = row.split(",");
				String data = pairs[0] + " " + pairs[1];
				writer.println(data);
			}
			reader2.close();

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void combine(String file1, String file2, String file3, String outFile) {
		try {
			BufferedReader reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(file1), "utf-8"));
			BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file2), "utf-8"));
			BufferedReader reader3 = new BufferedReader(new InputStreamReader(new FileInputStream(file3), "utf-8"));
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile, false), "utf-8"));

			String row;
			while ((row = reader1.readLine()) != null) {
				String[] pairs = row.split(",");
				String data = pairs[0] + " " + pairs[1];
				writer.println(data);
			}
			reader1.close();
			while ((row = reader2.readLine()) != null) {
				String[] pairs = row.split(",");
				String data = pairs[0] + " " + pairs[1];
				writer.println(data);
			}
			reader2.close();
			while ((row = reader3.readLine()) != null) {
				String[] pairs = row.split(",");
				String data = pairs[0] + " " + pairs[1];
				writer.println(data);
			}
			reader3.close();

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SWordNet.combine("D:\\SSwords\\SWordNet\\share\\iReport\\iReport_c_process.csv",
				"D:\\SSwords\\SWordNet\\share\\iReport\\iReport_cc_process.csv",
				"D:\\SSwords\\SWordNet\\share\\iReport\\iReport_id_process.csv",
				"D:\\SSwords\\SWordNet\\iReport_process.txt");
		SWordNet.extractLists("D:\\SSwords\\SWordNet\\iReport_process.txt",
				"D:\\SSwords\\SWordNet\\iReport_closet.txt");
		SWordNet.extractTransitiveLists("D:\\SSwords\\SWordNet\\iReport_closet.txt",
				"D:\\SSwords\\SWordNet\\iReport_closet_trans.txt");
	}

}
