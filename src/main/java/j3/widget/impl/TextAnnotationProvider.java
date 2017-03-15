package j3.widget.impl;

import j3.Canvas;
import j3.WidgetOptions;
import j3.widget.Widget;
import j3.widget.WidgetProvider;
import javafx.scene.image.Image;

public class TextAnnotationProvider implements WidgetProvider {

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
		return "Text Annotation";
	}

	@Override
	public String getDescription() {
		return "Annotate a data point with text";
	}

	@Override
	public Image getIcon() {
		return new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/textAnnotation_1x.png"));
	}

	@Override
	public Widget<?> createInstance() {
		return new TextAnnotation();
	}

}
