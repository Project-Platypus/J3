package j3.widget;

import j3.Canvas;
import javafx.scene.image.Image;

public interface WidgetProvider {
	
	public String getCategory();
	
	public boolean isEnabled(Canvas canvas);
	
	public String getName();
	
	public String getDescription();
	
	public Image getIcon();
	
	public Widget<?> createInstance();

}
