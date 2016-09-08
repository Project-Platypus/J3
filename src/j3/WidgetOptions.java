package j3;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.NotificationPane;

import j3.dataframe.DataFrame;
import j3.widgets.DataInspector;
import j3.widgets.TextWidget;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Translate;
import javafx.util.Pair;

public class WidgetOptions extends Pane {

	private final GUI gui;
	
	private final PopOver popover;
	
	public WidgetOptions(GUI gui, PopOver popover) {
		super();
		this.gui = gui;
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
			gui.setSingleClickHandler(event -> {
				TextWidget widget = new TextWidget();
				Point2D point = new Point2D(event.getScreenX(), event.getScreenY());
				
				point = gui.getContentRoot().screenToLocal(point);
				
				widget.setLayoutX(point.getX() - widget.prefWidth(30)/2);
				widget.setLayoutY(point.getY() + widget.prefHeight(30)/2);
				gui.getContentRoot().getChildren().add(widget);
				event.consume();
			});
			break;
		case "annotation":
			gui.setSingleClickHandler(event -> {
				Point2D point = new Point2D(event.getScreenX(), event.getScreenY());
				
				if ((event.getPickResult().getIntersectedNode() instanceof Shape3D) &&
						(event.getPickResult().getIntersectedNode().getUserData() != null)) {
					int index = (Integer)event.getPickResult().getIntersectedNode().getUserData();
					DataFrame table = gui.getTable();
					
					TableView tableView = new TableView();
					tableView.setEditable(false);
					tableView.setFocusTraversable(false);
					tableView.setPrefHeight(100);
					tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
					
					TableColumn keyColumn = new TableColumn("Key");
					TableColumn valueColumn = new TableColumn("Value");
					
					tableView.getColumns().addAll(keyColumn, valueColumn);

					for (int j = 0; j < table.attributeCount(); j++) {
						tableView.getItems().add(new Pair<String, Object>(table.getAttribute(j).getName(), table.getInstance(index).get(table.getAttribute(j))));
					}
					
					keyColumn.setCellValueFactory(new PropertyValueFactory<Pair<String, Number>, String>("key"));
					valueColumn.setCellValueFactory(new PropertyValueFactory<Pair<String, Number>, Number>("value"));
					
					Annotation annotation = new Annotation(tableView);
					annotation.setTitle("Details for point " + index);
					annotation.target(event.getPickResult().getIntersectedNode());
					annotation.getTransforms().add(new Translate(0, 0));
					
					gui.getContentRoot().getChildren().add(annotation);
				}
				
				event.consume();
			});
			break;
		case "inspector":
			gui.setSingleClickHandler(event -> {
				Point2D point = new Point2D(event.getScreenX(), event.getScreenY());
				
				point = gui.getContentRoot().screenToLocal(point);
				
				DataInspector inspector = new DataInspector(gui);
				inspector.setLayoutX(point.getX());
				inspector.setLayoutY(point.getY());
				gui.getContentRoot().getChildren().add(inspector);
				event.consume();
			});
			break;
		default:
			break;
		}
		
		popover.hide();
	}
	
}
