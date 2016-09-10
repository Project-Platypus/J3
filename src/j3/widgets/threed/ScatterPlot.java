package j3.widgets.threed;

import j3.Axis;
import j3.Canvas;
import j3.ColormapSelector;
import j3.EmptyAxis;
import j3.GUI;
import j3.colormap.Colormap;
import j3.dataframe.DataFrame;
import j3.widgets.Colorbar;
import j3.widgets.Widget;
import j3.widgets.threed.Subscene3D.MouseMode;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.ParallelCamera;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.SegmentedButton;

public class ScatterPlot implements Widget<Subscene3D> {
	
	private Subscene3D plot;
	
	private ScatterPoints scatter;
	
	private SegmentedButton mouseControls;
	
	private SegmentedButton axisControls;
	
	private SegmentedButton animationControls;
	
	private Button plotOptions;
	
	private Button changeColor;
	
	public ScatterPlot() {
		super();
	}

	@Override
	public Subscene3D getNode() {
		return plot;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Canvas canvas) {
		DataFrame table = (DataFrame)canvas.getSharedData().get("data").getValue();
		
		plot = new Subscene3D(400);
		scatter = new ScatterPoints(plot.getAxis3D(), table);
		
		ObjectProperty<Axis> xAxis = (ObjectProperty<Axis>)canvas.getSharedData().get("xAxis");
		ObjectProperty<Axis> yAxis = (ObjectProperty<Axis>)canvas.getSharedData().get("yAxis");
		ObjectProperty<Axis> zAxis = (ObjectProperty<Axis>)canvas.getSharedData().get("zAxis");
		ObjectProperty<Axis> colorAxis = (ObjectProperty<Axis>)canvas.getSharedData().get("colorAxis");
		ObjectProperty<Axis> sizeAxis = (ObjectProperty<Axis>)canvas.getSharedData().get("sizeAxis");
		ObjectProperty<Axis> visibilityAxis = (ObjectProperty<Axis>)canvas.getSharedData().get("visibilityAxis");
		ObjectProperty<Colormap> colormap = (ObjectProperty<Colormap>)canvas.getSharedData().get("colormap");
		
		scatter.xAxisProperty().bind(xAxis);
		scatter.yAxisProperty().bind(yAxis);
		scatter.zAxisProperty().bind(zAxis);
		scatter.colorAxisProperty().bind(colorAxis);
		scatter.sizeAxisProperty().bind(sizeAxis);
		scatter.visibilityAxisProperty().bind(visibilityAxis);
		scatter.colormapProperty().bind(colormap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onAdd(Canvas canvas) {
		plot.widthProperty().bind(canvas.widthProperty());
		plot.heightProperty().bind(canvas.heightProperty());
		
		plot.toBack();
		
		if (canvas.getToolBar() != null) {
			Image rotateImage = new Image(GUI.class.getResourceAsStream("/j3/icons/rotate_1x.png"));
			Image translateImage = new Image(GUI.class.getResourceAsStream("/j3/icons/translate_1x.png"));
			Image scaleImage = new Image(GUI.class.getResourceAsStream("/j3/icons/scale_1x.png"));
			Image splitImage = new Image(GUI.class.getResourceAsStream("/j3/icons/split_1x.png"));
			Image projectImage = new Image(GUI.class.getResourceAsStream("/j3/icons/project_1x.png"));
			Image legendImage = new Image(GUI.class.getResourceAsStream("/j3/icons/legend_1x.png"));
			Image rotateLeftImage = new Image(GUI.class.getResourceAsStream("/j3/icons/rotate_left_1x.png"));
			Image rotateRightImage = new Image(GUI.class.getResourceAsStream("/j3/icons/rotate_right_1x.png"));
			Image plotOptionsImage = new Image(GUI.class.getResourceAsStream("/j3/icons/settings_1x.png"));
			Image colorImage = new Image(GUI.class.getResourceAsStream("/j3/icons/color_1x.png"));
			
			ToggleButton rotate = new ToggleButton();
			rotate.setGraphic(new ImageView(rotateImage));
			rotate.setSelected(true);
			rotate.setTooltip(new Tooltip("Rotate the 3D axis using the mouse"));

			ToggleButton translate = new ToggleButton();
			translate.setGraphic(new ImageView(translateImage));
			translate.setTooltip(new Tooltip("Translate the 3D axis using the mouse"));

			ToggleButton scale = new ToggleButton();
			scale.setGraphic(new ImageView(scaleImage));
			scale.setTooltip(new Tooltip("Scale the 3D axis using the mouse"));
			
			ToggleButton split = new ToggleButton();
			split.setGraphic(new ImageView(splitImage));
			split.setTooltip(new Tooltip("Increase / decreate gap between axes"));

			ToggleButton project = new ToggleButton();
			project.setGraphic(new ImageView(projectImage));
			project.setTooltip(new Tooltip("Project the 3D view onto the 2D axes"));
			
			ToggleButton toggleLegend = new ToggleButton();
			toggleLegend.setGraphic(new ImageView(legendImage));
			toggleLegend.setTooltip(new Tooltip("Toggle legends"));
			
			ToggleButton rotateLeft = new ToggleButton();
			rotateLeft.setGraphic(new ImageView(rotateLeftImage));
			rotateLeft.setTooltip(new Tooltip("Animate the 3D view by rotating to the left"));

			ToggleButton rotateRight = new ToggleButton();
			rotateRight.setGraphic(new ImageView(rotateRightImage));
			rotateRight.setTooltip(new Tooltip("Animate the 3D view by rotating to the right"));
			
			plotOptions = new Button();
			plotOptions.setGraphic(new ImageView(plotOptionsImage));
			plotOptions.setTooltip(new Tooltip("Change plot options"));
			
			changeColor = new Button();
			changeColor.setGraphic(new ImageView(colorImage));
			changeColor.setTooltip(new Tooltip("Change the colormap"));
			
			rotate.setOnAction(event -> {
				plot.setMouseMode(rotate.isSelected() ? MouseMode.ROTATE : MouseMode.NONE);
			});
	
			translate.setOnAction(event -> {
				plot.setMouseMode(translate.isSelected() ? MouseMode.TRANSLATE : MouseMode.NONE);
			});
	
			scale.setOnAction(event -> {
				plot.setMouseMode(scale.isSelected() ? MouseMode.SCALE : MouseMode.NONE);
			});
			
			split.setOnAction(event -> {
				plot.getAxis3D().setSideGap(split.isSelected() ? 0.2 : 0.0);
			});
			
			List<ImageView> projectedImages = new ArrayList<ImageView>();

			project.setOnAction(event -> {
				if (project.isSelected()) {
					// check if we are already projecting images...which may occur
					// if the animation is still running
					if (!projectedImages.isEmpty()) {
						project.setSelected(false);
						return;
					}

					for (int i = 0; i < 6; i++) {
						SnapshotParameters params = new SnapshotParameters();
						params.setCamera(new ParallelCamera());
						params.setFill(Color.TRANSPARENT);

						if (i == 0) {
							//params.setTransform(new Affine());
						} else if (i == 1) {
							params.setTransform(new Rotate(-90, Rotate.X_AXIS));
						} else if (i == 2) {
							params.setTransform(new Rotate(-90, Rotate.Y_AXIS));
						} else if (i == 3) {
							params.setTransform(new Rotate(-90, Rotate.Y_AXIS));
						} else if (i == 4) {
							params.setTransform(new Rotate(-90, Rotate.X_AXIS));
						} else if (i == 5) {
							//params.setTransform(new Affine());
						}

						WritableImage image = plot.getAxis3D().getPlotContents().snapshot(params, null);
						ImageView imageView = new ImageView(image);

						imageView.setOpacity(0.0);

						plot.getAxis3D().getSide(i).getChildren().add(imageView);
						projectedImages.add(imageView);
					}

					ParallelTransition pt = new ParallelTransition();

					for (int i = 0; i < 6; i++) {
						FadeTransition ft = new FadeTransition(new Duration(1000), projectedImages.get(i));
						ft.setFromValue(0.0);
						ft.setToValue(1.0);
						pt.getChildren().add(ft);
					}

					pt.play();
				} else {
					ParallelTransition pt = new ParallelTransition();

					for (int i = 0; i < 6; i++) {
						FadeTransition ft = new FadeTransition(new Duration(1000), projectedImages.get(i));
						ft.setFromValue(1.0);
						ft.setToValue(0.0);
						pt.getChildren().add(ft);
					}

					pt.setOnFinished(ae -> {
						for (int i = 0; i < 6; i++) {
							plot.getAxis3D().getSide(i).getChildren().remove(projectedImages.get(i));
						}

						projectedImages.clear();
					});

					pt.play();
				}
			});
			
			Colorbar colorbar = new Colorbar(500, 40, Orientation.HORIZONTAL, new EmptyAxis());

			toggleLegend.setOnAction(event -> {
				if (toggleLegend.isSelected()) {				
					canvas.add(colorbar);
				} else {
					canvas.remove(colorbar);
				}
			});
			
			Transition rotateLeftTransition = new Transition() {

				private double start = 0.0;

				{
					setCycleDuration(new Duration(20000));
					setCycleCount(Transition.INDEFINITE);
					setInterpolator(Interpolator.LINEAR);
				}

				@Override
				protected void interpolate(double frac) {
					double value = start + 360*frac;

					if (value > 360) {
						value -= 360;
					}

					plot.rotateY.setAngle(value);
				}

				@Override
				public void play() {
					start = plot.rotateY.getAngle();
					super.play();
				}

			};

			Transition rotateRightTransition = new Transition() {

				private double start = 0.0;

				{
					setCycleDuration(new Duration(20000));
					setCycleCount(Transition.INDEFINITE);
					setInterpolator(Interpolator.LINEAR);
				}

				@Override
				protected void interpolate(double frac) {
					double value = start - 360*frac;

					if (value < 0) {
						value += 360;
					}

					plot.rotateY.setAngle(value);
				}

				@Override
				public void play() {
					start = plot.rotateY.getAngle();
					super.play();
				}

			};

			rotateLeft.setOnAction(event -> {
				if (rotateLeft.isSelected()) {
					rotateLeftTransition.play();
				} else {
					rotateLeftTransition.stop();
				}
			});

			rotateRight.setOnAction(event -> {
				if (rotateRight.isSelected()) {
					rotateRightTransition.play();
				} else {
					rotateRightTransition.stop();
				}
			});
			
			changeColor.setOnAction(event -> {
				PopOver popover = new PopOver();
				popover.setTitle("Select the new colormap");
				popover.setAutoHide(true);
				popover.setAutoFix(true);
				popover.setArrowLocation(ArrowLocation.TOP_CENTER);
				
				ColormapSelector colormapSelector = new ColormapSelector();
				popover.setContentNode(colormapSelector);
				
				colormapSelector.colormapProperty().addListener((observable, oldValue, newValue) -> {
					((ObjectProperty<Colormap>)canvas.getSharedData().get("colormap")).set(newValue);;
					popover.hide();
				});
				
				Bounds bounds = changeColor.localToScreen(changeColor.getBoundsInLocal());
				popover.show(canvas, bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight());
			});
			
			plotOptions.setOnAction(event -> {
				PopOver popover = new PopOver();
				popover.setTitle("Plot options");
				popover.setAutoHide(true);
				popover.setAutoFix(true);
				popover.setArrowLocation(ArrowLocation.TOP_CENTER);

				PlottingOptions plottingOptions = new PlottingOptions(canvas);
				popover.setContentNode(plottingOptions);

				Bounds bounds = plotOptions.localToScreen(plotOptions.getBoundsInLocal());
				popover.show(plot, bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight());
			});
			
			mouseControls = new SegmentedButton(rotate, translate, scale);  
			mouseControls.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
			
			axisControls = new SegmentedButton(split, project, toggleLegend);
			axisControls.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
			axisControls.setToggleGroup(null);
			
			animationControls = new SegmentedButton(rotateLeft, rotateRight);
			animationControls.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
			
			canvas.getToolBar().getItems().addAll(mouseControls, axisControls, animationControls, changeColor, plotOptions);
		}
	}

	@Override
	public void onRemove(Canvas canvas) {
		plot.widthProperty().unbind();
		plot.heightProperty().unbind();
		
		if (canvas.getToolBar() != null) {
			canvas.getToolBar().getItems().removeAll(mouseControls, axisControls, animationControls, changeColor, plotOptions);
		}
		
		scatter.xAxisProperty().unbind();
		scatter.yAxisProperty().unbind();
		scatter.zAxisProperty().unbind();
		scatter.colorAxisProperty().unbind();
		scatter.sizeAxisProperty().unbind();
		scatter.visibilityAxisProperty().unbind();
		scatter.colormapProperty().unbind();
	}
	
	public void update() {
		((ScatterPoints)plot.getAxis3D().getPlotContents()).update();
	}

}
