package j3.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.github.lwhite1.tablesaw.api.Table;

public interface TableReader {
	
	public List<String> getFileExtensions();
	
	public String getDescription();
	
	public Table load(File file) throws IOException;

}
