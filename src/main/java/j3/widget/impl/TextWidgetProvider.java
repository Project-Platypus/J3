package j3.widget.impl;

import j3.Canvas;
import j3.WidgetOptions;
import j3.widget.Widget;
import j3.widget.WidgetProvider;
import javafx.scene.image.Image;

public class TextWidgetProvider implements WidgetProvider {

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
		return "Text";
	}

	@Override
	public String getDescription() {
		return "Display text anywhere in canvas";
	}

	@Override
	public Image getIcon() {
		return new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/text_1x.png"));
	}

	@Override
	public Widget<?> createInstance() {
		return new TextWidget();
	}

}
