package j3;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;

public class RealDomain implements Domain<Number> {
	
    private DoubleProperty minValue = new DoublePropertyBase() {
        @Override protected void invalidated() {
            
        }

        @Override
        public Object getBean() {
            return RealDomain.this;
        }

        @Override
        public String getName() {
            return "minValue";
        }
    };

	public final double getMinValue() { return minValue.get(); }
    public final void setMinValue(double value) { minValue.set(value); }
    public final DoubleProperty minValueProperty() { return minValue; }
    
    private DoubleProperty maxValue = new DoublePropertyBase() {
        @Override protected void invalidated() {
            
        }

        @Override
        public Object getBean() {
            return RealDomain.this;
        }

        @Override
        public String getName() {
            return "maxValue";
        }
    };
    public final double getMaxValue() { return maxValue.get(); }
    public final void setMaxValue(double value) { maxValue.set(value); }
    public final DoubleProperty maxValueProperty() { return maxValue; }
    
    public RealDomain(double minValue, double maxValue) {
		super();
		setMinValue(minValue);
		setMaxValue(maxValue);
	}

}
