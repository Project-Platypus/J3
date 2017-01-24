package j3.widget.impl.scatter2d;

import j3.Axis;
import j3.Canvas;
import j3.colormap.Colormap;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.widget.TitledWidget;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;

public class ScatterPlot2D extends TitledWidget<ScatterPlot2D> {

	private Axis2D plot;
	
	private ScatterPoints2D scatter;
	
	public ScatterPlot2D() {
		super();
	}

	@Override
	public ScatterPlot2D getNode() {
		return this;
	}

	@Override
	public void initialize(Canvas canvas) {
		Pane container = new Pane();
		container.setPadding(new Insets(5, 5, 5, 5));
		
		// this is needed to prevent the height growing (need to fix the root cause)
		container.setPrefHeight(300);
		
		DataFrame table = (DataFrame)canvas.getPropertyRegistry().get("data").getValue();
		
		plot = new Axis2D();
		scatter = new ScatterPoints2D(plot, table);
		
		setContent(plot);
		setTitle("2D Scatter Plot");
		
		ObjectProperty<Axis> xAxis = canvas.getPropertyRegistry().get("xAxis");
		ObjectProperty<Axis> yAxis = canvas.getPropertyRegistry().get("yAxis");
		ObjectProperty<Axis> colorAxis = canvas.getPropertyRegistry().get("colorAxis");
		ObjectProperty<Axis> sizeAxis = canvas.getPropertyRegistry().get("sizeAxis");
		ObjectProperty<Axis> visibilityAxis = canvas.getPropertyRegistry().get("visibilityAxis");
		ObjectProperty<Colormap> colormap = canvas.getPropertyRegistry().get("colormap");
		ObjectProperty<Instance> selectedInstance = canvas.getPropertyRegistry().get("selectedInstance");
		
		scatter.xAxisProperty().bind(xAxis);
		scatter.yAxisProperty().bind(yAxis);
		scatter.colorAxisProperty().bind(colorAxis);
		scatter.sizeAxisProperty().bind(sizeAxis);
		scatter.visibilityAxisProperty().bind(visibilityAxis);
		scatter.colormapProperty().bind(colormap);
		scatter.selectedInstanceProperty().bindBidirectional(selectedInstance);
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
	public void onAdd(Canvas canvas) {
		
	}

	@Override
	public void onRemove(Canvas canvas) {
		scatter.xAxisProperty().unbind();
		scatter.yAxisProperty().unbind();
		scatter.colorAxisProperty().unbind();
		scatter.sizeAxisProperty().unbind();
		scatter.visibilityAxisProperty().unbind();
		scatter.colormapProperty().unbind();
		scatter.selectedInstanceProperty().unbind();
	}
	
	public void update() {
		scatter.update();
	}

}
