package j3.io;

import j3.Canvas;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface CanvasReader {

	public List<String> getFileExtensions();

	public String getDescription();

	public void load(File file, Canvas canvas) throws IOException;

	public void load(InputStream is, Canvas canvas) throws IOException;

}
