package jp.ac.osaka_u.ist.sdl.cheval.gui.nl;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
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

import jp.ac.osaka_u.ist.sdl.cheval.Change;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges.CLABEL;

public class NeighborList extends JTable implements Observer {

	static final int COLUMN_LENGTH_ID = 10;
	static final int COLUMN_LENGTH_LABEL = 10;
	static final int COLUMN_LENGTH_NEIGHBORS = 10;

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

			final SortedSet<Change> neighbors = new TreeSet<Change>();

			for (int index = firstIndex; index <= lastIndex; index++) {

				if (!NeighborList.this.selectionModel.isSelectedIndex(index)) {
					continue;
				}

				final int modelIndex = NeighborList.this
						.convertRowIndexToModel(index);
				final NeighborListModel model = (NeighborListModel) NeighborList.this
						.getModel();
				final Change change = model.changes[modelIndex];
				neighbors.add(change);
			}

			ObservedChanges.getInstance(CLABEL.NEIGHBORS).setAll(neighbors,
					NeighborList.this);
		}
	}

	public NeighborList(final Change[] changes) {

		super();

		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setModel(new TreeMap<Change, Double>());

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Similar changes"));

		this.selectionHandler = new OSelectionHandler();
		this.getSelectionModel()
				.addListSelectionListener(this.selectionHandler);
	}

	public void setModel(final SortedMap<Change, Double> changes) {

		final NeighborListModel model = new NeighborListModel(changes);
		this.setModel(model);
		final RowSorter<NeighborListModel> sorter = new TableRowSorter<NeighborListModel>(
				model);
		this.setRowSorter(sorter);

		for (int i = 0; i < this.getColumnCount(); i++) {
			this.getColumnModel().getColumn(i)
					.setCellRenderer(new NeighborListRenderer());
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

		if (o instanceof ObservedChanges) {
			final ObservedChanges changes = (ObservedChanges) o;
			if (changes.label.equals(CLABEL.SELECTED)) {
				if (changes.isSet()) {
					final SortedMap<Change, Double> similarChanges = changes
							.get().first().getSimilarChanges();
					this.setModel(similarChanges);
				} else {
					this.setModel(new TreeMap<Change, Double>());
				}
			}
		}

		this.getSelectionModel()
				.addListSelectionListener(this.selectionHandler);
	}
}
