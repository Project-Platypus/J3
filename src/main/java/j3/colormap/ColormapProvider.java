package j3.colormap;

import java.util.List;

public interface ColormapProvider {
	
	public String getCategory();
	
	public List<String> getNames();
	
	public Colormap getColormap(String name);

}
