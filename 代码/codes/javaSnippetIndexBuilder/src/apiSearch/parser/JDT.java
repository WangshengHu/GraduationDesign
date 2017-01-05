package apiSearch.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import apiSearch.staticClass.Setting;
import apiSearch.tool.Log;

/**
 * 采用JDT Core进行parse,其root为CompilationUnit类型
 * 
 * @author barry
 *
 */
public class JDT extends Parser {

	String src;
	String extension;

	public JDT(String language) {
		// TODO Auto-generated constructor stub
		super(language);
		extension = ".json";
	}

	@Override
	public void parse(String name) {
		// TODO Auto-generated method stub
		String str;

		try {

			long parseBegin = System.currentTimeMillis();
			// long read = System.currentTimeMillis();
			str = readFileToString(path);
			// if (!str.contains("." + name + "(")) {
			// root = null;
			// return;
			// }
			// System.out.println("file read: " + (System.currentTimeMillis() -
			// read));
			ASTParser parser = ASTParser.newParser(AST.JLS8);
			parser.setSource(str.toCharArray());
			parser.setEnvironment(
					// classpath，绝对路径
					null,
					// sourcepath，绝对路径
					// "/Users/barry/Documents/workspace/apiSearch/codehouse"
					new String[] {  }, null, true);
			parser.setUnitName("AST");
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			// 建立binding
			parser.setResolveBindings(true);
			parser.setBindingsRecovery(true);
			// long parse = System.currentTimeMillis();
			final CompilationUnit cu = (CompilationUnit) parser.createAST(null);
			// System.out.println("file parse: " + (System.currentTimeMillis() -
			// parse));
			root = cu;
			float interval = (System.currentTimeMillis() - parseBegin) / Setting.TimeUnit;
			// System.out.println("[consuming time] parsing file [" + path + "]
			// : " + interval + " minutes");
		} catch (Exception e) {
			e.printStackTrace();

			Log.log("project [" + this.src + "]\nfile [" + this.path + "] generate AST:\n" + e.getMessage());
		}

	}

	public String readFileToString(String filePath) throws IOException {

		StringBuilder fileData = new StringBuilder(2000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();

		return fileData.toString();
	}

	public void SetFile(String path) {
		this.path = path;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public CompilationUnit getRoot() {
		return (CompilationUnit) root;
	}

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		if (root == null) {
			return true;
		}
		return false;
	}

}
