package j3.io;

import j3.Canvas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractCanvasReader implements CanvasReader {

	@Override
	public void load(File file, Canvas canvas) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			load(is, canvas);
		}
	}
	
	@Override
	public String toString() {
		return getDescription();
	}

}
