package apiSearch.test;

import apiSearch.intermediate.InterRep;
import apiSearch.intermediate.Invocation;
import apiSearch.intermediate.Method;
import apiSearch.parser.JDT;
import apiSearch.search.JDTSearch;



/**
 * 单个文件的测试用例
 * @author barry
 *
 */
public class testSearch {

	public testSearch() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String path = "./codehouse/Orienteering/Orienteering.java";
		
		JDT jdt = new JDT("java");
		jdt.SetFile(path);
		jdt.setSrc("/Users/barry/Documents/workspace/apiSearch/codehouse");
//		jdt.parse();
		
		
		JDTSearch searcher = new JDTSearch();
//		search.setAPI("org.eclipse.jdt.core.dom.ASTParser.setSource");
		searcher.setAPI("java.util.Collection.size");
		
		InterRep json = new InterRep();
		
		json.setData(searcher.search(jdt.getRoot()));
		
		for (Method i : json.getData()) {
			System.out.println("----------");
			System.out.println(i.toString());
		}
		
	}

}
