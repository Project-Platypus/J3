package j3.io.impl;

import j3.Axis;
import j3.Canvas;
import j3.CategoryAxis;
import j3.RealAxis;
import j3.colormap.ColormapFactory;
import j3.dataframe.Attribute;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.io.AbstractCanvasReader;
import j3.widget.SerializableWidget;
import j3.widget.Widget;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class J3Reader extends AbstractCanvasReader {

	@Override
	public List<String> getFileExtensions() {
		return Arrays.asList("j3");
	}

	@Override
	public String getDescription() {
		return "J3 Export";
	}

	@Override
	public void load(InputStream is, Canvas canvas) throws IOException {
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read(is);
			Element root = document.getRootElement();
			
			// parse the data
			DataFrame table = new DataFrame();
			Element dataElement = root.element("data");
			
			Element attributesElement = dataElement.element("attributes");
			
			for (Object obj : attributesElement.elements("attribute")) {
				Element attributeElement = (Element)obj;
				Class<?> attributeClass = Class.forName(attributeElement.attributeValue("class"));
				
				Attribute<?> attribute = (Attribute<?>)attributeClass.getConstructor(String.class).newInstance(attributeElement.attributeValue("name"));
				table.addAttribute(attribute);
			}
			
			Element instancesElement = dataElement.element("instances");
			
			for (Object obj1 : instancesElement.elements("instance")) {
				Element instanceElement = (Element)obj1;
				Instance instance = new Instance();
				instance.setId(UUID.fromString(instanceElement.attributeValue("id")));
				
				int index = 0;
				
				for (Object obj2 : instanceElement.elements("value")) {
					Element valueElement = (Element)obj2;
					instance.set(table.getAttribute(index), valueElement.getText());
					index++;
				}
				
				table.addInstance(instance);
			}
			
			canvas.getPropertyRegistry().put("data", table);
			
			// parse the colormap
			if (root.element("colormap") != null) {
			canvas.getPropertyRegistry().put("colormap", ColormapFactory.getInstance().getColormap(
					root.elementText("colormap")));
			}
			
			// parse the selected axes
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
			
			canvas.getPropertyRegistry().get("xAxis").setValue(axes.size() > 0 ? axes.get(0) : null);
			canvas.getPropertyRegistry().get("yAxis").setValue(axes.size() > 1 ? axes.get(1) : null);
			canvas.getPropertyRegistry().get("zAxis").setValue(axes.size() > 2 ? axes.get(2) : null);
			canvas.getPropertyRegistry().get("colorAxis").setValue(axes.size() > 3 ? axes.get(3) : null);
			canvas.getPropertyRegistry().get("sizeAxis").setValue(axes.size() > 4 ? axes.get(4) : null);
			canvas.getPropertyRegistry().get("axes").setValue(axes);
			
			if (root.element("xAxis") != null) {
				canvas.getPropertyRegistry().get("xAxis").setValue(axes.get(Integer.parseInt(root.elementText("xAxis"))));
			}
			
			if (root.element("yAxis") != null) {
				canvas.getPropertyRegistry().get("yAxis").setValue(axes.get(Integer.parseInt(root.elementText("yAxis"))));
			}
			
			if (root.element("zAxis") != null) {
				canvas.getPropertyRegistry().get("zAxis").setValue(axes.get(Integer.parseInt(root.elementText("zAxis"))));
			}
			
			if (root.element("colorAxis") != null) {
				canvas.getPropertyRegistry().get("colorAxis").setValue(axes.get(Integer.parseInt(root.elementText("colorAxis"))));
			}
			
			if (root.element("sizeAxis") != null) {
				canvas.getPropertyRegistry().get("sizeAxis").setValue(axes.get(Integer.parseInt(root.elementText("sizeAxis"))));
			}
			
			if (root.element("visibilityAxis") != null) {
				int index = Integer.parseInt(root.elementText("visibilityAxis"));
				
				if (index >= 0 && index < axes.size()) {
						canvas.getPropertyRegistry().get("visibilityAxis").setValue(axes.get(index));
				}
			}
			
			// parse the widgets
			Element widgetsElement = root.element("widgets");
			
			for (Object obj : widgetsElement.elements("widget")) {
				Element widgetElement = (Element)obj;
				
				Class<?> widgetClass = Class.forName(widgetElement.attributeValue("class"));
				Object widgetInstance = widgetClass.newInstance();
				
				if (widgetInstance instanceof Widget) {
					canvas.add((Widget<?>)widgetInstance);
					
					if (widgetInstance instanceof SerializableWidget) {
						((SerializableWidget)widgetInstance).restoreState(widgetElement.elements().get(0), canvas);
					}
				}
			}
		} catch (DocumentException | ClassNotFoundException | IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			e.printStackTrace();
			throw new IOException(e);
		}
	}

}
