package j3.widget.impl;

import j3.Canvas;
import j3.Selector;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.widget.BoxWidget;
import j3.widget.SerializableWidget;
import j3.widget.TargetableWidget;
import j3.widget.Widget;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Transform;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class TextAnnotation extends BoxWidget<TextAnnotation> implements SerializableWidget {

	private Node target;

	private Line arrow;

	private Label text;

	private TextArea editor;

	private ChangeListener<? super Transform> changeListener;

	private ListChangeListener<? super Widget<?>> widgetChangeListener;

	public TextAnnotation() {
		super();

		text = new Label();
		text.setWrapText(true);
		setContent(text);
	}

	public void setText(String value) {
		text.setText(value);
	}

	public void target(Node node, Canvas canvas) {
		if (target != null) {
			getChildren().remove(arrow);
			target.localToSceneTransformProperty().removeListener(changeListener);
			pane.localToSceneTransformProperty().removeListener(changeListener);

			// remove this annotation as a dependency of the old target widget
			Widget<?> widget = canvas.findWidgetContaining(target);

			if (widget != null && widget instanceof TargetableWidget) {
				((TargetableWidget) widget).getDependencies().remove(this);
			}
		}

		target = node;

		arrow = new Line(0, 0, 0, 0);
		arrow.getStyleClass().add("j3-annotation-arrow");
		getChildren().add(arrow);
		arrow.toBack();

		changeListener = (observable, oldValue, newValue) -> {
			Bounds startBounds = pane.localToScreen(pane.getBoundsInLocal());

			if (startBounds != null) {
				Point2D startPoint = new Point2D((startBounds.getMinX() + startBounds.getMaxX()) / 2.0,
						(startBounds.getMinY() + startBounds.getMaxY()) / 2.0);

				startPoint = screenToLocal(startPoint);

				if (startPoint != null) {
					arrow.setStartX(startPoint.getX());
					arrow.setStartY(startPoint.getY());
				}
			}

			Bounds endBounds = node.localToScreen(node.getBoundsInLocal());

			if (endBounds != null) {
				Point2D endPoint = new Point2D((endBounds.getMinX() + endBounds.getMaxX()) / 2.0,
						(endBounds.getMinY() + endBounds.getMaxY()) / 2.0);

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

		// add this annotation as a dependency of the target widget
		Widget<?> widget = canvas.findWidgetContaining(target);

		if (widget != null && widget instanceof TargetableWidget) {
			((TargetableWidget) widget).getDependencies().add(this);
		}
	}

	@Override
	public TextAnnotation getNode() {
		return this;
	}

	@Override
	public void initialize(Canvas canvas) {

	}

	@Override
	public void onActivate(Canvas canvas) {
		canvas.setSingleClickHandler(event -> {
			if ((event.getPickResult().getIntersectedNode() instanceof Shape3D)
					&& (event.getPickResult().getIntersectedNode().getUserData() instanceof Instance)) {
				Instance instance = (Instance) event.getPickResult().getIntersectedNode().getUserData();
				DataFrame table = (DataFrame) canvas.getPropertyRegistry().get("data").getValue();

				text.setText("You selected data point " + table.getInstances().indexOf(instance)
						+ ".  Double-click here to edit this text.");

				target(event.getPickResult().getIntersectedNode(), canvas);
				canvas.add(this);
			}

			event.consume();
		});
	}

	@Override
	public void onAdd(Canvas canvas) {
		super.onAdd(canvas);

		text.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() % 2 == 0) {
				editor = new TextArea();
				editor.setText(text.getText());
				editor.setWrapText(true);

				editor.layoutBoundsProperty().addListener(l -> {
					if (getScene() != null) {
						Bounds bounds = pane.localToScene(pane.getBoundsInLocal());

						editor.setLayoutX(0);
						editor.setLayoutY(0);
						editor.setPrefSize(bounds.getWidth(), bounds.getHeight());
					}
				});

				editor.setOnKeyPressed(e -> {
					KeyCodeCombination shiftEnter = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);

					if (shiftEnter.match(e)) {
						IndexRange range = editor.getSelection();
						String text = editor.getText();

						editor.setText(text.substring(0, range.getStart()) + "\n" + text.substring(range.getEnd()));
						editor.selectRange(range.getStart() + 1, range.getStart() + 1);
					} else if (e.getCode() == KeyCode.ENTER) {
						apply();
					}
				});

				getChildren().addAll(editor);

				editor.requestFocus();
				editor.selectAll();

				editor.focusedProperty().addListener((observable, oldValue, newValue) -> {
					if (editor != null && !newValue) {
						apply();
					}
				});
			} else if (event.getButton() == MouseButton.SECONDARY) {
				ContextMenu menu = new ContextMenu();

				MenuItem delete = new MenuItem("Delete");
				delete.setOnAction(e -> {
					canvas.remove(this);
				});

				menu.getItems().add(delete);
				menu.show(this, event.getScreenX(), event.getScreenY());
			}
		});
	}

	protected void apply() {
		text.setText(editor.getText());

		getChildren().remove(editor);
		setContent(text);
		editor = null;
	}

	@Override
	public void onRemove(Canvas canvas) {
		if (target != null) {
			target.localToSceneTransformProperty().removeListener(changeListener);
			pane.localToSceneTransformProperty().removeListener(changeListener);
		}

		if (widgetChangeListener != null) {
			canvas.getWidgets().removeListener(widgetChangeListener);
		}
	}

	@Override
	public Element saveState(Canvas canvas) {
		Element element = DocumentHelper.createElement("textAnnotation");

		// save the pane size
		saveStateInternal(element);

		// save the targeted node
		Element targetElement = element.addElement("target");
		targetElement.setText(target.getId());

		// save the text
		Element textElement = element.addElement("text");
		textElement.setText(text.getText());

		return element;
	}

	@Override
	public void restoreState(Element element, Canvas canvas) {
		// restore the pane size
		restoreStateInternal(element);

		// retarget the node
		String id = element.elementText("target");
		target(Selector.on(canvas).select("#" + id).getFirst(), canvas);

		// update the text
		text.setText(element.elementText("text"));
	}

}
