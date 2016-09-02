package j3;

import j3.colormap.HSVColormap;

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
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;

public class Test extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		Scene scene = new Scene(new Group(), 400, 400, true, SceneAntialiasing.BALANCED);

		scene.getStylesheets().add(
				Test.class.getResource("j3.css").toExternalForm());
		
		Group root = (Group)scene.getRoot();
		
		// generate the plot contents
		Subscene3D subscene = new Subscene3D(400);
		
		InputStream is = Test.class.getResourceAsStream("cdice.txt");

		Table table = Table.createFromStream(
				new ColumnType[] { ColumnType.FLOAT, ColumnType.FLOAT, ColumnType.FLOAT, ColumnType.FLOAT },
				true, ',', is, "CDICE");

		new Scatter(subscene.getAxis3D(), table);
		
		Colorbar colorbar = new Colorbar(new HSVColormap(), 40, 500, Orientation.VERTICAL, "Color");
		colorbar.layoutXProperty().bind(scene.widthProperty().divide(2.0).subtract(200));
		colorbar.layoutYProperty().bind(scene.heightProperty().subtract(60));
		
		// add the plot to the scene
		root.getChildren().addAll(subscene, colorbar);
		
		subscene.widthProperty().bind(scene.widthProperty());
		subscene.heightProperty().bind(scene.heightProperty());

		
//		for (int i = 0; i < 6; i++) {
//				SnapshotParameters params = new SnapshotParameters();
//				params.setCamera(new ParallelCamera());
//				params.setFill(Color.TRANSPARENT);
//				
//				if (i == 0) {
//					params.setTransform(new Affine());
//				} else if (i == 1) {
//					params.setTransform(new Rotate(-90, Rotate.X_AXIS));
//				} else if (i == 2) {
//					params.setTransform(new Rotate(-90, Rotate.Y_AXIS));
//				} else if (i == 3) {
//					params.setTransform(new Rotate(-90, Rotate.Y_AXIS));
//				} else if (i == 4) {
//					params.setTransform(new Rotate(-90, Rotate.X_AXIS));
//				} else if (i == 5) {
//					params.setTransform(new Affine());
//				}
//				
//				WritableImage image = axis.getPlotContents().snapshot(params, null);
//				ImageView imageView = new ImageView(image);
//				//imageView.getTransforms().addAll(axis.getSide(i).getTransforms());
//				axis.getSide(i).getChildren().add(imageView);
//		}

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
