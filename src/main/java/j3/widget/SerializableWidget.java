package j3.widget;

import j3.Canvas;

import org.dom4j.Element;

/**
 * Optional interface for widgets to include save/restore in their lifecycle.
 * When restoring the state, widgets are first added to the canvas which invokes
 * their {@code initialize()} method; then their state is restored.
 */
public interface SerializableWidget {
	
	public Element saveState(Canvas canvas);
	
	public void restoreState(Element data, Canvas canvas);

}
