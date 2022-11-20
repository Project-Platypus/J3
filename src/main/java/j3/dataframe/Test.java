package j3.dataframe;

import j3.io.impl.CSVReader;

import java.io.File;
import java.io.IOException;

public class Test {

	public static void main(String[] args) throws IOException {
//		DataFrame dataFrame = new DataFrame();
//		
//		DoubleAttribute doubleAttr = new DoubleAttribute("double");
//		IntegerAttribute integerAttr = new IntegerAttribute("int");
//		StringAttribute stringAttr = new StringAttribute("string");
//		
//		Instance instance = new Instance();
//		instance.set(doubleAttr, 25.0);
//		instance.set(integerAttr, 15);
//		instance.set(stringAttr, "hello");
//		instance.set(doubleAttr, "19.5");
//		
//		Object doubleValue = instance.get(doubleAttr);
//		Object integerValue = instance.get(integerAttr);
//		Object stringValue = instance.get(stringAttr);
//		
//		System.out.println(doubleValue + " " + integerValue + " " + stringValue);

		CSVReader reader = new CSVReader();
		DataFrame frame = reader.load(new File("cdice.csv"));

		for (Attribute<?> attr : frame.getAttributes()) {
			System.out.println(attr.getName() + " " + attr.getType());
		}

		// instance.set(doubleAttr, 15);
	}

}
