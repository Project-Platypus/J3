package j3;

import j3.Subscene3D.MouseMode;
import j3.colormap.Colormap;
import j3.colormap.impl.RainbowColormap;
import j3.dataframe.DataFrame;
import j3.io.DataFrameReader;
import j3.io.DataFrameReaderFactory;
import j3.io.impl.CSVReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.control.SegmentedButton;
import org.controlsfx.property.BeanPropertyUtils;
import org.apache.commons.lang3.StringUtils;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.Transition;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.ParallelCamera;
import javafx.util.Duration;

public class GUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private ToolBar toolbar;

	private SubScene content;
	
	private DataFrame table;
	
	private Scatter scatter;
	
	private List<Axis> axes;
	
	private Colorbar colorbar;
	
	private double mousePosX, mousePosY;
	
	private double mouseOldX, mouseOldY;
	
	private EventHandler<MouseEvent> singleClickHandler;
	
	public void setSingleClickHandler(EventHandler<MouseEvent> singleClickHandler) {
		this.singleClickHandler = singleClickHandler;
		
		if (this.singleClickHandler != null) {
			getContentRoot().setCursor(Cursor.CROSSHAIR);
		}
	}
	
	public void invokeSingleClickHandler(MouseEvent event) {
		if (singleClickHandler != null) {
			singleClickHandler.handle(event);
			singleClickHandler = null;
			getContentRoot().setCursor(null);
		}
	}
	
	public Group getContentRoot() {
		return (Group)content.getRoot();
	}
	
	public DataFrame getTable() {
		return table;
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		root.setPrefSize(800, 600);

		Colormap colormap = new RainbowColormap();

		Subscene3D plot = new Subscene3D(400);
		
		axes = new ArrayList<Axis>();

		content = new SubScene(new Group(), plot.getWidth(), plot.getHeight());
		content.setManaged(false);

		Image rotateImage = new Image(GUI.class.getResourceAsStream("/j3/icons/rotate_1x.png"));
		Image translateImage = new Image(GUI.class.getResourceAsStream("/j3/icons/translate_1x.png"));
		Image scaleImage = new Image(GUI.class.getResourceAsStream("/j3/icons/scale_1x.png"));
		Image splitImage = new Image(GUI.class.getResourceAsStream("/j3/icons/split_1x.png"));
		Image projectImage = new Image(GUI.class.getResourceAsStream("/j3/icons/project_1x.png"));
		Image rotateLeftImage = new Image(GUI.class.getResourceAsStream("/j3/icons/rotate_left_1x.png"));
		Image rotateRightImage = new Image(GUI.class.getResourceAsStream("/j3/icons/rotate_right_1x.png"));
		Image legendImage = new Image(GUI.class.getResourceAsStream("/j3/icons/legend_1x.png"));
		Image cameraImage = new Image(GUI.class.getResourceAsStream("/j3/icons/camera_1x.png"));
		Image colorImage = new Image(GUI.class.getResourceAsStream("/j3/icons/color_1x.png"));
		Image plotOptionsImage = new Image(GUI.class.getResourceAsStream("/j3/icons/settings_1x.png"));
		Image commentImage = new Image(GUI.class.getResourceAsStream("/j3/icons/comment_1x.png"));
		Image fileImage = new Image(GUI.class.getResourceAsStream("/j3/icons/file_1x.png"));
		Image widgetImage = new Image(GUI.class.getResourceAsStream("/j3/icons/widgets_1x.png"));

		Button dummyData = new Button("Dummy Data");
		
		Button fileOpen = new Button();
		fileOpen.setGraphic(new ImageView(fileImage));
		fileOpen.setTooltip(new Tooltip("Open a file"));
		
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

		SegmentedButton mouseControls = new SegmentedButton(rotate, translate, scale);  
		mouseControls.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);

		ToggleButton split = new ToggleButton();
		split.setGraphic(new ImageView(splitImage));
		split.setTooltip(new Tooltip("Increase / decreate gap between axes"));

		ToggleButton project = new ToggleButton();
		project.setGraphic(new ImageView(projectImage));
		project.setTooltip(new Tooltip("Project the 3D view onto the 2D axes"));
		
		ToggleButton toggleLegend = new ToggleButton();
		toggleLegend.setGraphic(new ImageView(legendImage));
		toggleLegend.setTooltip(new Tooltip("Toggle legends"));

		SegmentedButton axisControls = new SegmentedButton(split, project, toggleLegend);
		axisControls.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
		axisControls.setToggleGroup(null);

		ToggleButton rotateLeft = new ToggleButton();
		rotateLeft.setGraphic(new ImageView(rotateLeftImage));
		rotateLeft.setTooltip(new Tooltip("Animate the 3D view by rotating to the left"));

		ToggleButton rotateRight = new ToggleButton();
		rotateRight.setGraphic(new ImageView(rotateRightImage));
		rotateRight.setTooltip(new Tooltip("Animate the 3D view by rotating to the right"));

		SegmentedButton animationControls = new SegmentedButton(rotateLeft, rotateRight);
		animationControls.getStyleClass().add(SegmentedButton.STYLE_CLASS_DARK);
		
		Button camera = new Button();
		camera.setGraphic(new ImageView(cameraImage));
		camera.setTooltip(new Tooltip("Save image of plot region"));
		
		ToggleButton editMode = new ToggleButton("Edit");
		
		Button changeColor = new Button();
		changeColor.setGraphic(new ImageView(colorImage));
		changeColor.setTooltip(new Tooltip("Change the colormap"));
		
		Button plotOptions = new Button();
		plotOptions.setGraphic(new ImageView(plotOptionsImage));
		plotOptions.setTooltip(new Tooltip("Change plot options"));
		
		Button widgets = new Button();
		widgets.setGraphic(new ImageView(widgetImage));
		widgets.setTooltip(new Tooltip("Add widgets to the plot"));

		ToggleButton commentOption = new ToggleButton();
		commentOption.setGraphic(new ImageView(commentImage));
		commentOption.setTooltip(new Tooltip("Click on a data point to create an annotation"));


		toolbar = new ToolBar(fileOpen, mouseControls, axisControls, animationControls, camera, changeColor, plotOptions, widgets, dummyData);

		dummyData.setOnAction(event -> {
			InputStream is = GUI.class.getResourceAsStream("cdice.txt");

			try {
				CSVReader reader = new CSVReader();
				table = reader.load(is);
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
			
			axes.clear();
			
			for (int i = 0; i < table.attributeCount(); i++) {
				if (Double.class.isAssignableFrom(table.getAttribute(i).getType())) {
					RealAxis axis = new RealAxis(i, table.getAttribute(i).getName());
					axis.scale(table.getValues(i));
					axes.add(axis);
				}
			}
			
			scatter = new Scatter(plot.getAxis3D(), table, axes.get(0), axes.get(1), axes.get(2), axes.get(3), null, colormap);
			
			colorbar.colormapProperty().bind(scatter.colormapProperty());
			colorbar.colorAxisProperty().bind(scatter.colorAxisProperty());
		});
		
		fileOpen.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			
			for (DataFrameReader reader : DataFrameReaderFactory.getInstance().getProviders()) {
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(reader.getDescription(),
						reader.getFileExtensions().stream().map(s -> "*." + s).collect(Collectors.toList()));
				fileChooser.getExtensionFilters().add(filter);
			}

			File selectedFile = fileChooser.showOpenDialog(primaryStage);
			
			if (selectedFile != null) {
				try {
					CSVReader reader = new CSVReader();
					table = reader.load(selectedFile);
					
					axes.clear();
					
					for (int i = 0; i < table.attributeCount(); i++) {
						if (Double.class.isAssignableFrom(table.getAttribute(i).getType())) {
							RealAxis axis = new RealAxis(i, table.getAttribute(i).getName());
							axis.scale(table.getValues(i));
							axes.add(axis);
						} else if (String.class.isAssignableFrom(table.getAttribute(i).getType())) {
							CategoryAxis axis = new CategoryAxis(i, table.getAttribute(i).getName());
							axis.scale(table.getValues(i));
							axes.add(axis);
						}
					}
					
					scatter = new Scatter(plot.getAxis3D(), table, axes.get(0), axes.get(1), axes.get(2), axes.get(3), null, colormap);
					
					colorbar.colormapProperty().bind(scatter.colormapProperty());
					colorbar.colorAxisProperty().bind(scatter.colorAxisProperty());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
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

		colorbar = new Colorbar(colormap, 500, 40, Orientation.HORIZONTAL, new EmptyAxis());
		Translate colorbarTranslate = new Translate();
		colorbar.getTransforms().addAll(colorbarTranslate);

		colorbar.setOnMousePressed(event -> {
			mouseOldX = event.getSceneX();
			mouseOldY = event.getSceneY();
		});
		
		colorbar.setOnMouseDragged(event -> {
			mousePosX = event.getSceneX();
			mousePosY = event.getSceneY();
			colorbarTranslate.setX(colorbarTranslate.getX() + (mousePosX - mouseOldX));
			colorbarTranslate.setY(colorbarTranslate.getY() + (mousePosY - mouseOldY));
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
		});
		
		toggleLegend.setOnAction(event -> {
			Group group = (Group)content.getRoot();
			
			if (toggleLegend.isSelected()) {
				colorbar.setOpacity(0.0);
				group.getChildren().add(colorbar);
				
				FadeTransition transition = new FadeTransition(new Duration(1000), colorbar);
				transition.setToValue(1.0);
				transition.play();
			} else {
				FadeTransition transition = new FadeTransition(new Duration(1000), colorbar);
				transition.setToValue(0.0);
				transition.setOnFinished(e -> {
					group.getChildren().remove(colorbar);
				});
				transition.play();
			}
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
		
		camera.setOnAction(event -> {
			SnapshotParameters params = new SnapshotParameters();
			params.setCamera(new ParallelCamera());
			WritableImage image = content.snapshot(params, null);
			
			FileChooser fileChooser = new FileChooser();

			for (String suffix : ImageIO.getWriterFileSuffixes()) {
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(StringUtils.upperCase(suffix) + " image", "*." + suffix);
				fileChooser.getExtensionFilters().add(filter);
				
				if (suffix.equalsIgnoreCase("png")) {
					fileChooser.setSelectedExtensionFilter(filter);
				}
			}
			
			if (fileChooser.getSelectedExtensionFilter() == null) {
				fileChooser.setSelectedExtensionFilter(fileChooser.getExtensionFilters().get(0));
			}
			
			LocalDateTime now = LocalDateTime.now();
			StringBuilder filename = new StringBuilder();
			filename.append("snapshot_");
			filename.append(String.format("%02d", now.getDayOfMonth()));
			filename.append(String.format("%02d", now.getMonthValue()));
			filename.append(String.format("%02d", now.getYear()));
			filename.append("_");
			filename.append(String.format("%02d", now.getHour()));
			filename.append(String.format("%02d", now.getMinute()));
			filename.append(String.format("%02d", now.getSecond()));
			filename.append(fileChooser.getSelectedExtensionFilter().getExtensions().get(0).substring(1));
			
			fileChooser.setInitialFileName(filename.toString());
			
			File selectedFile = fileChooser.showSaveDialog(primaryStage);
			
			if (selectedFile != null) {
				try {
					ImageIO.write(SwingFXUtils.fromFXImage(image, null),
							fileChooser.getSelectedExtensionFilter().getExtensions().get(0).substring(2),
							selectedFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		Pane editPane = new Pane();
		editPane.setPrefWidth(100);
		
		editMode.setOnAction(event -> {
			if (editMode.isSelected()) {
				root.setRight(editPane);
				
				plot.getAxis3D().setPickOnBounds(true);
				
				root.setOnMouseClicked(me -> {
					Node node = me.getPickResult().getIntersectedNode();
					PropertySheet sheet = new PropertySheet(BeanPropertyUtils.getProperties(node));
					
					ScrollPane scrollPane = new ScrollPane();
					scrollPane.setContent(sheet);
					
					editPane.getChildren().add(scrollPane);
					sheet.prefHeightProperty().bind(editPane.heightProperty());
					editPane.prefWidthProperty().bind(sheet.prefWidthProperty());
				});
			} else {
				root.setRight(null);
				root.setOnMouseClicked(null);
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
				scatter.setColormap(newValue);
				popover.hide();
			});
			
			Bounds bounds = changeColor.localToScreen(changeColor.getBoundsInLocal());
			popover.show(plot, bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight());
		});
		
		plotOptions.setOnAction(event -> {
			if (scatter != null) {
				PopOver popover = new PopOver();
				popover.setTitle("Plot options");
				popover.setAutoHide(true);
				popover.setAutoFix(true);
				popover.setArrowLocation(ArrowLocation.TOP_CENTER);
				
				PlottingOptions plottingOptions = new PlottingOptions(scatter, axes);
				popover.setContentNode(plottingOptions);
	
				Bounds bounds = plotOptions.localToScreen(plotOptions.getBoundsInLocal());
				popover.show(plot, bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight());
			}
		});
		
		widgets.setOnAction(event -> {
			PopOver popover = new PopOver();
			popover.setTitle("Widget options");
			popover.setAutoHide(true);
			popover.setAutoFix(true);
			popover.setArrowLocation(ArrowLocation.TOP_CENTER);
			
			WidgetOptions widgetOptions = new WidgetOptions(this, popover);
			popover.setContentNode(widgetOptions);

			Bounds bounds = widgets.localToScreen(widgets.getBoundsInLocal());
			popover.show(plot, bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight());
		});
		
		StackPane pane = new StackPane();
		pane.getChildren().add(content);
		((Group)content.getRoot()).getChildren().add(plot);

		root.setTop(toolbar);
		root.setCenter(pane);


		content.setOnMouseClicked(event -> {
			System.out.println("content Clicked");
			invokeSingleClickHandler(event);
		});

		
		Scene scene = new Scene(root);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Java High Dimensional Visualization");
		primaryStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/j3/icons/appicon.png")));
		primaryStage.show();
		
		content.widthProperty().addListener((observer, oldValue, newValue) -> {
			double fracX = (colorbarTranslate.getX() + colorbar.getWidth()/2.0) / oldValue.doubleValue();
			double newX = (fracX * newValue.doubleValue()) - colorbar.getWidth()/2.0;

			colorbarTranslate.setX(newX);
		});
		
		content.heightProperty().addListener((observer, oldValue, newValue) -> {
			double fracY = (colorbarTranslate.getY() + colorbar.getHeight()/2.0) / oldValue.doubleValue();
			double newY = (fracY * newValue.doubleValue()) - colorbar.getHeight()/2.0;

			colorbarTranslate.setY(newY);
		});
		
		content.widthProperty().bind(pane.widthProperty());
		content.heightProperty().bind(pane.heightProperty());
		plot.widthProperty().bind(content.widthProperty());
		plot.heightProperty().bind(content.heightProperty());
		
		scene.getStylesheets().add(
				GUI.class.getResource("j3.css").toExternalForm());
	}

}
