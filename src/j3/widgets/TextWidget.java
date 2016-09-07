package j3.widgets;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TextWidget extends Region {
	
	private Text text;
	
	private TextField editor;
	
	private ComboBox<Integer> fontSize;
	
	private double mousePosX, mousePosY;
	private double mouseOldX, mouseOldY;
	
	public TextWidget() {
		super();
		
		text = new Text("Text Widget");

		text.setOnMouseClicked(event -> {
			if (event.getClickCount() % 2 == 0) {
				double textWidth = text.prefWidth(30);
				double textHeight = text.prefHeight(textWidth);
				
				fontSize = new ComboBox<Integer>();
				fontSize.getItems().addAll(8, 10, 12, 14, 16, 18, 24);
				fontSize.setOnAction(e -> {
					text.setFont(Font.font(Font.getDefault().getFamily(), fontSize.getValue().doubleValue()));
				});
				
				FlowPane toolbar = new FlowPane();
				toolbar.getChildren().addAll(fontSize);
				
				editor = new TextField();
				editor.setText(text.getText());
				
				editor.setLayoutX(text.getLayoutX() + text.getTranslateX());
				editor.setLayoutY(text.getLayoutY() + text.getTranslateY() - textHeight);
				
				editor.setOnAction(e -> {
					text.setText(editor.getText());
					getChildren().removeAll(editor, toolbar);
					getChildren().add(text);
					editor = null;
					fontSize = null;
				});
				
				getChildren().remove(text);
				getChildren().addAll(editor, toolbar);
				
				editor.heightProperty().addListener(l -> {
					toolbar.setLayoutX(editor.getLayoutX());
					toolbar.setLayoutY(editor.getLayoutY() - editor.getHeight());
				});
				
				editor.requestFocus();
				editor.selectAll();
			}
		});
		
		text.setOnMousePressed(me -> {
			mouseOldX = me.getSceneX();
			mouseOldY = me.getSceneY();
		});

		text.setOnMouseDragged(me -> {
			mousePosX = me.getSceneX();
			mousePosY = me.getSceneY();
			text.setTranslateX(text.getTranslateX() + (mousePosX - mouseOldX));
			text.setTranslateY(text.getTranslateY() + (mousePosY - mouseOldY));
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
		});
		
		getChildren().add(text);
	}

}
