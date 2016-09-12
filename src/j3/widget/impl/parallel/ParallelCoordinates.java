package j3.widget.impl.parallel;

import j3.Axis;
import j3.Canvas;
import j3.colormap.Colormap;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.widget.TitledWidget;
import j3.widget.impl.scatter.Axis3D;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class ParallelCoordinates extends TitledWidget<ParallelCoordinates>  {

	private final List<VerticalAxis> verticalAxes = new ArrayList<>();

	private List<Integer> permutation = new ArrayList<Integer>();

	private final Map<Instance, List<Line>> lineMap = new LinkedHashMap<>();
	
	private final Group lineGroup = new Group();

	private DataFrame table;
	
	private ObjectProperty<Colormap> colormap = new ObjectPropertyBase<Colormap>() {

		@Override
		protected void invalidated() {
			updateColorAxis();
		}

		@Override
		public Object getBean() {
			return ParallelCoordinates.this;
		}

		@Override
		public String getName() {
			return "colormap";
		}

	};

	public void setColormap(Colormap colormap) {
		this.colormap.set(colormap);
	}

	public Colormap getColormap() {
		return colormap.get();
	}

	public ObjectProperty<Colormap> colormapProperty() {
		return colormap;
	}

	private ObjectProperty<Axis> colorAxis = new ObjectPropertyBase<Axis>() {
		
		@Override
		protected void invalidated() {
			updateColorAxis();
		}

		@Override
		public Object getBean() {
			return ParallelCoordinates.this;
		}

		@Override
		public String getName() {
			return "Color";
		}

	};

	public void setColorAxis(Axis axis) {
		colorAxis.set(axis);
	}

	public Axis getColorAxis() {
		return colorAxis.get();
	}

	public ObjectProperty<Axis> colorAxisProperty() {
		return colorAxis;
	}
	
	private ObjectProperty<Axis> visibilityAxis = new ObjectPropertyBase<Axis>() {
		
		@Override
		protected void invalidated() {
			updateVisibilityAxis();
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "Visibility";
		}

	};

	public void setVisibilityAxis(Axis axis) {
		visibilityAxis.set(axis);
	}

	public Axis getVisibilityAxis() {
		return visibilityAxis.get();
	}

	public ObjectProperty<Axis> visibilityAxisProperty() {
		return visibilityAxis;
	}
	
	private Point2D initPoint;

	private Point2D anchorPoint;

	public ParallelCoordinates() {
		super();
	}

	@Override
	public ParallelCoordinates getNode() {
		return this;
	}

	@Override
	public void onActivate(Canvas canvas) {
		super.onActivate(canvas);

		canvas.setBoxSelectionHandler(event -> {
			setLayoutX(canvas.getSelectionBox().getX());
			setLayoutY(canvas.getSelectionBox().getY());
			
			pane.setPrefWidth(canvas.getSelectionBox().getWidth());
			pane.setPrefHeight(canvas.getSelectionBox().getHeight());

			canvas.add(this);

			event.consume();
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Canvas canvas) {
		Pane container = new Pane();
		container.setPadding(new Insets(5, 5, 5, 5));
		
		// this is needed to prevent the height growing (need to fix the root cause)
		container.setPrefHeight(300);

		table = (DataFrame)canvas.getPropertyRegistry().get("data").getValue();
		colormap.bind((ObjectProperty<Colormap>)canvas.getPropertyRegistry().get("colormap"));
		colorAxis.bind((ObjectProperty<Axis>)canvas.getPropertyRegistry().get("colorAxis"));
		visibilityAxis.bind((ObjectProperty<Axis>)canvas.getPropertyRegistry().get("visibilityAxis"));
		
		List<Axis> options = ((ObjectProperty<List<Axis>>)canvas.getPropertyRegistry().get("axes")).get();

		for (Axis axis : options) {
			VerticalAxis verticalAxis = new VerticalAxis(axis);

			verticalAxis.label.setCursor(Cursor.H_RESIZE);

			verticalAxis.label.setOnMousePressed(event -> {
				initPoint = new Point2D(verticalAxis.getTranslateX(), verticalAxis.getTranslateY());
				anchorPoint = new Point2D(event.getSceneX(), event.getSceneY());
				event.consume();
			});

			verticalAxis.label.setOnMouseReleased(event -> {
				int ncol = verticalAxes.size();

				for (int i = 0; i <ncol; i++) {
					permutation.set(i, i);
				}

				Collections.sort(permutation, (i1, i2) -> {
					VerticalAxis a1 = verticalAxes.get(i1);
					VerticalAxis a2 = verticalAxes.get(i2);

					return Double.compare(a1.getLayoutX()+a1.getTranslateX(), a2.getLayoutX()+a2.getTranslateX());
				});

				double[] oldPositions = new double[ncol];
				double[] newPositions = new double[ncol];

				for (int i = 0; i < ncol; i++) {
					double spaceWidth = (container.getWidth()-60.0)/ncol;
					double spaceCenter = 30.0 + i*spaceWidth + spaceWidth/2.0;
					double newLayout = spaceCenter - verticalAxis.getWidth()/2.0;

					oldPositions[i] = verticalAxes.get(permutation.get(i)).getLayoutX();
					newPositions[i] = newLayout;
				}

				Timeline timeline = new Timeline();

				for (int i = 0; i < ncol; i++) {
						timeline.getKeyFrames().addAll(
								new KeyFrame(Duration.ZERO,
										new KeyValue(verticalAxes.get(permutation.get(i)).layoutXProperty(), oldPositions[i])),
										new KeyFrame(new Duration(250),
												new KeyValue(verticalAxes.get(permutation.get(i)).layoutXProperty(), newPositions[i])));
				}

				timeline.getKeyFrames().addAll(
						new KeyFrame(Duration.ZERO,
								new KeyValue(verticalAxis.translateXProperty(), verticalAxis.getTranslateX())),
								new KeyFrame(new Duration(250),
										new KeyValue(verticalAxis.translateXProperty(), 0.0)));

				timeline.play();

				initPoint = null;
				anchorPoint = null;
			});

			verticalAxis.label.setOnMouseDragged(event -> {
				verticalAxis.setTranslateX(initPoint.getX()+(event.getSceneX()-anchorPoint.getX()));

				int ncol = verticalAxes.size();
				List<Integer> newPermutation = new ArrayList<Integer>();

				for (int i = 0; i < ncol; i++) {
					newPermutation.add(i);
				}

				Collections.sort(newPermutation, (i1, i2) -> {
					VerticalAxis a1 = verticalAxes.get(i1);
					VerticalAxis a2 = verticalAxes.get(i2);

					return Double.compare(a1.getLayoutX()+a1.getTranslateX(), a2.getLayoutX()+a2.getTranslateX());
				});

				if (!newPermutation.equals(permutation)) {
					permutation = newPermutation;

					double[] oldPositions = new double[ncol];
					double[] newPositions = new double[ncol];

					for (int i = 0; i < ncol; i++) {
						double spaceWidth = (container.getWidth()-60.0)/ncol;
						double spaceCenter = 30.0 + i*spaceWidth + spaceWidth/2.0;
						double newLayout = spaceCenter - verticalAxis.getWidth()/2.0;

						oldPositions[i] = verticalAxes.get(permutation.get(i)).getLayoutX();
						newPositions[i] = newLayout;
					}

					Timeline timeline = new Timeline();

					for (int i = 0; i < ncol; i++) {
						if (verticalAxis != verticalAxes.get(permutation.get(i))) {
							timeline.getKeyFrames().addAll(
									new KeyFrame(Duration.ZERO,
											new KeyValue(verticalAxes.get(permutation.get(i)).layoutXProperty(), oldPositions[i])),
											new KeyFrame(new Duration(250),
													new KeyValue(verticalAxes.get(permutation.get(i)).layoutXProperty(), newPositions[i])));							
						}
					}

					timeline.play();
				}

				event.consume();
			});
			
			verticalAxis.layoutXProperty().addListener((observable, oldValue, newValue) -> relayoutLines());
			verticalAxis.translateXProperty().addListener((observable, oldValue, newValue) -> relayoutLines());

			permutation.add(permutation.size());
			verticalAxes.add(verticalAxis);
		}

		container.getChildren().add(lineGroup);
		container.getChildren().addAll(verticalAxes);

		container.heightProperty().addListener((observable, oldValue, newValue) -> {
			for (VerticalAxis verticalAxis : verticalAxes) {
				verticalAxis.setPrefHeight(newValue.doubleValue()-5);
			}
			
			relayoutLines();
		});

		container.widthProperty().addListener((observable, oldValue, newValue) -> {
			double spaceWidth = (newValue.doubleValue()-60.0)/verticalAxes.size();

			for (int i = 0; i < verticalAxes.size(); i++) {
				VerticalAxis verticalAxis = verticalAxes.get(permutation.get(i));
				double spaceCenter = 30.0 + i*spaceWidth + spaceWidth/2.0;

				verticalAxis.setLayoutX(spaceCenter - verticalAxis.getWidth()/2.0);
			}
			
			relayoutLines();
		});
		
		relayoutLines();

		setContent(container);
		setTitle("Parallel Coordinates");
	}

	/**
	 * A couple of notes:
	 * 
	 * 1. Here, we use individual lines instead of a path for performance
	 *    reasons.  Paths appear to be significantly slower to render.
	 * 2. We used to use bindings here to keep the line endpoints placed on each
	 *    vertical axis.  When the axes are permuted, this requires rebinding
	 *    the lines.  This resulting in memory and GC issues as one needs to
	 *    create potentially tens of thousands of new objects, leading to
	 *    periodic rendering delays during GC.
	 */
	public void relayoutLines() {
		int ncol = verticalAxes.size();
		Axis colorAxis = getColorAxis();
		Colormap colormap = getColormap();
		
		if (lineMap.isEmpty()) {
			table.getInstances().forEach(instance -> {
				List<Line> lines = new ArrayList<>();

				IntStream.range(0, ncol).forEach(i -> {
					Line line = new Line();
					line.setStroke(colormap.map(colorAxis.map(instance.get(colorAxis.getColumn()))));
					line.setStrokeWidth(1.5);
					line.setUserData(instance);
					line.setManaged(false);
					lines.add(line);
				});

				lineMap.put(instance, lines);
				lineGroup.getChildren().addAll(lines);
			});
		}

		table.getInstances().forEach(instance -> {
			List<Line> lines = lineMap.get(instance);
			
			IntStream.range(0, ncol-1).forEach(i -> {
				VerticalAxis axis1 = verticalAxes.get(permutation.get(i));
				VerticalAxis axis2 = verticalAxes.get(permutation.get(i+1));
				double value1 = axis1.getAxis().map(instance.get(axis1.getAxis().getColumn()));
				double value2 = axis2.getAxis().map(instance.get(axis2.getAxis().getColumn()));
				
				Line line = lines.get(i);
				line.setStartX(axis1.getLayoutX() + axis1.getTranslateX() + VerticalAxis.HALF_WIDTH);
				line.setStartY(axis1.line.getStartY() + (axis1.line.getEndY() - axis1.line.getStartY())*value1);
				line.setEndX(axis2.getLayoutX() + axis2.getTranslateX() + VerticalAxis.HALF_WIDTH);
				line.setEndY(axis2.line.getStartY() + (axis2.line.getEndY() - axis2.line.getStartY())*value2);
			});
		});
	}
	
	private class ColorTransition extends Transition {
		
		private Color[] start = new Color[table.instanceCount()];
		
		private Color[] end = new Color[table.instanceCount()];
		
		public ColorTransition() {
			super();
			
			setCycleDuration(new Duration(1000));
			setCycleCount(1);
			
			Axis colorAxis = getColorAxis();
			Colormap colormap = getColormap();
			
			for (int i = 0; i < table.instanceCount(); i++) {
				Instance instance = table.getInstance(i);
				
				start[i] = (Color)lineMap.get(instance).get(0).getStroke();
				end[i] = colormap.map(colorAxis == null ? 0.0 : colorAxis.map(instance.get(colorAxis.getColumn())));
			}
		}

		@Override
		protected void interpolate(double frac) {
			IntStream.range(0, table.instanceCount()).forEach(i -> {
				Instance instance = table.getInstance(i);
				lineMap.get(instance).forEach(line -> line.setStroke(start[i].interpolate(end[i], frac)));
			});
		}
		
	}
	
	public void updateColorAxis() {
		if (lineMap.isEmpty()) {
			return;
		}
		
		ColorTransition ct = new ColorTransition();
		ct.play();
	}
	
	public void updateVisibilityAxis() {
		if (lineMap.isEmpty()) {
			return;
		}

		Axis visibilityAxis = getVisibilityAxis();
		
		IntStream.range(0, table.instanceCount()).forEach(i -> {
			Instance instance = table.getInstance(i);
			lineMap.get(instance).forEach(line -> line.setVisible(visibilityAxis == null ? true : visibilityAxis.map(instance.get(visibilityAxis.getColumn())) > 0.5));
		});
	}
	
	public void update() {
		updateVisibilityAxis();
	}

	@Override
	public void onRemove(Canvas canvas) {
		colormap.unbind();
		colorAxis.unbind();
		visibilityAxis.unbind();
	}

}
