package jp.ac.osaka_u.ist.sdl.cheval.gui.cl;

import java.util.SortedMap;

import javax.swing.table.AbstractTableModel;

import jp.ac.osaka_u.ist.sdl.cheval.Vector;

public class ChangeListModel extends AbstractTableModel {

	static public final String[] TITLES = new String[] { "Change ID", "LABEL",
			"NEIGHBORS" };

	final public Vector[] changes;

	public ChangeListModel(final Vector[] changes) {
		this.changes = changes;
	}

	@Override
	public int getRowCount() {
		return this.changes.length;
	}

	@Override
	public int getColumnCount() {
		return TITLES.length;
	}

	@Override
	public Object getValueAt(int row, int col) {

		switch (col) {
		case 0:
			return this.changes[row].id;
		case 1:
			final StringBuilder label = new StringBuilder();
			label.append(Long.toString(this.changes[row].beforeMethodID));
			label.append("->");
			label.append(Long.toString(this.changes[row].afterMethodID));
			return label.toString();
		case 2:
			final SortedMap<Vector, Double> neighbors = this.changes[row]
					.getSimilarChanges();
			return neighbors.size();
		default:
			assert false : "Here sholdn't be reached!";
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
		case 0:
		case 2:
			return Integer.class;
		case 1:
			return String.class;
		default:
			assert false : "Here shouldn't be reached!";
			return Object.class;
		}
	}

	@Override
	public String getColumnName(int col) {
		return TITLES[col];
	}
}
