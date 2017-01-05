package util;

public class Helper {
	private static String splitByCamelCase(String input) {
		String ret = "";
		String[] words = input.split("_");
		for (String word : words) {
			boolean lowerTag = true;
			int upperCount = 0;
			for (int i = 0; i < word.length(); ++i) {
				if (word.charAt(i) >= 'A' && word.charAt(i) <= 'Z') {
					if (lowerTag) {
						word = word.substring(0, i) + " " + word.substring(i, word.length());
						++i;
						upperCount = 0;
					}
					upperCount++;
					lowerTag = false;
				} else {
					if (!lowerTag && upperCount > 1) {
						if (word.charAt(i) >= '0' && word.charAt(i) <= '9') {
							word = word.substring(0, i) + " " + word.substring(i, word.length());
						} else {
							word = word.substring(0, i - 1) + " " + word.substring(i - 1, word.length());
						}
						upperCount = 0;
						++i;
					}
					lowerTag = true;
				}
			}
			word = word.replaceAll("\\s+", " ").toLowerCase().trim();
			// stemming
			// word = stem(word);

			ret += word.toLowerCase() + " ";
		}

		return ret.trim();
	}
	
	public static void main(String[] args) {
		String method = "M3uPlaylist";
		System.out.println(Helper.splitByCamelCase(method));
	}
}
