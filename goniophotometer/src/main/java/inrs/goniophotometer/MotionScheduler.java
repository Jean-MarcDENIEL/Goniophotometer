package inrs.goniophotometer;

import c4sci.math.geometry.plane.PlaneVector;

/**
 * This interface defines classes that are able to choose measurement motions
 * according to measurement positions.
 * @author jeanmarc.deniel
 *
 */
public interface MotionScheduler {
	/**
	 * Enters a measurement position in the scheduler.
	 * 
	 * @param measurement_pos_c_g_rad A (C, gamma) position in radian.
	 */
	void 	addMeasurementPosition(PlaneVector measurement_pos_c_g_rad);
	/**
	 * Enters a set of measurement positions in the scheduler.<br>
	 * There is no relationship nor meaning in the order of the positions in the array parameter. 
	 * @param measurement_pos_c_g_rad_tab An array of (C, Gamma) positions in radian.
	 */
	void	addMeasurementPositions(PlaneVector[] measurement_pos_c_g_rad_tab);
	/**
	 * 
	 * @return true if there is still at last one measurement position to deliver. 
	 */
	boolean	hasWaitingMeasurementPositions();
	/**
	 * Schedules a measurement position among waiting measurement positions. This depends on previously scheduled positions and on waiting positions.<br>
	 * 
	 * @return The next measurement (C, Gamma) position in radian. 
	 */
	PlaneVector chooseNextMeasurementPosition();
}
