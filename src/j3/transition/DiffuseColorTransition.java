package j3.transition;

import javafx.animation.Transition;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.util.Duration;

public class DiffuseColorTransition extends Transition {
	
	private PhongMaterial material;
	
	private Color oldColor;
	
	private Color newColor;
	
	public DiffuseColorTransition(Duration duration, PhongMaterial material, Color newColor) {
		super();
		this.material = material;
		this.oldColor = material.getDiffuseColor();
		this.newColor = newColor;
		
		setCycleDuration(duration);
		setCycleCount(1);
	}

	@Override
	protected void interpolate(double frac) {
		material.setDiffuseColor(oldColor.interpolate(newColor, frac));
	}

}
