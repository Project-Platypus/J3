package j3.widget;

import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface TargetableWidget {

	public ObservableList<Widget<? extends Node>> getDependencies();
	
}
