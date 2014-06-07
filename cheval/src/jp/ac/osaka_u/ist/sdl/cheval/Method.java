package jp.ac.osaka_u.ist.sdl.cheval;

public class Method {

	final public int startRevision;
	final public int endRevision;
	final public String path;
	final public int startLine;
	final public int endLine;

	public Method(final int startRevision, final int endRevision,
			final String path, final int startLine, final int endLine) {

		this.startRevision = startRevision;
		this.endRevision = endRevision;
		this.path = path;
		this.startLine = startLine;
		this.endLine = endLine;
	}
}
