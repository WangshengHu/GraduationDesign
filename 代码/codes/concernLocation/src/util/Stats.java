package util;

import java.util.Scanner;

public class Stats {

	public static float fmeasure(float a, float b) {
		return 2 * a * b / (a + b);
	}

	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		while (true) {
			System.out.println("Please input float a, b:");
			float a = in.nextFloat();
			float b = in.nextFloat();
			System.out.println("F-measure is: " + fmeasure(a, b));
		}
	}
}
