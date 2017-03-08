package j3.widget.impl.animate;

import java.io.File;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import j3.Canvas;
import j3.widget.TitledWidget;
import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class AnimateWidget extends TitledWidget<AnimateWidget> {

	@Override
	public AnimateWidget getNode() {
		return this;
	}

	@Override
	public void initialize(Canvas canvas) {
		setTitle("Animation Controls");
	}

	@Override
	public void onActivate(Canvas canvas) {
		canvas.setSingleClickHandler(event -> {
			GridPane pane = new GridPane();

			ColumnConstraints column0 = new ColumnConstraints();
			column0.setHgrow(Priority.ALWAYS);

			ColumnConstraints column1 = new ColumnConstraints();

			pane.getColumnConstraints().addAll(column0, column1);

			TextField text = new TextField();
			GridPane.setRowIndex(text, 0);
			GridPane.setColumnIndex(text, 0);

			Button button = new Button();
			button.setText("Browse...");
			button.setOnAction(e -> {
				FileChooser fileChooser = new FileChooser();

				File initialFile = new File("animations/");

				if (initialFile.exists() && initialFile.isDirectory()) {
					fileChooser.setInitialDirectory(new File("animations/"));
				}

				File selectedFile = fileChooser.showOpenDialog(canvas.getScene().getWindow());

				if (selectedFile != null) {
					text.setText(selectedFile.getPath());
				}
			});
			GridPane.setRowIndex(button, 0);
			GridPane.setColumnIndex(button, 1);

			Button play = new Button();
			play.setText("Play");
			play.setOnAction(e -> {

				try {
					Binding bindings = new Binding();
					bindings.setVariable("canvas", canvas);

					GroovyShell shell = new GroovyShell(bindings);
					shell.evaluate(new File(text.getText()).toURI());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});
			GridPane.setRowIndex(play, 0);
			GridPane.setColumnIndex(play, 2);

			pane.getChildren().addAll(text, button, play);
			setContent(pane);

			// position this widget at the mouse location
			Point2D point = new Point2D(event.getScreenX(), event.getScreenY());

			point = canvas.screenToLocal(point);

			setLayoutX(point.getX() - prefWidth(30)/2);
			setLayoutY(point.getY() - prefHeight(30)/2);

			// add this widget to the canvas
			canvas.add(this);

			event.consume();
		});
	}

}
