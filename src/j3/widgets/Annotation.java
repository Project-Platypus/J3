package j3.widgets;

import j3.Canvas;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
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

public class Annotation extends TitledWidget<Annotation> {
	
	private Node target;

	private Line arrow;

	private ChangeListener<? super Transform> changeListener;

	public Annotation() {
		super();
	}
	
	public void target(Node node) {
		if (arrow != null) {
			getChildren().remove(arrow);
			target.localToSceneTransformProperty().removeListener(changeListener);
			pane.localToSceneTransformProperty().removeListener(changeListener);
		}
		
		target = node;
		
		arrow = new Line(200, 100, 300, 300);
		arrow.setStrokeWidth(2.0);
		arrow.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 5, 0.0, 0, 1);");
		getChildren().add(arrow);
		arrow.toBack();
		
		changeListener = (observable, oldValue, newValue) -> {
			Bounds bounds = node.localToScreen(node.getBoundsInLocal());
			Point2D point = new Point2D((bounds.getMinX()+bounds.getMaxX())/2.0, (bounds.getMinY()+bounds.getMaxY())/2.0);
			
			point = screenToLocal(point);
			
			if (point != null) {
				arrow.setEndX(point.getX());
				arrow.setEndY(point.getY());
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void onActivate(Canvas canvas) {
		super.onActivate(canvas);
		
		canvas.setSingleClickHandler(event -> {
			if ((event.getPickResult().getIntersectedNode() instanceof Shape3D) &&
					(event.getPickResult().getIntersectedNode().getUserData() instanceof Instance)) {
				Instance instance = (Instance)event.getPickResult().getIntersectedNode().getUserData();
				DataFrame table = (DataFrame)canvas.getSharedData().get("data").getValue();
				
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
				
				setContent(tableView);
				setTitle("Instance Details");
				target(event.getPickResult().getIntersectedNode());
				
				canvas.add(this);
			}
			
			event.consume();
		});
	}

	@Override
	public void onRemove(Canvas canvas) {
		if (target != null) {
			target.localToSceneTransformProperty().removeListener(changeListener);
			pane.localToSceneTransformProperty().removeListener(changeListener);
		}
	}

}
