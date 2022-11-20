package j3.widget.impl.animate;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import j3.Canvas;
import j3.widget.SerializableWidget;
import j3.widget.TitledWidget;

import java.io.File;
import java.nio.file.Files;

import javafx.geometry.Point2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class AnimateWidget extends TitledWidget<AnimateWidget> implements SerializableWidget {

	private String name;

	private String script;

	private Label label;

	@Override
	public AnimateWidget getNode() {
		return this;
	}

	@Override
	public void initialize(Canvas canvas) {
		setTitle("Animation Controls");

		GridPane pane = new GridPane();

		ColumnConstraints column0 = new ColumnConstraints();
		column0.setHgrow(Priority.ALWAYS);

		ColumnConstraints column1 = new ColumnConstraints();

		pane.getColumnConstraints().addAll(column0, column1);

		label = new Label();
		GridPane.setRowIndex(label, 0);
		GridPane.setColumnIndex(label, 0);

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
				try {
					script = new String(Files.readAllBytes(selectedFile.toPath()));
					name = selectedFile.getName();
					label.setText(name);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		GridPane.setRowIndex(button, 0);
		GridPane.setColumnIndex(button, 1);

		Button play = new Button();
		play.setText("Play");
		play.setOnAction(e -> {
			if (script != null) {
				try {
					Binding bindings = new Binding();
					bindings.setVariable("canvas", canvas);

					GroovyShell shell = new GroovyShell(bindings);
					shell.evaluate(script);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		GridPane.setRowIndex(play, 0);
		GridPane.setColumnIndex(play, 2);

		pane.getChildren().addAll(label, button, play);
		setContent(pane);
	}

	@Override
	public void onActivate(Canvas canvas) {
		canvas.setSingleClickHandler(event -> {
			// position this widget at the mouse location
			Point2D point = new Point2D(event.getScreenX(), event.getScreenY());

			point = canvas.screenToLocal(point);

			setLayoutX(point.getX() - prefWidth(30) / 2);
			setLayoutY(point.getY() - prefHeight(30) / 2);

			// add this widget to the canvas
			canvas.add(this);

			event.consume();
		});
	}

	@Override
	public Element saveState(Canvas canvas) {
		Element element = DocumentHelper.createElement("animate");

		// save the pane size
		saveStateInternal(element);

		// save the script name
		Element nameElement = element.addElement("name");
		nameElement.setText(name);

		// save the contents of the script file
		Element scriptElement = element.addElement("script");
		scriptElement.add(DocumentHelper.createCDATA(script));

		return element;
	}

	@Override
	public void restoreState(Element element, Canvas canvas) {
		// restore the pane size
		restoreStateInternal(element);

		// restore the animation pane settings
		name = element.elementText("name");
		script = element.elementText("script");
		label.setText(name);
	}

}
