package j3.widgets;

import j3.Canvas;
import javafx.scene.Node;

public interface Widget<T extends Node> {
	
	public T getNode();
	
	/**
	 * Invoked when the widget is being prepared for display, prior to adding
	 * it to the scene graph.
	 */
	public default void initialize(Canvas canvas) {
		
	}
	
	/**
	 * Invokved when the user manually activates the widget.  Typically, this
	 * could either add the widget to the scene or register an event handler
	 * for placing the widget on the scene.
	 */
	public default void onActivate(Canvas canvas) {
		
	}
	
	/**
	 * Invoked immediately after the widget has been added to the scene graph.
	 */
	public default void onAdd(Canvas canvas) {
		
	}
	
	/**
	 * Invokved immediately after the widget has been removed from the scene
	 * graph.
	 */
	public default void onRemove(Canvas canvas) {
		
	}

}
