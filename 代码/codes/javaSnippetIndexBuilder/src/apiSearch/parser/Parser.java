package apiSearch.parser;

/**
 * Parser基类，若要支持对新语言的parse，需要：
 * 	1.继承并实现相应的抽象方法
 *	2.root作为parse后AST的根，其类型可以自行向下转型
 * @author barry
 *
 */
public abstract class Parser {
	
	String language;//parser适用的语言
	String path;//单个源代码文件的存放位置
	
	Object root;

	public Parser(String language) {
		// TODO Auto-generated constructor stub
		this.language = language;
		this.path = new String();
		root = null;
	}
	
	public abstract void parse(String name);
	public abstract void SetFile(String path);
	public abstract void setSrc(String src);
	public abstract Object getRoot();
	public abstract boolean isEmpty();

}
