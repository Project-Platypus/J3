package j3.dataframe;

import org.apache.commons.lang3.ClassUtils;

public class Test {
	
	public static void main(String[] args) {
		DataFrame df = new DataFrame();
		df.appendColumn(new DoubleColumn());
		df.appendColumn(new StringColumn());
		
		df.getColumn(0).set(0, 25.0);
		df.getColumn(1).set(0, "hello");
		
		df.getColumn(0).set(0, 14);
		
		System.out.println(df.getColumn(0).get(0));
		System.out.println(df.getColumn(1).get(0));
		
		System.out.println(ClassUtils.isAssignable(Integer.class, Double.class, true));
	}

}
