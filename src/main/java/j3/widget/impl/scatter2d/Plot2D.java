package j3.widget.impl.scatter2d;

import j3.Axis;

import java.util.List;

import javafx.beans.property.ObjectProperty;

public interface Plot2D {

	public List<ObjectProperty<Axis>> getAxisProperties();

}
