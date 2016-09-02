package j3;

import j3.colormap.Colormap;
import javafx.geometry.Orientation;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class Colorbar extends Region {

	private Colormap colormap;
	
	private Orientation orientation;
	
	private String label;
	
	public Colorbar(Colormap colormap, int width, int height, Orientation orientation, String label) {
		super();
		this.colormap = colormap;
		this.orientation = orientation;
		this.label = label;
		
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
		
		ImageView imageView = new ImageView(image);
		
		Text text = new Text(label);
		text.setLayoutX(width/2);
		text.setLayoutY(height);
		
		getChildren().addAll(imageView);
		getStyleClass().add("j3-colorbar");
	}
	
}
