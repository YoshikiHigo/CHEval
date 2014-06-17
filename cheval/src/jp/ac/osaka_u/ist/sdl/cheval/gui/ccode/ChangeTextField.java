package jp.ac.osaka_u.ist.sdl.cheval.gui.ccode;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import jp.ac.osaka_u.ist.sdl.cheval.Change;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges.CLABEL;

public class ChangeTextField extends JTextField implements Observer {

	public enum TYPE {
		SELECTED, NEIGHBORS;
	}

	public final String database;
	public final TYPE type;

	public ChangeTextField(final String database, final TYPE type) {
		this.database = database;
		this.type = type;
		this.setEnabled(true);
		this.setEditable(true);

		this.setBorder(new TitledBorder(new LineBorder(Color.black),
				"Change Contents"));
	}

	@Override
	public void update(final Observable o, final Object arg) {

		if (o instanceof ObservedChanges) {

			if (this.type == TYPE.SELECTED) {

				final ObservedChanges observedChanges = (ObservedChanges) o;
				if (observedChanges.label.equals(CLABEL.SELECTED)) {

					this.setText("");

					if (observedChanges.isSet()) {

						final Change change = observedChanges.get().first();
						final String changeText = change
								.getChangedValueString();
						this.setText(changeText);
					}
				}

				else if (observedChanges.label.equals(CLABEL.NEIGHBORS)) {
					this.setText("");
				}
			}

			else if (this.type == TYPE.NEIGHBORS) {

				final ObservedChanges observedChanges = (ObservedChanges) o;
				if (observedChanges.label.equals(CLABEL.NEIGHBORS)) {

					this.setText("");

					if (observedChanges.isSet()) {

						final Change change = observedChanges.get().first();
						final String changeText = change
								.getChangedValueString();
						this.setText(changeText);
					}
				}

			}
		}
	}
}
