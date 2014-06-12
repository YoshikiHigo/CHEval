package jp.ac.osaka_u.ist.sdl.cheval.gui.ccode;

import java.awt.Color;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;

import jp.ac.osaka_u.ist.sdl.cheval.Method;
import jp.ac.osaka_u.ist.sdl.cheval.Vector;
import jp.ac.osaka_u.ist.sdl.cheval.db.MethodRetriever;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges;
import jp.ac.osaka_u.ist.sdl.cheval.gui.ObservedChanges.CLABEL;

import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCClient;

public class CCode extends JTextArea implements Observer {

	public enum BEFOREAFTER {
		BEFORE, AFTER;
	}

	public enum TYPE {
		SELECTED, NEIGHBORS;
	}

	static public final int TAB_SIZE = 4;

	public final JScrollPane scrollPane;

	private final String repository;
	private final String database;
	private final BEFOREAFTER beforeafter;
	private final TYPE type;

	private Method method;

	public CCode(final String repository, final String database,
			final BEFOREAFTER beforeafter, final TYPE type) {

		this.setTabSize(TAB_SIZE);

		this.repository = repository;
		this.database = database;
		this.beforeafter = beforeafter;
		this.type = type;
		this.method = null;

		final Insets margin = new Insets(5, 50, 5, 5);
		this.setMargin(margin);
		this.setUI(new CCodeUI(0, 0, this, margin));
		this.setText("");
		this.setEditable(false);

		this.scrollPane = new JScrollPane();
		this.scrollPane.setViewportView(this);
		this.scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		this.scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		this.setTitle(null);

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {

				final int button = e.getButton();
				final int clickCount = e.getClickCount();

				switch (button) {
				case MouseEvent.BUTTON1:
					switch (clickCount) {
					case 1:
						break;
					case 2:
						CCode.this.display();
						break;
					default:
					}
					break;
				case MouseEvent.BUTTON2:
					break;
				case MouseEvent.BUTTON3:
					break;
				default:
				}
			}
		});
	}

	private void setTitle(final String path) {
		final StringBuilder title = new StringBuilder();
		title.append("Source Code View");
		if (null != path) {
			title.append(" (");
			title.append(path);
			title.append(")");
		}
		this.scrollPane.setBorder(new TitledBorder(new LineBorder(Color.black),
				title.toString()));
	}

	@Override
	public void update(final Observable o, final Object arg) {

		if (o instanceof ObservedChanges) {

			if (this.type == TYPE.SELECTED) {

				final ObservedChanges observedChanges = (ObservedChanges) o;
				if (observedChanges.label.equals(CLABEL.SELECTED)) {

					this.setText("");

					if (observedChanges.isSet()) {

						try {

							final Vector change = observedChanges.get().first();
							final long methodID = this.beforeafter == BEFOREAFTER.BEFORE ? change.beforeMethodID
									: change.afterMethodID;

							final MethodRetriever retriever = MethodRetriever
									.getInstance(this.database);
							this.method = retriever.getMethod(methodID);

							final SVNURL fileurl = SVNURL.fromFile(new File(
									this.repository + File.separator
											+ this.method.path));
							final SVNWCClient wcClient = SVNClientManager
									.newInstance().getWCClient();

							final StringBuilder text = new StringBuilder();
							wcClient.doGetFileContents(
									fileurl,
									SVNRevision.create(this.method.endRevision),
									SVNRevision.create(this.method.endRevision),
									false, new OutputStream() {
										@Override
										public void write(int b)
												throws IOException {
											text.append((char) b);
										}
									});

							final Insets margin = new Insets(5, 50, 5, 5);
							this.setMargin(margin);
							this.setUI(new CCodeUI(this.method.startLine,
									this.method.endLine, this, margin));
							this.setText(text.toString());
							this.setTitle(this.method.path);

						} catch (final Exception e) {
							e.printStackTrace();
						}
					} else {
						this.method = null;
					}
				}

			} else if (this.type == TYPE.NEIGHBORS) {

				final ObservedChanges observedChanges = (ObservedChanges) o;
				if (observedChanges.label.equals(CLABEL.NEIGHBORS)) {

					this.setText("");

					if (observedChanges.isSet()) {

						try {

							final Vector change = observedChanges.get().first();
							final long methodID = this.beforeafter == BEFOREAFTER.BEFORE ? change.beforeMethodID
									: change.afterMethodID;

							final MethodRetriever retriever = MethodRetriever
									.getInstance(this.database);
							this.method = retriever.getMethod(methodID);

							final SVNURL fileurl = SVNURL.fromFile(new File(
									this.repository + File.separator
											+ this.method.path));
							final SVNWCClient wcClient = SVNClientManager
									.newInstance().getWCClient();

							final StringBuilder text = new StringBuilder();
							wcClient.doGetFileContents(
									fileurl,
									SVNRevision.create(this.method.endRevision),
									SVNRevision.create(this.method.endRevision),
									false, new OutputStream() {
										@Override
										public void write(int b)
												throws IOException {
											text.append((char) b);
										}
									});

							final Insets margin = new Insets(5, 50, 5, 5);
							this.setMargin(margin);
							this.setUI(new CCodeUI(this.method.startLine,
									this.method.endLine, this, margin));
							this.setText(text.toString());
							this.setTitle(this.method.path);

						} catch (final Exception e) {
							e.printStackTrace();
						}
					} else {
						this.method = null;
					}

				} else if (observedChanges.label.equals(CLABEL.SELECTED)) {
					this.setText("");
				}
			}
		}
	}

	private void display() {

		try {

			final Document doc = this.getDocument();
			final Element root = doc.getDefaultRootElement();
			final Element element = root.getElement(this.method.startLine);
			final int nextOffset = element.getStartOffset();

			final Rectangle rect = this.modelToView(nextOffset);
			final Rectangle vr = this.scrollPane.getViewport().getViewRect();

			if ((null != rect) && (null != vr)) {
				rect.setSize(10, vr.height);
				this.scrollRectToVisible(rect);
				this.setCaretPosition(nextOffset);
			}

		} catch (BadLocationException e) {
			System.err.println(e.getMessage());
		}
	}
}
