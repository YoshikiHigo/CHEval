package jp.ac.osaka_u.ist.sdl.cheval.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jp.ac.osaka_u.ist.sdl.cheval.Method;

public class MethodRetriever {

	static private MethodRetriever SINGLETON = null;

	static public MethodRetriever getInstance(final String database)
			throws Exception {
		if (null == SINGLETON) {
			SINGLETON = new MethodRetriever(database);
		}
		return SINGLETON;
	}

	static public void deleteInstance() throws Exception {
		if (null != SINGLETON) {
			SINGLETON.statement.close();
			SINGLETON.connector.close();
			SINGLETON = null;
		}
	}

	final private Connection connector;
	final private PreparedStatement statement;

	private MethodRetriever(final String database) throws Exception {

		final StringBuilder url = new StringBuilder();
		url.append("jdbc:sqlite:");
		url.append(database);
		if (url.charAt(url.length() - 1) != File.separatorChar) {
			url.append(File.separator);
		}
		Class.forName("org.sqlite.JDBC");
		this.connector = DriverManager.getConnection(url.toString());

		final StringBuilder text = new StringBuilder();
		text.append("select START_REVISION_ID, ");
		text.append("END_REVISION_ID, ");
		text.append("(select f.file_path from FILE f where f.file_id = m.owner_file_id), ");
		text.append("m.START_LINE, ");
		text.append("m.END_LINE from Method m where m.method_id=?");
		this.statement = this.connector.prepareStatement(text.toString());
	}

	public Method getMethod(final long methodID) throws Exception {

		this.statement.setLong(1, methodID);
		final ResultSet result = this.statement.executeQuery();

		if (!result.next()) {
			System.err.println("incorrect method ID.");
			System.exit(0);
		}

		final int startRevision = result.getInt(1);
		final int endRevision = result.getInt(2);
		final String path = result.getString(3);
		final int startLine = result.getInt(4);
		final int endLine = result.getInt(5);

		final Method method = new Method(startRevision, endRevision, path,
				startLine, endLine);

		return method;
	}

	public void close() throws Exception {
		this.statement.close();
		this.connector.close();
		SINGLETON = null;
	}
}
