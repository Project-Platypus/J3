package j3.widget.impl.intro;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class FrameView extends GridPane {
	
	private ObjectProperty<Frame> frame = new ObjectPropertyBase<Frame>() {

		@Override
		protected void invalidated() {
			if (frame.get() == null) {
				imageView.setImage(null);
				title.setText("");
				body.setText("");
			} else {
				imageView.setImage(frame.get().getImage());
				title.setText(frame.get().getTitle());
				body.setText(frame.get().getBody());
			}
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
		
		//setBackground(new Background(new BackgroundFill(Color.web("#336699"), CornerRadii.EMPTY, Insets.EMPTY)));
		
		imageView = new ImageView() {

			@Override
			public double minWidth(double height) {
				return 50;
			}

			@Override
			public double minHeight(double width) {
				return 50;
			}

			@Override
			public double maxWidth(double height) {
				return 16384;
			}

			@Override
			public double maxHeight(double width) {
				return 16384;
			}

			@Override
			public double prefWidth(double height) {
				Image image = getImage();
		        if (image == null) return minWidth(height);
		        return image.getWidth();
			}
			
			@Override
			public double prefHeight(double width) {
				Image image = getImage();
		        if (image == null) return minHeight(width);
		        return image.getHeight();
			}

			@Override
			public boolean isResizable() {
				return true;
			}
			
			@Override
		    public void resize(double width, double height)
		    {
		        setFitWidth(width);
		        setFitHeight(height);
		    }
			
		};
		//imageView.setX(10);
		//imageView.setY(10);
		imageView.setPreserveRatio(true);
		//widthProperty().addListener((o, v, k) -> imageView.setFitWidth(getWidth()-20));
		//heightProperty().addListener(l -> imageView.setFitHeight(getHeight()-20));
		//imageView.fitWidthProperty().bind(widthProperty());
		
		title = new Text();
		title.setStyle("-fx-font-family: Verdana; -fx-font-size: 18; -fx-fill: black; -fx-effect: dropshadow(gaussian, rgba(1,1,1,0.9), 2, 0.0, 1, 1);");
		
		
		body = new Text();
		body.setStyle("-fx-font-family: Verdana; -fx-font-size: 14");
		//setPadding(new Insets(15, 15, 15, 15));
		
		add(new TextFlow(title), 0, 0);
		add(imageView, 0, 1);
		add(new TextFlow(body), 0, 2);
		//getChildren().addAll(new TextFlow(title), imageView, new TextFlow(body));
		
	}

}
