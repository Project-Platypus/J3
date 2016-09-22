package j3.widget;

import j3.Canvas;
import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;

public abstract class TitledWidget<T> extends Region implements Widget<TitledWidget<T>> {
	
	private StringProperty title = new StringPropertyBase("") {

		@Override
		protected void invalidated() {
			titleText.setText(title.get());
		}

		@Override
		public Object getBean() {
			return TitledWidget.this;
		}

		@Override
		public String getName() {
			return "title";
		}
		
	};
	
	public String getTitle() {
		return title.get();
	}
	
	public void setTitle(String title) {
		this.title.set(title);
	}
	
	public StringProperty titleProperty() {
		return title;
	}
	
	private Text titleText;
	
	private Button closeButton;
	
	protected BorderPane pane;
	
	private double mousePosX, mousePosY, mouseStartX, mouseStartY;
	
	private double initX, initY, initWidth, initHeight;
	
	private Translate translate = new Translate();

	public TitledWidget() {
		pane = new BorderPane();
		pane.getStyleClass().add("j3-titled-widget");
		pane.setPrefWidth(300);
		pane.setPadding(new Insets(5, 5, 5, 5));
		
		titleText = new Text();
		titleText.setText(getTitle());
		titleText.getStyleClass().add("j3-titled-widget-title");
		
		closeButton = new Button();
		closeButton.getStyleClass().add("j3-close-button");
		
		BorderPane title = new BorderPane();
		title.setCenter(titleText);
		title.setRight(closeButton);
		
		pane.setTop(title);
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
			
			if (pane.getCursor() == Cursor.S_RESIZE || pane.getCursor() == Cursor.SW_RESIZE || pane.getCursor() == Cursor.SE_RESIZE) {
				diffHeight = mousePosY - mouseStartY;
			}
			
			if (pane.getCursor() == Cursor.N_RESIZE || pane.getCursor() == Cursor.NW_RESIZE || pane.getCursor() == Cursor.NE_RESIZE) {
				diffY = mousePosY - mouseStartY;
				diffHeight = -diffY;
			}
			
			if (pane.getCursor() == Cursor.E_RESIZE || pane.getCursor() == Cursor.NE_RESIZE || pane.getCursor() == Cursor.SE_RESIZE) {
				diffWidth = mousePosX - mouseStartX;
			}
			
			if (pane.getCursor() == Cursor.W_RESIZE || pane.getCursor() == Cursor.NW_RESIZE || pane.getCursor() == Cursor.SW_RESIZE) {
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
		
		closeButton.setOnMouseMoved(event -> {
			pane.setCursor(Cursor.DEFAULT);
		});
		
		getTransforms().add(translate);
		setPickOnBounds(false);
		setManaged(false);
	}

	@Override
	public TitledWidget<T> getNode() {
		return this;
	}

	@Override
	public void onActivate(Canvas canvas) {
		closeButton.setOnAction(e -> {
			canvas.remove(this);
		});
	}
	
	public void setContent(Node node) {
		pane.setCenter(node);
		
		node.addEventFilter(MouseEvent.MOUSE_ENTERED, event -> {
			pane.setCursor(null);
		});
	}

}
