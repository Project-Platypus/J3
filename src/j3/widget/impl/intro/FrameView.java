package j3.widget.impl.intro;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class FrameView extends VBox {
	
	private ObjectProperty<Frame> frame = new ObjectPropertyBase<Frame>() {

		@Override
		protected void invalidated() {
			imageView.setImage(frame.get().getImage());
			title.setText(frame.get().getTitle());
			body.setText(frame.get().getBody());
		}

		@Override
		public Object getBean() {
			return FrameView.this;
		}

		@Override
		public String getName() {
			return "frame";
		}
		
	};
	
	public Frame getFrame() {
		return frame.get();
	}
	
	public void setFrame(Frame frame) {
		this.frame.set(frame);
	}
	
	private ImageView imageView;
	
	private Text title;
	
	private Text body;
	
	public FrameView() {
		super();
		
		setBackground(new Background(new BackgroundFill(Color.BLUEVIOLET, CornerRadii.EMPTY, Insets.EMPTY)));
		
		imageView = new ImageView();
		imageView.setX(10);
		imageView.setY(10);
		imageView.setPreserveRatio(true);
		
		widthProperty().addListener(l -> imageView.setFitWidth(getWidth()-20));
		heightProperty().addListener(l -> imageView.setFitHeight(getHeight()-20));
		
		title = new Text();
		body = new Text();
		
		getChildren().addAll(imageView, title, body);
	}

}
