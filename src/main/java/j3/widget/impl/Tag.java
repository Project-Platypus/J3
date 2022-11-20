package j3.widget.impl;

import j3.Canvas;
import j3.Selector;
import j3.dataframe.Instance;
import j3.widget.SerializableWidget;
import j3.widget.TargetableWidget;
import j3.widget.Widget;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class Tag extends Group implements Widget<Tag>, SerializableWidget {

	private Node target;

	private Circle shape;

	private Color color = Color.RED;

	private ChangeListener<? super Transform> changeListener;

	public Tag() {
		super();

		shape = new Circle(0, 0, 20);
		shape.getStyleClass().add("j3-tag");
		shape.setFill(null);
		shape.setStroke(color);
		shape.setStrokeWidth(4.0);
		shape.setPickOnBounds(false);

		setPickOnBounds(false);
	}

	public void target(Node node, Canvas canvas) {
		if (target != null) {
			getChildren().remove(shape);
			target.localToSceneTransformProperty().removeListener(changeListener);

			// remove this tag as a dependency of the old target widget
			Widget<?> widget = canvas.findWidgetContaining(target);

			if (widget != null && widget instanceof TargetableWidget) {
				((TargetableWidget) widget).getDependencies().remove(this);
			}
		}

		target = node;

		getChildren().add(shape);
		shape.toBack();

		changeListener = (observable, oldValue, newValue) -> {
			Bounds endBounds = node.localToScreen(node.getBoundsInLocal());

			if (endBounds != null) {
				Point2D endPoint = new Point2D((endBounds.getMinX() + endBounds.getMaxX()) / 2.0,
						(endBounds.getMinY() + endBounds.getMaxY()) / 2.0);

				endPoint = screenToLocal(endPoint);

				if (endPoint != null) {
					shape.getTransforms().setAll(new Translate(endPoint.getX(), endPoint.getY()));
				}
			}
		};

		node.localToSceneTransformProperty().addListener(changeListener);
		changeListener.changed(null, null, null);

		// add this tag as a dependency of the target widget
		Widget<?> widget = canvas.findWidgetContaining(target);

		if (widget != null && widget instanceof TargetableWidget) {
			((TargetableWidget) widget).getDependencies().add(this);
		}
	}

	@Override
	public Tag getNode() {
		return this;
	}

	@Override
	public void onActivate(Canvas canvas) {
		canvas.setSingleClickHandler(event -> {
			if ((event.getPickResult().getIntersectedNode() instanceof Shape3D)
					&& (event.getPickResult().getIntersectedNode().getUserData() instanceof Instance)
					&& (event.getPickResult().getIntersectedNode().getId() != null)) {
				target(event.getPickResult().getIntersectedNode(), canvas);
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

	@Override
	public Element saveState(Canvas canvas) {
		Element element = DocumentHelper.createElement("tag");

		Element targetElement = element.addElement("target");
		targetElement.setText(target.getId());

		Element colorElement = element.addElement("color");
		colorElement.addAttribute("r", Double.toString(color.getRed()));
		colorElement.addAttribute("g", Double.toString(color.getGreen()));
		colorElement.addAttribute("b", Double.toString(color.getBlue()));
		colorElement.addAttribute("a", Double.toString(color.getOpacity()));

		Element sizeElement = element.addElement("size");
		sizeElement.setText(Double.toString(shape.getRadius()));

		Element thicknessElement = element.addElement("thickness");
		thicknessElement.setText(Double.toString(shape.getStrokeWidth()));

		return element;
	}

	@Override
	public void restoreState(Element element, Canvas canvas) {
		String id = element.elementText("target");
		target(Selector.on(canvas).select("#" + id).getFirst(), canvas);

		Element colorElement = element.element("color");
		color = Color.color(Double.parseDouble(colorElement.attributeValue("r")),
				Double.parseDouble(colorElement.attributeValue("g")),
				Double.parseDouble(colorElement.attributeValue("b")),
				Double.parseDouble(colorElement.attributeValue("a")));

		shape.setStroke(color);
		shape.setRadius(Double.parseDouble(element.elementText("size")));
		shape.setStrokeWidth(Double.parseDouble(element.elementText("thickness")));
	}

}
