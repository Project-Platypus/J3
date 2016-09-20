package j3.widget.impl;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import j3.Canvas;
import j3.dataframe.Attribute;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.widget.TitledWidget;

public class DataTableWidget extends TitledWidget<DataTableWidget> {
	
	private TableView<Instance> table;
	
	private DataFrame data;
	
	private ObjectProperty<Instance> selectedInstance = new ObjectPropertyBase<Instance>() {
		
		@Override
		protected void invalidated() {
			table.getSelectionModel().select(selectedInstance.get());

			// This uses some internal API and may break in future releases,
			// but appears to be the only way to ensure a given row is visible.
			// TableView does have a scrollTo method, but it scrolls even if the
			// row was already visible.
			int index = data.getInstances().indexOf(selectedInstance.get());
			TableViewSkin<?> skin = (TableViewSkin<?>)table.getSkin();
			VirtualFlow<?> flow = (VirtualFlow<?>)skin.getChildren().get(1); 
			
		    int first = flow.getFirstVisibleCell().getIndex();
		    int last = flow.getLastVisibleCell().getIndex();
		    if (index <= first){
		        while (index <= first && flow.adjustPixels(-1) < 0){
		            first = flow.getFirstVisibleCell().getIndex();
		        }
		    } else {
		        while (index >= last && flow.adjustPixels(1) > 0){
		            last = flow.getLastVisibleCell().getIndex();
		        }
		    }
		}

		@Override
		public Object getBean() {
			return DataTableWidget.this;
		}

		@Override
		public String getName() {
			return "selectedInstance";
		}

	};

	public void setSelectedInstance(Instance instance) {
		selectedInstance.set(instance);
	}

	public Instance getSelectedInstance() {
		return selectedInstance.get();
	}

	public ObjectProperty<Instance> selectedInstanceProperty() {
		return selectedInstance;
	}
	
	public DataTableWidget() {
		super();
	}

	@Override
	public DataTableWidget getNode() {
		return this;
	}
	
	@Override
	public void onActivate(Canvas canvas) {
		super.onActivate(canvas);

		canvas.setBoxSelectionHandler(event -> {
			setLayoutX(canvas.getSelectionBox().getX());
			setLayoutY(canvas.getSelectionBox().getY());
			
			pane.setPrefWidth(Math.max(300, canvas.getSelectionBox().getWidth()));
			pane.setPrefHeight(Math.max(200, canvas.getSelectionBox().getHeight()));

			canvas.add(this);

			event.consume();
		});
	}

	@Override
	public void initialize(Canvas canvas) {
		data = (DataFrame)canvas.getPropertyRegistry().get("data").getValue();
		table = new TableView<>();
		
		for (Attribute<?> attribute : data.getAttributes()) {
			TableColumn<Instance, Object> column = new TableColumn<>(attribute.getName());
			
			column.setCellValueFactory(p -> {
				return new SimpleObjectProperty<Object>(p.getValue().get(attribute));
			});
			
			table.getColumns().add(column);
		}
		
		table.getItems().addAll(data.getInstances());
		
		table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			setSelectedInstance(newValue);
		});
		
		setContent(table);
		setTitle("Data Table Viewer");
	}

	@Override
	public void onAdd(Canvas canvas) {
		selectedInstance.bindBidirectional(canvas.getPropertyRegistry().get("selectedInstance"));
	}

	@Override
	public void onRemove(Canvas canvas) {
		selectedInstance.unbind();
	}

}
