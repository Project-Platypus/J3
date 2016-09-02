package j3;

import java.io.InputStream;

import com.github.lwhite1.tablesaw.api.ColumnType;
import com.github.lwhite1.tablesaw.api.Table;

import javafx.animation.Transition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.ParallelCamera;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.geometry.Point2D;

public class TestAxis extends Application {

	private double mousePosX, mousePosY;
	private double mouseOldX, mouseOldY;
	private final Rotate rotateX = new Rotate(20, Rotate.X_AXIS);
	private final Rotate rotateY = new Rotate(-45, Rotate.Y_AXIS);
	private final Translate translate = new Translate(0, 0, 0);
	private final Scale scale = new Scale(0.5, 0.5, 0.5);

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {

		Scene scene = new Scene(new Group(), 400, 400, true, SceneAntialiasing.BALANCED);

		scene.getStylesheets().add(
				TestAxis.class.getResource("j3.css").toExternalForm());

		scene.setCamera(new PerspectiveCamera());
		
		scene.setOnMousePressed(me -> {
			mouseOldX = me.getScreenX();
			mouseOldY = me.getScreenY();
		});


				scene.setOnMouseDragged(me -> {
		            mousePosX = me.getSceneX();
		            mousePosY = me.getSceneY();
		            rotateX.setAngle(rotateX.getAngle() - (mousePosY - mouseOldY));
		            rotateY.setAngle(rotateY.getAngle() + (mousePosX - mouseOldX));
		            mouseOldX = mousePosX;
		            mouseOldY = mousePosY;
		            
		            //updateAxes();
		        });
				
		Group root = (Group)scene.getRoot();
		Group textGroup = new Group();
		
		translate.setX(200);
		translate.setY(200);

		

		Axis3D axis = new Axis3D(400, textGroup);
		root.getChildren().addAll(axis, textGroup);
		axis.getTransforms().addAll(translate, rotateX, rotateY, scale);

		InputStream is = TestAxis.class.getResourceAsStream("cdice.txt");

		Table table = Table.createFromStream(
				new ColumnType[] { ColumnType.FLOAT, ColumnType.FLOAT, ColumnType.FLOAT, ColumnType.FLOAT },
				true, ',', is, "CDICE");

		new Scatter(axis, table);

		scene.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				double delta = 1.2;

				double scaleValue = scale.getX();

				if (event.getDeltaY() < 0) {
					scaleValue /= delta;
				} else {
					scaleValue *= delta;
				}

				if (scaleValue < 0.001) {
					scaleValue = 0.001;
				} else if (scaleValue > 45) {
					scaleValue = 45;
				}
				
				System.out.println(scaleValue);

				scale.setX(scaleValue);
				scale.setY(scaleValue);
				scale.setZ(scaleValue);

				//updateAxes();
				event.consume();

			}

		});

		scene.widthProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				translate.setX(scene.getWidth()/2.0);

				if (scene.getWidth() < scene.getHeight()) {
					double scaleValue = scale.getX() * (newValue.doubleValue() / oldValue.doubleValue());
					scale.setX(scaleValue);
					scale.setY(scaleValue);
					scale.setZ(scaleValue);
				}

				//updateAxes();
			}

		});

		scene.heightProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				translate.setY(scene.getHeight()/2.0);

				if (scene.getHeight() < scene.getWidth()) {
					double scaleValue = scale.getX() * (newValue.doubleValue() / oldValue.doubleValue());
					scale.setX(scaleValue);
					scale.setY(scaleValue);
					scale.setZ(scaleValue);
				}

				//updateAxes();
			}

		});
		
		for (int i = 0; i < 6; i++) {
				SnapshotParameters params = new SnapshotParameters();
				params.setCamera(new ParallelCamera());
				params.setFill(Color.TRANSPARENT);
				
				if (i == 0) {
					params.setTransform(new Affine());
				} else if (i == 1) {
					params.setTransform(new Rotate(-90, Rotate.X_AXIS));
				} else if (i == 2) {
					params.setTransform(new Rotate(-90, Rotate.Y_AXIS));
				} else if (i == 3) {
					params.setTransform(new Rotate(-90, Rotate.Y_AXIS));
				} else if (i == 4) {
					params.setTransform(new Rotate(-90, Rotate.X_AXIS));
				} else if (i == 5) {
					params.setTransform(new Affine());
				}
				
				WritableImage image = axis.getPlotContents().snapshot(params, null);
				ImageView imageView = new ImageView(image);
				//imageView.getTransforms().addAll(axis.getSide(i).getTransforms());
				axis.getSide(i).getChildren().add(imageView);
		}

		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMaximized(true);

		//		Transition t = new Transition() {
		//			
		//			{
		//				setCycleDuration(new Duration(10000));
		//			}
		//
		//			@Override
		//			protected void interpolate(double frac) {
		//				rotateX.setAngle(360*frac);
		//			}
		//			
		//		};
		//		t.play();
	}

}
