package j3.widget.impl.scatter2d;

import javafx.scene.image.Image;
import j3.Canvas;
import j3.WidgetOptions;
import j3.widget.Widget;
import j3.widget.WidgetProvider;

public class Scatter2DProvider implements WidgetProvider {

	@Override
	public String getCategory() {
		return "Charts";
	}

	@Override
	public boolean isEnabled(Canvas canvas) {
		return canvas.getPropertyRegistry().contains("data") && canvas.getPropertyRegistry().get("data").get() != null &&
				canvas.getPropertyRegistry().contains("axes") && canvas.getPropertyRegistry().get("axes").get() != null;
	}

	@Override
	public String getName() {
		return "2D Scatter Plot";
	}

	@Override
	public String getDescription() {
		return "Show a 2D scatter plot";
	}

	@Override
	public Image getIcon() {
		return new Image(WidgetOptions.class.getResourceAsStream("/j3/icons/scatter2d_1x.png"));
	}

	@Override
	public Widget<?> createInstance() {
		return new ScatterPlot2D();
	}

}
