package j3.widget.impl.scatter2d;

import j3.Axis;
import j3.Dimension;

import java.util.Arrays;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Axis2D extends Pane {
	
	private ObjectProperty<Axis> xAxis = new ObjectPropertyBase<Axis>() {
		
		{
			addListener((observable, oldValue, newValue) -> {
				updateAxes();
			});
		}

		@Override
		public Object getBean() {
			return Axis2D.class;
		}

		@Override
		public String getName() {
			return "xAxis";
		}
		
	};
	
	public void setXAxis(Axis axis) {
		xAxis.set(axis);
	}
	
	public Axis getXAxis() {
		return xAxis.get();
	}
	
	public ObjectProperty<Axis> xAxisProperty() {
		return xAxis;
	}
	
	private ObjectProperty<Axis> yAxis = new ObjectPropertyBase<Axis>() {

		{
			addListener((observable, oldValue, newValue) -> {
				updateAxes();
			});
		}
		
		@Override
		public Object getBean() {
			return Axis2D.class;
		}

		@Override
		public String getName() {
			return "yAxis";
		}
		
	};
	
	public void setYAxis(Axis axis) {
		yAxis.set(axis);
	}
	
	public Axis getYAxis() {
		return yAxis.get();
	}
	
	public ObjectProperty<Axis> yAxisProperty() {
		return yAxis;
	}

	private Node plotContents;
	
	private Line xLine;
	
	private Line yLine;
	
	private Text xLabel;
	
	private Text yLabel;

	public Axis2D() {
		super();
		
		//setStyle("-fx-background-color: blue");
		
		xLine = new Line();
		yLine = new Line();
		xLabel = new Text();
		yLabel = new Text();
		
		getChildren().addAll(xLine, yLine, xLabel, yLabel);
		
		widthProperty().addListener((observable, oldValue, newValue) -> {
			updateAxes();
		});
		
		heightProperty().addListener((observable, oldValue, newValue) -> {
			updateAxes();
		});

//		getTransforms().addListener((ListChangeListener.Change<? extends Transform> c) -> {
//			while (c.next()) {
//				c.getRemoved().forEach(a -> a.setOnTransformChanged(null));
//				c.getAddedSubList().forEach(a -> a.setOnTransformChanged(e -> updateAxes()));
//			}
//		});
		
		//setManaged(false);
		setPickOnBounds(false);
	}
	
	protected Axis getAxis(int index) {
		return new Axis[] { getXAxis(), getYAxis(), }[index];
	}
	
	protected ObjectProperty<Axis> getAxisProperty(int index) {
		return Arrays.asList(xAxisProperty(), yAxisProperty()).get(index);
	}
	
	protected Dimension getDimension(int index) {
		return new Dimension[] { Dimension.X, Dimension.Y }[index];
	}

	@Override
	public ObservableList<Node> getChildren() {
		return super.getChildren();
	}

	public void setPlotContents(Node node) {
		if (plotContents != null) {
			getChildren().remove(plotContents);
		}
		
		plotContents = node;
		getChildren().add(plotContents);
		
		updateAxes();
	}

	public Node getPlotContents() {
		return plotContents;
	}

	public void updateAxes() {
		if (getXAxis() != null) {
			xLabel.setText(getXAxis().getLabel());
		}
		
		if (getYAxis() != null) {
			yLabel.setText(getYAxis().getLabel());
		}
				
		double xWidth = xLabel.getBoundsInLocal().getWidth();
		double xHeight = xLabel.getBoundsInLocal().getHeight();
		double yWidth = yLabel.getBoundsInLocal().getWidth();
		double yHeight = yLabel.getBoundsInLocal().getHeight();

		xLabel.setY(getHeight());
		xLabel.setX(yHeight + (getWidth() - xWidth - yHeight) / 2);

		yLabel.getTransforms().clear();
		yLabel.getTransforms().addAll(new Translate(0, (getHeight() - xHeight - yWidth)/2), new Rotate(90));
		
		xLine.setStartX(0 + yHeight);
		xLine.setStartY(getHeight() - xHeight);
		xLine.setEndX(getWidth());
		xLine.setEndY(getHeight() - xHeight);
		
		yLine.setStartX(0 + yHeight);
		yLine.setStartY(0);
		yLine.setEndX(0 + yHeight);
		yLine.setEndY(getHeight() - xHeight);
		
		if (plotContents instanceof Pane) {
			Pane pane = (Pane)plotContents;
			
			pane.relocate(yHeight, 0);
			pane.setPrefSize(getWidth() - yHeight, getHeight() - xHeight);
		}
	}

}
