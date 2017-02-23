package j3.io.impl;

import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.dataframe.MagicTyping;
import j3.dataframe.StringAttribute;
import j3.io.AbstractDataFrameReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class PFReader extends AbstractDataFrameReader {

	@Override
	public List<String> getFileExtensions() {
		return Arrays.asList("pf", "dat");
	}

	@Override
	public String getDescription() {
		return "MOEA Framework Pareto Front";
	}

	@Override
	public DataFrame load(InputStream is) throws IOException {
		DataFrame frame = new DataFrame();
		int columns = -1;
		
		// load file into a data frame containing strings
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			String line = null;
			
			while ((line = reader.readLine()) != null) {
				Instance instance = new Instance();
				String[] tokens = line.split("\\s+");
				
				if (columns < 0) {
					columns = tokens.length;
					
					for (int i = 0; i < columns; i++) {
						frame.addAttribute(new StringAttribute("Obj" + (i+1)));
					}
				}
				
				for (int i = 0; i < columns; i++) {
					instance.set(frame.getAttribute(i), tokens[i].trim());
				}
				
				frame.addInstance(instance);
			}
		}
		
		MagicTyping typing = new MagicTyping();
		typing.convert(frame);
		
		return frame;
	}

}
