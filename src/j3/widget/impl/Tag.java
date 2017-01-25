package j3.widget.impl;

import j3.Canvas;
import j3.dataframe.Instance;
import j3.widget.Widget;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

public class Tag extends Group implements Widget<Tag> {
	
	private Node target;

	private Circle shape;
	
	private Color color = Color.RED;

	private ChangeListener<? super Transform> changeListener;

	public Tag() {
		super();
	}
	
	public void target(Node node) {
		if (shape != null) {
			getChildren().remove(shape);
			target.localToSceneTransformProperty().removeListener(changeListener);
		}
		
		target = node;
		
		shape = new Circle(0, 0, 20);
		shape.getStyleClass().add("j3-tag");
		shape.setFill(null);
		shape.setStroke(color);
		shape.setStrokeWidth(4.0);
		shape.setPickOnBounds(false);
		getChildren().add(shape);
		shape.toBack();
		
		changeListener = (observable, oldValue, newValue) -> {
			Bounds endBounds = node.localToScreen(node.getBoundsInLocal());
			
			if (endBounds != null) {
				Point2D endPoint = new Point2D((endBounds.getMinX()+endBounds.getMaxX())/2.0, (endBounds.getMinY()+endBounds.getMaxY())/2.0);
				
				endPoint = screenToLocal(endPoint);
				
				if (endPoint != null) {
					shape.getTransforms().setAll(new Translate(endPoint.getX(), endPoint.getY()));
				}
			}
		};
		
		node.localToSceneTransformProperty().addListener(changeListener);
		changeListener.changed(null, null, null);
		
		setPickOnBounds(false);
	}

	@Override
	public Tag getNode() {
		return this;
	}

	@Override
	public void onActivate(Canvas canvas) {
		canvas.setSingleClickHandler(event -> {
			if ((event.getPickResult().getIntersectedNode() instanceof Shape3D) &&
					(event.getPickResult().getIntersectedNode().getUserData() instanceof Instance)) {
				target(event.getPickResult().getIntersectedNode());
				canvas.add(this);
			}
			
			event.consume();
		});
	}

	@Override
	public void onAdd(Canvas canvas) {
		shape.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.SECONDARY) {
				ContextMenu menu = new ContextMenu();
				
				// delete menu item
				MenuItem delete = new MenuItem("Delete");
				delete.setOnAction(e -> {
					canvas.remove(this);
				});
				
				// create custom menu item for controls
				GridPane grid = new GridPane();
				grid.setAlignment(Pos.CENTER);
				
				Label sizeLabel = new Label("Size: ");
				grid.add(sizeLabel, 0, 0);
				
				Slider sizeSlider = new Slider();
				sizeSlider.setMin(1);
				sizeSlider.setMax(50);
				sizeSlider.setValue(shape.getRadius());
				sizeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
					shape.setRadius(sizeSlider.getValue());
				});
				grid.add(sizeSlider, 1, 0);
				
				Label thicknessLabel = new Label("Thickness: ");
				grid.add(thicknessLabel, 0, 1);
				
				Slider thicknessSlider = new Slider();
				thicknessSlider.setMin(1);
				thicknessSlider.setMax(10);
				thicknessSlider.setValue(shape.getStrokeWidth());
				thicknessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
					shape.setStrokeWidth(thicknessSlider.getValue());
				});
				grid.add(thicknessSlider, 1, 1);

				Label colorLabel = new Label("Color: ");
				grid.add(colorLabel, 0, 2);
				
				ColorPicker colorPicker = new ColorPicker();
				colorPicker.valueProperty().set(color);
				colorPicker.valueProperty().addListener((observable, oldValue, newValue) -> {
					color = newValue;
					shape.setStroke(color);
				});
				grid.add(colorPicker, 1, 2);
				
				MenuItem controlMenu = new CustomMenuItem(grid, false);
				
				// create the menu
				menu.getItems().addAll(delete, new SeparatorMenuItem(), controlMenu);
	
				// display the menu
				menu.show(this, event.getScreenX(), event.getScreenY());
			}
		});
	}

	@Override
	public void onRemove(Canvas canvas) {
		if (target != null) {
			target.localToSceneTransformProperty().removeListener(changeListener);
		}
	}

}
