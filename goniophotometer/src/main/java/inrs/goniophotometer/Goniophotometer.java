package inrs.goniophotometer;

import c4sci.math.geometry.plane.PlaneVector;
import inrs.goniophotometer.exceptions.GoniometryException;
import inrs.goniophotometer.exceptions.RadiometryException;
import inrs.goniophotometer.motion.MotionEngine;

/**
 * This class is the main loop of the measurement process.
 * @author jeanmarc.deniel
 *
 */
public class Goniophotometer {
	private boolean 			shouldStopFlag;
	private MotionScheduler		motionScheduler;
	private MeasurementStrategy	measurementStrategy;
	private MotionEngine		armEngine;
	private MotionEngine		turntableEngine;
	
	private static final float	HALF_ROUND_DEGREE = 180.0f;
	
	public Goniophotometer(MotionScheduler motion_scheduler, MeasurementStrategy meas_strat, MotionEngine arm_engine, MotionEngine turntable_engine){
		shouldStopFlag 		= false;
		motionScheduler		= motion_scheduler;
		measurementStrategy	= meas_strat;
		armEngine			= arm_engine;
		turntableEngine		= turntable_engine;
	}
	
	public synchronized boolean shouldStop() {
		return shouldStopFlag;
	}
	public synchronized void setShouldStop(boolean should_stop) {
		shouldStopFlag = should_stop;
	}

	public void performMeasurement() throws GoniometryException, RadiometryException {
		// first make a 0 positioning
		//
		goToPosition(new PlaneVector(-HALF_ROUND_DEGREE, -HALF_ROUND_DEGREE));
		
		// initialize primary measurements to perform
		//
		motionScheduler.addMeasurementPositions(measurementStrategy.getPrimaryMeasurementPositions());
		
		// main loop
		//
		while (motionScheduler.hasWaitingMeasurementPositions() && ! shouldStop()){
			PlaneVector _new_pos = motionScheduler.chooseNextMeasurementPosition();
			goToPosition(_new_pos);
			motionScheduler.addMeasurementPositions(measurementStrategy.performMeasurement(_new_pos));
		}
		
		
	}
	
	private void goToPosition(PlaneVector c_g_pos_deg) throws GoniometryException{
		armEngine.processAbsoluteMove(c_g_pos_deg.getY());
		turntableEngine.processAbsoluteMove(c_g_pos_deg.getX());
		armEngine.waitForEndOfMotion();
		turntableEngine.waitForEndOfMotion();
		if (armEngine.isFaulty()){
			throw new GoniometryException("Arm engine is faulty : " + armEngine.getUserFaults());
		}
		if (turntableEngine.isFaulty()){
			throw new GoniometryException("Turntable engine is faulty : " + turntableEngine.getUserFaults());
		}
	}
}
