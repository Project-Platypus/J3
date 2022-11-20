package j3.io;

import j3.Canvas;

import java.io.File;
import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

public class CanvasReaderFactory {

	private static CanvasReaderFactory INSTANCE;

	private final ServiceLoader<CanvasReader> loader;

	private CanvasReaderFactory() {
		super();

		loader = ServiceLoader.load(CanvasReader.class);
	}

	public static CanvasReaderFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new CanvasReaderFactory();
		}

		return INSTANCE;
	}

	public List<CanvasReader> getProviders() {
		List<CanvasReader> providers = new ArrayList<>();
		DataFrameReaderFactory.getInstance().getProviders()
				.forEach(provider -> providers.add(new DataFrameToCanvasReader(provider)));
		loader.forEach(provider -> providers.add(provider));
		return providers;
	}

	public CanvasReader getReader(File file) {
		String extension = FilenameUtils.getExtension(file.getName());

		for (CanvasReader reader : getProviders()) {
			for (String ext : reader.getFileExtensions()) {
				if (StringUtils.equalsIgnoreCase(ext, extension)) {
					return reader;
				}
			}
		}

		throw new ProviderNotFoundException("no reader for file extension '" + extension + "' found");
	}

	public void load(File file, Canvas canvas) throws IOException {
		getReader(file).load(file, canvas);
	}

}
