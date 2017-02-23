package j3.widget.impl.scatter;

import j3.Axis;
import j3.Dimension;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

public class Side extends Region {

	private int size;
	
	private final Dimension dimension1;
	
	public Dimension getDimension1() {
		return dimension1;
	}
	
	private final Dimension dimension2;
	
	public Dimension getDimension2() {
		return dimension2;
	}

	private final ObjectProperty<Axis> axis1 = new ObjectPropertyBase<Axis>() {

		@Override
		protected void invalidated() {
			getChildren().removeAll(tickLines1);
			tickLines1.clear();
			
			if (getAxis1() != null) {
				double[] tickPositions = getAxis1().getTickPositions();
	
				for (int i = 0; i < tickPositions.length; i++) {
					Line line = new Line(0, 0, 0, size);
					line.getStyleClass().add("j3-tick-line");
					line.setStroke(Color.BLACK);
					line.setFill(Color.BLACK);
					line.setTranslateX(size*tickPositions[i]);
					tickLines1.add(line);
				}
				
				getChildren().addAll(tickLines1);
			}
		}

		@Override
		public Object getBean() {
			return Side.class;
		}

		@Override
		public String getName() {
			return "axis1";
		}
		
	};
	
	public void setAxis1(Axis axis) {
		axis1.set(axis);
	}
	
	public Axis getAxis1() {
		return axis1.get();
	}
	
	public ObjectProperty<Axis> axis1Property() {
		return axis1;
	}
	
	private final ObjectProperty<Axis> axis2 = new ObjectPropertyBase<Axis>() {

		@Override
		protected void invalidated() {
			getChildren().removeAll(tickLines2);
			tickLines2.clear();
			
			if (getAxis2() != null) {
				double[] tickPositions = getAxis2().getTickPositions();
	
				for (int i = 0; i < tickPositions.length; i++) {
					Line line = new Line(0, 0, size, 0);
					line.getStyleClass().add("j3-tick-line");
					line.setStroke(Color.BLACK);
					line.setFill(Color.BLACK);
					line.setTranslateY(size*tickPositions[i]);
					tickLines2.add(line);
				}
				
				getChildren().addAll(tickLines2);
			}
		}
		
		@Override
		public Object getBean() {
			return Side.class;
		}

		@Override
		public String getName() {
			return "axis2";
		}
		
	};
	
	public void setAxis2(Axis axis) {
		axis2.set(axis);
	}
	
	public Axis getAxis2() {
		return axis2.get();
	}
	
	public ObjectProperty<Axis> axis2Property() {
		return axis2;
	}

	private Rectangle side;

	private final List<Line> tickLines1 = new ArrayList<Line>();
	
	private final List<Line> tickLines2 = new ArrayList<Line>();

	/**
	 * Internal structure storing lines on which the tick labels are drawn.
	 * This is used to quickly detect which ticks are to be drawn, since JavaFX
	 * lacks basic geometric functions.
	 */
	private final List<Line> tickLabelLines = new ArrayList<Line>();;

	/**
	 * Internal structure storing lines on which the axis labels are drawn.
	 * This is used to quickly detect which labels are to be drawn, since JavaFX
	 * lacks basic geometric functions.
	 */
	private final List<Line> axisLabelLines = new ArrayList<Line>();

	public Side(int size, Dimension dimension1, ObjectProperty<Axis> axis1, Dimension dimension2, ObjectProperty<Axis> axis2) {
		super();
		this.size = size;
		this.dimension1 = dimension1;
		this.dimension2 = dimension2;
		setAxis1(axis1.get());
		setAxis2(axis2.get());
		this.axis1.bind(axis1);
		this.axis2.bind(axis2);

		side = new Rectangle(size, size);
		side.setFill(Color.WHITE);
		side.getStyleClass().add("j3-axis-side");
		getChildren().add(side);

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

		getChildren().addAll(tickLabelLines);
		getChildren().addAll(axisLabelLines);

		setManaged(false);
	}

	public int getSize() {
		return size;
	}
	
	Line getInteralTickLabelLine(int index) {
		return tickLabelLines.get(index);
	}

	Line getInternalAxisLabelLine(int index) {
		return axisLabelLines.get(index);
	}
	
	Axis getInternalAxis(int index) {
		return index < 2 ? axis1.get() : axis2.get();
	}
	
	Dimension getInternalDimension(int index) {
		return index < 2 ? dimension1 : dimension2;
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
