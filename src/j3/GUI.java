package j3;

import j3.colormap.impl.HSVColormap;
import j3.dataframe.Attribute;
import j3.dataframe.DataFrame;
import j3.io.DataFrameReader;
import j3.io.DataFrameReaderFactory;
import j3.widget.impl.intro.IntroWidget;
import j3.widget.impl.scatter.ScatterPlot;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.ParallelCamera;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.controlsfx.control.PopOver;
import org.controlsfx.control.PopOver.ArrowLocation;

public class GUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private ToolBar toolbar;
	
	private Canvas canvas;

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 800, 600);

		toolbar = new ToolBar();
		
		canvas = new Canvas(400, 400, toolbar);
		
		canvas.getPropertyRegistry().put("data", null);
		canvas.getPropertyRegistry().put("axes", null);
		canvas.getPropertyRegistry().put("colormap", new HSVColormap());
		canvas.getPropertyRegistry().put("xAxis", null, "X");
		canvas.getPropertyRegistry().put("yAxis", null, "Y");
		canvas.getPropertyRegistry().put("zAxis", null, "Z");
		canvas.getPropertyRegistry().put("colorAxis", null, "Color");
		canvas.getPropertyRegistry().put("sizeAxis", null, "Size");
		canvas.getPropertyRegistry().put("visibilityAxis", null, "Visibility");
		canvas.getPropertyRegistry().put("selectedInstance", null);
		canvas.getPropertyRegistry().put("theme", null);
		
		Image cameraImage = new Image(GUI.class.getResourceAsStream("/j3/icons/camera_1x.png"));
		Image fileImage = new Image(GUI.class.getResourceAsStream("/j3/icons/file_1x.png"));
		Image widgetImage = new Image(GUI.class.getResourceAsStream("/j3/icons/widgets_1x.png"));

		Button fileOpen = new Button();
		fileOpen.setGraphic(new ImageView(fileImage));
		fileOpen.setTooltip(new Tooltip("Open a file"));
		
		Button camera = new Button();
		camera.setGraphic(new ImageView(cameraImage));
		camera.setTooltip(new Tooltip("Save image of plot region"));
		
		Button widgets = new Button();
		widgets.setGraphic(new ImageView(widgetImage));
		widgets.setTooltip(new Tooltip("Add widgets to the plot"));

		toolbar.getItems().addAll(fileOpen, camera, widgets);
		
		fileOpen.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			
			File initialFile = new File("data/");
			
			if (initialFile.exists() && initialFile.isDirectory()) {
				fileChooser.setInitialDirectory(new File("data/"));
			}
			
			List<DataFrameReader> readers = DataFrameReaderFactory.getInstance().getProviders();
			
			for (DataFrameReader reader : readers) {
				FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter(reader.getDescription(),
						reader.getFileExtensions().stream().map(s -> "*." + s).collect(Collectors.toList()));
				fileChooser.getExtensionFilters().add(filter);
			}

			File selectedFile = fileChooser.showOpenDialog(primaryStage);
			
			if (selectedFile != null) {
				try {
					canvas.removeAll();
					
					DataFrameReader selectedReader = readers.get(fileChooser.getExtensionFilters().indexOf(
							fileChooser.getSelectedExtensionFilter()));

					DataFrame table = selectedReader.load(selectedFile);
					
					canvas.getPropertyRegistry().get("xAxis").setValue(null);
					canvas.getPropertyRegistry().get("yAxis").setValue(null);
					canvas.getPropertyRegistry().get("zAxis").setValue(null);
					canvas.getPropertyRegistry().get("colorAxis").setValue(null);
					canvas.getPropertyRegistry().get("sizeAxis").setValue(null);
					canvas.getPropertyRegistry().get("visibilityAxis").setValue(null);
					
					List<Axis> axes = new ArrayList<Axis>();
					
					for (int i = 0; i < table.attributeCount(); i++) {
						Attribute<?> attribute = table.getAttribute(i);

						if (!attribute.getName().isEmpty()) {
							if (Double.class.isAssignableFrom(attribute.getType())) {
								RealAxis axis = new RealAxis(attribute);
								axis.scale(table.getValues(i));
								axes.add(axis);
							} else if (String.class.isAssignableFrom(attribute.getType())) {
								CategoryAxis axis = new CategoryAxis(attribute);
								axis.scale(table.getValues(i));
								axes.add(axis);
							}
						}
					}
					
					canvas.getPropertyRegistry().get("data").setValue(table);
					canvas.getPropertyRegistry().get("xAxis").setValue(axes.size() > 0 ? axes.get(0) : null);
					canvas.getPropertyRegistry().get("yAxis").setValue(axes.size() > 1 ? axes.get(1) : null);
					canvas.getPropertyRegistry().get("zAxis").setValue(axes.size() > 2 ? axes.get(2) : null);
					canvas.getPropertyRegistry().get("colorAxis").setValue(axes.size() > 3 ? axes.get(3) : null);
					canvas.getPropertyRegistry().get("sizeAxis").setValue(axes.size() > 4 ? axes.get(4) : null);
					canvas.getPropertyRegistry().get("visibilityAxis").setValue(null);
					canvas.getPropertyRegistry().get("axes").setValue(axes);
					
					canvas.add(new ScatterPlot());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		camera.setOnAction(event -> {
			double scale = 2.0;
			WritableImage image = new WritableImage((int)Math.rint(scale*canvas.getWidth()), (int)Math.rint(scale*canvas.getHeight()));
			SnapshotParameters params = new SnapshotParameters();
			params.setCamera(new ParallelCamera());
			params.setTransform(Transform.scale(2.0, 2.0));
			
			image = canvas.snapshot(params, image);
			
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
		
//		Pane editPane = new Pane();
//		editPane.setPrefWidth(100);
//		
//		editMode.setOnAction(event -> {
//			if (editMode.isSelected()) {
//				root.setRight(editPane);
//				
////				plot.getAxis3D().setPickOnBounds(true);
//				
//				root.setOnMouseClicked(me -> {
//					Node node = me.getPickResult().getIntersectedNode();
//					PropertySheet sheet = new PropertySheet(BeanPropertyUtils.getProperties(node));
//					
//					ScrollPane scrollPane = new ScrollPane();
//					scrollPane.setContent(sheet);
//					
//					editPane.getChildren().add(scrollPane);
//					sheet.prefHeightProperty().bind(editPane.heightProperty());
//					editPane.prefWidthProperty().bind(sheet.prefWidthProperty());
//				});
//			} else {
//				root.setRight(null);
//				root.setOnMouseClicked(null);
//			}
//		});
		
		widgets.setOnAction(event -> {
			PopOver popover = new PopOver();
			popover.setTitle("Widget options");
			popover.setAutoHide(true);
			popover.setAutoFix(true);
			popover.setArrowLocation(ArrowLocation.TOP_CENTER);
			
			WidgetOptions widgetOptions = new WidgetOptions(canvas, popover);
			popover.setContentNode(widgetOptions);

			Bounds bounds = widgets.localToScreen(widgets.getBoundsInLocal());
			popover.show(canvas, bounds.getMinX() + bounds.getWidth()/2, bounds.getMinY() + bounds.getHeight());
		});
		
		Pane pane = new Pane();
		pane.getChildren().add(canvas);

		root.setTop(toolbar);
		root.setCenter(pane);

		primaryStage.setScene(scene);
		primaryStage.setTitle("Java High Dimensional Visualization");
		primaryStage.getIcons().add(new Image(GUI.class.getResourceAsStream("/j3/icons/appicon.png")));
		primaryStage.setMaximized(true);
		primaryStage.show();
		
		canvas.widthProperty().bind(pane.widthProperty());
		canvas.heightProperty().bind(pane.heightProperty());
		
		canvas.getPropertyRegistry().get("theme").addListener((observable, oldValue, newValue) -> {
			scene.getStylesheets().setAll(newValue == null || ((String)newValue).equalsIgnoreCase("Light") ?
					GUI.class.getResource("j3.css").toExternalForm() :
					GUI.class.getResource("j3-dark.css").toExternalForm());
		});
		
		canvas.getPropertyRegistry().get("theme").set("Light");
		
		// display the intro widget if this is the first time
		if (IntroWidget.shouldShow()) {
			canvas.add(new IntroWidget());
		}
	}

}
