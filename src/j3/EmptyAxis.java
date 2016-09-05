package j3;

import java.util.List;

public class EmptyAxis extends Axis {

	public EmptyAxis() {
		super(-1, "");
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
	public void scale(List values) {
		// do nothing
	}

	@Override
	public double map(Object value) {
		return 0;
	}

}
