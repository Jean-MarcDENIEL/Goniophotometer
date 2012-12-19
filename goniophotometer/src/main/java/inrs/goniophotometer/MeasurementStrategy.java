package inrs.goniophotometer;

import c4sci.math.geometry.plane.PlaneVector;

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
	PlaneVector[] getPrimaryMeasurementPositions();
	/**
	 * Measures a signal, then decides of following measurement positions. 
	 * @param measurement_pos_c_g_deg The measurement position in degree.
	 * @return A set of measurements (C, Gamma, in degrees) to perform after the parameter position measurement has been performed.
	 */
	PlaneVector[] performMeasurement(PlaneVector measurement_pos_c_g_deg);
}
