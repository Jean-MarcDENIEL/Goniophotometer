package inrs.goniophotoradiometer;

import c4sci.math.geometry.plane.PlaneVector;
import inrs.goniophotoradiometer.exceptions.GoniometryException;
import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;
import inrs.goniophotoradiometer.motion.MotionEngine;

/**
 * This class is the main loop of the measurement process.
 * @author jeanmarc.deniel
 *
 */
public class Goniophotoradiometer {
	private boolean 			shouldStopFlag;
	private MotionScheduler		motionScheduler;
	private MeasurementStrategy	measurementStrategy;
	private MotionEngine		armEngine;
	private MotionEngine		turntableEngine;
	
	private static final float	HALF_ROUND_DEGREE 	= 180f;
	private static final float	QUATER_ROUND_DEGREE = 90f;
	private static final float	ROUND_DEGREE		= 360f;
	
	public Goniophotoradiometer(MotionScheduler motion_scheduler, MeasurementStrategy meas_strat, MotionEngine arm_engine, MotionEngine turntable_engine){
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
		armEngine.setToZeroPosition();
		armEngine.setMaxPosition(QUATER_ROUND_DEGREE);
		armEngine.setMinPosition(-QUATER_ROUND_DEGREE);
		turntableEngine.setMinPosition(0f);
		turntableEngine.setMaxPosition(ROUND_DEGREE);
		
		
		// initialize primary measurements to perform
		//
		motionScheduler.addMeasurementPositions(measurementStrategy.getPrimaryMeasurementPositions());
		
		// main loop
		//
		while (motionScheduler.hasWaitingMeasurementPositions() && ! shouldStop()){
			MeasurementPoint _new_pos = motionScheduler.chooseNextMeasurementPosition();
			goToPosition(_new_pos.getMeasurementPosition());
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
