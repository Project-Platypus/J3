package j3.widgets;

import java.util.ArrayList;
import java.util.List;

import j3.Axis;
import j3.Canvas;
import j3.colormap.Colormap;
import j3.colormap.impl.RainbowColormap;
import j3.transition.ImageTransition;
import j3.widgets.threed.Axis3D;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.util.Duration;

public class Colorbar extends Region implements Widget<Colorbar> {
	
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
	
	private ObjectProperty<Axis> colorAxis = new ObjectPropertyBase<Axis>() {
		
		{
			addListener((observable, oldValue, newValue) -> {
				updateLabels();
			});
		}

		@Override
		public Object getBean() {
			return Axis3D.class;
		}

		@Override
		public String getName() {
			return "Color";
		}

	};

	public void setColorAxis(Axis axis) {
		colorAxis.set(axis);
	}

	public Axis getColorAxis() {
		return colorAxis.get();
	}

	public ObjectProperty<Axis> colorAxisProperty() {
		return colorAxis;
	}
	
	private ImageView imageView;
	
	private List<Line> lines = new ArrayList<Line>();
	
	private List<Text> labels = new ArrayList<Text>();;
	
	private Text axisLabel = new Text();
	
	private Translate location = new Translate(0, 0);
	
	private double mousePosX, mousePosY, mouseOldX, mouseOldY;
	
	private ChangeListener<? super Number> widthListener, heightListener;
	
	public Colorbar(int width, int height, Orientation orientation, Axis axis) {
		super();
		this.orientation = orientation;
		
		setColorAxis(axis);
		setPrefWidth(width);
		setPrefHeight(height);
		
		imageView = new ImageView();
		
		axisLabel.getStyleClass().add("j3-axis-label");
		axisLabel.layoutBoundsProperty().addListener(event -> layoutLabels());
		
		getChildren().addAll(imageView, axisLabel);
		getStyleClass().add("j3-colorbar");
		
		getTransforms().addAll(location);
		
		setOnMousePressed(event -> {
			mouseOldX = event.getSceneX();
			mouseOldY = event.getSceneY();
		});
		
		setOnMouseDragged(event -> {
			mousePosX = event.getSceneX();
			mousePosY = event.getSceneY();
			location.setX(location.getX() + (mousePosX - mouseOldX));
			location.setY(location.getY() + (mousePosY - mouseOldY));
			mouseOldX = mousePosX;
			mouseOldY = mousePosY;
		});
	}
	
	public void updateLabels() {
		int width = (int)getPrefWidth();
		int height = (int)getPrefHeight();
		
		getChildren().removeAll(lines);
		getChildren().removeAll(labels);
		lines.clear();
		labels.clear();
		axisLabel.setText("");

		// generate the tick lines and labels
		if (getColorAxis() != null) {
			double[] tickPositions = getColorAxis().getTickPositions();
			String[] tickLabels = getColorAxis().getTickLabels();
			
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
				label.layoutBoundsProperty().addListener(event -> layoutLabels());
				labels.add(label);
			}
			
			getChildren().addAll(lines);
			getChildren().addAll(labels);
			
			axisLabel.setText(getColorAxis().getLabel());
			
			layoutLabels();
		}
	}
	
	public void layoutLabels() {
		if (getColorAxis() != null) {
			double[] tickPositions = getColorAxis().getTickPositions();
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

	@Override
	public Colorbar getNode() {
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void initialize(Canvas canvas) {
		Property<Colormap> colormap = null;
		Property<Axis> colorAxis = null;
		
		if (canvas.getSharedData().contains("colormap")) {
			colormap = (Property<Colormap>)canvas.getSharedData().get("colormap");
		} else {
			colormap = canvas.getSharedData().put("colormap", new RainbowColormap());
		}
		
		if (canvas.getSharedData().contains("colorAxis")) {
			colorAxis = (Property<Axis>)canvas.getSharedData().get("colorAxis");
		} else {
			colorAxis = canvas.getSharedData().put("colorAxis", null);
		}
		
		this.colormap.bind(colormap);
		this.colorAxis.bind(colorAxis);
	}

	@Override
	public void onAdd(Canvas canvas) {
		update();
		updateLabels();
		
		widthListener = (observer, oldValue, newValue) -> {
			double fracX = (location.getX() + getWidth()/2.0) / oldValue.doubleValue();
			double newX = (fracX * newValue.doubleValue()) - getWidth()/2.0;
	
			location.setX(newX);
		};
		
		heightListener = (observer, oldValue, newValue) -> {
			double fracY = (location.getY() + getHeight()/2.0) / oldValue.doubleValue();
			double newY = (fracY * newValue.doubleValue()) - getHeight()/2.0;

			location.setY(newY);
		};
		
		canvas.widthProperty().addListener(widthListener);
		canvas.heightProperty().addListener(heightListener);
	}
	
	@Override
	public void onRemove(Canvas canvas) {
		canvas.widthProperty().removeListener(widthListener);
		canvas.heightProperty().removeListener(heightListener);
	}
	
}
