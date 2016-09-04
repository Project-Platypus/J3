package j3;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class Side extends Region {

	private int size;

	private Axis axis1;

	private Axis axis2;

	private Rectangle side;

	private List<Line> tickLines;

	/**
	 * Internal structure storing lines on which the tick labels are drawn.
	 * This is used to quickly detect which ticks are to be drawn, since JavaFX
	 * lacks basic geometric functions.
	 */
	private List<Line> tickLabelLines;

	/**
	 * Internal structure storing lines on which the axis labels are drawn.
	 * This is used to quickly detect which labels are to be drawn, since JavaFX
	 * lacks basic geometric functions.
	 */
	private List<Line> axisLabelLines;

	public Side(int size, Axis axis1, Axis axis2) {
		super();
		this.size = size;
		this.axis1 = axis1;
		this.axis2 = axis2;

		side = new Rectangle(size, size);
		side.getStyleClass().add("j3-axis-side");
		getChildren().add(side);
		
		tickLines = new ArrayList<Line>();
		tickLabelLines = new ArrayList<Line>();
		axisLabelLines = new ArrayList<Line>();

		{
			double[] tickPositions = axis1.getTickPositions();

			for (int i = 0; i < tickPositions.length; i++) {
				Line line = new Line(0, 0, size, 0);
				line.getStyleClass().add("j3-tick-line");
				line.setStroke(Color.BLACK);
				line.setFill(Color.BLACK);
				line.setTranslateY(size*tickPositions[i]);
				tickLines.add(line);
			}
		}

		{
			double[] tickPositions = axis2.getTickPositions();

			for (int i = 0; i < tickPositions.length; i++) {
				Line line = new Line(0, 0, 0, size);
				line.getStyleClass().add("j3-tick-line");
				line.setStroke(Color.BLACK);
				line.setFill(Color.BLACK);
				line.setTranslateX(size*tickPositions[i]);
				tickLines.add(line);
			}
		}

		{
			Line tickLine1 = new Line(0, 0, size, 0);
			tickLine1.getStyleClass().add("j3-internal-tick-label-line");
			tickLine1.setTranslateY(-30);
			tickLine1.setManaged(false);
			tickLabelLines.add(tickLine1);

			Line tickLine2 = new Line(0, 0, size, 0);
			tickLine2.getStyleClass().add("j3-internal-tick-label-line");
			tickLine2.setTranslateY(size + 30);
			tickLine2.setManaged(false);
			tickLabelLines.add(tickLine2);

			Line tickLine3 = new Line(0, 0, 0, size);
			tickLine3.getStyleClass().add("j3-internal-tick-label-line");
			tickLine3.setTranslateX(-30);
			tickLine3.setManaged(false);
			tickLabelLines.add(tickLine3);

			Line tickLine4 = new Line(0, 0, 0, size);
			tickLine4.getStyleClass().add("j3-internal-tick-label-line");
			tickLine4.setTranslateX(size + 30);
			tickLine4.setManaged(false);
			tickLabelLines.add(tickLine4);

			Line labelLine1 = new Line(0, 0, size, 0);
			labelLine1.getStyleClass().add("j3-internal-axis-label-line");
			labelLine1.setTranslateY(-60);
			labelLine1.setManaged(false);
			axisLabelLines.add(labelLine1);

			Line labelLine2 = new Line(0, 0, size, 0);
			labelLine2.getStyleClass().add("j3-internal-axis-label-line");
			labelLine2.setTranslateY(size + 60);
			labelLine2.setManaged(false);
			axisLabelLines.add(labelLine2);

			Line labelLine3 = new Line(0, 0, 0, size);
			labelLine3.getStyleClass().add("j3-internal-axis-label-line");
			labelLine3.setTranslateX(-60);
			labelLine3.setManaged(false);
			axisLabelLines.add(labelLine3);

			Line labelLine4 = new Line(0, 0, 0, size);
			labelLine4.getStyleClass().add("j3-internal-axis-label-line");
			labelLine4.setTranslateX(size + 60);
			labelLine4.setManaged(false);
			axisLabelLines.add(labelLine4);
		}

		getChildren().addAll(tickLines);
		getChildren().addAll(tickLabelLines);
		getChildren().addAll(axisLabelLines);

		setManaged(false);
	}

	public int getSize() {
		return size;
	}

	public Axis getAxis1() {
		return axis1;
	}

	public Axis getAxis2() {
		return axis2;
	}

	public List<Line> getTickLines() {
		return tickLines;
	}
	
	Line getInteralTickLabelLine(int index) {
		return tickLabelLines.get(index);
	}

	Line getInternalAxisLabelLine(int index) {
		return axisLabelLines.get(index);
	}
	
	Axis getInternalAxis(int index) {
		return index < 2 ? axis1 : axis2;
	}
	
	List<Line> getInternalTickLabelLines() {
		return tickLabelLines;
	}

	List<Line> getInternalAxisLabelLines() {
		return axisLabelLines;
	}

	public double getCentroidZ() {
		Bounds bounds = localToScene(getBoundsInLocal());
		
		if (bounds == null) {
			return 0.0;
		} else {
			return (bounds.getMinZ() + bounds.getMaxZ()) / 2.0;
		}
	}
	
	public double getCentroidY() {
		Bounds bounds = localToScene(getBoundsInLocal());
		
		if (bounds == null) {
			return 0.0;
		} else {
			return (bounds.getMinY() + bounds.getMaxY()) / 2.0;
		}
	}

	@Override
	public ObservableList<Node> getChildren() {
		return super.getChildren();
	}

}
