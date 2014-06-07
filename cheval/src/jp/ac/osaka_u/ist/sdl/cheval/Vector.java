package jp.ac.osaka_u.ist.sdl.cheval;

import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Vector implements Comparable<Vector> {

	public static final int ANNOTATION_TYPE_DECLARATION = 0;
	public static final int ANNOTATION_TYPE_MEMBER_DECLARATION = 1;
	public static final int ANONYMOUS_CLASS_DECLARATION = 2;
	public static final int ARRAY_ACCESS = 3;
	public static final int ARRAY_CREATION = 4;
	public static final int ARRAY_INITIALIZER = 5;
	public static final int ARRAY_TYPE = 6;
	public static final int ASSERT_STATEMENT = 7;
	public static final int ASSIGNMENT = 8;
	public static final int BLOCK = 9;
	public static final int BLOCK_COMMENT = 10;
	public static final int BOOLEAN_LITERAL = 11;
	public static final int BREAK_STATEMENT = 12;
	public static final int CAST_EXPRESSION = 13;
	public static final int CATCH_CLAUSE = 14;
	public static final int CHARACTER_LITERAL = 15;
	public static final int CLASS_INSTANCE_CREATION = 16;
	public static final int COMPILATION_UNIT = 17;
	public static final int CONDITIONAL_EXPRESSION = 18;
	public static final int CONSTRUCTOR_INVOCATION = 19;
	public static final int CONTINUE_STATEMENT = 20;
	public static final int DO_STATEMENT = 21;
	public static final int EMPTY_STATEMENT = 22;
	public static final int ENHANCED_FOR_STATEMENT = 23;
	public static final int ENUM_CONSTANT_DECLARATION = 24;
	public static final int ENUM_DECLARATION = 25;
	public static final int EXPRESSION_STATEMENT = 26;
	public static final int FIELD_ACCESS = 27;
	public static final int FIELD_DECLARATION = 28;
	public static final int FOR_STATEMENT = 29;
	public static final int IF_STATEMENT = 30;
	public static final int IMPORT_DECLARATION = 31;
	public static final int INFIX_EXPRESSION = 32;
	public static final int INITIALIZER = 33;
	public static final int INSTANCEOF_EXPRESSION = 34;
	public static final int JAVADOC = 35;
	public static final int LABELED_STATEMENT = 36;
	public static final int LINE_COMMENT = 37;
	public static final int MARKER_ANNOTATION = 38;
	public static final int MEMBER_REF = 39;
	public static final int MEMBER_VALUE_PAIR = 40;
	public static final int METHOD_DECLARATION = 41;
	public static final int METHOD_INVOCATION = 42;
	public static final int METHOD_REF = 43;
	public static final int METHOD_REF_PARAMETER = 44;
	public static final int MODIFIER = 45;
	public static final int NORMAL_ANNOTATION = 46;
	public static final int NULL_LITERAL = 47;
	public static final int NUMBER_LITERAL = 48;
	public static final int PACKAGE_DECLARATION = 49;
	public static final int PARAMETERIZED_TYPE = 50;
	public static final int PARENTHESIZED_EXPRESSION = 51;
	public static final int POSTFIX_EXPRESSION = 52;
	public static final int PREFIX_EXPRESSION = 53;
	public static final int PRIMITIVE_TYPE = 54;
	public static final int QUALIFIED_NAME = 55;
	public static final int QUALIFIED_TYPE = 56;
	public static final int RETURN_STATEMENT = 57;
	public static final int SIMPLE_NAME = 58;
	public static final int SIMPLE_TYPE = 59;
	public static final int SINGLE_MEMBER_ANNOTATION = 60;
	public static final int SINGLE_VARIABLE_DECLARATION = 61;
	public static final int STRING_LITERAL = 62;
	public static final int SUPER_CONSTRUCTOR_INVOCATION = 63;
	public static final int SUPER_FIELD_ACCESS = 64;
	public static final int SUPER_METHOD_INVOCATION = 65;
	public static final int SWITCH_CASE = 66;
	public static final int SWITCH_STATEMENT = 67;
	public static final int SYNCHRONIZED_STATEMENT = 68;
	public static final int TAG_ELEMENT = 69;
	public static final int TEXT_ELEMENT = 70;
	public static final int THIS_EXPRESSION = 71;
	public static final int THROW_STATEMENT = 72;
	public static final int TRY_STATEMENT = 73;
	public static final int TYPE_DECLARATION = 74;
	public static final int TYPE_DECLARATION_STATEMENT = 75;
	public static final int TYPE_LITERAL = 76;
	public static final int TYPE_PARAMETER = 77;
	public static final int VARIABLE_DECLARATION_EXPRESSION = 78;
	public static final int VARIABLE_DECLARATION_FRAGMENT = 79;
	public static final int VARIABLE_DECLARATION_STATEMENT = 80;
	public static final int WHILE_STATEMENT = 81;
	public static final int WILDCARD_TYPE = 82;
	public static final int NUMBER_OF_ELEMENTS = 83;

	private static final AtomicInteger IDGENERATOR = new AtomicInteger(0);

	public final int id;
	public final long beforeMethodID;
	public final long afterMethodID;
	public final int[] data;

	private final SortedMap<Vector, Double> similarChanges;

	public static String toString(final int[] vector) {
		final StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (int element : vector) {
			builder.append(Integer.toString(element));
			builder.append(",");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("}");
		return builder.toString();
	}

	public Vector(final long beforeMethodID, final long afterMethodID,
			final int[] data) {
		this.id = IDGENERATOR.getAndIncrement();
		this.beforeMethodID = beforeMethodID;
		this.afterMethodID = afterMethodID;
		this.data = data;
		this.similarChanges = new TreeMap<Vector, Double>();
	}

	public void addSimilarChange(final Vector change, final double similarity) {
		this.similarChanges.put(change, similarity);
	}

	public SortedMap<Vector, Double> getSimilarChanges() {
		return Collections.unmodifiableSortedMap(this.similarChanges);
	}

	@Override
	public int hashCode() {
		return (int) (this.beforeMethodID + this.afterMethodID);
	}

	@Override
	public boolean equals(final Object o) {

		if (null == o) {
			return false;
		}

		if (!(o instanceof Vector)) {
			return false;
		}

		final Vector target = (Vector) o;
		return (this.beforeMethodID == target.beforeMethodID)
				&& (this.afterMethodID == target.afterMethodID);
	}

	@Override
	public int compareTo(final Vector v) {
		if (this.beforeMethodID < v.beforeMethodID) {
			return -1;
		} else if (this.beforeMethodID > v.beforeMethodID) {
			return 1;
		} else if (this.afterMethodID < v.afterMethodID) {
			return -1;
		} else if (this.afterMethodID > v.afterMethodID) {
			return 1;
		} else {
			return 0;
		}
	}

}
