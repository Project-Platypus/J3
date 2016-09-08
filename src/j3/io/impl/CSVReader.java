package j3.io.impl;

import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.dataframe.MagicTyping;
import j3.dataframe.StringAttribute;
import j3.io.DataFrameReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

public class CSVReader implements DataFrameReader {

	@Override
	public List<String> getFileExtensions() {
		return Arrays.asList("csv");
	}

	@Override
	public String getDescription() {
		return "CSV File with Header";
	}

	@Override
	public DataFrame load(File file) throws IOException {
		try (InputStream is = new FileInputStream(file)) {
			return load(is);
		}
	}

	@Override
	public DataFrame load(InputStream is) throws IOException {
		DataFrame frame = new DataFrame();
		
		// load CSV file into a data frame containing strings
		try (CSVParser parser = new CSVParser(new InputStreamReader(is), CSVFormat.DEFAULT.withHeader())) {
			int size = parser.getHeaderMap().size();
			
			for (String column : parser.getHeaderMap().keySet()) {
				frame.addAttribute(new StringAttribute(column));
			}
			
			parser.forEach(record -> {
				Instance instance = new Instance();
				
				for (int i = 0; i < size; i++) {
					instance.set(frame.getAttribute(i), record.get(i));
				}
				
				frame.addInstance(instance);
			});
		}
		
		MagicTyping typing = new MagicTyping();
		typing.convert(frame);
		
		return frame;
	}

}
