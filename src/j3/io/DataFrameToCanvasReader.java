package j3.io;

import j3.Axis;
import j3.Canvas;
import j3.CategoryAxis;
import j3.RealAxis;
import j3.dataframe.Attribute;
import j3.dataframe.DataFrame;
import j3.widget.impl.scatter.ScatterPlot;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DataFrameToCanvasReader extends AbstractCanvasReader {
	
	private final DataFrameReader reader;
	
	public DataFrameToCanvasReader(DataFrameReader reader) {
		super();
		this.reader = reader;
	}

	@Override
	public List<String> getFileExtensions() {
		return reader.getFileExtensions();
	}

	@Override
	public String getDescription() {
		return reader.getDescription();
	}

	@Override
	public void load(InputStream is, Canvas canvas) throws IOException {
		DataFrame table = reader.load(is);
		
		canvas.getPropertyRegistry().get("xAxis").setValue(null);
		canvas.getPropertyRegistry().get("yAxis").setValue(null);
		canvas.getPropertyRegistry().get("zAxis").setValue(null);
		canvas.getPropertyRegistry().get("colorAxis").setValue(null);
		canvas.getPropertyRegistry().get("sizeAxis").setValue(null);
		canvas.getPropertyRegistry().get("visibilityAxis").setValue(null);
		
		List<Axis> axes = new ArrayList<Axis>();
		
		for (int i = 0; i < table.attributeCount(); i++) {
			Attribute<?> attribute = table.getAttribute(i);

			if (!attribute.getName().isEmpty()) {
				if (Number.class.isAssignableFrom(attribute.getType())) {
					RealAxis axis = new RealAxis(attribute);
					axis.scale(table.getValues(i));
					axes.add(axis);
				} else if (String.class.isAssignableFrom(attribute.getType())) {
					CategoryAxis axis = new CategoryAxis(attribute);
					axis.scale(table.getValues(i));
					axes.add(axis);
				}
			}
		}
		
		canvas.getPropertyRegistry().get("data").setValue(table);
		canvas.getPropertyRegistry().get("xAxis").setValue(axes.size() > 0 ? axes.get(0) : null);
		canvas.getPropertyRegistry().get("yAxis").setValue(axes.size() > 1 ? axes.get(1) : null);
		canvas.getPropertyRegistry().get("zAxis").setValue(axes.size() > 2 ? axes.get(2) : null);
		canvas.getPropertyRegistry().get("colorAxis").setValue(axes.size() > 3 ? axes.get(3) : null);
		canvas.getPropertyRegistry().get("sizeAxis").setValue(axes.size() > 4 ? axes.get(4) : null);
		canvas.getPropertyRegistry().get("visibilityAxis").setValue(null);
		canvas.getPropertyRegistry().get("axes").setValue(axes);
		
		canvas.add(new ScatterPlot());
	}

}
