package j3.widget.impl.scatter;

import org.controlsfx.control.ToggleSwitch;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class RotationOptions extends Pane {

	public RotationOptions(ScatterPlot plot) {
		super();

		GridPane root = new GridPane();

		root.setHgap(5);
		root.setVgap(5);
		root.setPadding(new Insets(5, 5, 5, 5));

		ColumnConstraints column1 = new ColumnConstraints();
		column1.setHgrow(Priority.NEVER);

		ColumnConstraints column2 = new ColumnConstraints();
		column2.setHgrow(Priority.ALWAYS);
		column2.setFillWidth(true);

		root.getColumnConstraints().addAll(column1, column2);

		Label labelSpeed = new Label();
		labelSpeed.setText("Speed:");

		Slider slider = new Slider();
		slider.setMin(0.0);
		slider.setMax(10.0);
		slider.setValue(plot.getRotationSpeed());

		Label labelAxis = new Label();
		labelAxis.setText("Axis:");

		ToggleSwitch axisSwitch = new ToggleSwitch();
		axisSwitch.setSelected(plot.getRotationAxis());

		plot.rotationSpeedProperty().bind(slider.valueProperty());
		plot.rotationAxisProperty().bind(axisSwitch.selectedProperty());

		root.add(labelSpeed, 0, 0);
		root.add(slider, 1, 0);
		root.add(labelAxis, 0, 1);
		root.add(axisSwitch, 1, 1);

		TitledPane pane = new TitledPane();
		pane.setText("Rotation Options");
		pane.setContent(root);

		getChildren().add(pane);
	}

}
