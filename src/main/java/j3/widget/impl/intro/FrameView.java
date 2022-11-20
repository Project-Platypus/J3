package j3.widget.impl.intro;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;

public class FrameView extends GridPane {

	private ObjectProperty<Frame> frame = new ObjectPropertyBase<Frame>() {

		@Override
		protected void invalidated() {
			if (frame.get() == null) {
				imageView.setImage(null);
				title.setText("");
				body.setText("");
			} else {
				ParallelTransition pt = new ParallelTransition();

				FadeTransition ft1 = new FadeTransition(new Duration(250), imageView);
				ft1.setFromValue(1.0);
				ft1.setToValue(0.0);
				ft1.setOnFinished(e -> imageView.setImage(frame.get().getImage()));

				FadeTransition ft2 = new FadeTransition(new Duration(250), imageView);
				ft2.setFromValue(0.0);
				ft2.setToValue(1.0);
				ft2.setDelay(new Duration(250));

				FadeTransition ft3 = new FadeTransition(new Duration(250), title);
				ft3.setFromValue(1.0);
				ft3.setToValue(0.0);
				ft3.setOnFinished(e -> title.setText(frame.get().getTitle()));

				FadeTransition ft4 = new FadeTransition(new Duration(250), title);
				ft4.setFromValue(0.0);
				ft4.setToValue(1.0);
				ft4.setDelay(new Duration(250));

				FadeTransition ft5 = new FadeTransition(new Duration(250), body);
				ft5.setFromValue(1.0);
				ft5.setToValue(0.0);
				ft5.setOnFinished(e -> body.setText(frame.get().getBody()));

				FadeTransition ft6 = new FadeTransition(new Duration(250), body);
				ft6.setFromValue(0.0);
				ft6.setToValue(1.0);
				ft6.setDelay(new Duration(250));

				pt.getChildren().addAll(ft1, ft2, ft3, ft4, ft5, ft6);
				pt.play();
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

		// setBackground(new Background(new BackgroundFill(Color.web("#336699"),
		// CornerRadii.EMPTY, Insets.EMPTY)));

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
				if (image == null)
					return minWidth(height);
				return image.getWidth();
			}

			@Override
			public double prefHeight(double width) {
				Image image = getImage();
				if (image == null)
					return minHeight(width);
				return image.getHeight();
			}

			@Override
			public boolean isResizable() {
				return true;
			}

			@Override
			public void resize(double width, double height) {
				setFitWidth(width);
				setFitHeight(height);
			}

		};
		// imageView.setX(10);
		// imageView.setY(10);
		imageView.setPreserveRatio(true);
		// widthProperty().addListener((o, v, k) ->
		// imageView.setFitWidth(getWidth()-20));
		// heightProperty().addListener(l -> imageView.setFitHeight(getHeight()-20));
		// imageView.fitWidthProperty().bind(widthProperty());

		title = new Text();
		title.setStyle(
				"-fx-font-family: Verdana; -fx-font-size: 18; -fx-fill: black; -fx-effect: dropshadow(gaussian, rgba(1,1,1,0.9), 2, 0.0, 1, 1);");

		body = new Text();
		body.setStyle("-fx-font-family: Verdana; -fx-font-size: 14");
		// setPadding(new Insets(15, 15, 15, 15));

		add(new TextFlow(title), 0, 0);
		add(imageView, 0, 1);
		add(new TextFlow(body), 0, 2);
		// getChildren().addAll(new TextFlow(title), imageView, new TextFlow(body));

	}

}
