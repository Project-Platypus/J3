package j3.widget.impl.animate;

import j3.Canvas;
import j3.WidgetOptions;
import j3.widget.Widget;
import j3.widget.WidgetProvider;
import javafx.scene.image.Image;

public class AnimateWidgetProvider implements WidgetProvider {

	@Override
	public String getCategory() {
		return "Standard";
	}

	@Override
	public boolean isEnabled(Canvas canvas) {
		return true;
	}

	@Override
	public String getName() {
		return "Animate";
	}

	@Override
	public String getDescription() {
		return "Scripted animations";
	}

	@Override
	public Image getIcon() {
		return new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/animate_1x.png"));
	}

	@Override
	public Widget<?> createInstance() {
		return new AnimateWidget();
	}

}
