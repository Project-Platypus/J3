package j3.widget.impl;

import j3.Canvas;
import j3.widget.SerializableWidget;
import j3.widget.Widget;

import org.controlsfx.control.SegmentedButton;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.converter.IntegerStringConverter;

public class TextWidget extends Pane implements Widget<TextWidget>, SerializableWidget {
	
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

	@Override
	public TextWidget getNode() {
		return this;
	}

	@Override
	public void onActivate(Canvas canvas) {
		canvas.setSingleClickHandler(event -> {
			Point2D point = new Point2D(event.getScreenX(), event.getScreenY());
			
			point = canvas.screenToLocal(point);
			
			setLayoutX(point.getX() - prefWidth(30)/2);
			setLayoutY(point.getY() - prefHeight(30)/2);
			canvas.add(this);
			
			event.consume();
		});
	}
	
	@Override
	public void onAdd(Canvas canvas) {
		text.setOnMouseClicked(event -> {
			if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() % 2 == 0) {
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
				
				// another weird thing...the font subproperties (size, weight, ...)
				// all have null values
				fontFamily.setValue(text.getFont().getFamily());
				fontSize.setValue((int)text.getFont().getSize());
				bold.setSelected(text.getFont().getStyle().contains("Bold"));
				italic.setSelected(text.getFont().getStyle().contains("Italic"));
				
				// special call to determine if text is underlined
				underline.setSelected(isUnderline());
				
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
	
	@SuppressWarnings("unchecked")
	private boolean isUnderline() {
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
				
				return value;
			}
		}
		
		return false;
	}

	@Override
	public Element saveState(Canvas canvas) {
		Bounds bounds = text.getBoundsInLocal();
		bounds = text.getLocalToSceneTransform().transform(bounds);
		
		Element element = DocumentHelper.createElement("textWidget");
		element.addElement("text").setText(text.getText());
		element.addElement("posX").setText(Double.toString(bounds.getMinX()));
		element.addElement("posY").setText(Double.toString(bounds.getMaxY()));
		element.addElement("fontFamily").setText(text.getFont().getFamily());
		element.addElement("fontSize").setText(Integer.toString((int)text.getFont().getSize()));
		element.addElement("isBold").setText(Boolean.toString(text.getFont().getStyle().contains("Bold")));
		element.addElement("isItalic").setText(Boolean.toString(text.getFont().getStyle().contains("Italic")));
		element.addElement("isUnderline").setText(Boolean.toString(isUnderline()));
		return element;
	}

	@Override
	public void restoreState(Element element, Canvas canvas) {
		// set position
		text.setLayoutX(Double.parseDouble(element.elementText("posX")));
		text.setLayoutY(Double.parseDouble(element.elementText("posY")));

		// set style using CSS
		StringBuilder style = new StringBuilder();
		style.append("-fx-font-size: ");
		style.append(Integer.parseInt(element.elementText("fontSize")));
		style.append(";");
		style.append("-fx-font-family: '");
		style.append(element.elementText("fontFamily"));
		style.append("';");
		
		if (Boolean.parseBoolean(element.elementText("isBold"))) {
			style.append("-fx-font-weight: bold;");
		}
		
		if (Boolean.parseBoolean(element.elementText("isItalic"))) {
			style.append("-fx-font-style: italic;");
		}
		
		if (Boolean.parseBoolean(element.elementText("isUnderline"))) {
			style.append("-fx-underline: true;");
		}
		
		text.setStyle(style.toString());
		text.setText(element.elementText("text"));
	}

}
