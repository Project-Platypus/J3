package j3.widget.impl.scatter2d;

import j3.Axis;
import j3.Dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
	
	private List<Text> xTickLabels;
	
	private List<Line> xTickLines;
	
	private List<Text> yTickLabels;
	
	private List<Line> yTickLines;

	public Axis2D() {
		super();
		
		xTickLabels = new ArrayList<>();
		xTickLines = new ArrayList<>();
		yTickLabels = new ArrayList<>();
		yTickLines = new ArrayList<>();
		
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
		
		setManaged(true);
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
		// remove the previous tick labels
		getChildren().removeAll(xTickLabels);
		getChildren().removeAll(xTickLines);
		getChildren().removeAll(yTickLabels);
		getChildren().removeAll(yTickLines);
		xTickLabels.clear();
		xTickLines.clear();
		yTickLabels.clear();
		yTickLines.clear();
		
		// first add the x and y axis labels
		if (getXAxis() != null) {
			xLabel.setText(getXAxis().getLabel());
			
			for (String tickLabel : getXAxis().getTickLabels()) {
				Line line = new Line(0, 0, 0, 4);
				line.getStyleClass().add("j3-tick-line");
				line.setStroke(Color.BLACK);
				line.setFill(Color.BLACK);
				xTickLines.add(line);
				
				Text text = new Text(tickLabel);
				text.getStyleClass().add("j3-tick-label");
				xTickLabels.add(text);
			}
		}
		
		if (getYAxis() != null) {
			yLabel.setText(getYAxis().getLabel());
			
			for (String tickLabel : getYAxis().getTickLabels()) {
				Line line = new Line(0, 0, -4, 0);
				line.getStyleClass().add("j3-tick-line");
				line.setStroke(Color.BLACK);
				line.setFill(Color.BLACK);
				yTickLines.add(line);
				
				Text text = new Text(tickLabel);
				text.getStyleClass().add("j3-tick-label");
				yTickLabels.add(text);
			}
		}
		
		getChildren().addAll(xTickLabels);
		getChildren().addAll(xTickLines);
		getChildren().addAll(yTickLabels);
		getChildren().addAll(yTickLines);
		
		// compute the offsets in the x and y axis
		double xOffset = 0.0;
		double yOffset = 0.0;
		
		for (Text text : xTickLabels) {
			yOffset = Math.max(xOffset, text.getBoundsInLocal().getHeight());
		}
		
		for (Text text : yTickLabels) {
			xOffset = Math.max(xOffset, text.getBoundsInLocal().getHeight());
		}
		
		yOffset += xLabel.getBoundsInLocal().getHeight();
		xOffset += yLabel.getBoundsInLocal().getHeight();
			
		// now position the labels
		double xWidth = xLabel.getBoundsInLocal().getWidth();
		double xHeight = xLabel.getBoundsInLocal().getHeight();
		double yWidth = yLabel.getBoundsInLocal().getWidth();
		double yHeight = yLabel.getBoundsInLocal().getHeight();
		
		xLabel.getTransforms().setAll(new Translate(xOffset + (getWidth() - xWidth - 2*xOffset)/2, getHeight()));
		yLabel.getTransforms().setAll(new Translate(0, (getHeight() - xHeight - yWidth)/2), new Rotate(90));
		
		if (getXAxis() != null) {
			double[] xTickPositions = getXAxis().getTickPositions();
			
			for (int i = 0; i < xTickPositions.length; i++) {
				Bounds labelBounds = xTickLabels.get(i).getBoundsInLocal();
				
				xTickLabels.get(i).getTransforms().setAll(new Translate(
						xOffset + xTickPositions[i] * (getWidth() - 2*xOffset) - labelBounds.getWidth() / 2.0,
						getHeight() - xHeight));
				xTickLines.get(i).getTransforms().setAll(new Translate(
						xOffset + xTickPositions[i] * (getWidth() - 2*xOffset),
						getHeight() - yOffset));
			}
		}
		
		if (getYAxis() != null) {
			double[] yTickPositions = getYAxis().getTickPositions();
			
			for (int i = 0; i < yTickPositions.length; i++) {
				Bounds labelBounds = yTickLabels.get(i).getBoundsInLocal();
				
				yTickLabels.get(i).getTransforms().setAll(new Translate(
						yHeight,
						yTickPositions[i] * (getHeight() - yOffset) - labelBounds.getWidth() / 2.0),
						new Rotate(90));
				yTickLines.get(i).getTransforms().setAll(new Translate(
						xOffset,
						yTickPositions[i] * (getHeight() - yOffset)));
			}
		}
		
		// update the axis lines
		xLine.setStartX(0 + xOffset);
		xLine.setStartY(getHeight() - yOffset);
		xLine.setEndX(getWidth() - xOffset);
		xLine.setEndY(getHeight() - yOffset);
		
		yLine.setStartX(0 + xOffset);
		yLine.setStartY(0);
		yLine.setEndX(0 + xOffset);
		yLine.setEndY(getHeight() - yOffset);
		
		if (plotContents instanceof Pane) {
			Pane pane = (Pane)plotContents;
			
			pane.relocate(xOffset, 0);
			pane.setPrefSize(getWidth() - 2*xOffset, getHeight() - yOffset);
		}
	}

}
