package j3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import j3.widget.Widget;
import javafx.animation.Animation.Status;
import javafx.animation.FadeTransition;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class Canvas extends SubScene {
	
	private final Group root;

	private final ObservableList<Widget<? extends Node>> widgets;
	
	private final PropertyRegistry propertyRegistry;
	
	private final Map<Widget<? extends Node>, Transition> transitions;
	
	private final ToolBar toolBar;
	
	private EventHandler<MouseEvent> singleClickHandler;

	public Canvas(double width, double height, ToolBar toolBar) {
		super(new Group(), width, height);
		this.toolBar = toolBar;
		
		root = (Group)getRoot();
		propertyRegistry = new PropertyRegistry();
		widgets = FXCollections.observableArrayList();
		transitions = new HashMap<>();
		
		widgets.addListener((ListChangeListener.Change<? extends Widget<? extends Node>> change) -> {
			while (change.next()) {
				if (change.wasAdded()) {
					root.getChildren().addAll(getNodes(change.getAddedSubList()));
				}
				
				if (change.wasRemoved()) {
					root.getChildren().removeAll(getNodes(change.getRemoved()));
				}
			}
		});
		
		setOnMouseClicked(event -> {
			invokeSingleClickHandler(event);
		});
	}
	
	public void setSingleClickHandler(EventHandler<MouseEvent> singleClickHandler) {
		this.singleClickHandler = singleClickHandler;
		
		if (this.singleClickHandler != null) {
			setCursor(Cursor.CROSSHAIR);
		}
	}
	
	public void invokeSingleClickHandler(MouseEvent event) {
		if (singleClickHandler != null) {
			singleClickHandler.handle(event);
			singleClickHandler = null;
			setCursor(null);
		}
	}
	
	protected Collection<Node> getNodes(Collection<? extends Widget<? extends Node>> widgets) {
		List<Node> result = new ArrayList<>();
		
		for (Widget<? extends Node> widget : widgets) {
			result.add(widget.getNode());
		}
		
		return result;
	}
	
	public ObservableList<Widget<?>> getWidgets() {
		return widgets;
	}
	
	public PropertyRegistry getPropertyRegistry() {
		return propertyRegistry;
	}
	
	public ToolBar getToolBar() {
		return toolBar;
	}
	
	public void add(Widget<? extends Node> widget) {
		widget.initialize(this);
		
		// stop any running transitions
		Transition oldTransition = transitions.get(widget);
		
		if ((oldTransition != null) && (oldTransition.getStatus() == Status.RUNNING)) {
			oldTransition.stop();
		}
		
		// add the widget
		Node node = widget.getNode();
		node.setOpacity(0.0);
		
		if (node.getParent() == null) {
			widgets.add(widget);
			widget.onAdd(this);
		}
		
		// start the transition
		FadeTransition newTransition = new FadeTransition(new Duration(1000), node);
		newTransition.setToValue(1.0);
		newTransition.play();
		transitions.put(widget, newTransition);
	}
	
	public void remove(Widget<? extends Node> widget) {
		// stop any running transitions
		Transition oldTransition = transitions.get(widget);
		
		if ((oldTransition != null) && (oldTransition.getStatus() == Status.RUNNING)) {
			oldTransition.stop();
		}
		
		// call the onRemove method so the widget can unregister any listeners/bindings immediately
		widget.onRemove(this);
		
		// start the transition
		Node node = widget.getNode();
		FadeTransition newTransition = new FadeTransition(new Duration(1000), node);
		newTransition.setToValue(0.0);
		newTransition.setOnFinished(e -> {
			widgets.remove(widget);
		});
		newTransition.play();
	}
	
	public void removeAll() {
		for (Widget<?> widget : widgets) {
			remove(widget);
		}
	}

}
