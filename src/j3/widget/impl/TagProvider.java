package j3.widget.impl;

import j3.Canvas;
import j3.WidgetOptions;
import j3.widget.Widget;
import j3.widget.WidgetProvider;
import javafx.scene.image.Image;

public class TagProvider implements WidgetProvider {

	@Override
	public String getCategory() {
		return "Tagging / Annotations";
	}

	@Override
	public boolean isEnabled(Canvas canvas) {
		return canvas.getPropertyRegistry().contains("data") && canvas.getPropertyRegistry().get("data").get() != null;
	}

	@Override
	public String getName() {
		return "Tag";
	}

	@Override
	public String getDescription() {
		return "Tag or mark a specific data point";
	}

	@Override
	public Image getIcon() {
		return new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/tag_1x.png"));
	}

	@Override
	public Widget<?> createInstance() {
		return new Tag();
	}

}
