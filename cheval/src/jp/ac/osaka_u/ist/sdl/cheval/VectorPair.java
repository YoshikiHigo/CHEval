package jp.ac.osaka_u.ist.sdl.cheval;

public class VectorPair {

	public final Vector left;
	public final Vector right;
	public final double similarity;

	public VectorPair(final Vector left, final Vector right, double similarity) {
		this.left = left;
		this.right = right;
		this.similarity = similarity;
	}
}
