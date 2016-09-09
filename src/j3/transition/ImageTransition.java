package j3.transition;

import javafx.animation.Transition;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class ImageTransition extends Transition {
	
	WritableImage writableImage;
	
	WritableImage oldImage;
	
	Image newImage;
	
	public ImageTransition(Duration duration, WritableImage writableImage, Image newImage) {
		super();
		setCycleDuration(duration);
		setCycleCount(1);
		
		this.writableImage = writableImage;
		this.newImage = newImage;
		
		if (writableImage.getWidth() != newImage.getWidth() && writableImage.getHeight() != newImage.getHeight()) {
			throw new IllegalArgumentException("images must be the same size");
		}
		
		oldImage = new WritableImage((int)writableImage.getWidth(), (int)writableImage.getHeight());
		
		for (int i = 0; i < writableImage.getWidth(); i++) {
			for (int j = 0; j < writableImage.getHeight(); j++) {
				oldImage.getPixelWriter().setColor(i, j, writableImage.getPixelReader().getColor(i, j));
			}
		}
	}

	@Override
	protected void interpolate(double frac) {
		for (int i = 0; i < writableImage.getWidth(); i++) {
			for (int j = 0; j < writableImage.getHeight(); j++) {
				Color oldColor = oldImage.getPixelReader().getColor(i, j);
				Color newColor = newImage.getPixelReader().getColor(i, j);
				
				writableImage.getPixelWriter().setColor(i, j, oldColor.interpolate(newColor, frac));
			}
		}
	}

}
