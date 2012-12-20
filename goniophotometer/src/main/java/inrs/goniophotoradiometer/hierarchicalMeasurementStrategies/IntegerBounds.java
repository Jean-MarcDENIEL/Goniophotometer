package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;
/**
 * This class indicates integer value bounds
 * @author jeanmarc.deniel
 *
 */
public class IntegerBounds {
	private int upperBound;
	private int lowerBound;
	
	public IntegerBounds(int lower_bound, int upper_bound){
		setLowerBound(lower_bound);
		setUpperBound(upper_bound);
	}
	
	/**
	 * 
	 * @param tested_value
	 * @return true if tested_value is in the range of lower to upper bounds (included).
	 */
	public boolean boundsValue(int tested_value){
		return (getLowerBound() <= tested_value)&& (tested_value <= getUpperBound());
	}
	
	public int getUpperBound() {
		return upperBound;
	}
	public void setUpperBound(int upper_bound) {
		this.upperBound = upper_bound;
	}
	public int getLowerBound() {
		return lowerBound;
	}
	public void setLowerBound(int lower_bound) {
		this.lowerBound = lower_bound;
	}
}
