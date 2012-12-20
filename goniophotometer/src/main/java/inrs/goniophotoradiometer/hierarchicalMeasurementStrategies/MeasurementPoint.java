package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;
/**
 * This class represents the informations referring to the measurement in a certain (C, Gamma) position.<br>
 * The initial state is "not yet measured"
 * @author jeanmarc.deniel
 *
 */
public class MeasurementPoint {
	private boolean hasBeenMeasuredFlag;
	
	public boolean hasBeenYetMeasured(){
		return hasBeenMeasuredFlag;
	}
	public void setAsMeasured(){
		hasBeenMeasuredFlag = true;
	}
	public void setAsNotYetMeasured(){
		hasBeenMeasuredFlag = false;
	}
}
