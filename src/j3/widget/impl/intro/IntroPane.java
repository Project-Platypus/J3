package j3.widget.impl.intro;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;

public class IntroPane extends BorderPane {
	
	private ObservableList<Frame> frames = FXCollections.observableArrayList();
	
	private int currentIndex = -1;
	
	private Button next = new Button("Next");
	
	private Button previous = new Button("Previous");
	
	private FrameView frameView = new FrameView();
	
	private EventHandler<ActionEvent> finishHandler;
	
	public IntroPane() {
		super();
		
		frames.addListener((ListChangeListener.Change<? extends Frame> change) -> {
			if (frames.isEmpty()) {
				currentIndex = -1;
				frameView.setFrame(null);
				next.setText("Finish");
			} else {
				currentIndex = 0;
				frameView.setFrame(frames.get(currentIndex));
				
				if (frames.size() > 1) {
					next.setText("Next");
				} else {
					next.setText("Finish");
				}
			}
			
			previous.setDisable(true);
		});
		
		next.setOnAction(event -> {
			if (currentIndex == frames.size()-1) {
				if (finishHandler != null) {
					finishHandler.handle(event);
				}
			} else {
				currentIndex++;
				frameView.setFrame(frames.get(currentIndex));
				
				if (currentIndex < frames.size()-1) {
					next.setText("Next");
				} else {
					next.setText("Finish");
				}
				
				previous.setDisable(false);
			}
		});
		
		previous.setOnAction(event -> {
			currentIndex--;
			frameView.setFrame(frames.get(currentIndex));
			
			next.setText("Next");
			
			if (currentIndex == 0) {
				previous.setDisable(true);
			}
		});
		
		setCenter(frameView);
		
		FlowPane flowPane = new FlowPane();
		flowPane.getChildren().addAll(previous, next);
		flowPane.setAlignment(Pos.CENTER_RIGHT);
		flowPane.setHgap(5);
		flowPane.setPadding(new Insets(15, 0, 0, 0));
		setBottom(flowPane);
		
		setPadding(new Insets(15, 15, 15, 15));
	}
	
	public ObservableList<Frame> getFrames() {
		return frames;
	}
	
	public void setOnFinishHandler(EventHandler<ActionEvent> handler) {
		finishHandler = handler;
	}

}
