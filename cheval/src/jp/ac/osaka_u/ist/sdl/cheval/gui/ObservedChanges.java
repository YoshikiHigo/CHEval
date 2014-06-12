package jp.ac.osaka_u.ist.sdl.cheval.gui;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.cheval.Change;

public class ObservedChanges extends Observable {

	public enum CLABEL {
		SELECTED, NEIGHBORS;
	}

	private static final Map<CLABEL, ObservedChanges> INSTANCES = new HashMap<CLABEL, ObservedChanges>();

	private final SortedSet<Change> changes;
	public final CLABEL label;
	private Observer source;

	private ObservedChanges(final CLABEL label) {

		if (null == label) {
			throw new NullPointerException();
		}

		this.changes = new TreeSet<Change>();
		this.source = null;
		this.label = label;
	}

	public static final ObservedChanges getInstance(final CLABEL label) {
		ObservedChanges instance = INSTANCES.get(label);
		if (null == instance) {
			instance = new ObservedChanges(label);
			INSTANCES.put(label, instance);
		}
		return instance;
	}

	public boolean add(final Change change, final Observer source) {

		if (null == change) {
			return false;
		}

		this.changes.add(change);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);

		return true;
	}

	public boolean addAll(final Collection<Change> change, final Observer source) {

		if (null == changes) {
			return false;
		}

		this.changes.addAll(changes);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);

		return true;
	}

	public boolean remove(final Change change, final Observer source) {

		if (null == change) {
			return false;
		}

		this.changes.remove(change);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);

		return true;
	}

	public boolean removeAll(final Collection<Change> changes,
			final Observer source) {

		if (null == changes) {
			return false;
		}

		this.changes.removeAll(changes);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);

		return true;
	}

	public boolean set(final Change change, final Observer source) {

		if (null == change) {
			return false;
		}

		this.changes.clear();
		this.changes.add(change);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);

		return true;
	}

	public boolean setAll(final Collection<Change> changes,
			final Observer source) {

		if (null == changes) {
			return false;
		}

		this.changes.clear();
		this.changes.addAll(changes);
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);

		return true;
	}

	public boolean isSet() {
		return !this.changes.isEmpty();
	}

	public void clear(final Observer source) {

		this.changes.clear();
		this.source = source;

		this.setChanged();
		this.notifyObservers(source);
	}

	public SortedSet<Change> get() {
		return Collections.unmodifiableSortedSet(this.changes);
	}

	public Observer getSource() {
		return this.source;
	}
}
