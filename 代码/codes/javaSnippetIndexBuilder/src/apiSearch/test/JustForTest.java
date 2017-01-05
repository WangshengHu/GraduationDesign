package apiSearch.test;

import apiSearch.api.JDK;
import apiSearch.intermediate.Method;

public class JustForTest {
	public static void main(String[] args) {
		JDK jdk = new JDK();

		// jdk.readPackages("D:\\Code\\apiSearch-master\\.\\codehouse\\Orienteering\\Orienteering.java");
		// jdk.containsAPI("a.b.c.d.e");

		Method method = new Method();
		method.setBody(
				"{\nMap m = loadMap();\nlong start = System.currentTimeMillis();\nint[][] dist = reduction(m);\nint result = tsp(dist, m.waypoints.size());\nSystem.out.println(result);\nSystem.out.println(\"time=\" + (System.currentTimeMillis() - start) + \"ms\");\n}");
		method.setSimHash();
	}
}
