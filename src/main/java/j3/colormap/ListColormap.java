package j3.colormap;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListColormap extends AbstractListColormap {
	
	private final String name;
	
	private final double[][] internalColorList;
	
	public ListColormap(String name, double[][] colors) {
		super();
		this.name = name;
		this.internalColorList = colors;
	}
	
	public ListColormap(String name, InputStream stream) throws IOException {
		super();
		this.name = name;
		this.internalColorList = loadColormap(stream);
	}
	
	private final double[][] loadColormap(InputStream stream) throws IOException {
		List<double[]> list = new ArrayList<double[]>();
		LineNumberReader reader = null;
		String line = null;
		
		try {
			reader = new LineNumberReader(new InputStreamReader(stream));
			
			while ((line = reader.readLine()) != null) {
				String[] tokens = line.split("\\s+");
				
				if (tokens.length < 3 || tokens.length > 4) {
					throw new IOException("malformed colormap on line " + reader.getLineNumber() + ", requires 3 or 4 values");
				} else {
					list.add(Arrays.stream(tokens).mapToDouble(Double::parseDouble).toArray());
				}
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		return list.toArray(new double[list.size()][]);
	}
	
	public double[][] getColorList() {
		return internalColorList;
	}

	@Override
	public String getName() {
		return name;
	}

}
