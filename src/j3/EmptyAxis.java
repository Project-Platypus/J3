package j3;

import java.util.Collection;

public class EmptyAxis extends Axis {

	public EmptyAxis() {
		super(null);
	}

	@Override
	public String[] getTickLabels() {
		return new String[0];
	}

	@Override
	public double[] getTickPositions() {
		return new double[0];
	}

	@Override
	public void scale(Collection<?> values) {
		// do nothing
	}

	@Override
	public double map(Object value) {
		return 0;
	}

}
