package jp.ac.osaka_u.ist.sdl.cheval;

import java.util.HashMap;

public class ChangePair {

	static private final HashMap<Integer, String> CHANGE_TEXT = new HashMap<Integer, String>();

	public final Change left;
	public final Change right;
	public final double similarity;

	public ChangePair(final Change left, final Change right, final double similarity) {

		this.left = left;
		this.right = right;
		this.similarity = similarity;
	}
}
