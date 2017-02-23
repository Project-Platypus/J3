package j3.widget.impl;

import javafx.scene.image.Image;
import j3.Canvas;
import j3.WidgetOptions;
import j3.widget.Widget;
import j3.widget.WidgetProvider;

public class DataTableWidgetProvider implements WidgetProvider {

	@Override
	public String getCategory() {
		return "Standard";
	}

	@Override
	public boolean isEnabled(Canvas canvas) {
		return canvas.getPropertyRegistry().contains("data") && canvas.getPropertyRegistry().get("data").get() != null;
	}

	@Override
	public String getName() {
		return "Data Table";
	}

	@Override
	public String getDescription() {
		return "Displays a table containing all data";
	}

	@Override
	public Image getIcon() {
		return new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/list_1x.png"));
	}

	@Override
	public Widget<?> createInstance() {
		return new DataTableWidget();
	}

}
