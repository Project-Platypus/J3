package j3.widgets;

import org.controlsfx.control.SegmentedButton;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

public class TextWidget extends Pane {
	
	private Text text;
	
	private TextField editor;
	
	private ToggleButton bold;
	
	private ToggleButton italic;
	
	private ToggleButton underline;
	
	private ComboBox<Integer> fontSize;
	
	private ComboBox<String> fontFamily;
	
	private BorderPane pane;
	
	private double mousePosX, mousePosY;
	private double mouseOldX, mouseOldY;
	
	public TextWidget() {
		super();

		text = new Text("Text Widget");

		text.setOnMouseClicked(event -> {
			if (event.getClickCount() % 2 == 0) {
				double textWidth = text.prefWidth(30);
				double textHeight = text.prefHeight(textWidth);
				
				fontFamily = new ComboBox<String>();
				fontFamily.getItems().addAll(Font.getFamilies());
				
				fontSize = new ComboBox<Integer>();
				fontSize.getItems().addAll(8, 10, 12, 14, 16, 18, 24, 36, 48);
				fontSize.setEditable(true);
				fontSize.setConverter(new IntegerStringConverter());
				fontSize.setPrefWidth(60);
				
				bold = new ToggleButton();
				bold.setGraphic(new ImageView(new Image(TextWidget.class.getResourceAsStream("/j3/icons/bold_1x_nopadding.png"))));
				italic = new ToggleButton();
				italic.setGraphic(new ImageView(new Image(TextWidget.class.getResourceAsStream("/j3/icons/italic_1x_nopadding.png"))));
				underline = new ToggleButton();
				underline.setGraphic(new ImageView(new Image(TextWidget.class.getResourceAsStream("/j3/icons/underline_1x_nopadding.png"))));
				
				SegmentedButton group = new SegmentedButton(bold, italic, underline);
				group.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
				group.setToggleGroup(null);
				
				// interestingly, the Font class has no methods to access certain font
				// properties; thus, we scan through the CSS properties
				for (CssMetaData<? extends Styleable, ?> data : text.getCssMetaData()) {
					if (data.getProperty().equals("-fx-underline")) {
						CssMetaData<Text, Boolean> metadata = (CssMetaData<Text, Boolean>)data;
						StyleableProperty<Boolean> property = metadata.getStyleableProperty(text);
						Boolean value = null;
						
						if (property == null) {
							value = metadata.getInitialValue(text);
						} else {
							value = property.getValue();
						}
						
						underline.setSelected(value);
					}
				}
				
				// another weird thing...the font subproperties (size, weight, ...)
				// all have null values
				fontFamily.setValue(text.getFont().getFamily());
				fontSize.setValue((int)text.getFont().getSize());
				bold.setSelected(text.getFont().getStyle().contains("Bold"));
				italic.setSelected(text.getFont().getStyle().contains("Italic"));
				
				pane = new BorderPane();
				
				HBox toolbar = new HBox(4);
				toolbar.getChildren().addAll(fontFamily, fontSize, group);
				
				editor = new TextField();
				editor.setText(text.getText());
				
				Button okButton = new Button();
				okButton.setGraphic(new ImageView(new Image(TextWidget.class.getResourceAsStream("/j3/icons/ok.png"))));
				okButton.setStyle("-fx-background-color: transparent; -fx-padding: 0 0 0 0;");
				Button cancelButton = new Button();
				cancelButton.setGraphic(new ImageView(new Image(TextWidget.class.getResourceAsStream("/j3/icons/cancel.png"))));
				cancelButton.setStyle("-fx-background-color: transparent; -fx-padding: 0 0 0 0;");
				
				HBox content = new HBox(4);
				content.setPadding(new Insets(4, 0, 0, 0));
				HBox.setHgrow(editor, Priority.ALWAYS);
				content.getChildren().addAll(editor, okButton, cancelButton);
				
				pane.setTop(toolbar);
				pane.setCenter(content);
				
				pane.layoutBoundsProperty().addListener(l -> {
					if (getScene() != null) {
						Bounds bounds = pane.localToScene(pane.getBoundsInLocal());
						
						if (bounds.getMaxX() > getScene().getWidth()) {
							pane.setLayoutX(pane.getLayoutX() - (bounds.getMaxX() - getScene().getWidth()));
						}
						
						if (bounds.getMaxY() > getScene().getHeight()) {
							pane.setLayoutY(pane.getLayoutY() - (bounds.getMaxY() - getScene().getHeight()));
						}
					}
				});
				
				editor.setOnAction(e -> {
					apply();
				});
				
				okButton.setOnAction(e -> {
					apply();
				});
				
				cancelButton.setOnAction(e -> {
					getChildren().removeAll(pane);
					getChildren().add(text);
					pane = null;
				});
				
				getChildren().remove(text);
				getChildren().addAll(pane);
				
				editor.requestFocus();
				editor.selectAll();
			}
		});
		
		text.setOnMousePressed(me -> {
			mouseOldX = me.getScreenX();
			mouseOldY = me.getScreenY();
		});

		text.setOnMouseDragged(me -> {
			mousePosX = me.getScreenX();
			mousePosY = me.getScreenY();
			setLayoutX(getLayoutX() + (mousePosX - mouseOldX));
			setLayoutY(getLayoutY() + (mousePosY - mouseOldY));
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
		});
		
		text.setLayoutY(text.prefHeight(30));
		getChildren().add(text);
	}
	
	protected void apply() {
		StringBuilder style = new StringBuilder();
		style.append("-fx-font-size: ");
		style.append(fontSize.getValue().intValue());
		style.append(";");
		style.append("-fx-font-family: '");
		style.append(fontFamily.getValue());
		style.append("';");
		
		if (bold.isSelected()) {
			style.append("-fx-font-weight: bold;");
		}
		
		if (italic.isSelected()) {
			style.append("-fx-font-style: italic;");
		}
		
		if (underline.isSelected()) {
			style.append("-fx-underline: true;");
		}
		
		text.setStyle(style.toString());
		text.setText(editor.getText());
		
		getChildren().removeAll(pane);
		getChildren().add(text);
		pane = null;
	}

}
