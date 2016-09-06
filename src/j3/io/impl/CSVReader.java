package j3.io.impl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.github.lwhite1.tablesaw.api.Table;

import j3.io.TableReader;

public class CSVReader implements TableReader {

	@Override
	public Table load(File file) throws IOException {
		return Table.createFromCsv(file.getAbsolutePath());
	}

	@Override
	public List<String> getFileExtensions() {
		return Arrays.asList("csv");
	}

	@Override
	public String getDescription() {
		return "CSV File with Header";
	}

	
	
}
