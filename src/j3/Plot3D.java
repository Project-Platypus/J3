package j3;

import java.util.List;

import javafx.beans.property.ObjectProperty;

public interface Plot3D {

	public List<ObjectProperty<Axis>> getAxisProperties();
	
}
