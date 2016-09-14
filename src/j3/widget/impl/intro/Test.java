package j3.widget.impl.intro;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Test extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		StackPane root = new StackPane();
		
		Frame frame1 = new Frame();
		frame1.setTitle("Welcome to J3");
		frame1.setBody("J3 aims to revolutionize the way we create and share complex data sets.");
		frame1.setImage(new Image(Test.class.getResourceAsStream("frame1.png")));
		
		FrameView frameView = new FrameView();
		frameView.setFrame(frame1);
		root.getChildren().add(frameView);
		
		frameView.setPrefWidth(400);
		frameView.setPrefHeight(400);
		
		Scene scene = new Scene(root, 400, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
}
