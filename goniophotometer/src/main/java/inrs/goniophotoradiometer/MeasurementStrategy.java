package inrs.goniophotoradiometer;

import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;

/**
 * This interface represents the device that is able to perform measurement on a certain position, then 
 * decides of following measurement positions.
 *    
 * @author jeanmarc.deniel
 *
 */
public interface MeasurementStrategy {
	/**
	 * 
	 * @return The measurements to perform at the beginning of the measurement session, in (C, Gamma) expressed in degrees.
	 */
	MeasurementPoint[] getPrimaryMeasurementPositions() throws RadiometryException;
	/**
	 * Measures a signal, sets it as measured, then decides of following measurement positions. 
	 * @param measurement_pos_c_g_deg The measurement point that will be completed.
	 * @return A set of measurements (C, Gamma, in degrees) to perform after the parameter position measurement has been performed.
	 */
	MeasurementPoint[] performMeasurement(MeasurementPoint measurement_pos_c_g_deg) throws RadiometryException;
}
