package j3;

import java.util.ArrayList;
import java.util.List;

import j3.colormap.Colormap;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.geometry.Orientation;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Colorbar extends Region {
	
	private static final int TICK_LENGTH = 5;

	private ObjectProperty<Colormap> colormap = new ObjectPropertyBase<Colormap>() {

		@Override
		protected void invalidated() {
			update();
		}

		@Override
		public Object getBean() {
			return Colorbar.this;
		}

		@Override
		public String getName() {
			return "colormap";
		}
		
	};
	
	public void setColormap(Colormap colormap) {
		this.colormap.set(colormap);
	}
	
	public Colormap getColormap() {
		return colormap.get();
	}
	
	public ObjectProperty<Colormap> colormapProperty() {
		return colormap;
	}
	
	private Orientation orientation;
	
	private Axis axis;
	
	private ImageView imageView;
	
	private List<Line> lines;
	
	private List<Text> labels;
	
	private Text axisLabel;
	
	public Colorbar(Colormap colormap, int width, int height, Orientation orientation, Axis<?> axis) {
		super();
		setColormap(colormap);
		this.orientation = orientation;
		this.axis = axis;
		
		setPrefWidth(width);
		setPrefHeight(height);
		
		imageView = new ImageView();
		
		// generate the label for the colorbar
		axisLabel = new Text(axis.getLabel());
		axisLabel.getStyleClass().add("j3-axis-label");
		axisLabel.layoutBoundsProperty().addListener(event -> updateLabels());
		
		// generate the tick lines and labels
		lines = new ArrayList<Line>();
		labels = new ArrayList<Text>();
		
		double[] tickPositions = axis.getTickPositions();
		String[] tickLabels = axis.getTickLabels();
		
		for (int i = 0; i < tickPositions.length; i++) {
			Line line = new Line(0, 0, 0, TICK_LENGTH);
			line.getStyleClass().add("j3-tick-line");
			line.setStroke(Color.BLACK);
			line.setFill(Color.BLACK);
			line.setTranslateX(width*tickPositions[i]);
			line.setTranslateY(height);
			lines.add(line);
			
			Text label = new Text(tickLabels[i]);
			label.getStyleClass().add("j3-tick-label");
			label.layoutBoundsProperty().addListener(event -> updateLabels());
			labels.add(label);
		}
		
		getChildren().addAll(imageView, axisLabel);
		getChildren().addAll(lines);
		getChildren().addAll(labels);
		getStyleClass().add("j3-colorbar");
		
		update();
		updateLabels();
	}
	
	public void updateLabels() {
		double[] tickPositions = axis.getTickPositions();
		double maxLabelHeight = 0.0;
		
		for (int i = 0; i < labels.size(); i++) {
			Text label = labels.get(i);
			
			double labelWidth = label.prefWidth(30);
			double labelHeight = label.prefHeight(labelWidth);
			
			label.setTranslateX(getPrefWidth()*tickPositions[i] - labelWidth/2);
			label.setTranslateY(getPrefHeight() + TICK_LENGTH + labelHeight);
			
			maxLabelHeight = Math.max(maxLabelHeight, labelHeight);
		}
		
		double textWidth = axisLabel.prefWidth(200);
		double textHeight = axisLabel.prefHeight(textWidth);

		axisLabel.setLayoutX(getPrefWidth()/2 - textWidth/2);
		axisLabel.setLayoutY(getPrefHeight() + TICK_LENGTH + maxLabelHeight + textHeight);
	}
	
	public void update() {
		int width = (int)getPrefWidth();
		int height = (int)getPrefHeight();
		Colormap colormap = getColormap();
		
		if (width <= 0 || height <= 0) {
			return;
		}

		WritableImage image = createImage(colormap, width, height, orientation);
		
		if (imageView.getImage() == null) {
			imageView.setImage(image);
		} else {
			ImageTransition transition = new ImageTransition(new Duration(1000), (WritableImage)imageView.getImage(), image);
			transition.play();
		}
	}
	
	public static WritableImage createImage(Colormap colormap, int width, int height, Orientation orientation) {
		WritableImage image = new WritableImage(width, height);
		
		if (orientation == Orientation.HORIZONTAL) {
			for (int i = 0; i < width; i++) {
				Color color = colormap.map(i / (double)width);
				
				for (int j = 0; j < height; j++) {
					image.getPixelWriter().setColor(i, j, color);
				}
			}
		} else {
			for (int i = 0; i < height; i++) {
				Color color = colormap.map(i / (double)height);
				
				for (int j = 0; j < width; j++) {
					image.getPixelWriter().setColor(j, i, color);
				}
			}
		}
		
		return image;
	}
	
}
