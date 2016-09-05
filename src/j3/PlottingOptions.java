package j3;

import java.util.List;

import com.github.lwhite1.tablesaw.api.Table;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

public class PlottingOptions extends Pane {

	public PlottingOptions(Table table) {
		GridPane root = new GridPane();
		
		root.setHgap(5);
		root.setVgap(5);
		root.setPadding(new Insets(5, 5, 5, 5));
		
		ColumnConstraints column1 = new ColumnConstraints();
//		column1.setPercentWidth(10.0);
		column1.setHgrow(Priority.NEVER);
		
		ColumnConstraints column2 = new ColumnConstraints();
//		column2.setPercentWidth(90.0);
		column2.setHgrow(Priority.ALWAYS);
		column2.setFillWidth(true);
		
		root.getColumnConstraints().addAll(column1, column2);
		
		List<String> columns = table.columnNames();
		columns.add(0, "");
		
		Label xAxisLabel = new Label();
		xAxisLabel.setText("X:");
		
		ComboBox<String> xAxis = new ComboBox<>();
		xAxis.getItems().addAll(columns);
		xAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		xAxisLabel.setLabelFor(xAxis);
		
		Label yAxisLabel = new Label();
		yAxisLabel.setText("Y:");
		
		ComboBox<String> yAxis = new ComboBox<>();
		yAxis.getItems().addAll(columns);
		yAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		yAxisLabel.setLabelFor(yAxis);
		
		Label zAxisLabel = new Label();
		zAxisLabel.setText("Z:");
		
		ComboBox<String> zAxis = new ComboBox<>();
		zAxis.getItems().addAll(columns);
		zAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		zAxisLabel.setLabelFor(zAxis);
		
		Label colorAxisLabel = new Label();
		colorAxisLabel.setText("Color:");
		
		ComboBox<String> colorAxis = new ComboBox<>();
		colorAxis.getItems().addAll(columns);
		colorAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		colorAxisLabel.setLabelFor(colorAxis);
		
		Label sizeAxisLabel = new Label();
		sizeAxisLabel.setText("Size:");
		
		ComboBox<String> sizeAxis = new ComboBox<>();
		sizeAxis.getItems().addAll(columns);
		sizeAxis.setMaxWidth(Double.POSITIVE_INFINITY);
		sizeAxisLabel.setLabelFor(sizeAxis);
		
		root.add(xAxisLabel, 0, 0);
		root.add(xAxis, 1, 0);
		root.add(yAxisLabel, 0, 1);
		root.add(yAxis, 1, 1);
		root.add(zAxisLabel, 0, 2);
		root.add(zAxis, 1, 2);
		root.add(colorAxisLabel, 0, 3);
		root.add(colorAxis, 1, 3);
		root.add(sizeAxisLabel, 0, 4);
		root.add(sizeAxis, 1, 4);
		
		TitledPane pane = new TitledPane();
		pane.setText("Axes");
		pane.setContent(root);
		
		getChildren().add(pane);
	}

}
