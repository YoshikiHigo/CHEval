package jp.ac.osaka_u.ist.sdl.cheval;

public class ChangePair {

	public final Change left;
	public final Change right;
	public final double similarity;

	public ChangePair(final Change left, final Change right, double similarity) {
		this.left = left;
		this.right = right;
		this.similarity = similarity;
	}
}
