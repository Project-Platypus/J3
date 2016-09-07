//package j3.dataframe;
//
//import java.util.List;
//import java.util.Map;
//import java.util.function.Function;
//
//public class FactorColumn implements Column<String> {
//	
//	private List<Integer> values;
//	
//	private Map<Integer, String> intToStringMapping;
//	
//	private Map<String, Integer> stringToIntMapping;
//
//	@Override
//	public String get(int index) {
//		return intToStringMapping.get(values.get(index));
//	}
//
//	@Override
//	public void set(int index, String value) {
//		Integer factor = stringToIntMapping.get(value);
//		
//		if (factor)
//		
//		values.set(index, stringToIntM)
//	}
//
//	@Override
//	public Column<? extends String> apply(
//			Function<? super String, ? extends String> op) {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//}
