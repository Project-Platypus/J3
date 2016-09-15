package j3.widget.impl.parallel;

import j3.Axis;
import j3.Canvas;
import j3.EmptyAxis;
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

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.text.Text;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class ParallelCoordinates extends TitledWidget<ParallelCoordinates>  {
	
	public static final double DEFAULT_LINE_THICKNESS = 1.5;

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
	
	private ObjectProperty<Instance> selectedInstance = new ObjectPropertyBase<Instance>() {
		
		private Instance oldInstance = null;

		@Override
		protected void invalidated() {
			if (oldInstance != null) {
				lineMap.get(oldInstance).forEach(line -> line.setStrokeWidth(1.0));
			}
			
			if (lineMap.isEmpty()) {
				oldInstance = null;
			} else {
				lineMap.get(selectedInstance.get()).forEach(line -> line.setStrokeWidth(4.0));
				oldInstance = selectedInstance.get();
			}
		}

		@Override
		public Object getBean() {
			return ParallelCoordinates.this;
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
	
	private Point2D initPoint;

	private Point2D anchorPoint;
	
	private Button toolbarButton;

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
			
			pane.setPrefWidth(Math.max(300, canvas.getSelectionBox().getWidth()));
			pane.setPrefHeight(Math.max(200, canvas.getSelectionBox().getHeight()));

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
		selectedInstance.bindBidirectional((ObjectProperty<Instance>)canvas.getPropertyRegistry().get("selectedInstance"));
		
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

				IntStream.range(0, ncol-1).forEach(i -> {
					Line line = new Line();
					line.setStroke(colormap.map(colorAxis.map(instance)));
					line.setStrokeWidth(2.0);
					line.setUserData(instance);
					line.setManaged(false);
					line.setOnMouseClicked(event -> selectedInstance.set(instance));
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
				double value1 = axis1.getAxis().map(instance);
				double value2 = axis2.getAxis().map(instance);
				
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
				end[i] = colormap.map(colorAxis == null ? 0.0 : colorAxis.map(instance));
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
			lineMap.get(instance).forEach(line -> line.setVisible(visibilityAxis == null ? true : visibilityAxis.map(instance) > 0.5));
		});
	}
	
	public void update() {
		updateVisibilityAxis();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onAdd(Canvas canvas) {
		toolbarButton = new Button();
		toolbarButton.setGraphic(new ImageView(new ParallelCoordinatesProvider().getIcon()));
		toolbarButton.setTooltip(new Tooltip("Options for the parallel coordinates plot"));
		
		toolbarButton.setOnAction(event -> {
			PopOver popover = new PopOver();
			popover.setTitle("Parallel Coordinates Plot Options");
			popover.setAutoHide(true);
			popover.setAutoFix(true);
			popover.setArrowLocation(ArrowLocation.TOP_CENTER);
			
			GridPane content = new GridPane();
			content.setHgap(5);
			content.setVgap(5);
			content.setPadding(new Insets(5, 5, 5, 5));

			ColumnConstraints column1 = new ColumnConstraints();
			column1.setHgrow(Priority.NEVER);

			ColumnConstraints column2 = new ColumnConstraints();
			column2.setHgrow(Priority.ALWAYS);
			column2.setFillWidth(true);

			content.getColumnConstraints().addAll(column1, column2);
			
			content.add(new Text("Line Thickness:"), 0, 0);
			
			Slider thicknessSlider = new Slider();
			thicknessSlider.setMin(0.1);
			thicknessSlider.setMax(5.0);
			thicknessSlider.setValue(2.0);
			thicknessSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
				table.getInstances().forEach(instance -> {
					lineMap.get(instance).forEach(line -> line.setStrokeWidth(newValue.doubleValue()));
				});
			});			
			content.add(thicknessSlider, 1, 0);

			content.add(new Text("Line Transparency:"), 0, 1);
			
			Slider transparencySlider = new Slider();
			transparencySlider.setMin(0.0);
			transparencySlider.setMax(1.0);
			transparencySlider.setValue(1.0);
			transparencySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
				table.getInstances().forEach(instance -> {
					lineMap.get(instance).forEach(line -> line.setOpacity(newValue.doubleValue()));
				});
			});
			content.add(transparencySlider, 1, 1);
			
			content.add(new Text("Z Order:"), 0, 2);
			
			List<Axis> options = new ArrayList<Axis>(((ObjectProperty<List<Axis>>)canvas.getPropertyRegistry().get("axes")).get());
			
			EmptyAxis empty = new EmptyAxis();
			options.add(0, empty);
			
			ComboBox<Axis> combobox = new ComboBox<>();
			combobox.getItems().addAll(options);
			combobox.setMaxWidth(Double.POSITIVE_INFINITY);
			combobox.setOnAction(e -> {
				Axis axis = combobox.getValue();
				List<Instance> instances = table.getInstances();
				
				instances.sort((i1, i2) -> {
					double v1 = axis.map(i1);
					double v2 = axis.map(i2);
					return Double.compare(v1, v2);
				});
				
				instances.forEach(instance -> {
					lineMap.get(instance).forEach(line -> line.toFront());
				});
			});
			content.add(combobox, 1, 2);
			
			popover.setContentNode(content);

			Bounds bounds = toolbarButton.localToScreen(toolbarButton.getBoundsInLocal());
			popover.show(canvas, bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight());
		});
		
		canvas.getToolBar().getItems().add(toolbarButton);
	}

	@Override
	public void onRemove(Canvas canvas) {
		colormap.unbind();
		colorAxis.unbind();
		visibilityAxis.unbind();
		selectedInstance.unbind();
		
		canvas.getToolBar().getItems().remove(toolbarButton);
	}

}
