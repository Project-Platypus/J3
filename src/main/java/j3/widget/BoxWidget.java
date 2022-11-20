package j3.widget;

import org.dom4j.Element;

import j3.Canvas;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.transform.Translate;

public abstract class BoxWidget<T> extends Region implements Widget<BoxWidget<T>> {

	protected BorderPane pane;

	private double mousePosX, mousePosY, mouseStartX, mouseStartY;

	private double initX, initY, initWidth, initHeight;

	private Translate translate = new Translate();

	public BoxWidget() {
		pane = new BorderPane();
		pane.getStyleClass().add("j3-box-widget");
		pane.setPrefWidth(300);
		pane.setPadding(new Insets(5, 5, 5, 5));

		getChildren().add(pane);

		pane.setOnMousePressed(event -> {
			mouseStartX = event.getScreenX();
			mouseStartY = event.getScreenY();
			initX = translate.getX();
			initY = translate.getY();
			initWidth = pane.getWidth();
			initHeight = pane.getHeight();

			event.consume();
		});

		pane.setOnMouseDragged(event -> {
			mousePosX = event.getScreenX();
			mousePosY = event.getScreenY();

			double diffX = 0.0, diffY = 0.0, diffWidth = 0.0, diffHeight = 0.0;

			if (pane.getCursor() == null || pane.getCursor() == Cursor.MOVE || pane.getCursor() == Cursor.DEFAULT) {
				diffX = mousePosX - mouseStartX;
				diffY = mousePosY - mouseStartY;

			}

			if (pane.getCursor() == Cursor.S_RESIZE || pane.getCursor() == Cursor.SW_RESIZE
					|| pane.getCursor() == Cursor.SE_RESIZE) {
				diffHeight = mousePosY - mouseStartY;
			}

			if (pane.getCursor() == Cursor.N_RESIZE || pane.getCursor() == Cursor.NW_RESIZE
					|| pane.getCursor() == Cursor.NE_RESIZE) {
				diffY = mousePosY - mouseStartY;
				diffHeight = -diffY;
			}

			if (pane.getCursor() == Cursor.E_RESIZE || pane.getCursor() == Cursor.NE_RESIZE
					|| pane.getCursor() == Cursor.SE_RESIZE) {
				diffWidth = mousePosX - mouseStartX;
			}

			if (pane.getCursor() == Cursor.W_RESIZE || pane.getCursor() == Cursor.NW_RESIZE
					|| pane.getCursor() == Cursor.SW_RESIZE) {
				diffX = mousePosX - mouseStartX;
				diffWidth = -diffX;
			}

			translate.setX(initX + diffX);
			translate.setY(initY + diffY);
			pane.setPrefWidth(initWidth + diffWidth);
			pane.setPrefHeight(initHeight + diffHeight);
			event.consume();
		});

		pane.setOnMouseMoved(event -> {
			mousePosX = event.getScreenX();
			mousePosY = event.getScreenY();

			Point2D point = new Point2D(mousePosX, mousePosY);
			point = pane.screenToLocal(point);

			boolean resizeLeft = false, resizeTop = false, resizeRight = false, resizeBottom = false;

			if (Math.abs(point.getX() - 0) < 2) {
				resizeLeft = true;
			} else if (Math.abs(point.getX() - pane.getWidth()) < 2) {
				resizeRight = true;
			}

			if (Math.abs(point.getY() - 0) < 2) {
				resizeTop = true;
			} else if (Math.abs(point.getY() - pane.getHeight()) < 2) {
				resizeBottom = true;
			}

			if (resizeLeft && resizeTop) {
				pane.setCursor(Cursor.NW_RESIZE);
			} else if (resizeLeft && resizeBottom) {
				pane.setCursor(Cursor.SW_RESIZE);
			} else if (resizeRight && resizeTop) {
				pane.setCursor(Cursor.NE_RESIZE);
			} else if (resizeRight && resizeBottom) {
				pane.setCursor(Cursor.SE_RESIZE);
			} else if (resizeLeft) {
				pane.setCursor(Cursor.W_RESIZE);
			} else if (resizeRight) {
				pane.setCursor(Cursor.E_RESIZE);
			} else if (resizeTop) {
				pane.setCursor(Cursor.N_RESIZE);
			} else if (resizeBottom) {
				pane.setCursor(Cursor.S_RESIZE);
			} else {
				pane.setCursor(null);
			}
		});

		getTransforms().add(translate);
		setPickOnBounds(false);
		setManaged(false);
	}

	@Override
	public BoxWidget<T> getNode() {
		return this;
	}

	@Override
	public void onAdd(Canvas canvas) {

	}

	public void setContent(Node node) {
		pane.setCenter(node);

		node.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			pane.setCursor(null);
		});
	}

	protected void saveStateInternal(Element element) {
		Element width = element.addElement("width");
		width.setText(Double.toString(pane.getWidth()));

		Element height = element.addElement("height");
		height.setText(Double.toString(pane.getHeight()));

		Bounds bounds = pane.getBoundsInLocal();
		bounds = pane.getLocalToSceneTransform().transform(bounds);

		Element x = element.addElement("posX");
		x.setText(Double.toString(bounds.getMinX()));

		Element y = element.addElement("posY");
		y.setText(Double.toString(bounds.getMinY()));
	}

	protected void restoreStateInternal(Element element) {
		pane.setLayoutX(Double.parseDouble(element.elementText("posX")));
		pane.setLayoutY(Double.parseDouble(element.elementText("posY")));
		pane.setPrefWidth(Double.parseDouble(element.elementText("width")));
		pane.setPrefHeight(Double.parseDouble(element.elementText("height")));
	}

}
