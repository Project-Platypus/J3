package j3.io;

import j3.dataframe.DataFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractDataFrameReader implements DataFrameReader {

	@Override
	public DataFrame load(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return load(is);
		}
	}

	@Override
	public String toString() {
		return getDescription();
	}

}
