package concernLocation;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class QueryExpansion {

	private Map<String, Map<String, Double>> word2vecSet;
	private Map<String, Map<String, List<String>>> swordnetSet;

	public QueryExpansion() {
		this.word2vecSet = new HashMap<String, Map<String, Double>>();
		this.swordnetSet = new HashMap<String, Map<String, List<String>>>();

		// fetchWord2vecTerms("D:/SSwords/word2vec/body_camelcase_closet.txt");
		fetchSwordnetTerms("iReport", "D:/SSwords/SWordNet/iReport_closet.txt");
		fetchSwordnetTerms("javaHMO", "D:/SSwords/SWordNet/javaHMO_closet.txt");
		fetchSwordnetTerms("jBidWatcher", "D:/SSwords/SWordNet/jbidwatcher_closet.txt");
		fetchSwordnetTerms("jajuk", "D:/SSwords/SWordNet/jajuk_closet.txt");
	}

	public Map<String, List<String>> relatedWords(List<String> query, String type, String project, int num) {
		if (type.equals("word2vec")) {
			return closestTerm(query, "D:/SSwords/word2vec/small_corpus/verb_noun_filtered.txt");
		} else {
			return relatedWord(query, project);
		}
	}

	public Map<String, List<String>> relatedWord(List<String> query, String project) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		Map<String, List<String>> projectWords = swordnetSet.get(project);
		for (String keyword : query) {
			List<String> list = projectWords.get(keyword);
			if (list != null) {
				result.put(keyword, list);
			}
		}

		return result;
	}

	public Map<String, List<String>> closestTerm(List<String> query, String path) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));

			int num = 0;
			String row;
			while ((row = reader.readLine()) != null) {
				String[] wordsWithSim = row.split("\\s+");
				String word = wordsWithSim[0];
				for (String keyword : query) {
					if (word.equals(keyword)) {
						List<String> list = new ArrayList<String>();
						for (int i = 1; i + 1 < wordsWithSim.length; i++) {
							list.add(wordsWithSim[i]);
							i++;
						}
						result.put(word, list);
						num++;
						if (num == query.size()) {
							break;
						}
					}
				}
			}

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public Map<String, List<String>> closestTerm(List<String> query, int num) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		List<ExpandedTerm> ranked_terms = new ArrayList<ExpandedTerm>();
		for (String keyword : query) {
			Map<String, Double> expandedTerms = this.word2vecSet.get(keyword);
			if (expandedTerms != null) {
				for (Map.Entry<String, Double> entry : expandedTerms.entrySet()) {
					ranked_terms.add(new ExpandedTerm(entry.getKey(), keyword, entry.getValue()));
				}
			}
		}

		Collections.sort(ranked_terms, new Comparator<ExpandedTerm>() {
			public int compare(ExpandedTerm t1, ExpandedTerm t2) {
				double v1 = t1.similarity;
				double v2 = t2.similarity;

				if (Math.abs(v1 - v2) < 0.000000001) {
					return 0;
				} else if (v1 > v2) {
					return -1;
				} else {
					return 1;
				}

				// return v1 - v2 >= 0 ? -1 : 1;
			}
		});

		for (int i = 0; i < num && i < ranked_terms.size(); i++) {
			ExpandedTerm term = ranked_terms.get(i);
			if (result.containsKey(term.orgTerm)) {
				List<String> expandedTerms = result.get(term.orgTerm);
				expandedTerms.add(term.term);
				result.put(term.orgTerm, expandedTerms);
			} else {
				List<String> expandedTerms = new ArrayList<String>();
				expandedTerms.add(term.term);
				result.put(term.orgTerm, expandedTerms);
			}
		}

		return result;
	}

	public Map<String, List<String>> closestTerm(List<String> query, double thres) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		List<ExpandedTerm> ranked_terms = new ArrayList<ExpandedTerm>();
		for (String keyword : query) {
			Map<String, Double> expandedTerms = this.word2vecSet.get(keyword);
			if (expandedTerms != null) {
				for (Map.Entry<String, Double> entry : expandedTerms.entrySet()) {
					ranked_terms.add(new ExpandedTerm(entry.getKey(), keyword, entry.getValue()));
				}
			}
		}

		Collections.sort(ranked_terms, new Comparator<ExpandedTerm>() {
			public int compare(ExpandedTerm t1, ExpandedTerm t2) {
				double v1 = t1.similarity;
				double v2 = t2.similarity;

				if (Math.abs(v1 - v2) < 0.000000001) {
					return 0;
				} else if (v1 > v2) {
					return -1;
				} else {
					return 1;
				}

				// return v1 - v2 >= 0 ? -1 : 1;
			}
		});

		for (int i = 0; i < ranked_terms.size(); i++) {
			ExpandedTerm term = ranked_terms.get(i);
			if (term.similarity < thres) {
				break;
			}
			if (result.containsKey(term.orgTerm)) {
				List<String> expandedTerms = result.get(term.orgTerm);
				expandedTerms.add(term.term);
				result.put(term.orgTerm, expandedTerms);
			} else {
				List<String> expandedTerms = new ArrayList<String>();
				expandedTerms.add(term.term);
				result.put(term.orgTerm, expandedTerms);
			}
		}

		return result;
	}

	public Map<String, List<String>> closestTerm(List<String> query, int num, double thres) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();

		Set<String> uniqueSet = new HashSet<String>();
		List<ExpandedTerm> ranked_terms = new ArrayList<ExpandedTerm>();
		for (String keyword : query) {
			Map<String, Double> expandedTerms = this.word2vecSet.get(keyword);
			if (expandedTerms != null) {
				for (Map.Entry<String, Double> entry : expandedTerms.entrySet()) {
					ranked_terms.add(new ExpandedTerm(entry.getKey(), keyword, entry.getValue()));
				}
			}
		}

		Collections.sort(ranked_terms, new Comparator<ExpandedTerm>() {
			public int compare(ExpandedTerm t1, ExpandedTerm t2) {
				double v1 = t1.similarity;
				double v2 = t2.similarity;

				if (Math.abs(v1 - v2) < 0.000000001) {
					return 0;
				} else if (v1 > v2) {
					return -1;
				} else {
					return 1;
				}

				// return v1 - v2 >= 0 ? -1 : 1;
			}
		});

		// remove duplicated terms
		for (int i = 0; i < ranked_terms.size();) {
			ExpandedTerm term = ranked_terms.get(i);
			if (query.contains(term.term) || uniqueSet.contains(term.term)) {
				ranked_terms.remove(i);
				continue;
			}
			uniqueSet.add(term.term);
			i++;
		}

		int i = 0;
		for (; i < ranked_terms.size() && i < num; i++) {
			ExpandedTerm term = ranked_terms.get(i);
			if (term.similarity < thres) {
				break;
			}
			if (result.containsKey(term.orgTerm)) {
				List<String> expandedTerms = result.get(term.orgTerm);
				expandedTerms.add(term.term);
				result.put(term.orgTerm, expandedTerms);
			} else {
				List<String> expandedTerms = new ArrayList<String>();
				expandedTerms.add(term.term);
				result.put(term.orgTerm, expandedTerms);
			}
		}

		// ensure each original term has at least 3 expanded terms
		if (i < ranked_terms.size()) {
			for (Entry<String, List<String>> entry : result.entrySet()) {
				String orgTerm = entry.getKey();
				List<String> expandedTerms = entry.getValue();
				int sum = expandedTerms.size();
				if (sum < 3) {
					for (i = num; i < ranked_terms.size(); i++) {
						ExpandedTerm term = ranked_terms.get(i);
						if (term.orgTerm.equals(orgTerm)) {
							expandedTerms.add(term.term);
							sum++;

							if (sum == 3) {
								break;
							}
						}
					}
					result.put(orgTerm, expandedTerms);
				}
			}
		}

		return result;
	}

	private void fetchWord2vecTerms(String path) {
		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));

			String row;
			while ((row = reader.readLine()) != null) {
				String[] wordsWithSim = row.split("\\s+");
				String word = wordsWithSim[0];
				Map<String, Double> candidates = new HashMap<String, Double>();
				for (int i = 1; i + 1 < wordsWithSim.length; i++) {
					double w = Double.parseDouble(wordsWithSim[i + 1]);
					candidates.put(wordsWithSim[i], w);
					i++;
				}
				this.word2vecSet.put(word, candidates);
			}

			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void fetchSwordnetTerms(String project, String path) {
		try {
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path), "utf-8"));

			Map<String, List<String>> terms = new HashMap<String, List<String>>();
			String row;
			while ((row = reader.readLine()) != null) {
				String[] words = row.split("\\s+");
				String word = words[0];
				List<String> list = new ArrayList<String>();
				for (int i = 1; i < words.length; i++) {
					list.add(words[i]);
				}
				terms.put(word, list);
			}

			swordnetSet.put(project, terms);

			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ExpandedTerm {
		private String term;
		private String orgTerm;
		private double similarity;

		public ExpandedTerm(String term, String orgTerm, double similarity) {
			this.term = term;
			this.orgTerm = orgTerm;
			this.similarity = similarity;
		}
	}

}
