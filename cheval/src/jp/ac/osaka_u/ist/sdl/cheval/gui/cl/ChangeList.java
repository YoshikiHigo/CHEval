package jp.ac.osaka_u.ist.sdl.cheval.gui.cl;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowSorter;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import jp.ac.osaka_u.ist.sdl.cheval.Vector;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges.CLABEL;

public class ChangeList extends JTable implements Observer {

	static final int COLUMN_LENGTH_ID = 10;
	static final int COLUMN_LENGTH_LABEL = 10;
	static final int COLUMN_LENGTH_NEIGHBORS = 10;

	final Vector[] changes;

	final public JScrollPane scrollPane;
	final private OSelectionHandler selectionHandler;

	class OSelectionHandler implements ListSelectionListener {

		@Override
		public void valueChanged(final ListSelectionEvent e) {

			if (e.getValueIsAdjusting()) {
				return;
			}

			final int firstIndex = e.getFirstIndex();
			final int lastIndex = e.getLastIndex();

			final SortedSet<Vector> changes = new TreeSet<Vector>();

			for (int index = firstIndex; index <= lastIndex; index++) {

				if (!ChangeList.this.selectionModel.isSelectedIndex(index)) {
					continue;
				}

				final int modelIndex = ChangeList.this
						.convertRowIndexToModel(index);
				final ChangeListModel model = (ChangeListModel) ChangeList.this
						.getModel();
				final Vector change = model.changes[modelIndex];
				changes.add(change);
			}

			ObservedChanges.getInstance(CLABEL.SELECTED).setAll(changes,
					ChangeList.this);
			ObservedChanges.getInstance(CLABEL.NEIGHBORS)
					.clear(ChangeList.this);
		}
	}

	public ChangeList(final Vector[] changes) {

		super();

		this.changes = changes;

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setModel(changes);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Changes"));

		this.selectionHandler = new OSelectionHandler();
		this.getSelectionModel()
				.addListSelectionListener(this.selectionHandler);
	}

	public void setModel(final Vector[] changes) {

		final ChangeListModel model = new ChangeListModel(changes);
		this.setModel(model);
		final RowSorter<ChangeListModel> sorter = new TableRowSorter<ChangeListModel>(
				model);
		this.setRowSorter(sorter);

		for (int i = 0; i < this.getColumnCount(); i++) {
			this.getColumnModel().getColumn(i)
					.setCellRenderer(new ChangeListRenderer());
		}

		this.getColumnModel().getColumn(0).setPreferredWidth(COLUMN_LENGTH_ID);
		this.getColumnModel().getColumn(1)
				.setPreferredWidth(COLUMN_LENGTH_LABEL);
		this.getColumnModel().getColumn(2)
				.setPreferredWidth(COLUMN_LENGTH_NEIGHBORS);
	}

	@Override
	public void update(final Observable o, final Object arg) {

		this.getSelectionModel().removeListSelectionListener(
				this.selectionHandler);

		this.getSelectionModel()
				.addListSelectionListener(this.selectionHandler);
	}
}
