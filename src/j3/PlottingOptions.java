package j3;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;

public class PlottingOptions extends Pane {
	
	private final List<Axis> options;

	public PlottingOptions(Plot3D plot, List<Axis> options) {
		super();
		this.options = new ArrayList<Axis>(options);
		
		EmptyAxis empty = new EmptyAxis();
		this.options.add(0, empty);
		
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

		for (ObjectProperty<Axis> axisProperty : plot.getAxisProperties()) {
			Label label = new Label(StringUtils.capitalize(axisProperty.getName()) + ":");

			ComboBox<Axis> combobox = new ComboBox<>();
			combobox.getItems().addAll(this.options);
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
			
//			combobox.getSelectionModel().select(axisProperty.get());
//			axisProperty.bind(combobox.getSelectionModel().selectedItemProperty());

			root.add(label, 0, count);
			root.add(combobox, 1, count);
			count++;
		}



		//		Label xAxisLabel = new Label();
		//		xAxisLabel.setText("X:");
		//		
		//		ComboBox<Axis> xAxis = new ComboBox<>();
		//		xAxis.getItems().addAll(options);
		//		xAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		//		xAxisLabel.setLabelFor(xAxis);
		//		xAxis.getSelectionModel().select(plot.getXAxis());
		//		plot.xAxisProperty().bind(xAxis.getSelectionModel().selectedItemProperty());
		//		
		//		Label yAxisLabel = new Label();
		//		yAxisLabel.setText("Y:");
		//		
		//		ComboBox<Axis> yAxis = new ComboBox<>();
		//		yAxis.getItems().addAll(options);
		//		yAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		//		yAxisLabel.setLabelFor(yAxis);
		//		yAxis.getSelectionModel().select(plot.getYAxis());
		//		plot.yAxisProperty().bind(yAxis.getSelectionModel().selectedItemProperty());
		//		
		//		Label zAxisLabel = new Label();
		//		zAxisLabel.setText("Z:");
		//		
		//		ComboBox<Axis> zAxis = new ComboBox<>();
		//		zAxis.getItems().addAll(options);
		//		zAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		//		zAxisLabel.setLabelFor(zAxis);
		//		zAxis.getSelectionModel().select(plot.getZAxis());
		//		plot.zAxisProperty().bind(zAxis.getSelectionModel().selectedItemProperty());
		//		
		//		Label colorAxisLabel = new Label();
		//		colorAxisLabel.setText("Color:");
		//		
		//		ComboBox<Axis> colorAxis = new ComboBox<>();
		//		colorAxis.getItems().addAll(options);
		//		colorAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		//		colorAxisLabel.setLabelFor(colorAxis);
		//		colorAxis.getSelectionModel().select(plot.getColorAxis());
		//		plot.colorAxisProperty().bind(colorAxis.getSelectionModel().selectedItemProperty());
		//	
		//		Label sizeAxisLabel = new Label();
		//		sizeAxisLabel.setText("Size:");
		//		
		//		ComboBox<Axis> sizeAxis = new ComboBox<>();
		//		sizeAxis.getItems().addAll(options);
		//		sizeAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		//		sizeAxisLabel.setLabelFor(sizeAxis);
		//		sizeAxis.getSelectionModel().select(plot.getSizeAxis());
		//		plot.sizeAxisProperty().bind(sizeAxis.getSelectionModel().selectedItemProperty());
		//		
		//		root.add(xAxisLabel, 0, 0);
		//		root.add(xAxis, 1, 0);
		//		root.add(yAxisLabel, 0, 1);
		//		root.add(yAxis, 1, 1);
		//		root.add(zAxisLabel, 0, 2);
		//		root.add(zAxis, 1, 2);
		//		root.add(colorAxisLabel, 0, 3);
		//		root.add(colorAxis, 1, 3);
		//		root.add(sizeAxisLabel, 0, 4);
		//		root.add(sizeAxis, 1, 4);

		TitledPane pane = new TitledPane();
		pane.setText("Axes");
		pane.setContent(root);

		getChildren().add(pane);
	}

}
