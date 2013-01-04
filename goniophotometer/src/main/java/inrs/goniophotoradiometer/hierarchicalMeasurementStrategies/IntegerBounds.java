package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;
/**
 * This class indicates integer value bounds
 * @author jeanmarc.deniel
 *
 */
public class IntegerBounds {
	private int upperBound;
	private int lowerBound;
	
	public IntegerBounds(){
		setLowerBound(0);
		setUpperBound(0);
	}
	
	public IntegerBounds(int lower_bound, int upper_bound){
		setLowerBound(lower_bound);
		setUpperBound(upper_bound);
	}
	
	/**
	 * 
	 * @param tested_value
	 * @return true if tested_value is in the range of lower to upper bounds (included).
	 */
	public final boolean boundsValue(int tested_value){
		return (getLowerBound() <= tested_value)&& (tested_value <= getUpperBound());
	}
	
	public final float getWidth(){
		return getUpperBound() - getLowerBound();
	}
	
	public final int getUpperBound() {
		return upperBound;
	}
	public final void setUpperBound(int upper_bound) {
		this.upperBound = upper_bound;
	}
	public final int getLowerBound() {
		return lowerBound;
	}
	public final void setLowerBound(int lower_bound) {
		this.lowerBound = lower_bound;
	}
}
