package j3;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Annotation extends Application {
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
//		Group root = new Group();
//		
//		Line line = new Line(200, 100, 300, 300);
//		line.setStrokeWidth(2.0);
//		root.getChildren().add(line);
//		
//		Rectangle r = new Rectangle(400, 200);
//		r.setFill(Color.WHITE);
//		r.setStroke(Color.BLACK);
//		r.setStrokeWidth(2.0);
//		root.getChildren().add(r);
//		
//		TextField comment = new TextField();
//		comment.setTranslateX(6);
//		comment.setTranslateY(6);
//		comment.setPrefWidth(400-12);
//		root.getChildren().add(comment);
		
		
		 int rowCount = 15;
	     int columnCount = 10;
	     GridBase grid = new GridBase(rowCount, columnCount);
	     
	     ObservableList<ObservableList<SpreadsheetCell>> rows = FXCollections.observableArrayList();
	     for (int row = 0; row < grid.getRowCount(); ++row) {
	         final ObservableList<SpreadsheetCell> list = FXCollections.observableArrayList();
	         for (int column = 0; column < grid.getColumnCount(); ++column) {
	             list.add(SpreadsheetCellType.STRING.createCell(row, column, 1, 1,"value"));
	         }
	         rows.add(list);
	     }
	     grid.setRows(rows);
	     grid.getColumnHeaders().addAll("Hello", "World");

	     SpreadsheetView spv = new SpreadsheetView(grid);
		
		
		
		Scene scene = new Scene(spv, 400, 400);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

}
