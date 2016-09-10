package j3;

import org.controlsfx.control.PopOver;

import j3.widget.WidgetFactory;
import j3.widget.WidgetProvider;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

public class WidgetOptions extends Pane {

	private final Canvas canvas;
	
	private final PopOver popover;
	
	public WidgetOptions(Canvas canvas, PopOver popover) {
		super();
		this.canvas = canvas;
		this.popover = popover;
		
		VBox root = new VBox();
		
		for (String category : WidgetFactory.getInstance().getCategories()) {
			TilePane tiles = new TilePane();
			tiles.setHgap(5);
			tiles.setVgap(5);
			
			for (WidgetProvider provider : WidgetFactory.getInstance().getProviders(category)) {
				Button button = new Button();
				button.setGraphic(new ImageView(provider.getIcon()));
				button.setOnAction(event -> createWidget(provider));
				button.setTooltip(new Tooltip(provider.getDescription()));
				button.setDisable(!provider.isEnabled(canvas));
				
				tiles.getChildren().add(button);
			}
			
			TitledPane pane = new TitledPane();
			pane.setText(category);
			pane.setContent(tiles);
			
			root.getChildren().add(pane);
		}
		
//		Button textWidget = new Button();
//		textWidget.setGraphic(new ImageView(new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/text_1x.png"))));
//		textWidget.setOnAction(event -> handleWidget("text"));
//		textWidget.setTooltip(new Tooltip("Display text anywhere in canvas"));
//		
//		Button annotationWidget = new Button();
//		annotationWidget.setGraphic(new ImageView(new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/comment_1x.png"))));
//		annotationWidget.setOnAction(event -> handleWidget("annotation"));
//		annotationWidget.setTooltip(new Tooltip("Display details about a selected data point"));
//		
//		Button inspectorWidget = new Button();
//		inspectorWidget.setGraphic(new ImageView(new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/inspect_1x.png"))));
//		inspectorWidget.setOnAction(event -> handleWidget("inspector"));
//		inspectorWidget.setTooltip(new Tooltip("Inspects points hovered by the mouse"));
//		
//		Button brushingWidget = new Button();
//		brushingWidget.setGraphic(new ImageView(new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/brush_1x.png"))));
//		brushingWidget.setOnAction(event -> handleWidget("brushing"));
//		brushingWidget.setTooltip(new Tooltip("Enable brushing points by their range"));
//		
//		Button animateWidget = new Button();
//		animateWidget.setGraphic(new ImageView(new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/animate_1x.png"))));
//		animateWidget.setOnAction(event -> handleWidget("animate"));
//		animateWidget.setTooltip(new Tooltip("Animations"));
//		
//		
//		

		
		getChildren().add(root);
	}
	
	public void createWidget(WidgetProvider provider) {
		provider.createInstance().onActivate(canvas);
		popover.hide();
	}
	
}
