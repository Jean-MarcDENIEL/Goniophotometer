package inrs.goniophotoradiometer;

import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;

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
	void 	addMeasurementPosition(MeasurementPoint measurement_pos_c_g_rad);
	/**
	 * Enters a set of measurement positions in the scheduler.<br>
	 * There is no relationship nor meaning in the order of the positions in the array parameter. 
	 * @param measurement_pos_c_g_rad_tab An array of (C, Gamma) positions in radian.
	 */
	void	addMeasurementPositions(MeasurementPoint[] measurement_pos_c_g_rad_tab);
	/**
	 * 
	 * @return true if there is still at last one measurement position to deliver. 
	 */
	boolean	hasWaitingMeasurementPositions();
	/**
	 * Schedules a measurement position among waiting measurement positions. 
	 * This depends on previously scheduled positions and on waiting positions.<br>
	 * The returned position is removed from the waiting positions set.
	 * 
	 * @return The next measurement (C, Gamma) position in radian or null if there is no.
	 */
	MeasurementPoint chooseNextMeasurementPosition();
}
