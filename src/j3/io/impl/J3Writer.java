package j3.io.impl;

import j3.Axis;
import j3.Canvas;
import j3.colormap.Colormap;
import j3.dataframe.Attribute;
import j3.dataframe.DataFrame;
import j3.dataframe.Instance;
import j3.widget.SerializableWidget;
import j3.widget.Widget;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class J3Writer {
	
	@SuppressWarnings("unchecked")
	public void save(File file, Canvas canvas) throws IOException {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("j3");
		
		// save the data frame contents
		DataFrame table = (DataFrame)canvas.getPropertyRegistry().get("data").getValue();
		Element dataElement = root.addElement("data");
		
		Element attributesElement = dataElement.addElement("attributes");
		
		for (Attribute<?> attribute : table.getAttributes()) {
			Element attributeElement = attributesElement.addElement("attribute");
			attributeElement.addAttribute("name", attribute.getName());
			attributeElement.addAttribute("class", attribute.getClass().getCanonicalName());
		}
		
		Element instancesElement = dataElement.addElement("instances");
		
		for (Instance instance : table.getInstances()) {
			Element instanceElement = instancesElement.addElement("instance");
			instanceElement.addAttribute("id", instance.getId().toString());
			
			for (Attribute<?> attribute : table.getAttributes()) {
				Element valueElement = instanceElement.addElement("value");
				valueElement.setText(instance.get(attribute).toString());
			}
		}
		
		// save the colormap
		Colormap colormap = (Colormap)canvas.getPropertyRegistry().get("colormap").getValue();
		
		if (colormap != null) {
			root.addElement("colormap").setText(colormap.getName());
		}
		
		// save the selected axes
		List<Axis> axes = (List<Axis>)canvas.getPropertyRegistry().get("axes").getValue();
		
		if (canvas.getPropertyRegistry().contains("xAxis") && canvas.getPropertyRegistry().get("xAxis").getValue() != null) {
			root.addElement("xAxis").setText(Integer.toString(axes.indexOf(canvas.getPropertyRegistry().get("xAxis").getValue())));
		}
		
		if (canvas.getPropertyRegistry().contains("yAxis") && canvas.getPropertyRegistry().get("yAxis").getValue() != null) {
			root.addElement("yAxis").setText(Integer.toString(axes.indexOf(canvas.getPropertyRegistry().get("yAxis").getValue())));
		}
		
		if (canvas.getPropertyRegistry().contains("zAxis") && canvas.getPropertyRegistry().get("zAxis").getValue() != null) {
			root.addElement("zAxis").setText(Integer.toString(axes.indexOf(canvas.getPropertyRegistry().get("zAxis").getValue())));
		}
		
		if (canvas.getPropertyRegistry().contains("colorAxis") && canvas.getPropertyRegistry().get("colorAxis").getValue() != null) {
			root.addElement("colorAxis").setText(Integer.toString(axes.indexOf(canvas.getPropertyRegistry().get("colorAxis").getValue())));
		}
		
		if (canvas.getPropertyRegistry().contains("sizeAxis") && canvas.getPropertyRegistry().get("sizeAxis").getValue() != null) {
			root.addElement("sizeAxis").setText(Integer.toString(axes.indexOf(canvas.getPropertyRegistry().get("sizeAxis").getValue())));
		}
		
		if (canvas.getPropertyRegistry().contains("visibilityAxis") && canvas.getPropertyRegistry().get("visibilityAxis").getValue() != null) {
			root.addElement("visibilityAxis").setText(Integer.toString(axes.indexOf(canvas.getPropertyRegistry().get("visibilityAxis").getValue())));
		}

		// save the widgets
		Element widgets = root.addElement("widgets");
		boolean unsupportedWidget = false;

		for (Widget<?> widget : canvas.getWidgets()) {
			if (widget instanceof SerializableWidget) {
				Element widgetElement = widgets.addElement("widget");
				widgetElement.addAttribute("class", widget.getClass().getCanonicalName());
				widgetElement.add(((SerializableWidget)widget).saveState(canvas));
			} else {
				unsupportedWidget = true;
			}
		}

		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = null;

		try {
			writer = new XMLWriter(new FileWriter(file), format);
			writer.write(document);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		
		if (unsupportedWidget) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Missing Widgets");
			alert.setHeaderText(null);
			alert.setContentText("Some widgets could not be saved.  These will not appear when reopening this export.");
			alert.showAndWait();
		}
	}

}
