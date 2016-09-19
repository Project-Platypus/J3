package j3.widget.impl.scatter;

import j3.Axis;
import j3.Canvas;
import j3.EmptyAxis;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class PlottingOptions extends Pane {
	
	@SuppressWarnings("unchecked")
	public PlottingOptions(Canvas canvas) {
		super();
		
		List<Axis> options = new ArrayList<Axis>((List<Axis>)canvas.getPropertyRegistry().get("axes").getValue());
		
		EmptyAxis empty = new EmptyAxis();
		options.add(0, empty);
		
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

		// use reflection to identify all axis properties
		int count = 0;
		
		List<ObjectProperty<Axis>> axisProperties = Arrays.asList(
				canvas.getPropertyRegistry().get("xAxis"),
				canvas.getPropertyRegistry().get("yAxis"),
				canvas.getPropertyRegistry().get("zAxis"),
				canvas.getPropertyRegistry().get("colorAxis"),
				canvas.getPropertyRegistry().get("sizeAxis"));

		for (ObjectProperty<Axis> axisProperty : axisProperties) {
			Label label = new Label(StringUtils.capitalize(axisProperty.getName()) + ":");

			ComboBox<Axis> combobox = new ComboBox<>();
			combobox.getItems().addAll(options);
			combobox.setMaxWidth(Double.POSITIVE_INFINITY);
			label.setLabelFor(combobox);
			
			Axis axis = axisProperty.get();
			
			if (axis == null) {
				combobox.getSelectionModel().select(empty);
			} else {
				combobox.getSelectionModel().select(axis);
			}
			
			combobox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
				if (newValue == empty) {
					axisProperty.set(null);
				} else {
					axisProperty.set(newValue);
				}
			});

			root.add(label, 0, count);
			root.add(combobox, 1, count);
			
			count++;
		}
		
		root.add(new Separator(), 0, count++, 2, 1);
		
		ComboBox<String> themeSelection = new ComboBox<>();
		themeSelection.getItems().addAll("Light", "Dark");
		themeSelection.getSelectionModel().select((String)canvas.getPropertyRegistry().get("theme").getValue());
		themeSelection.setOnAction(event -> {
			canvas.getPropertyRegistry().get("theme").set(themeSelection.getSelectionModel().getSelectedItem());
		});
		
		root.add(new Label("Theme:"), 0, count);
		root.add(themeSelection, 1, count++);

		TitledPane pane = new TitledPane();
		pane.setText("Axes");
		pane.setContent(root);

		getChildren().add(pane);
	}

}
