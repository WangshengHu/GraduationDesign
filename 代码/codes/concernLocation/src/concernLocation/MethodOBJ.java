package concernLocation;

public class MethodOBJ {
	public String class_name;
	public String method_name;
	public String signature;
	public String filepath;
	public long start;
	public long end;
	public double score;

	public MethodOBJ() {
		this.class_name = "";
		this.method_name = "";
		this.filepath = "";
		this.start = 0;
		this.end = 0;
		this.score = 0.0;
	}

	public MethodOBJ(String class_name, String method_name, String filepath, String remark, double score) {
		this.class_name = class_name;
		this.method_name = method_name;
		this.filepath = filepath;
		this.score = score;
	}

	public String toString() {
		String ret = "";
		ret += "class: " + this.class_name + ", method: " + this.method_name + "\n";
		ret += "filepath: " + this.filepath + "\n";
		ret += "score: " + this.score + "\n";
		ret += "==================================================";
		return ret;
	}

	public String getClassName() {
		return this.class_name;
	}

	public void setClassName(String class_name) {
		this.class_name = class_name;
	}

	public String getMethodName() {
		return this.method_name;
	}

	public void setMethodName(String method_name) {
		this.method_name = method_name;
	}

	public String getSignature() {
		return this.signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getFilepath() {
		return this.filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public double getScore() {
		return this.score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof MethodOBJ) {
			MethodOBJ obj = (MethodOBJ) object;
			return filepath.equals(obj.filepath) && start == obj.start && end == obj.end;
		}

		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}
}