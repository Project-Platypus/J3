package j3.widget.impl.scatter;

import j3.Axis;
import j3.Canvas;
import j3.ColormapSelector;
import j3.GUI;
import j3.colormap.Colormap;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.widget.TargetableWidget;
import j3.widget.SerializableWidget;
import j3.widget.Widget;
import j3.widget.impl.scatter.Subscene3D.MouseMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.BooleanPropertyBase;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.SegmentedButton;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class ScatterPlot implements Widget<Subscene3D>, SerializableWidget, TargetableWidget {
	
	private Subscene3D plot;
	
	private ScatterPoints scatter;
	
	private SegmentedButton mouseControls;
	
	private SegmentedButton axisControls;
	
	private SegmentedButton animationControls;
	
	private Button plotOptions;
	
	private Button changeColor;
	
	private DoubleProperty rotationSpeed = new DoublePropertyBase(2.0) {

		@Override
		public Object getBean() {
			return ScatterPlot.class;
		}

		@Override
		public String getName() {
			return "rotationSpeed";
		}

	};

	public void setRotationSpeed(double speed) {
		rotationSpeed.set(speed);
	}

	public double getRotationSpeed() {
		return rotationSpeed.get();
	}

	public DoubleProperty rotationSpeedProperty() {
		return rotationSpeed;
	}
	
	private BooleanProperty rotationAxis = new BooleanPropertyBase(false) {

		@Override
		public Object getBean() {
			return ScatterPlot.class;
		}

		@Override
		public String getName() {
			return "rotationAxis";
		}

	};

	public void setRotationAxis(boolean axis) {
		rotationAxis.set(axis);
	}

	public boolean getRotationAxis() {
		return rotationAxis.get();
	}

	public BooleanProperty rotationAxisProperty() {
		return rotationAxis;
	}
	
	private ObservableList<Widget<?>> dependencies = FXCollections.observableArrayList();
	
	public ScatterPlot() {
		super();
	}

	@Override
	public Subscene3D getNode() {
		return plot;
	}

	@Override
	public void initialize(Canvas canvas) {
		DataFrame table = (DataFrame)canvas.getPropertyRegistry().get("data").getValue();
		
		plot = new Subscene3D(400);
		scatter = new ScatterPoints(plot.getAxis3D(), table);
		
		ObjectProperty<Axis> xAxis = canvas.getPropertyRegistry().get("xAxis");
		ObjectProperty<Axis> yAxis = canvas.getPropertyRegistry().get("yAxis");
		ObjectProperty<Axis> zAxis = canvas.getPropertyRegistry().get("zAxis");
		ObjectProperty<Axis> colorAxis = canvas.getPropertyRegistry().get("colorAxis");
		ObjectProperty<Axis> sizeAxis = canvas.getPropertyRegistry().get("sizeAxis");
		ObjectProperty<Axis> visibilityAxis = canvas.getPropertyRegistry().get("visibilityAxis");
		ObjectProperty<Colormap> colormap = canvas.getPropertyRegistry().get("colormap");
		ObjectProperty<Instance> selectedInstance = canvas.getPropertyRegistry().get("selectedInstance");
		
		scatter.xAxisProperty().bind(xAxis);
		scatter.yAxisProperty().bind(yAxis);
		scatter.zAxisProperty().bind(zAxis);
		scatter.colorAxisProperty().bind(colorAxis);
		scatter.sizeAxisProperty().bind(sizeAxis);
		scatter.visibilityAxisProperty().bind(visibilityAxis);
		scatter.colormapProperty().bind(colormap);
		scatter.selectedInstanceProperty().bindBidirectional(selectedInstance);
	}

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
			
			Transition rotateLeftTransition = new Transition() {

				{
					setCycleDuration(new Duration(60000));
					setCycleCount(Transition.INDEFINITE);
					setInterpolator(Interpolator.LINEAR);
				}

				@Override
				protected void interpolate(double frac) {
					Rotate rotate = getRotationAxis() ? plot.rotateX : plot.rotateY;
					double value = rotate.getAngle();
					
					value += getRotationSpeed()*360 / 2000;

					if (value > 360) {
						value -= 360;
					}

					rotate.setAngle(value);
				}

				@Override
				public void play() {
					super.play();
				}

			};

			Transition rotateRightTransition = new Transition() {

				{
					setCycleDuration(new Duration(60000));
					setCycleCount(Transition.INDEFINITE);
					setInterpolator(Interpolator.LINEAR);
				}

				@Override
				protected void interpolate(double frac) {
					Rotate rotate = getRotationAxis() ? plot.rotateX : plot.rotateY;
					double value = rotate.getAngle();
					
					value -= getRotationSpeed()*360 / 2000;

					if (value < 0) {
						value += 360;
					}

					rotate.setAngle(value);
				}

				@Override
				public void play() {
					super.play();
				}

			};
			
			EventHandler<? super MouseEvent> rotateOptionsHandler = event -> {
				if (event.getButton() == MouseButton.SECONDARY) {
					PopOver popover = new PopOver();
					popover.setTitle("Rotation Options");
					popover.setAutoHide(true);
					popover.setAutoFix(true);
					popover.setArrowLocation(ArrowLocation.TOP_CENTER);
					
					RotationOptions rotationOptions = new RotationOptions(this);
					popover.setContentNode(rotationOptions);

					ToggleButton button = (ToggleButton)event.getSource();
					Bounds bounds = button.localToScreen(button.getBoundsInLocal());
					popover.show(plot, bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight());
				}
			};
			
			rotateLeft.setOnMouseClicked(rotateOptionsHandler);
			rotateRight.setOnMouseClicked(rotateOptionsHandler);

			rotateLeft.setOnAction(event -> {
				if (rotateLeft.isSelected()) {
					rotateRightTransition.stop();
					rotateLeftTransition.play();
				} else {
					rotateLeftTransition.stop();
				}
			});

			rotateRight.setOnAction(event -> {
				if (rotateRight.isSelected()) {
					rotateLeftTransition.stop();
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
					canvas.getPropertyRegistry().get("colormap").setValue(newValue);;
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
			
			axisControls = new SegmentedButton(split, project);
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
		scatter.selectedInstanceProperty().unbind();
	}
	
	public void update() {
		((ScatterPoints)plot.getAxis3D().getPlotContents()).update();
	}

	@Override
	public Element saveState(Canvas canvas) {
		Element element = DocumentHelper.createElement("scatter3d");
		
		Element scale = element.addElement("scale");
		scale.addAttribute("x", Double.toString(plot.scale.getX()));
		scale.addAttribute("y", Double.toString(plot.scale.getY()));
		scale.addAttribute("z", Double.toString(plot.scale.getZ()));
		
		Element translate = element.addElement("translate");
		translate.addAttribute("x", Double.toString(plot.translate.getX()));
		translate.addAttribute("y", Double.toString(plot.translate.getY()));
		translate.addAttribute("z", Double.toString(plot.translate.getZ()));
		
		Element rotate = element.addElement("rotate");
		rotate.addAttribute("x", Double.toString(plot.rotateX.getAngle()));
		rotate.addAttribute("y", Double.toString(plot.rotateY.getAngle()));
		
		// store the node ids
		ScatterPoints points = (ScatterPoints)plot.getAxis3D().getPlotContents();
		Element mapping = element.addElement("mapping");
		
		for (Node node : points.getPoints()) {
			Instance instance = (Instance)node.getUserData();
			
			Element map = mapping.addElement("map");
			map.addAttribute("instanceId", instance.getId().toString());
			map.addAttribute("nodeId", node.getId());
		}
		
		return element;
	}

	@Override
	public void restoreState(Element element, Canvas canvas) {
		Element scale = element.element("scale");
		plot.scale.setX(Double.parseDouble(scale.attributeValue("x")));
		plot.scale.setY(Double.parseDouble(scale.attributeValue("y")));
		plot.scale.setZ(Double.parseDouble(scale.attributeValue("z")));
		
		Element translate = element.element("translate");
		plot.translate.setX(Double.parseDouble(translate.attributeValue("x")));
		plot.translate.setY(Double.parseDouble(translate.attributeValue("y")));
		plot.translate.setZ(Double.parseDouble(translate.attributeValue("z")));
		
		Element rotate = element.element("rotate");
		plot.rotateX.setAngle(Double.parseDouble(rotate.attributeValue("x")));
		plot.rotateY.setAngle(Double.parseDouble(rotate.attributeValue("y")));
		
		// update the node ids
		Map<UUID, UUID> cache = new HashMap<UUID, UUID>();
		Element mapping = element.element("mapping");
		
		for (Object obj : mapping.elements("map")) {
			Element map = (Element)obj;
			cache.put(UUID.fromString(map.attributeValue("instanceId")),
					UUID.fromString(map.attributeValue("nodeId")));
		}
		
		ScatterPoints points = (ScatterPoints)plot.getAxis3D().getPlotContents();

		for (Node node : points.getPoints()) {
			Instance instance = (Instance)node.getUserData();
			node.setId(cache.get(instance.getId()).toString());
		}
	}

	@Override
	public ObservableList<Widget<?>> getDependencies() {
		return dependencies;
	}

}
