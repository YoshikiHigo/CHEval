package jp.ac.osaka_u.ist.sdl.cheval;

public class CosineSimilarity {

	public static double calculate(final int[] v1, final int[] v2) {
		final int numerator = numerator(v1, v2);
		final double denominator = denominator(v1, v2);
		return (double) numerator / denominator;
	}

	private static int numerator(final int[] v1, final int[] v2) {
		assert v1.length == v2.length : "invalid input!";
		int result = 0;
		for (int i = 0; i < v1.length; i++) {
			result += v1[i] * v2[i];
		}
		return result;
	}

	private static double denominator(final int[] v1, final int[] v2) {
		assert v1.length == v2.length : "invalid input!";
		int sum1 = 0;
		int sum2 = 0;
		for (int i = 0; i < v1.length; i++) {
			sum1 += v1[i] * v1[i];
			sum2 += v2[i] * v2[i];
		}
		return Math.sqrt(sum1 * sum2);
	}
}
