package j3;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.IntegerPropertyBase;

public class PlotControls {
	
	private IntegerProperty x = new IntegerPropertyBase() {

		@Override
		public Object getBean() {
			return PlotControls.this;
		}

		@Override
		public String getName() {
			return "x";
		}
		
	};

}
