package j3.widget.impl;

import j3.Canvas;
import j3.WidgetOptions;
import j3.widget.Widget;
import j3.widget.WidgetProvider;
import javafx.scene.image.Image;

public class BrushingWidgetProvider implements WidgetProvider {

	@Override
	public String getCategory() {
		return "Standard";
	}

	@Override
	public boolean isEnabled(Canvas canvas) {
		return canvas.getPropertyRegistry().contains("data") && canvas.getPropertyRegistry().get("data").get() != null &&
				canvas.getPropertyRegistry().contains("axes") && canvas.getPropertyRegistry().get("axes").get() != null;
	}

	@Override
	public String getName() {
		return "Brushing";
	}

	@Override
	public String getDescription() {
		return "Enable brushing points by their range";
	}

	@Override
	public Image getIcon() {
		return new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/brush_1x.png"));
	}

	@Override
	public Widget<?> createInstance() {
		return new BrushingWidget();
	}

}
