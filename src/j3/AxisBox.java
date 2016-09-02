package j3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
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

public class AxisBox extends Region {

	private List<Axis> axes;

	private List<Side> sides;

	private double sideGap = 0.2;

	private Group textGroup;
	
	private Node plotContents;

	public AxisBox(int size, Group textGroup) {
		super();
		this.textGroup = textGroup;
		
		axes = new ArrayList<Axis>();
		sides = new ArrayList<Side>();

		axes.add(new RealAxis(Dimension.X, "X"));
		axes.add(new RealAxis(Dimension.Y, "Y"));
		axes.add(new RealAxis(Dimension.Z, "Z"));

		for (int i = 0; i < 6; i++) {
			double fx, fy, fz, r;
			Point3D axis;
			int side1, side2;

			switch (i) {
			case 0:
				fx = -0.5;
				fy = -0.5;
				fz = 0.5 + sideGap;
				r = 0.0;
				axis = Rotate.X_AXIS;
				side1 = 0;
				side2 = 1;
				break;
			case 1:
				fx = -0.5;
				fy = -0.5;
				fz = -0.5 - sideGap;
				r = 90;
				axis = Rotate.X_AXIS;
				side1 = 0;
				side2 = 2;
				break;
			case 2:
				fx = -0.5;
				fy = -0.5;
				fz = -0.5 - sideGap;
				r = 90;
				axis = Rotate.Y_AXIS;
				side1 = 2;
				side2 = 1;
				break;
			case 3:
				fx = -0.5;
				fy = -0.5;
				fz = 0.5 + sideGap;
				r = 90;
				axis = Rotate.Y_AXIS;
				side1 = 2;
				side2 = 1;
				break;
			case 4:
				fx = -0.5;
				fy = -0.5;
				fz = 0.5 + sideGap;
				r = 90;
				axis = Rotate.X_AXIS;
				side1 = 0;
				side2 = 2;
				break;
			case 5:
				fx = -0.5;
				fy = -0.5;
				fz = -0.5 - sideGap;
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

			Side side = new Side(size, axes.get(side1), axes.get(side2));
			side.getTransforms().addAll(new Rotate(r, axis), new Translate(fx*size, fy*size, fz*size));

			sides.add(side);
		}

		getChildren().addAll(sides);

		getTransforms().addListener((ListChangeListener.Change<? extends Transform> c) -> {
			while (c.next()) {
				c.getRemoved().forEach(a -> a.setOnTransformChanged(null));
				c.getAddedSubList().forEach(a -> a.setOnTransformChanged(e -> updateAxes()));
			}
		});
	}
	
	protected Axis getAxis(int index) {
		return axes.get(index);
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
				double centerY = (bounds.getMaxY()+bounds.getMinY())/2;
	
				// add some stability when selecting the bottom axis
				if (centerY - maximumY > 10) {
					bottom = side;
					maximumY = centerY;
				}
			}
			
			// create mapping between the label lines and the axis
			Map<Line, Side> lineToSideMapping = new HashMap<Line, Side>();
			Map<Line, Axis<?>> lineToAxisMapping = new HashMap<Line, Axis<?>>();
			Map<Line, Line> labelToTickMapping = new HashMap<Line, Line>();
			
			for (Side side : visibleSides) {
				for (int i = 0; i < 4; i++) {
					lineToSideMapping.put(side.getInternalAxisLabelLine(i), side);
					lineToAxisMapping.put(side.getInternalAxisLabelLine(i), side.getInternalAxis(i));
					labelToTickMapping.put(side.getInternalAxisLabelLine(i), side.getInteralTickLabelLine(i));
				}
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
			Axis<?> selectedAxis1 = lineToAxisMapping.get(selectedLines.get(0));
			Axis<?> selectedAxis2 = lineToAxisMapping.get(selectedLines.get(1));
			List<Line> otherLines = new ArrayList<Line>();
			
			for (Side side : visibleSides) {
				for (Line line : side.getInternalAxisLabelLines()) {
					Axis<?> lineAxis = lineToAxisMapping.get(line);
					
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
			
			selectedLines.add(otherLines.get(0));

			// now update the text labels
			textGroup.getChildren().clear();
			
			for (Line labelLine : selectedLines) {
				Side side = lineToSideMapping.get(labelLine);
				Axis<?> axis = lineToAxisMapping.get(labelLine);
				Line tickLine = labelToTickMapping.get(labelLine);

				double[] tickPositions = axis.getTickPositions();
				String[] tickLabels = axis.getTickLabels();

				for (int j = 0; j < tickPositions.length; j++) {
					double offset = side.getSize() * tickPositions[j];
					double offsetX = 0.0;
					double offsetY = 0.0;
					
					// flip y axis
					if (axis.getDimension() == Dimension.Y) {
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

					text.setTranslateX(point.getX()-width/2);
					text.setTranslateY(point.getY());
					text.setTranslateZ(point.getZ());

					textGroup.getChildren().add(text);
				}

				{
					double offset = side.getSize()/2;
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
					
					text.setTranslateX(point.getX()-width/2);
					text.setTranslateY(point.getY());
					text.setTranslateZ(point.getZ());
					text.setStyle("-fx-font-weight: bold;");

					textGroup.getChildren().add(text);
				}
			}
		}
	}

}
