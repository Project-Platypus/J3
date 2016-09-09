package j3;

import org.controlsfx.control.PopOver;
import j3.widgets.Annotation;
import j3.widgets.TextWidget;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;

public class WidgetOptions extends Pane {

	private final Canvas canvas;
	
	private final PopOver popover;
	
	public WidgetOptions(Canvas canvas, PopOver popover) {
		super();
		this.canvas = canvas;
		this.popover = popover;
		
		TilePane tiles = new TilePane();
		tiles.setHgap(5);
		tiles.setVgap(5);
		
		Button textWidget = new Button();
		textWidget.setGraphic(new ImageView(new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/text_1x.png"))));
		textWidget.setOnAction(event -> handleWidget("text"));
		textWidget.setTooltip(new Tooltip("Display text anywhere in canvas"));
		
		Button annotationWidget = new Button();
		annotationWidget.setGraphic(new ImageView(new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/comment_1x.png"))));
		annotationWidget.setOnAction(event -> handleWidget("annotation"));
		annotationWidget.setTooltip(new Tooltip("Display details about a selected data point"));
		
		Button inspectorWidget = new Button();
		inspectorWidget.setGraphic(new ImageView(new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/inspect_1x.png"))));
		inspectorWidget.setOnAction(event -> handleWidget("inspector"));
		inspectorWidget.setTooltip(new Tooltip("Inspects points hovered by the mouse"));
		
		tiles.getChildren().addAll(textWidget, annotationWidget);
		
		TitledPane pane = new TitledPane();
		pane.setText("Standard Widgets");
		pane.setContent(tiles);
		
		getChildren().add(pane);
	}
	
	public void handleWidget(String type) {
		switch (type) {
		case "text":
			new TextWidget().onActivate(canvas);
			break;
		case "annotation":
			new Annotation().onActivate(canvas);
			break;
		case "inspector":
//			gui.setSingleClickHandler(event -> {
//				Point2D point = new Point2D(event.getScreenX(), event.getScreenY());
//				
//				point = gui.getContentRoot().screenToLocal(point);
//				
//				DataInspector inspector = new DataInspector(gui);
//				inspector.setLayoutX(point.getX());
//				inspector.setLayoutY(point.getY());
//				gui.getContentRoot().getChildren().add(inspector);
//				event.consume();
//			});
			break;
		default:
			break;
		}
		
		popover.hide();
	}
	
}
