package j3.widget.impl;

import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape3D;
import javafx.scene.transform.Transform;
import j3.Canvas;
import j3.Selector;
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

			if (selectedInstance.get() == null) {
				target(null);
			} else {
				// This uses some internal API and may break in future releases,
				// but appears to be the only way to ensure a given row is visible.
				// TableView does have a scrollTo method, but it scrolls even if the
				// row was already visible.
				int index = data.getInstances().indexOf(selectedInstance.get());
				TableViewSkin<?> skin = (TableViewSkin<?>)table.getSkin();
				
				if (skin != null) {
					VirtualFlow<?> flow = (VirtualFlow<?>)skin.getChildren().get(1); 
					
				    int first = flow.getFirstVisibleCell().getIndex();
				    int last = flow.getLastVisibleCell().getIndex();
				    if (index <= first) {
				        while (index <= first && flow.adjustPixels(-1) < 0){
				            first = flow.getFirstVisibleCell().getIndex();
				        }
				    } else {
				        while (index >= last && flow.adjustPixels(1) > 0){
				            last = flow.getLastVisibleCell().getIndex();
				        }
				    }
				    
				    if (getScene() != null) {
					    for (Shape3D shape : Selector.on(getScene()).get(Shape3D.class)) {
					    	if ((shape.getUserData() instanceof Instance) && (shape.getUserData().equals(selectedInstance.get()))) {
					    		target(shape);
					    		break;
					    	}
					    }
				    }
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
	
	private Node target;

	private Line arrow;

	private ChangeListener<? super Transform> changeListener;
	
	public DataTableWidget() {
		super();
	}

	@Override
	public DataTableWidget getNode() {
		return this;
	}
	
	public void target(Node node) {
		if (arrow != null) {
			getChildren().remove(arrow);
			target.localToSceneTransformProperty().removeListener(changeListener);
			pane.localToSceneTransformProperty().removeListener(changeListener);
		}
		
		target = node;
		
		arrow = new Line(0, 0, 0, 0);
		arrow.getStyleClass().add("j3-annotation-arrow");
		getChildren().add(arrow);
		arrow.toBack();
		
		changeListener = (observable, oldValue, newValue) -> {
			Bounds startBounds = pane.localToScreen(pane.getBoundsInLocal());
			
			if (startBounds != null) {
				Point2D startPoint = new Point2D((startBounds.getMinX()+startBounds.getMaxX())/2.0, (startBounds.getMinY()+startBounds.getMaxY())/2.0);
				
				startPoint = screenToLocal(startPoint);
				
				if (startPoint != null) {
					arrow.setStartX(startPoint.getX());
					arrow.setStartY(startPoint.getY());
				}
			}
			
			Bounds endBounds = node.localToScreen(node.getBoundsInLocal());
			
			if (endBounds != null) {
				Point2D endPoint = new Point2D((endBounds.getMinX()+endBounds.getMaxX())/2.0, (endBounds.getMinY()+endBounds.getMaxY())/2.0);
				
				endPoint = screenToLocal(endPoint);
				
				if (endPoint != null) {
					arrow.setEndX(endPoint.getX());
					arrow.setEndY(endPoint.getY());
				}
			}
		};
		
		pane.layoutBoundsProperty().addListener((observable, oldValue, newValue) -> {
			changeListener.changed(null, null, null);
		});
		
		node.localToSceneTransformProperty().addListener(changeListener);
		pane.localToSceneTransformProperty().addListener(changeListener);
		
		changeListener.changed(null, null, null);
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
		super.onAdd(canvas);
		
		selectedInstance.bindBidirectional(canvas.getPropertyRegistry().get("selectedInstance"));
	}

	@Override
	public void onRemove(Canvas canvas) {
		selectedInstance.unbind();
	}

}
