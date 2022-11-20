package j3.widget.impl.scatter;

import j3.Axis;
import j3.Dimension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Axis3D extends Region {

	private ObjectProperty<Axis> xAxis = new ObjectPropertyBase<Axis>() {

		{
			addListener((observable, oldValue, newValue) -> {
				updateAxes();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
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
			return Axis3D.class;
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

	private ObjectProperty<Axis> zAxis = new ObjectPropertyBase<Axis>() {

		{
			addListener((observable, oldValue, newValue) -> {
				updateAxes();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "zAxis";
		}

	};

	public void setZAxis(Axis axis) {
		zAxis.set(axis);
	}

	public Axis getZAxis() {
		return zAxis.get();
	}

	public ObjectProperty<Axis> zAxisProperty() {
		return zAxis;
	}

	private List<Side> sides;

	private DoubleProperty sideGap = new DoublePropertyBase(0.0) {

		@Override
		protected void invalidated() {
			Transition t = new Transition() {

				private double[] oldZ = new double[6];
				private double[] newZ = new double[6];

				{
					setCycleDuration(Duration.seconds(1));

					oldZ[0] = ((Translate) sides.get(0).getTransforms().get(1)).getZ();
					oldZ[1] = ((Translate) sides.get(1).getTransforms().get(1)).getZ();
					oldZ[2] = ((Translate) sides.get(2).getTransforms().get(1)).getZ();
					oldZ[3] = ((Translate) sides.get(3).getTransforms().get(1)).getZ();
					oldZ[4] = ((Translate) sides.get(4).getTransforms().get(1)).getZ();
					oldZ[5] = ((Translate) sides.get(5).getTransforms().get(1)).getZ();

					newZ[0] = sides.get(0).getSize() * (0.5 + getSideGap());
					newZ[1] = sides.get(1).getSize() * (-0.5 - getSideGap());
					newZ[2] = sides.get(2).getSize() * (-0.5 - getSideGap());
					newZ[3] = sides.get(3).getSize() * (0.5 + getSideGap());
					newZ[4] = sides.get(4).getSize() * (0.5 + getSideGap());
					newZ[5] = sides.get(5).getSize() * (-0.5 - getSideGap());
				}

				@Override
				protected void interpolate(double frac) {
					for (int i = 0; i < 6; i++) {
						((Translate) sides.get(i).getTransforms().get(1)).setZ(oldZ[i] + frac * (newZ[i] - oldZ[i]));
					}

					updateAxes();
				}

			};

			t.setOnFinished(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					updateAxes();
				}

			});

			t.play();
		}

		@Override
		public Object getBean() {
			return Axis3D.this;
		}

		@Override
		public String getName() {
			return "sideGap";

		}

	};

	public double getSideGap() {
		return sideGap.get();
	}

	public void setSideGap(double value) {
		sideGap.set(value);
	}

	public DoubleProperty sideGapProperty() {
		return sideGap;
	}

	private Group textGroup;

	private Node plotContents;

	public Axis3D(int size, Group textGroup) {
		super();
		this.textGroup = textGroup;

		sides = new ArrayList<Side>();

		for (int i = 0; i < 6; i++) {
			double fx, fy, fz, r;
			Point3D axis;
			int side1, side2;

			switch (i) {
			case 0:
				fx = -0.5;
				fy = -0.5;
				fz = 0.5 + getSideGap();
				r = 0.0;
				axis = Rotate.X_AXIS;
				side1 = 0;
				side2 = 1;
				break;
			case 1:
				fx = -0.5;
				fy = -0.5;
				fz = -0.5 - getSideGap();
				r = 90;
				axis = Rotate.X_AXIS;
				side1 = 0;
				side2 = 2;
				break;
			case 2:
				fx = -0.5;
				fy = -0.5;
				fz = -0.5 - getSideGap();
				r = 90;
				axis = Rotate.Y_AXIS;
				side1 = 2;
				side2 = 1;
				break;
			case 3:
				fx = -0.5;
				fy = -0.5;
				fz = 0.5 + getSideGap();
				r = 90;
				axis = Rotate.Y_AXIS;
				side1 = 2;
				side2 = 1;
				break;
			case 4:
				fx = -0.5;
				fy = -0.5;
				fz = 0.5 + getSideGap();
				r = 90;
				axis = Rotate.X_AXIS;
				side1 = 0;
				side2 = 2;
				break;
			case 5:
				fx = -0.5;
				fy = -0.5;
				fz = -0.5 - getSideGap();
				r = 0;
				axis = Rotate.X_AXIS;
				side1 = 0;
				side2 = 1;
				break;
			default:
				fx = 0.0;
				fy = 0.0;
				fz = 0.0;
				r = 0;
				axis = Rotate.X_AXIS;
				side1 = -1;
				side2 = -1;
				break;
			}

			Side side = new Side(size, getDimension(side1), getAxisProperty(side1), getDimension(side2),
					getAxisProperty(side2));
			side.getTransforms().addAll(new Rotate(r, axis), new Translate(fx * size, fy * size, fz * size));

			sides.add(side);
		}

		getChildren().addAll(sides);

		getTransforms().addListener((ListChangeListener.Change<? extends Transform> c) -> {
			while (c.next()) {
				c.getRemoved().forEach(a -> a.setOnTransformChanged(null));
				c.getAddedSubList().forEach(a -> a.setOnTransformChanged(e -> updateAxes()));
			}
		});

		setManaged(false);
		setPickOnBounds(false);
	}

	protected Axis getAxis(int index) {
		return new Axis[] { getXAxis(), getYAxis(), getZAxis() }[index];
	}

	protected ObjectProperty<Axis> getAxisProperty(int index) {
		return Arrays.asList(xAxisProperty(), yAxisProperty(), zAxisProperty()).get(index);
	}

	protected Dimension getDimension(int index) {
		return new Dimension[] { Dimension.X, Dimension.Y, Dimension.Z }[index];
	}

	protected Side getSide(int index) {
		return sides.get(index);
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
	}

	public Node getPlotContents() {
		return plotContents;
	}

	public void updateAxes() {
//		if (getXAxis() == null || getYAxis() == null || getZAxis() == null) {
//			for (Side side : sides) {
//				side.setVisible(false);
//			}
//			
//			return;
//		}

		// sort sides by Z dimension
		List<Side> sortedSides = new ArrayList<Side>(sides);

		Collections.sort(sortedSides, (a1, a2) -> {
			Bounds bounds1 = a1.localToScene(a1.getBoundsInLocal());
			Bounds bounds2 = a2.localToScene(a2.getBoundsInLocal());

			double z1 = (bounds1.getMaxZ() + bounds1.getMinZ()) / 2.0;
			double z2 = (bounds2.getMaxZ() + bounds2.getMinZ()) / 2.0;

			return Double.compare(z1, z2);
		});

		// determine which axes are visible
		List<Side> visibleSides = new ArrayList<Side>();

		for (int i = 0; i < sides.size(); i++) {
			if (sortedSides.indexOf(sides.get(i)) < 3) {
				sides.get(i).setVisible(false);
			} else {
				sides.get(i).setVisible(true);
				visibleSides.add(sides.get(i));
			}
		}

		if (textGroup != null) {
			// determine which axis is the bottom
			Side bottom = null;
			double maximumY = 0.0;

			for (Side side : visibleSides) {
				Bounds bounds = side.localToScene(side.getBoundsInLocal());
				double centerY = (bounds.getMaxY() + bounds.getMinY()) / 2;

				// add some stability when selecting the bottom axis
				if ((bottom == null) || (centerY - maximumY > 10)) {
					bottom = side;
					maximumY = centerY;
				}
			}

			// create mapping between the label lines and the axis
			Map<Line, Side> lineToSideMapping = new HashMap<Line, Side>();
			Map<Line, Dimension> lineToDimensionMapping = new HashMap<Line, Dimension>();
			Map<Line, Axis> lineToAxisMapping = new HashMap<Line, Axis>();
			Map<Line, Line> labelToTickMapping = new HashMap<Line, Line>();

			for (Side side : visibleSides) {
				for (int i = 0; i < 4; i++) {
					lineToSideMapping.put(side.getInternalAxisLabelLine(i), side);
					lineToDimensionMapping.put(side.getInternalAxisLabelLine(i), side.getInternalDimension(i));
					lineToAxisMapping.put(side.getInternalAxisLabelLine(i), side.getInternalAxis(i));
					labelToTickMapping.put(side.getInternalAxisLabelLine(i), side.getInteralTickLabelLine(i));
				}
			}

			// if no bottom identified, we likely aren't displayed yet
			if (bottom == null) {
				return;
			}

			// determine the two sides of the axis to draw labels (note: this is a
			// simple approach to avoid having to implement geometric reasoning)
			List<Line> selectedLines = new ArrayList<Line>();
			List<Line> bottomLines = new ArrayList<Line>(bottom.getInternalAxisLabelLines());

			Collections.sort(bottomLines, (l1, l2) -> {
				Bounds b1 = l1.getParent().localToScene(l1.getBoundsInParent());
				Bounds b2 = l2.getParent().localToScene(l2.getBoundsInParent());

				double z1 = (b1.getMaxZ() + b1.getMinZ()) / 2.0;
				double z2 = (b2.getMaxZ() + b2.getMinZ()) / 2.0;

				return Double.compare(z1, z2);
			});

			selectedLines.add(bottomLines.get(0));
			selectedLines.add(bottomLines.get(1));

			// from the remaining visible axes, determine the third axis
			visibleSides.remove(bottom);

			// exclude any lines for axes we have already selected
			Dimension selectedAxis1 = lineToDimensionMapping.get(selectedLines.get(0));
			Dimension selectedAxis2 = lineToDimensionMapping.get(selectedLines.get(1));
			List<Line> otherLines = new ArrayList<Line>();

			for (Side side : visibleSides) {
				for (Line line : side.getInternalAxisLabelLines()) {
					Dimension lineAxis = lineToDimensionMapping.get(line);

					if (lineAxis != selectedAxis1 && lineAxis != selectedAxis2) {
						otherLines.add(line);
					}
				}
			}

			Collections.sort(otherLines, (l1, l2) -> {
				Bounds b1 = l1.getParent().localToScene(l1.getBoundsInParent());
				Bounds b2 = l2.getParent().localToScene(l2.getBoundsInParent());

				double z1 = (b1.getMaxZ() + b1.getMinZ()) / 2.0;
				double z2 = (b2.getMaxZ() + b2.getMinZ()) / 2.0;

				// add some stability when selecting the line
				if (Math.abs(z1 - z2) < 10) {
					return 0;
				}

				return Double.compare(z1, z2);
			});

			if (otherLines.isEmpty()) {
				return;
			}

			selectedLines.add(otherLines.get(0));

			// now update the text labels
			textGroup.getChildren().clear();

			for (Line labelLine : selectedLines) {
				Side side = lineToSideMapping.get(labelLine);
				Axis axis = lineToAxisMapping.get(labelLine);
				Line tickLine = labelToTickMapping.get(labelLine);

				if (axis == null) {
					continue;
				}

				double[] tickPositions = axis.getTickPositions();
				String[] tickLabels = axis.getTickLabels();

				for (int j = 0; j < tickPositions.length; j++) {
					double offset = side.getSize() * tickPositions[j];
					double offsetX = 0.0;
					double offsetY = 0.0;

					// flip y axis
					if (axis == getAxis(1)) {
						offset = side.getSize() - offset;
					}

					Text text = new Text(tickLabels[j]);
					double width = text.prefWidth(30);

					if (tickLine.getStartX() == tickLine.getEndX()) {
						offsetY = offset;
					} else {
						offsetX = offset;
					}

					Point3D point = new Point3D(offsetX, offsetY, 0);
					point = tickLine.localToScene(point);

					text.setTranslateX(point.getX() - width / 2);
					text.setTranslateY(point.getY());
					text.setTranslateZ(point.getZ());
					text.getStyleClass().add("j3-tick-label");

					textGroup.getChildren().add(text);
				}

				{
					double offset = side.getSize() / 2;
					double offsetX = 0.0;
					double offsetY = 0.0;

					Text text = new Text();
					text.setText(axis.getLabel());
					double width = text.prefWidth(30);

					if (labelLine.getStartX() == labelLine.getEndX()) {
						offsetY = offset;
					} else {
						offsetX = offset;
					}

					Point3D point = new Point3D(offsetX, offsetY, 0);
					point = labelLine.localToScene(point);

					text.setTranslateX(point.getX() - width / 2);
					text.setTranslateY(point.getY());
					text.setTranslateZ(point.getZ());
					text.getStyleClass().add("j3-axis-label");

					textGroup.getChildren().add(text);
				}
			}
		}
	}

}
