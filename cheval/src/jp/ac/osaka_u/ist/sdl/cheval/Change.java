package jp.ac.osaka_u.ist.sdl.cheval;

import java.util.Collections;
import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Change implements Comparable<Change> {

	static private final HashMap<Integer, String> CHANGE_TEXT = new HashMap<Integer, String>();

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
	private final int changeHash;

	public final int[] data;

	private final SortedMap<Change, Double> similarChanges;

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

	public Change(final long beforeMethodID, final long afterMethodID,
			final int[] data) {
		this.id = IDGENERATOR.getAndIncrement();
		this.beforeMethodID = beforeMethodID;
		this.afterMethodID = afterMethodID;
		this.changeHash = 0;
		this.data = data;
		this.similarChanges = new TreeMap<Change, Double>();
	}

	public Change(final long beforeMethodID, final long afterMethodID,
			final String changeText) {
		this.id = IDGENERATOR.getAndIncrement();
		this.beforeMethodID = beforeMethodID;
		this.afterMethodID = afterMethodID;
		this.changeHash = changeText.hashCode();
		this.data = new int[0];
		this.similarChanges = new TreeMap<Change, Double>();

		if (!CHANGE_TEXT.containsKey(changeText.hashCode())) {
			CHANGE_TEXT.put(changeText.hashCode(), changeText);
		}
	}

	public void addSimilarChange(final Change change, final double similarity) {
		this.similarChanges.put(change, similarity);
	}

	public SortedMap<Change, Double> getSimilarChanges() {
		return Collections.unmodifiableSortedMap(this.similarChanges);
	}

	public String getChangedValueString() {

		if (0 == this.data.length) {
			return CHANGE_TEXT.get(this.changeHash);
		}

		else {
			final StringBuilder text = new StringBuilder();
			text.append(this.getChangedValueString(this.data[0],
					"ANNOTATION_TYPE_DECLARATION"));
			text.append(this.getChangedValueString(this.data[1],
					"ANNOTATION_TYPE_MEMBER_DECLARATION"));
			text.append(this.getChangedValueString(this.data[2],
					"ANONYMOUS_CLASS_DECLARATION"));
			text.append(this
					.getChangedValueString(this.data[3], "ARRAY_ACCESS"));
			text.append(this.getChangedValueString(this.data[4],
					"ARRAY_CREATION"));
			text.append(this.getChangedValueString(this.data[5],
					"ARRAY_INITIALIZER"));
			text.append(this.getChangedValueString(this.data[6], "ARRAY_TYPE"));
			text.append(this.getChangedValueString(this.data[0],
					"ASSERT_STATEMENT"));
			text.append(this.getChangedValueString(this.data[7], "ASSIGNMENT"));
			text.append(this.getChangedValueString(this.data[8], "BLOCK"));
			text.append(this.getChangedValueString(this.data[9],
					"BLOCK_COMMENT"));
			text.append(this.getChangedValueString(this.data[10],
					"BOOLEAN_LITERAL"));
			text.append(this.getChangedValueString(this.data[11],
					"BREAK_STATEMENT"));
			text.append(this.getChangedValueString(this.data[12],
					"CAST_EXPRESSION"));
			text.append(this.getChangedValueString(this.data[13],
					"CATCH_CLAUSE"));
			text.append(this.getChangedValueString(this.data[14],
					"CHARACTER_LITERAL"));
			text.append(this.getChangedValueString(this.data[15],
					"CLASS_INSTANCE_CREATION"));
			text.append(this.getChangedValueString(this.data[0],
					"COMPILATION_UNIT"));
			text.append(this.getChangedValueString(this.data[16],
					"CONDITIONAL_EXPRESSION"));
			text.append(this.getChangedValueString(this.data[17],
					"CONSTRUCTOR_INVOCATION"));
			text.append(this.getChangedValueString(this.data[18],
					"CONTINUE_STATEMENT"));
			text.append(this.getChangedValueString(this.data[19],
					"DO_STATEMENT"));
			text.append(this.getChangedValueString(this.data[20],
					"EMPTY_STATEMENT"));
			text.append(this.getChangedValueString(this.data[21],
					"ENHANCED_FOR_STATEMENT"));
			text.append(this.getChangedValueString(this.data[22],
					"ENUM_CONSTANT_DECLARATION"));
			text.append(this.getChangedValueString(this.data[0],
					"ENUM_DECLARATION"));
			text.append(this.getChangedValueString(this.data[23],
					"EXPRESSION_STATEMENT"));
			text.append(this.getChangedValueString(this.data[24],
					"FIELD_ACCESS"));
			text.append(this.getChangedValueString(this.data[25],
					"FIELD_DECLARATION"));
			text.append(this.getChangedValueString(this.data[26],
					"FOR_STATEMENT"));
			text.append(this.getChangedValueString(this.data[27],
					"IF_STATEMENT"));
			text.append(this.getChangedValueString(this.data[28],
					"IMPORT_DECLARATION"));
			text.append(this.getChangedValueString(this.data[0],
					"INFIX_EXPRESSION"));
			text.append(this
					.getChangedValueString(this.data[29], "INITIALIZER"));
			text.append(this.getChangedValueString(this.data[30],
					"INSTANCEOF_EXPRESSION"));
			text.append(this.getChangedValueString(this.data[31], "JAVADOC"));
			text.append(this.getChangedValueString(this.data[32],
					"LABELED_STATEMENT"));
			text.append(this.getChangedValueString(this.data[33],
					"LINE_COMMENT"));
			text.append(this.getChangedValueString(this.data[34],
					"MARKER_ANNOTATION"));
			text.append(this.getChangedValueString(this.data[35], "MEMBER_REF"));
			text.append(this.getChangedValueString(this.data[36],
					"MEMBER_VALUE_PAIR"));
			text.append(this.getChangedValueString(this.data[37],
					"METHOD_DECLARATION"));
			text.append(this.getChangedValueString(this.data[38],
					"METHOD_INVOCATION"));
			text.append(this.getChangedValueString(this.data[39], "METHOD_REF"));
			text.append(this.getChangedValueString(this.data[40],
					"METHOD_REF_PARAMETER"));
			text.append(this.getChangedValueString(this.data[41], "MODIFIER"));
			text.append(this.getChangedValueString(this.data[42],
					"NORMAL_ANNOTATION"));
			text.append(this.getChangedValueString(this.data[43],
					"NULL_LITERAL"));
			text.append(this.getChangedValueString(this.data[44],
					"NUMBER_LITERAL"));
			text.append(this.getChangedValueString(this.data[45],
					"PACKAGE_DECLARATION"));
			text.append(this.getChangedValueString(this.data[46],
					"PARAMETERIZED_TYPE"));
			text.append(this.getChangedValueString(this.data[47],
					"PARENTHESIZED_EXPRESSION"));
			text.append(this.getChangedValueString(this.data[48],
					"POSTFIX_EXPRESSION"));
			text.append(this.getChangedValueString(this.data[49],
					"PREFIX_EXPRESSION"));
			text.append(this.getChangedValueString(this.data[50],
					"PRIMITIVE_TYPE"));
			text.append(this.getChangedValueString(this.data[51],
					"QUALIFIED_NAME"));
			text.append(this.getChangedValueString(this.data[52],
					"QUALIFIED_TYPE"));
			text.append(this.getChangedValueString(this.data[0],
					"RETURN_STATEMENT"));
			text.append(this
					.getChangedValueString(this.data[53], "SIMPLE_NAME"));
			text.append(this
					.getChangedValueString(this.data[54], "SIMPLE_TYPE"));
			text.append(this.getChangedValueString(this.data[55],
					"SINGLE_MEMBER_ANNOTATION"));
			text.append(this.getChangedValueString(this.data[56],
					"SINGLE_VARIABLE_DECLARATION"));
			text.append(this.getChangedValueString(this.data[57],
					"STRING_LITERAL"));
			text.append(this.getChangedValueString(this.data[58],
					"SUPER_CONSTRUCTOR_INVOCATION"));
			text.append(this.getChangedValueString(this.data[59],
					"SUPER_FIELD_ACCESS"));
			text.append(this.getChangedValueString(this.data[60],
					"SUPER_METHOD_INVOCATION"));
			text.append(this
					.getChangedValueString(this.data[61], "SWITCH_CASE"));
			text.append(this.getChangedValueString(this.data[0],
					"SWITCH_STATEMENT"));
			text.append(this.getChangedValueString(this.data[0],
					"SYNCHRONIZED_STATEMENT"));
			text.append(this.getChangedValueString(this.data[0], "TAG_ELEMENT"));
			text.append(this
					.getChangedValueString(this.data[0], "TEXT_ELEMENT"));
			text.append(this.getChangedValueString(this.data[0],
					"THIS_EXPRESSION"));
			text.append(this.getChangedValueString(this.data[0],
					"THROW_STATEMENT"));
			text.append(this.getChangedValueString(this.data[0],
					"TRY_STATEMENT"));
			text.append(this.getChangedValueString(this.data[0],
					"TYPE_DECLARATION"));
			text.append(this.getChangedValueString(this.data[0],
					"TYPE_DECLARATION_STATEMENT"));
			text.append(this
					.getChangedValueString(this.data[0], "TYPE_LITERAL"));
			text.append(this.getChangedValueString(this.data[0],
					"TYPE_PARAMETER"));
			text.append(this.getChangedValueString(this.data[0],
					"VARIABLE_DECLARATION_EXPRESSION"));
			text.append(this.getChangedValueString(this.data[0],
					"VARIABLE_DECLARATION_FRAGMENT"));
			text.append(this.getChangedValueString(this.data[0],
					"VARIABLE_DECLARATION_STATEMENT"));
			text.append(this.getChangedValueString(this.data[0],
					"WHILE_STATEMENT"));
			text.append(this.getChangedValueString(this.data[0],
					"WILDCARD_TYPE"));
			text.append(this.getChangedValueString(this.data[0],
					"NUMBER_OF_ELEMENTS"));
			return text.toString();
		}
	}

	public String getChangedValueString(final int value, final String label) {
		final StringBuilder text = new StringBuilder();
		if (0 != value) {
			text.append(Integer.toString(value));
			text.append("@");
			text.append(label);
			text.append(":");
		}
		return text.toString();
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

		if (!(o instanceof Change)) {
			return false;
		}

		final Change target = (Change) o;
		return (this.beforeMethodID == target.beforeMethodID)
				&& (this.afterMethodID == target.afterMethodID);
	}

	@Override
	public int compareTo(final Change v) {
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
