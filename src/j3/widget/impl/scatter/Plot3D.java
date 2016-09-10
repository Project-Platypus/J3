package j3.widget.impl.scatter;

import j3.Axis;

import java.util.List;

import javafx.beans.property.ObjectProperty;

public interface Plot3D {

	public List<ObjectProperty<Axis>> getAxisProperties();
	
}
