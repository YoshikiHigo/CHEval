package jp.ac.osaka_u.ist.sdl.cheval;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ChangeAssembler {

	/**
	 * 
	 * @param args
	 *            第一引数はprevolデータベース，第二引数は類似修正検出のしきい値，第三引数は検出結果出力ファイル
	 */
	public static void main(final String[] args) {

		if(3 != args.length){
			System.err.println("the number of arguments must be 3.");
		}
		
		final String database = args[0];
		final double threshold = Double.parseDouble(args[1]);
		final String output = args[2];

		final StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:sqlite:");
		urlBuilder.append(database);
		final String url = urlBuilder.toString();

		final List<Vector> vectors = new ArrayList<Vector>();
		try {
			Class.forName("org.sqlite.JDBC");

			final Connection connection = DriverManager.getConnection(url);
			final Statement statement = connection.createStatement();
			final String query = makeSQL();
			final ResultSet result = statement.executeQuery(query);
			while (result.next()) {
				final Vector vector = createVector(result);
				if (!isZero(vector)) {
					vectors.add(vector);
				}
			}
			statement.close();
			connection.close();

		} catch (final Exception e) {
			e.printStackTrace();
		}

		try {
			final BufferedWriter writer = new BufferedWriter(new FileWriter(
					output));
			for (int i = 0; i < vectors.size(); i++) {
				for (int j = i + 1; j < vectors.size(); j++) {
					final double similarity = CosineSimilarity.calculate(
							vectors.get(i).data, vectors.get(j).data);
					if (threshold <= similarity) {
						writer.write(Long.toString(vectors.get(i).beforeID));
						writer.write(", ");
						writer.write(Long.toString(vectors.get(i).afterID));
						writer.write(", ");
						writer.write(Long.toString(vectors.get(j).beforeID));
						writer.write(", ");
						writer.write(Long.toString(vectors.get(j).afterID));
						writer.write(", ");
						writer.write(Double.toString(similarity));
						writer.newLine();
					}
				}
			}
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String makeSQL() {

		final StringBuilder text = new StringBuilder();
		text.append("select ");
		text.append("v.BEFORE_VECTOR_ID, ");
		text.append("v.AFTER_VECTOR_ID, ");
		text.append("(select v1.ANNOTATION_TYPE_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ANNOTATION_TYPE_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ANNOTATION_TYPE_MEMBER_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ANNOTATION_TYPE_MEMBER_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ANONYMOUS_CLASS_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ANONYMOUS_CLASS_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ARRAY_ACCESS from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ARRAY_ACCESS from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ARRAY_CREATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ARRAY_CREATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ARRAY_INITIALIZER from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ARRAY_INITIALIZER from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ARRAY_TYPE from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ARRAY_TYPE from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ASSERT_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ASSERT_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ASSIGNMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ASSIGNMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.BLOCK from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.BLOCK from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.BLOCK_COMMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.BLOCK_COMMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.BOOLEAN_LITERAL from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.BOOLEAN_LITERAL from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.BREAK_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.BREAK_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.CAST_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.CAST_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.CATCH_CLAUSE from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.CATCH_CLAUSE from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.CHARACTER_LITERAL from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.CHARACTER_LITERAL from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.CLASS_INSTANCE_CREATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.CLASS_INSTANCE_CREATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.COMPILATION_UNIT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.COMPILATION_UNIT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.CONDITIONAL_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.CONDITIONAL_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.CONSTRUCTOR_INVOCATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.CONSTRUCTOR_INVOCATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.CONTINUE_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.CONTINUE_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.DO_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.DO_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.EMPTY_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.EMPTY_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ENHANCED_FOR_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ENHANCED_FOR_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ENUM_CONSTANT_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ENUM_CONSTANT_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.ENUM_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.ENUM_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.EXPRESSION_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.EXPRESSION_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.FIELD_ACCESS from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.FIELD_ACCESS from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.FIELD_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.FIELD_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.FOR_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.FOR_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.IF_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.IF_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.IMPORT_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.IMPORT_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.INFIX_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.INFIX_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.INITIALIZER from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.INITIALIZER from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.INSTANCEOF_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.INSTANCEOF_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.JAVADOC from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.JAVADOC from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.LABELED_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.LABELED_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.LINE_COMMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.LINE_COMMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.MARKER_ANNOTATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.MARKER_ANNOTATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.MEMBER_REF from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.MEMBER_REF from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.MEMBER_VALUE_PAIR from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.MEMBER_VALUE_PAIR from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.METHOD_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.METHOD_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.METHOD_INVOCATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.METHOD_INVOCATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.METHOD_REF from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.METHOD_REF from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.METHOD_REF_PARAMETER from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.METHOD_REF_PARAMETER from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.MODIFIER from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.MODIFIER from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.NORMAL_ANNOTATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.NORMAL_ANNOTATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.NULL_LITERAL from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.NULL_LITERAL from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.NUMBER_LITERAL from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.NUMBER_LITERAL from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.PACKAGE_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.PACKAGE_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.PARAMETERIZED_TYPE from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.PARAMETERIZED_TYPE from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.PARENTHESIZED_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.PARENTHESIZED_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.POSTFIX_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.POSTFIX_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.PREFIX_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.PREFIX_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.PRIMITIVE_TYPE from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.PRIMITIVE_TYPE from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.QUALIFIED_NAME from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.QUALIFIED_NAME from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.QUALIFIED_TYPE from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.QUALIFIED_TYPE from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.RETURN_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.RETURN_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SIMPLE_NAME from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SIMPLE_NAME from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SIMPLE_TYPE from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SIMPLE_TYPE from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SINGLE_MEMBER_ANNOTATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SINGLE_MEMBER_ANNOTATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SINGLE_VARIABLE_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SINGLE_VARIABLE_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.STRING_LITERAL from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.STRING_LITERAL from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SUPER_CONSTRUCTOR_INVOCATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SUPER_CONSTRUCTOR_INVOCATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SUPER_FIELD_ACCESS from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SUPER_FIELD_ACCESS from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SUPER_METHOD_INVOCATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SUPER_METHOD_INVOCATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SWITCH_CASE from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SWITCH_CASE from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SWITCH_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SWITCH_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.SYNCHRONIZED_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.SYNCHRONIZED_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.TAG_ELEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.TAG_ELEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.TEXT_ELEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.TEXT_ELEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.THIS_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.THIS_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.THROW_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.THROW_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.TRY_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.TRY_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.TYPE_DECLARATION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.TYPE_DECLARATION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.TYPE_DECLARATION_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.TYPE_DECLARATION_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.TYPE_LITERAL from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.TYPE_LITERAL from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.TYPE_PARAMETER from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.TYPE_PARAMETER from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.VARIABLE_DECLARATION_EXPRESSION from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.VARIABLE_DECLARATION_EXPRESSION from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.VARIABLE_DECLARATION_FRAGMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.VARIABLE_DECLARATION_FRAGMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.VARIABLE_DECLARATION_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.VARIABLE_DECLARATION_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.WHILE_STATEMENT from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.WHILE_STATEMENT from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID), ");
		text.append("(select v1.WILDCARD_TYPE from vector v1 where v1.VECTOR_ID = v.AFTER_VECTOR_ID) - (select v2.WILDCARD_TYPE from vector v2 where v2.VECTOR_ID = v.BEFORE_VECTOR_ID) ");
		text.append("from vector_link v");
		return text.toString();
	}

	private static Vector createVector(ResultSet rs) throws SQLException {
		int column = 0;
		final long before_vector_ID = rs.getLong(++column);
		final long after_vector_ID = rs.getLong(++column);
		final int annotationTypeDeclaration = rs.getInt(++column);
		final int annotationTypeMemberDeclaration = rs.getInt(++column);
		final int anonymousClassDeclaration = rs.getInt(++column);
		final int arrayAccess = rs.getInt(++column);
		final int arrayCreation = rs.getInt(++column);
		final int arrayInitializer = rs.getInt(++column);
		final int arrayType = rs.getInt(++column);
		final int assertStatement = rs.getInt(++column);
		final int assignment = rs.getInt(++column);
		final int block = rs.getInt(++column);
		final int blockComment = rs.getInt(++column);
		final int booleanLiteral = rs.getInt(++column);
		final int breakStatement = rs.getInt(++column);
		final int castExpression = rs.getInt(++column);
		final int catchClause = rs.getInt(++column);
		final int characterLiteral = rs.getInt(++column);
		final int classInstanceCreation = rs.getInt(++column);
		final int compilationUnit = rs.getInt(++column);
		final int conditionalExpression = rs.getInt(++column);
		final int constructorInvocation = rs.getInt(++column);
		final int continueStatement = rs.getInt(++column);
		final int doStatement = rs.getInt(++column);
		final int emptyStatement = rs.getInt(++column);
		final int enhancedForStatement = rs.getInt(++column);
		final int enumConstantDeclaration = rs.getInt(++column);
		final int enumDeclaration = rs.getInt(++column);
		final int expressionStatement = rs.getInt(++column);
		final int fieldAccess = rs.getInt(++column);
		final int fieldDeclaration = rs.getInt(++column);
		final int forStatement = rs.getInt(++column);
		final int ifStatement = rs.getInt(++column);
		final int importDeclaration = rs.getInt(++column);
		final int infixExpression = rs.getInt(++column);
		final int initializer = rs.getInt(++column);
		final int instanceofExpression = rs.getInt(++column);
		final int javadoc = rs.getInt(++column);
		final int labeledStatement = rs.getInt(++column);
		final int lineComment = rs.getInt(++column);
		final int markerAnnotation = rs.getInt(++column);
		final int memberRef = rs.getInt(++column);
		final int memberValuePair = rs.getInt(++column);
		final int methodDeclaration = rs.getInt(++column);
		final int methodInvocation = rs.getInt(++column);
		final int methodRef = rs.getInt(++column);
		final int methodRefParameter = rs.getInt(++column);
		final int modifier = rs.getInt(++column);
		final int normalAnnotation = rs.getInt(++column);
		final int nullLiteral = rs.getInt(++column);
		final int numberLiteral = rs.getInt(++column);
		final int packageDeclaration = rs.getInt(++column);
		final int parameterizedType = rs.getInt(++column);
		final int parenthesizedExpression = rs.getInt(++column);
		final int postfixExpression = rs.getInt(++column);
		final int prefixExpression = rs.getInt(++column);
		final int primitiveType = rs.getInt(++column);
		final int qualifiedName = rs.getInt(++column);
		final int qualifiedType = rs.getInt(++column);
		final int returnStatement = rs.getInt(++column);
		final int simpleName = rs.getInt(++column);
		final int simpleType = rs.getInt(++column);
		final int singleMemberAnnotation = rs.getInt(++column);
		final int singleVariableDeclaration = rs.getInt(++column);
		final int stringLiteral = rs.getInt(++column);
		final int superConstructorInvocation = rs.getInt(++column);
		final int superFieldAccess = rs.getInt(++column);
		final int superMethodInvocation = rs.getInt(++column);
		final int switchCase = rs.getInt(++column);
		final int switchStatement = rs.getInt(++column);
		final int synchronizedStatement = rs.getInt(++column);
		final int tagElement = rs.getInt(++column);
		final int textElement = rs.getInt(++column);
		final int thisExpression = rs.getInt(++column);
		final int throwStatement = rs.getInt(++column);
		final int tryStatement = rs.getInt(++column);
		final int typeDeclaration = rs.getInt(++column);
		final int typeDeclarationStatement = rs.getInt(++column);
		final int typeLiteral = rs.getInt(++column);
		final int typeParameter = rs.getInt(++column);
		final int variableDeclarationExpression = rs.getInt(++column);
		final int variableDeclarationFragment = rs.getInt(++column);
		final int variableDeclarationStatement = rs.getInt(++column);
		final int whileStatement = rs.getInt(++column);
		final int wildcardType = rs.getInt(++column);

		final int[] data = { annotationTypeDeclaration,
				annotationTypeMemberDeclaration, anonymousClassDeclaration,
				arrayAccess, arrayCreation, arrayInitializer, arrayType,
				assertStatement, assignment, block, blockComment,
				booleanLiteral, breakStatement, castExpression, catchClause,
				characterLiteral, classInstanceCreation, compilationUnit,
				conditionalExpression, constructorInvocation,
				continueStatement, doStatement, emptyStatement,
				enhancedForStatement, enumConstantDeclaration, enumDeclaration,
				expressionStatement, fieldAccess, fieldDeclaration,
				forStatement, ifStatement, importDeclaration, infixExpression,
				initializer, instanceofExpression, javadoc, labeledStatement,
				lineComment, markerAnnotation, memberRef, memberValuePair,
				methodDeclaration, methodInvocation, methodRef,
				methodRefParameter, modifier, normalAnnotation, nullLiteral,
				numberLiteral, packageDeclaration, parameterizedType,
				parenthesizedExpression, postfixExpression, prefixExpression,
				primitiveType, qualifiedName, qualifiedType, returnStatement,
				simpleName, simpleType, singleMemberAnnotation,
				singleVariableDeclaration, stringLiteral,
				superConstructorInvocation, superFieldAccess,
				superMethodInvocation, switchCase, switchStatement,
				synchronizedStatement, tagElement, textElement, thisExpression,
				throwStatement, tryStatement, typeDeclaration,
				typeDeclarationStatement, typeLiteral, typeParameter,
				variableDeclarationExpression, variableDeclarationFragment,
				variableDeclarationStatement, whileStatement, wildcardType };
		return new Vector(before_vector_ID, after_vector_ID, data);
	}

	private static boolean isZero(final Vector vector) {
		for (int element : vector.data) {
			if (0 != element) {
				return false;
			}
		}
		return true;
	}
}
