package j3.io;

import j3.dataframe.DataFrame;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface DataFrameReader {

	public List<String> getFileExtensions();

	public String getDescription();

	public DataFrame load(File file) throws IOException;

	public DataFrame load(InputStream is) throws IOException;

}
