package j3.widget;

import j3.Canvas;
import javafx.scene.Node;

/**
 * Methods for defining the lifecycle of a widget within J3. A widget is
 * typically constructed two ways: either procedurally in code or as a result of
 * the user manually adding the widget. For the former, the code constructs the
 * widget and adds it to the canvas. Doing so will automatically invoke the
 * {@code initialize} and {@code onAdd}. When the user manually creates a widget
 * from the widget pane, {@code onActivate} is called. From there, the desired
 * behavior can be implemented. For example, if you want the user to click on
 * the canvas to position the widget, register a single-click handler with the
 * canvas.
 * 
 * Widgets can also add buttons to the toolbar, register as event handlers
 * within the canvas, interact with other scene graph nodes, etc.
 */
public interface Widget<T extends Node> {

	public T getNode();

	/**
	 * Invoked when the widget is being prepared for display, prior to adding it to
	 * the scene graph.
	 */
	public default void initialize(Canvas canvas) {

	}

	/**
	 * Invokved when the user manually activates the widget. Typically, this could
	 * either add the widget to the scene or register an event handler for placing
	 * the widget on the scene.
	 */
	public default void onActivate(Canvas canvas) {

	}

	/**
	 * Invoked immediately after the widget has been added to the scene graph.
	 */
	public default void onAdd(Canvas canvas) {

	}

	/**
	 * Invokved immediately after the widget has been removed from the scene graph.
	 */
	public default void onRemove(Canvas canvas) {

	}

}
