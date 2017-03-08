package j3.theme;

import java.util.List;

public interface ThemeProvider {
	
	public String getName();
	
	public String getDescription();
	
	public List<String> getStylesheets();

}
