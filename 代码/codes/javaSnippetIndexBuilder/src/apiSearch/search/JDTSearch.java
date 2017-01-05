package apiSearch.search;

import java.awt.Window.Type;
import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.internal.corext.dom.ASTNodes;

import apiSearch.api.JDK;
import apiSearch.intermediate.Invocation;
import apiSearch.intermediate.Method;
import apiSearch.tool.Helper;

/**
 * 自定义的ASTVisitor基类
 * 
 * @author barry
 *
 */
class MyVisitor extends ASTVisitor {

	// String type;
	// String name;
	int counter;

	public MyVisitor() {
		// this.type = type;
		// this.name = name;
		this.counter = 0;
	}

	// 用于处理java.util.Collection<java.lang.String>这样的情况，即带有类型参数的那些类型
	public String trimName(String name) {
		if (name.contains("<")) {
			String[] names = name.split("<");
			return names[0];
		}
		return name;
	}

	public int getCount() {
		return this.counter;
	}
}

class MethodVisitor extends MyVisitor {

	final int MIN_NUM_OF_LINES = 5;
	final int MIN_NUM_OF_BODY_LINES = 4;
	final int MAX_NUM_OF_LINES = 160;
	final int MAX_NUM_OF_BODY_LINES = 128;
	final int MAX_NUM_OF_CHARS = 9012;

	CompilationUnit cu;
	ArrayList<Method> input;
	int level; // 0 for invocation only, 1 for field access extra, 2 for
				// constructor extra

	public MethodVisitor(CompilationUnit cu, ArrayList<Method> input, int level) {
		this.cu = cu;
		this.input = input;
		this.level = level;
	}

	public boolean visit(MethodDeclaration node) {

		// filter some noises (methods that are constructors, too short and
		// etc.)
		if (node.isConstructor()) {
			return true;
		}

		int start = node.getStartPosition();
		int length = node.getLength();
		if (length >= MAX_NUM_OF_CHARS) {
			return true;
		}
		int numLines = cu.getLineNumber(start + length) - cu.getLineNumber(start) + 1;
		if (numLines < MIN_NUM_OF_LINES) {
			return true;
		}

		Method method = new Method();

		String methodName = node.getName().toString();
		// if method is main function, ignore it
		if (methodName.equalsIgnoreCase("main")) {
			return true;
		}
		// if method is for testing, ignore it
		if (Helper.splitByCamelCase(methodName).contains("test")) {
			return true;
		}

		String all = node.toString();
		String className = "";
		String body = "";
		String comment = "";
		IMethodBinding binding = node.resolveBinding();
		if (binding == null) {
			return true;
		}
		ITypeBinding declaringClass = binding.getDeclaringClass();
		// if (declaringClass != null) {
		// // if it's anonymous inner class, className will be ""
		// className = declaringClass.getName();
		// }
		if (declaringClass == null) {
			return true;
		}
		className = declaringClass.getName();

		Javadoc doc = node.getJavadoc();
		if (doc != null) {
			comment = doc.toString();
		}

		Block block = node.getBody();
		if (block != null) {
			int bodyStart = block.getStartPosition();
			int bodyLength = block.getLength();
			int numBodyLines = cu.getLineNumber(bodyStart + bodyLength) - cu.getLineNumber(bodyStart) + 1;
			if (numBodyLines < MIN_NUM_OF_BODY_LINES) {
				return true;
			}
			body = block.toString();
		} else {
			return true;
		}

		method.setAll(all);
		method.setMethodName(methodName);
		method.setClassName(className);
		method.setBody(body);
		method.setComment(comment);
		method.setPos(start, length);
		method.setSimHash();

		// traverse method body to find all api invocations
		int count = 0;
		ArrayList<Invocation> invocations = new ArrayList<Invocation>();

		InvocationVisitor invocationVisitor = new InvocationVisitor(invocations);
		node.accept(invocationVisitor);
		count += invocationVisitor.getCount();

		if (this.level > 0) {
			FieldAccessVisitor fieldAccessVisitor = new FieldAccessVisitor(invocations);
			node.accept(fieldAccessVisitor);
			count += fieldAccessVisitor.getCount();
		}

		if (this.level > 1) {
			ConstructorVisitor constructorVisitor = new ConstructorVisitor(invocations);
			node.accept(constructorVisitor);
			count += constructorVisitor.getCount();
		}

		method.setInvocations(invocations);

		input.add(method);

		return true;
	}
}

class InvocationVisitor extends MyVisitor {

	ArrayList<Invocation> input;

	public InvocationVisitor(ArrayList<Invocation> input) {
		super();
		this.input = input;
	}

	public boolean visit(MethodInvocation node) {

		Expression exp = node.getExpression();

		if (exp == null) {
			return true;
		}

		ITypeBinding typeBinding = exp.resolveTypeBinding();

		if (typeBinding != null) {
			String type = trimName(typeBinding.getQualifiedName());
			if (JDK.containsAPI(type)) {
				Invocation invocation = new Invocation(false);

				int start = node.getStartPosition();
				int length = node.getLength();
				// 记录api的相关信息
				invocation.setPos(start, length);
				invocation.setApi(type + "." + node.getName());

				// System.out.println(type + "." + node.getName() + " " + start
				// + " " + length);

				input.add(invocation);
			}
		}

		counter++;

		return true;
	}

}

class FieldAccessVisitor extends MyVisitor {

	ArrayList<Invocation> input;

	public FieldAccessVisitor(ArrayList<Invocation> input) {
		super();
		this.input = input;
	}

	public boolean visit(QualifiedName node) {

		Expression exp = node.getQualifier();
		if (exp == null) {
			return true;
		}

		ITypeBinding typeBinding = exp.resolveTypeBinding();

		if (typeBinding != null) {

			String type = trimName(typeBinding.getQualifiedName());
			if (JDK.containsAPI(type)) {
				Invocation invocation = new Invocation(false);

				int start = node.getStartPosition();
				int length = node.getLength();
				// 记录api的相关信息
				invocation.setPos(start, length);
				invocation.setApi(type + "." + node.getName());

				// System.out.println(type + "." + node.getName() + " " + start
				// + " " + length);
			}
		}

		counter++;

		return true;
	}

}

class ConstructorVisitor extends MyVisitor {

	ArrayList<Invocation> input;

	public ConstructorVisitor(ArrayList<Invocation> input) {
		super();
		this.input = input;
	}

	public boolean visit(ClassInstanceCreation node) {

		IMethodBinding methodBinding = node.resolveConstructorBinding();

		if (methodBinding == null) {
			return true;
		}

		ITypeBinding typeBinding = methodBinding.getDeclaringClass();

		if (typeBinding != null) {

			String type = trimName(typeBinding.getQualifiedName());

			if (JDK.containsAPI(type)) {
				Invocation invocation = new Invocation(true);

				int start = node.getStartPosition();
				int length = node.getLength();
				// 记录api的相关信息
				invocation.setPos(start, length);
				invocation.setApi(type);

				// System.out.println(type + " " + start + " " + length);
			}
		}

		counter++;

		return true;
	}

}

/**
 * 采用JDT Core进行API搜索，实现了若干个Visitor用于进行API搜索
 * 
 * @author barry
 *
 */
public class JDTSearch extends Search {

	public JDTSearch() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Consider both method invocation and field access and constructor
	 * invocation, which could be really slow
	 *
	 */
	public ArrayList<Method> search(Object root, int level) {

		final CompilationUnit cu = (CompilationUnit) root;

		ArrayList<Method> methods = new ArrayList<Method>();

		MethodVisitor methodVisitor = new MethodVisitor(cu, methods, level);
		cu.accept(methodVisitor);

		return methods;
	}

	@Override
	public ArrayList<Method> search(Object root) {
		// TODO Auto-generated method stub
		return null;
	}

}
