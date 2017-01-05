package apiSearch.intermediate;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import apiSearch.tool.Log;;

public class Method {

	private final int HASH_SIZE = 32;

	private String all;
	private String methodName;
	private String className;
	private String body;
	private String comment;
	private int simHash;
	private int start;
	private int length;
	private ArrayList<Invocation> invocations;

	public Method() {
		this.all = new String();
		this.methodName = new String();
		this.className = new String();
		this.body = new String();
		this.comment = new String();
		this.invocations = new ArrayList<Invocation>();
	}

	public void setAll(String all) {
		this.all = all;
	}
	
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public void setComment(String comment) {
		this.comment = comment;
	}

	public void setPos(int start, int length) {
		this.start = start;
		this.length = length;
	}

	public void setInvocations(ArrayList<Invocation> invocations) {
		this.invocations = invocations;
	}

	public void setSimHash() {
		ArrayList<Integer> tokenHashes = tokenHash(tokenise(this.body));

		int[] vector = new int[HASH_SIZE];
		for (int i = 0; i < HASH_SIZE; i++) {
			vector[i] = 0;
		}

		for (int value : tokenHashes) {
			for (int i = 0; i < HASH_SIZE; i++) {
				if (isBitSet(value, i)) {
					vector[i] += 1;
				} else {
					vector[i] -= 1;
				}
			}
		}

		int fingerPrint = 0;
		for (int i = 0; i < HASH_SIZE; i++) {
			if (vector[i] > 0) {
				fingerPrint += 1 << i;
			}
		}

		this.simHash = fingerPrint;
	}

	public String getAll() {
		return this.all;
	}
	
	public String getMethodName() {
		return this.methodName;
	}

	public String getClassName() {
		return this.className;
	}

	public String getBody() {
		return this.body;
	}
	
	public String getComment() {
		return this.comment;
	}

	public int getSimHash() {
		return this.simHash;
	}

	public int getStart() {
		return this.start;
	}

	public int getLength() {
		return this.length;
	}

	public ArrayList<Invocation> getInvocations() {
		return this.invocations;
	}

	public String getData() {
		String data = this.methodName + " | " + this.className + " | " + this.start + " | " + this.length + " | "
				+ this.simHash + "\n";
		data += this.comment + "\n";
		data += this.body + "\n";
		for (Invocation invocation : this.invocations) {
			data += invocation.getApi() + " | " + invocation.getStart() + " | " + invocation.getLength() + "\n";
		}
		return data;
	}

	private ArrayList<String> tokenise(String input) {
		StringReader sr = new StringReader(input);
		LetterTokenizer tokens = new LetterTokenizer(sr);
		ArrayList<String> texts = new ArrayList<String>();
		try {
			tokens.reset();
			while (tokens.incrementToken()) {
				String text = tokens.addAttribute(CharTermAttribute.class).toString();
				texts.add(text);
				// System.out.println(text);
			}
			tokens.end();
			tokens.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return texts;
	}

	private ArrayList<Integer> tokenHash(ArrayList<String> input) {
		ArrayList<Integer> tokenHashes = new ArrayList<Integer>();
		for (String token : input) {
			tokenHashes.add(token.hashCode());
		}
		return tokenHashes;
	}

	private boolean isBitSet(int b, int pos) {
		return (b & (1 << pos)) != 0;
	}
}
