package j3.widget.impl;

import j3.Canvas;
import j3.Selector;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.widget.SerializableWidget;
import j3.widget.TitledWidget;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Transform;
import javafx.util.Pair;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Annotation extends TitledWidget<Annotation> implements SerializableWidget {
	
	private Node target;

	private Line arrow;

	private ChangeListener<? super Transform> changeListener;

	public Annotation() {
		super();
	}
	
	public void target(Node node) {
		if (target != null) {
			getChildren().remove(arrow);
			target.localToSceneTransformProperty().removeListener(changeListener);
			pane.localToSceneTransformProperty().removeListener(changeListener);
		}
		
		target = node;
		
		arrow = new Line(0, 0, 0, 0);
		arrow.getStyleClass().add("j3-annotation-arrow");
		getChildren().add(arrow);
		arrow.toBack();
		
		changeListener = (observable, oldValue, newValue) -> {
			Bounds startBounds = pane.localToScreen(pane.getBoundsInLocal());
			
			if (startBounds != null) {
				Point2D startPoint = new Point2D((startBounds.getMinX()+startBounds.getMaxX())/2.0, (startBounds.getMinY()+startBounds.getMaxY())/2.0);
				
				startPoint = screenToLocal(startPoint);
				
				if (startPoint != null) {
					arrow.setStartX(startPoint.getX());
					arrow.setStartY(startPoint.getY());
				}
			}
			
			Bounds endBounds = node.localToScreen(node.getBoundsInLocal());
			
			if (endBounds != null) {
				Point2D endPoint = new Point2D((endBounds.getMinX()+endBounds.getMaxX())/2.0, (endBounds.getMinY()+endBounds.getMaxY())/2.0);
				
				endPoint = screenToLocal(endPoint);
				
				if (endPoint != null) {
					arrow.setEndX(endPoint.getX());
					arrow.setEndY(endPoint.getY());
				}
			}
		};
		
		pane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
			changeListener.changed(null, null, null);
		});
		
		node.localToSceneTransformProperty().addListener(changeListener);
		pane.localToSceneTransformProperty().addListener(changeListener);
	}

	@Override
	public Annotation getNode() {
		return this;
	}
	
	@Override
	public void initialize(Canvas canvas) {
		setTitle("Instance Details");
	}

	@Override
	public void onActivate(Canvas canvas) {
		canvas.setSingleClickHandler(event -> {
			if ((event.getPickResult().getIntersectedNode() instanceof Shape3D) &&
					(event.getPickResult().getIntersectedNode().getUserData() instanceof Instance)) {
				Instance instance = (Instance)event.getPickResult().getIntersectedNode().getUserData();
				DataFrame table = (DataFrame)canvas.getPropertyRegistry().get("data").getValue();
				
				setContent(createContent(instance, table));
				target(event.getPickResult().getIntersectedNode());
				
				canvas.add(this);
			}
			
			event.consume();
		});
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected TableView createContent(Instance instance, DataFrame table) {
		TableView tableView = new TableView();
		tableView.setEditable(false);
		tableView.setFocusTraversable(false);
		tableView.setPrefHeight(100);
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn keyColumn = new TableColumn("Key");
		TableColumn valueColumn = new TableColumn("Value");
		
		tableView.getColumns().addAll(keyColumn, valueColumn);

		for (int j = 0; j < table.attributeCount(); j++) {
			tableView.getItems().add(new Pair<String, Object>(table.getAttribute(j).getName(), instance.get(table.getAttribute(j))));
		}
		
		keyColumn.setCellValueFactory(new PropertyValueFactory<Pair<String, Number>, String>("key"));
		valueColumn.setCellValueFactory(new PropertyValueFactory<Pair<String, Number>, Number>("value"));
		
		return tableView;
	}

	@Override
	public void onRemove(Canvas canvas) {
		if (target != null) {
			target.localToSceneTransformProperty().removeListener(changeListener);
			pane.localToSceneTransformProperty().removeListener(changeListener);
		}
	}
	
	@Override
	public Element saveState(Canvas canvas) {
		Element element = DocumentHelper.createElement("tag");
		
		// save the pane size
		saveStateInternal(element);

		// save the targeted node
		Element targetElement = element.addElement("target");
		targetElement.setText(target.getId());
		
		return element;
	}

	@Override
	public void restoreState(Element element, Canvas canvas) {
		// restore the pane size
		restoreStateInternal(element);
		
		// retarget the node
		String id = element.elementText("target");
		target(Selector.on(canvas).select("#" + id).getFirst());
		
		// update the data table
		Instance instance = (Instance)target.getUserData();
		DataFrame table = (DataFrame)canvas.getPropertyRegistry().get("data").getValue();
		
		setContent(createContent(instance, table));
	}

}
