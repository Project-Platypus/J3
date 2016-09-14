package j3.widget.impl.intro;

import java.util.prefs.Preferences;

import j3.Canvas;
import j3.widget.TitledWidget;
import javafx.scene.image.Image;

public class IntroWidget extends TitledWidget<IntroWidget> {

	public IntroWidget() {
		super();
		setTitle("Welcome to J3");
		pane.setPrefWidth(630);
		pane.setPrefHeight(560);
	}
	
	public static boolean shouldShow() {
		Preferences preferences = Preferences.userNodeForPackage(IntroWidget.class);
		return preferences.getBoolean("SHOW_INTRO", true);
	}

	@Override
	public void initialize(Canvas canvas) {
		Frame frame1 = new Frame();
		frame1.setTitle("Welcome to J3");
		frame1.setBody("J3 is a new Java framework for visualizing and analyzing high dimensional datasets.  Click the Next button to begin this quick tutorial on using J3.");
		frame1.setImage(new Image(IntroWidget.class.getResourceAsStream("image4.png")));
		
		Frame frame2 = new Frame();
		frame2.setTitle("Loading Data Files");
		frame2.setBody("To get started, click the New File icon to open a data file.  We currently support CSV and ARFF files.  Here, we opened the Iris dataset.");
		frame2.setImage(new Image(IntroWidget.class.getResourceAsStream("image1.png")));
		
		Frame frame3 = new Frame();
		frame3.setTitle("Changing Settings");
		frame3.setBody("Use the buttons in the toolbar to access various options.  You can change plotting options, colormaps, choose to hide or display legends, and more.");
		frame3.setImage(new Image(IntroWidget.class.getResourceAsStream("image2.png")));
		
		Frame frame4 = new Frame();
		frame4.setTitle("Everything is a Widget");
		frame4.setBody("J3's power comes from its plug-and-play widget system.  Access widgets via the widget menu.  Widgets enable additional visualizations or interactivity with your data.");
		frame4.setImage(new Image(IntroWidget.class.getResourceAsStream("image3.png")));
		
		Frame frame5 = new Frame();
		frame5.setTitle("That's it!");
		frame5.setBody("That's J3 in a nutshell.  Click Finish to begin!");
		frame5.setImage(new Image(IntroWidget.class.getResourceAsStream("image4.png")));
		
		IntroPane intro = new IntroPane();
		intro.getFrames().addAll(frame1, frame2, frame3, frame4, frame5);
		intro.setPrefWidth(600);
		intro.setPrefHeight(520);
		
		intro.setOnFinishHandler(event -> {
			Preferences preferences = Preferences.userNodeForPackage(IntroWidget.class);
			preferences.putBoolean("SHOW_INTRO", false);
			
			canvas.remove(this);
		});
		
		setContent(intro);
	}

	@Override
	public void onAdd(Canvas canvas) {
		super.onAdd(canvas);
		
		setLayoutX((canvas.getWidth() - pane.getPrefWidth())/2.0);
		setLayoutY((canvas.getHeight() - pane.getPrefHeight())/2.0);
		
		canvas.widthProperty().addListener((observable, oldValue, newValue) -> {
			setLayoutX((canvas.getWidth() - pane.getPrefWidth())/2.0);
		});
		
		canvas.heightProperty().addListener((observable, oldValue, newValue) -> {
			setLayoutY((canvas.getHeight() - pane.getPrefHeight())/2.0);
		});
		
	}
	
}
