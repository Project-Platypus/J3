package j3.io;

import java.io.File;
import java.io.IOException;
import java.nio.file.ProviderNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import com.github.lwhite1.tablesaw.api.Table;

public class TableReaderFactory {
	
	private static TableReaderFactory INSTANCE;
	
	private final ServiceLoader<TableReader> loader;
	
	private TableReaderFactory() {
		super();
		
		loader = ServiceLoader.load(TableReader.class);
	}
	
	public static TableReaderFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new TableReaderFactory();
		}
		
		return INSTANCE;
	}
	
	public List<TableReader> getProviders() {
		List<TableReader> providers = new ArrayList<TableReader>();
		loader.forEach(provider -> providers.add(provider));
		return providers;
	}
	
	public TableReader getReader(File file) {
		String extension = FilenameUtils.getExtension(file.getName());
		Iterator<TableReader> iterator = loader.iterator();
		
		while (iterator.hasNext()) {
			TableReader reader = iterator.next();

			for (String ext : reader.getFileExtensions()) {
				if (StringUtils.equalsIgnoreCase(ext, extension)) {
					return reader;
				}
			}
		}
		
		throw new ProviderNotFoundException("no reader for file extension '" + extension + "' found");
	}
	
	public Table load(File file) throws IOException {
		return getReader(file).load(file);
	}

}
