package j3.widgets;

import j3.GUI;
import j3.dataframe.Attribute;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Shape3D;
import javafx.util.Callback;

public class DataInspector extends Pane {
	
	private int currentIndex = -1;
	
	public DataInspector(GUI gui) {
		super();
		
		TableView<Integer> table = new TableView<>();
		
		for (int i = 0; i < gui.getTable().attributeCount(); i++) {
			Attribute<?> attribute = gui.getTable().getAttribute(i);
			TableColumn<Integer, Object> column = new TableColumn<>(attribute.getName());
			
			column.setCellValueFactory(new Callback<CellDataFeatures<Integer, Object>, ObservableValue<Object>>() {
				public ObservableValue<Object> call(CellDataFeatures<Integer, Object> p) {
					return new ReadOnlyObjectWrapper<Object>(gui.getTable().getInstance(p.getValue()).get(attribute));
				}
			});

			table.getColumns().add(column);
		}
		
		table.setPrefHeight(200);
		getChildren().add(table);
		
		gui.getContentRoot().addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
			if ((event.getPickResult().getIntersectedNode() instanceof Shape3D) &&
				(event.getPickResult().getIntersectedNode().getUserData() != null)) {
				if (currentIndex >= 0) {
					table.getItems().remove(currentIndex);
				}
				
				table.getItems().add((Integer)event.getPickResult().getIntersectedNode().getUserData());
				currentIndex = table.getItems().size()-1;
			}
		});
		
		gui.getContentRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
			if ((event.getPickResult().getIntersectedNode() instanceof Shape3D) &&
					(event.getPickResult().getIntersectedNode().getUserData() != null)) {
					if (currentIndex >= 0) {
						table.getItems().remove(currentIndex);
					}
					
					table.getItems().add((Integer)event.getPickResult().getIntersectedNode().getUserData());
					
					currentIndex = -1;
				}
		});
	}

}
