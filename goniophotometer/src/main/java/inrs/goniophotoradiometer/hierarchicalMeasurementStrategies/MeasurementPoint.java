package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;

import c4sci.math.geometry.plane.PlaneVector;

/**
 * This class represents the informations referring to the measurement in a certain (C, Gamma) position.<br>
 * The initial state is "not yet measured"
 * @author jeanmarc.deniel
 *
 */
public class MeasurementPoint {
	private boolean hasBeenMeasuredFlag;
	private PlaneVector measurementPosition;
	
	public MeasurementPoint(PlaneVector meas_point) {
		measurementPosition = meas_point;
		setAsNotYetMeasured();
	}
	@SuppressWarnings("unused")
	private MeasurementPoint(){}
	
	public final boolean hasBeenYetMeasured(){
		return hasBeenMeasuredFlag;
	}
	public final void setAsMeasured(){
		hasBeenMeasuredFlag = true;
	}
	public final void setAsNotYetMeasured(){
		hasBeenMeasuredFlag = false;
	}
	public final PlaneVector getMeasurementPosition() {
		return measurementPosition;
	}
}
