package j3;

import javafx.beans.property.StringProperty;
import javafx.beans.property.StringPropertyBase;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

public class Annotation extends Region {
	
	private StringProperty title = new StringPropertyBase("") {

		@Override
		protected void invalidated() {
			titleText.setText(title.get());
		}

		@Override
		public Object getBean() {
			return Annotation.this;
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
	
	private Node target;
	
	private Text titleText = new Text();
	
	private Line arrow;
	
	private BorderPane pane;
	
	private ChangeListener<? super Transform> changeListener;
	
	private double mousePosX, mousePosY;
	private double mouseStartX, mouseStartY;
	private double initX, initY, initWidth, initHeight;
	private Translate translate = new Translate();

	public Annotation(Node content) {
		pane = new BorderPane();
		pane.setStyle("-fx-background-radius: 5; -fx-background-color: white; -fx-border-color: black; -fx-border-radius: 5; -fx-border-width: 2; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.9), 5, 0.0, 0, 1);");
		pane.setPrefWidth(300);
		pane.setPadding(new Insets(5, 5, 5, 5));
		
		titleText.setText(getTitle());
		
		Button button = new Button();
		button.setGraphic(new ImageView(new Image(Annotation.class.getResourceAsStream("/j3/icons/close_1x_nopadding.png"))));
		button.setStyle("-fx-background-color: transparent;");
		
		BorderPane title = new BorderPane();
		title.setCenter(titleText);
		title.setRight(button);
		
		pane.setTop(title);
		pane.setCenter(content);
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
			
			if (pane.getCursor() == Cursor.MOVE || pane.getCursor() == Cursor.DEFAULT) {
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
				pane.setCursor(Cursor.DEFAULT);
			}
		});
		
		button.setOnAction(event -> {
			((Group)getParent()).getChildren().remove(this);
			
			if (target != null) {
				target.localToSceneTransformProperty().removeListener(changeListener);
				pane.localToSceneTransformProperty().removeListener(changeListener);
			}
		});
		
		button.setOnMouseMoved(event -> {
			pane.setCursor(Cursor.DEFAULT);
		});
		
		getTransforms().add(translate);
		setPickOnBounds(false);
		setManaged(false);
	}
	
	public void target(Subscene3D plot, Node node) {
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

}
