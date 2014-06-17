package jp.ac.osaka_u.ist.sdl.cheval.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import jp.ac.osaka_u.ist.sdl.cheval.Change;
import jp.ac.osaka_u.ist.sdl.cheval.ChangePair;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges.CLABEL;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ccode.CCode;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ccode.CCode.BEFOREAFTER;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ccode.CCode.TYPE;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ccode.ChangeTextField;
import jp.ac.osaka_u.ist.sdl.cheval.gui.cl.ChangeList;
import jp.ac.osaka_u.ist.sdl.cheval.gui.nl.NeighborList;

public class ChangeViewer extends JFrame {

	public static void main(final String[] args) {

		if (3 != args.length) {
			System.out.println("the number of arguments must be 3.");
			System.exit(0);
		}

		final String repository = args[0];
		final String database = args[1];
		final String file = args[2];
		final ChangeViewer viewer = new ChangeViewer(repository, database, file);
		viewer.setVisible(true);
	}

	public final String repository;
	public final String database;

	private ChangeViewer(final String repository, final String database,
			final String file) {

		this.repository = repository;
		this.database = database;
		final List<ChangePair> vectorPairs = readVectorPairs(file);
		final Change[] vectors = getVectorData(vectorPairs);

		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(new Dimension(d.width - 5, d.height - 27));
		this.addWindowListener(new ChangeViewerListener());

		final ChangeList changeList = new ChangeList(vectors);
		ObservedChanges.getInstance(CLABEL.SELECTED).addObserver(changeList);
		ObservedChanges.getInstance(CLABEL.NEIGHBORS).addObserver(changeList);

		final NeighborList neighborList = new NeighborList(vectors);
		ObservedChanges.getInstance(CLABEL.SELECTED).addObserver(neighborList);
		ObservedChanges.getInstance(CLABEL.NEIGHBORS).addObserver(neighborList);

		final JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		leftPanel.add(changeList.scrollPane, JSplitPane.TOP);
		leftPanel.add(neighborList.scrollPane, JSplitPane.BOTTOM);

		this.getContentPane().add(leftPanel, BorderLayout.WEST);

		final CCode beforeChangeCode = new CCode(repository, database,
				BEFOREAFTER.BEFORE, TYPE.SELECTED);
		ObservedChanges.getInstance(CLABEL.SELECTED).addObserver(
				beforeChangeCode);
		ObservedChanges.getInstance(CLABEL.NEIGHBORS).addObserver(
				beforeChangeCode);

		final CCode afterChangeCode = new CCode(repository, database,
				BEFOREAFTER.AFTER, TYPE.SELECTED);
		ObservedChanges.getInstance(CLABEL.SELECTED).addObserver(
				afterChangeCode);
		ObservedChanges.getInstance(CLABEL.NEIGHBORS).addObserver(
				afterChangeCode);

		final JSplitPane topPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		topPanel.add(beforeChangeCode.scrollPane, JSplitPane.LEFT);
		topPanel.add(afterChangeCode.scrollPane, JSplitPane.RIGHT);

		final ChangeTextField selectedChangeText = new ChangeTextField(
				this.database, ChangeTextField.TYPE.SELECTED);
		ObservedChanges.getInstance(CLABEL.SELECTED).addObserver(
				selectedChangeText);
		ObservedChanges.getInstance(CLABEL.NEIGHBORS).addObserver(
				selectedChangeText);

		final JPanel topPanel2 = new JPanel(new BorderLayout());
		topPanel2.add(topPanel, BorderLayout.CENTER);
		topPanel2.add(selectedChangeText, BorderLayout.NORTH);

		final CCode beforeChangeCode2 = new CCode(repository, database,
				BEFOREAFTER.BEFORE, TYPE.NEIGHBORS);
		ObservedChanges.getInstance(CLABEL.SELECTED).addObserver(
				beforeChangeCode2);
		ObservedChanges.getInstance(CLABEL.NEIGHBORS).addObserver(
				beforeChangeCode2);

		final CCode afterChangeCode2 = new CCode(repository, database,
				BEFOREAFTER.AFTER, TYPE.NEIGHBORS);
		ObservedChanges.getInstance(CLABEL.SELECTED).addObserver(
				afterChangeCode2);
		ObservedChanges.getInstance(CLABEL.NEIGHBORS).addObserver(
				afterChangeCode2);

		final JSplitPane bottomPanel = new JSplitPane(
				JSplitPane.HORIZONTAL_SPLIT);
		bottomPanel.add(beforeChangeCode2.scrollPane, JSplitPane.LEFT);
		bottomPanel.add(afterChangeCode2.scrollPane, JSplitPane.RIGHT);

		final JSplitPane mainPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPanel.add(topPanel2, JSplitPane.TOP);
		mainPanel.add(bottomPanel, JSplitPane.BOTTOM);

		this.getContentPane().add(mainPanel, BorderLayout.CENTER);

		this.setVisible(true);

		leftPanel.setDividerLocation(d.height / 2);
		topPanel.setDividerLocation(topPanel.getWidth() / 2);
		bottomPanel.setDividerLocation(bottomPanel.getWidth() / 2);
	}

	private static List<ChangePair> readVectorPairs(final String path) {

		final List<ChangePair> pairs = new ArrayList<ChangePair>();

		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					path));
			while (reader.ready()) {
				final String line = reader.readLine();
				final StringTokenizer tokenizer = new StringTokenizer(line,
						", ");
				final String leftBeforeID = tokenizer.nextToken();
				final String leftAfterID = tokenizer.nextToken();
				final String rightBeforeID = tokenizer.nextToken();
				final String rightAfterID = tokenizer.nextToken();
				final String similarity = tokenizer.nextToken();
				final String leftText = tokenizer.nextToken();
				final String rightText = tokenizer.nextToken();

				final ChangePair pair = new ChangePair(new Change(
						Long.parseLong(leftBeforeID),
						Long.parseLong(leftAfterID), leftText), new Change(
						Long.parseLong(rightBeforeID),
						Long.parseLong(rightAfterID), rightText),
						Double.parseDouble(similarity));
				pairs.add(pair);
			}
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		return pairs;
	}

	private static Change[] getVectorData(final List<ChangePair> pairs) {

		final SortedMap<Change, Change> vectorData = new TreeMap<Change, Change>();
		for (int i = 0; i < pairs.size(); i++) {

			Change vector = vectorData.get(pairs.get(i).left);
			if (null == vector) {
				vector = pairs.get(i).left;
				vectorData.put(vector, vector);
			}
			vector.addSimilarChange(pairs.get(i).right, pairs.get(i).similarity);

			vector = vectorData.get(pairs.get(i).right);
			if (null == vector) {
				vector = pairs.get(i).right;
				vectorData.put(vector, vector);
			}
			vector.addSimilarChange(pairs.get(i).left, pairs.get(i).similarity);
		}

		return vectorData.keySet().toArray(new Change[] {});
	}

	class ChangeViewerListener implements WindowListener {

		public void windowActivated(WindowEvent e) {
		}

		public void windowClosed(WindowEvent e) {
		}

		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}

		public void windowDeactivated(WindowEvent e) {
		}

		public void windowDeiconified(WindowEvent e) {
		}

		public void windowIconified(WindowEvent e) {
		}

		public void windowOpened(WindowEvent e) {
		}
	}
}
