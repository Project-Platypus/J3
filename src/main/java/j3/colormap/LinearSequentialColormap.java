package j3.colormap;

public class LinearSequentialColormap extends AbstractListColormap {

	private String name;

	private double[][] red;

	private double[][] green;

	private double[][] blue;

	public LinearSequentialColormap(String name, double[][] values) {
		super();
		this.name = name;

		red = new double[values.length][3];
		green = new double[values.length][3];
		blue = new double[values.length][3];

		for (int i = 0; i < values.length; i++) {
			double x = i / (double) (values.length - 1);

			red[i][0] = x;
			red[i][1] = red[i][2] = values[i][0];
			green[i][0] = x;
			green[i][1] = green[i][2] = values[i][1];
			blue[i][0] = x;
			blue[i][1] = blue[i][2] = values[i][2];
		}
	}

	public LinearSequentialColormap(String name, double[][] red, double[][] green, double[][] blue) {
		super();
		this.name = name;
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	protected double[] createMapping(int size, double[][] data, double gamma) {
		if (data[0][0] != 0.0 || data[data.length - 1][0] != 1.0) {
			throw new IllegalArgumentException("data mapping points must start wth x=0 and end with x=1");
		}

		for (int i = 0; i < data.length - 1; i++) {
			if (data[i][0] >= data[i + 1][0]) {
				throw new IllegalArgumentException("data mapping points must have x in increasing order");
			}
		}

		double[] values = new double[size];
		int lastIndex = 0;
		values[0] = data[0][2];

		for (int i = 1; i < data.length; i++) {
			int nextIndex = (int) (data[i][0] * (size - 1));

			for (int j = lastIndex + 1; j <= nextIndex; j++) {
				values[j] = data[i - 1][2]
						+ (data[i][1] - data[i - 1][2]) * (j - lastIndex - 1) / (double) (nextIndex - lastIndex - 1);

				if (values[j] < 0.0) {
					values[j] = 0.0;
				} else if (values[j] > 1.0) {
					values[j] = 1.0;
				}
			}

			lastIndex = nextIndex;
		}

		return values;
	}

	@Override
	public double[][] getColorList() {
		double[] redValues = createMapping(256, red, 1.0);
		double[] greenValues = createMapping(256, green, 1.0);
		double[] blueValues = createMapping(256, blue, 1.0);

		double[][] colorList = new double[256][3];

		for (int i = 0; i < 256; i++) {
			colorList[i][0] = redValues[i];
			colorList[i][1] = greenValues[i];
			colorList[i][2] = blueValues[i];
		}

		return colorList;
	}

	@Override
	public String getName() {
		return name;
	}

}
