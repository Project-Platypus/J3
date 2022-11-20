package j3.io;

import j3.dataframe.DataFrame;

import java.io.File;
import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class DataFrameReaderFactory {

	private static DataFrameReaderFactory INSTANCE;

	private final ServiceLoader<DataFrameReader> loader;

	private DataFrameReaderFactory() {
		super();

		loader = ServiceLoader.load(DataFrameReader.class);
	}

	public static DataFrameReaderFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new DataFrameReaderFactory();
		}

		return INSTANCE;
	}

	public List<DataFrameReader> getProviders() {
		List<DataFrameReader> providers = new ArrayList<>();
		loader.forEach(provider -> providers.add(provider));
		return providers;
	}

	public DataFrameReader getReader(File file) {
		String extension = FilenameUtils.getExtension(file.getName());

		for (DataFrameReader reader : getProviders()) {
			for (String ext : reader.getFileExtensions()) {
				if (StringUtils.equalsIgnoreCase(ext, extension)) {
					return reader;
				}
			}
		}

		throw new ProviderNotFoundException("no reader for file extension '" + extension + "' found");
	}

	public DataFrame load(File file) throws IOException {
		return getReader(file).load(file);
	}

}
