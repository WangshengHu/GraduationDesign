package concernLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.search.SearchHit;

public class ConcernLocater {

	private TransportClient client;
	private QueryExpansion queryExpansion;

	private final int MAX_VALUE = 99999;

	public ConcernLocater(String index_address, int port) {
		this.queryExpansion = new QueryExpansion();
		initClient(index_address, port);
	}

	private void initClient(String index_address, int port) {
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "escodehow_innerProject").build();
		this.client = new TransportClient(settings);
		client.addTransportAddress(new InetSocketTransportAddress(index_address, port));
	}

	private void matchingMethods(List<String> queries, String project, int ret_num) {
		Set<MethodOBJ> hits = new HashSet<MethodOBJ>();
		for (String query : queries) {
			SearchResponse response = this.client.prepareSearch("snippet").setTypes(project)
					.setSearchType(SearchType.DEFAULT).setQuery(query).setFrom(0).setSize(ret_num).execute()
					.actionGet();
			for (SearchHit hit : response.getHits()) {
				MethodOBJ m_obj = new MethodOBJ();
				Map<String, Object> doc = hit.getSource();
				m_obj.signature = (String) doc.get("signature");
				m_obj.filepath = (String) doc.get("filepath");
				m_obj.start = (int) doc.get("start");
				m_obj.end = (int) doc.get("end");

				hits.add(m_obj);
			}
		}

		System.out.println("----------search hits----------");
		Logger.log("----------search hits----------");

		int totalHits = 0;
		for (MethodOBJ m_obj : hits) {
			System.out.println(m_obj.signature + "  @" + m_obj.filepath + " " + m_obj.start + " " + m_obj.end);
			Logger.log(m_obj.signature + "  @" + m_obj.filepath + " " + m_obj.start + " " + m_obj.end);
			totalHits++;
		}

		System.out.println("----------total " + totalHits + " hits----------\n");
		Logger.log("----------total " + totalHits + " hits----------\n");

	}

	private List<String> constructQuery(List<String> keywords, Map<String, List<String>> expandedWords,
			String searchType) {
		List<String> queries = new ArrayList<String>();

		if (searchType.equals("camelcase")) {
			StringBuilder sb = new StringBuilder();
			boolean notFirst = false;
			for (String keyword : keywords) {
				if (notFirst) {
					sb.append(" AND ");
				} else {
					notFirst = true;
				}
				sb.append("(" + keyword);
				if (expandedWords != null) {
					List<String> words = expandedWords.get(keyword);
					if (words != null) {
						for (String word : words) {
							sb.append(" OR " + word);
						}
					}
				}
				sb.append(")");
			}
			String query_text = sb.toString();
			String query = "{\"query_string\" : {\"fields\" : [\"method_camelcase\", \"class_camelcase\"], \"query\" : \""
					+ query_text + "\"}}";
			queries.add(query);
		} else {
			List<String> perms = permutation(keywords);
			List<String> regexps = new ArrayList<String>();
			for (String perm : perms) {
				List<String> tmpRegexps = new ArrayList<String>();
				String[] words = perm.split("\\s+");
				String word = words[0];
				tmpRegexps.add("/.*" + word + ".*");
				if (expandedWords != null) {
					if (expandedWords.containsKey(word)) {
						for (String expandedWord : expandedWords.get(word)) {
							tmpRegexps.add("/.*" + expandedWord + ".*");
						}
					}
				}
				for (int i = 1; i < words.length; i++) {
					List<String> tmp = tmpRegexps;
					tmpRegexps = new ArrayList<String>();
					for (String regexp : tmp) {
						word = words[i];
						tmpRegexps.add(regexp + word + ".*");
						if (expandedWords != null) {
							if (expandedWords.containsKey(word)) {
								for (String expandedWord : expandedWords.get(word)) {
									tmpRegexps.add(regexp + expandedWord + ".*");
								}
							}
						}
					}
				}
				for (int i = 0; i < tmpRegexps.size(); i++) {
					tmpRegexps.set(i, tmpRegexps.get(i) + "/");
				}

				regexps.addAll(tmpRegexps);
			}

			StringBuilder sb = new StringBuilder();
			boolean notFirst = false;
			int clauseNum = 0;
			int i = 0;
			int num = regexps.size();
			for (String regexp : regexps) {
				if (notFirst) {
					sb.append(" OR ");
				} else {
					notFirst = true;
				}
				sb.append(regexp);
				clauseNum++;
				i++;
				if (i % 64 == 0 || i == num) {
					String query_text = sb.toString();
					String query = "{\"query_string\" : {\"default_field\" : \"class_method\", \"query\" : \""
							+ query_text + "\"}}";
					queries.add(query);
					sb = new StringBuilder();
					notFirst = false;
				}
			}

			System.out.println("----------boolean clause num : " + clauseNum + "----------\n");
			Logger.log("----------boolean clause num : " + clauseNum + "----------\n");
		}

		return queries;
	}

	private List<String> permutation(List<String> keywords) {
		List<String> list = new ArrayList<String>();
		allRange(keywords, 0, keywords.size(), list);

		return list;
	}

	private void allRange(List<String> keywords, int start, int length, List<String> list) {
		if (start == length) {
			String perm = keywords.get(0);
			for (int i = 1; i < keywords.size(); i++) {
				perm += " " + keywords.get(i);
			}
			list.add(perm);
		} else {
			for (int i = start; i < length; i++) {
				swap(keywords, start, i);
				allRange(keywords, start + 1, length, list);
				swap(keywords, start, i);
			}
		}
	}

	private void swap(List<String> list, int a, int b) {
		String tmp = list.get(a);
		list.set(a, list.get(b));
		list.set(b, tmp);
	}

	private void locate(String concern, String expansionType, String searchType, String project) {
		String[] query = concern.split("\\s+");
		List<String> keywords = new ArrayList<String>();
		for (String word : query) {
			if (!keywords.contains(word)) {
				keywords.add(word);
			}
		}

		int num = 40 * query.length;
		Map<String, List<String>> expandedWords = queryExpansion.relatedWords(keywords, expansionType, project, num);
		int expandedNum = 0;
		for (Entry<String, List<String>> entry : expandedWords.entrySet()) {
			expandedNum += entry.getValue().size();
		}

		System.out.println("----------expansion num : " + expandedNum + " ----------");
		System.out.println("----------expansion----------");
		Logger.log("----------expansion num : " + expandedNum + " ----------");
		Logger.log("----------expansion----------");

		for (String word : expandedWords.keySet()) {
			List<String> list = expandedWords.get(word);
			String words = word + " :";
			for (String expandedWord : list) {
				words += " " + expandedWord;
			}
			System.out.println(words);
			Logger.log(words);
		}

		// matchingMethods(constructQuery(keywords, expandedWords, searchType),
		// project, MAX_VALUE);
		matchingMethods(constructQuery(keywords, null, searchType), project, MAX_VALUE);
	}

	public static void main(String[] args) {
		ConcernLocater locater = new ConcernLocater("127.0.0.1", 9308);
		String[] projects = { "iReport", "javaHMO", "jBidWatcher", "jajuk" };

		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("========================================================");
			System.out.println("Please choose project (1.iReport 2.javaHMO 3.jBidWatcher 4.jajuk):");
			int p = in.nextInt();
			in.nextLine();
			String project = projects[p - 1];

			System.out.println("Please input query (q for quit):");
			String query = in.nextLine();
			if (query.equalsIgnoreCase("q")) {
				break;
			}

			Logger.setDest(project, query);
			Logger.log("PROJECT : " + project + "  QUERY : " + query);

			// System.out.println("****************************SWordNet****************************\n");
			// Logger.log("****************************SWordNet****************************\n");
			// locater.locate(query, "swordnet", "regexp", project);
			Logger.log("****************************word2vec****************************\n");
			System.out.println("****************************word2vec****************************\n");
			locater.locate(query, "word2vec", "regexp", project);
			System.out.println();
			Logger.log("");

			Logger.close();
		}
	}
}
