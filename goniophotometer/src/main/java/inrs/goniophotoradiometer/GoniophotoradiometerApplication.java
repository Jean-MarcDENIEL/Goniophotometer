package inrs.goniophotoradiometer;

import inrs.goniophotoradiometer.exceptions.GoniometryException;
import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.fileSupportImplementation.FileSupportHierarchicalMeasurementStrategy;
import inrs.goniophotoradiometer.measurementImplementations.CameraHead;
import inrs.goniophotoradiometer.motion.ShortestTravelTimeMotionScheduler;
import inrs.goniophotoradiometer.motion.xliControlledImplementation.XliControlledMotionEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


public class GoniophotoradiometerApplication {
	
	public static final int 		MAX_C_DELTA 			= 30;
	public static final int 		MAX_G_DELTA 			= 40;
	public static final String 		ARM_PORT 				= "COM1";	// COM1 = right plug
	public static final int			ARM_COUNT_PER_REV 		= 4000;
	public static final int			ARM_REV_RATIO 			= 500 ;
	public static final float		ARM_MAX_SPEED_DEG_SEC 	= 1.163f;
	public static final float		ARM_ACC_DEG_SEC_2 		= 1.0f;	
	public static final String 		TURNTABLE_PORT 			= "COM2";	// COM2 = left plug
	public static final int			TURNTABLE_COUNT_PER_REV = 4000;
	public static final int			TURNTABLE_REV_RATIO 	= 64;
	public static final int			TURNTABLE_SMALL_GEAR 	= 15;
	public static final int			TURNTABLE_BIG_GEAR 		= 120;
	public static final float		TURNTABLE_MAX_SPEED_DEG_SEC = 3.35f;
	public static final float		TURNTABLE_ACC_DEG_SEC_2 = 1.0f;
	public static final float		HOMING_SMALL_MOTION 	= 10f;
	public static final String		BMP_IMAGE_FORMAT 		= "bmp";
	public static final float		MIN_G_VALUE	=			0f;
	public static final float		MAX_G_VALUE = 			90f;
	public static final float		MIN_C_VALUE	=			0f;
	public static final float		MAX_C_VALUE	=			90f;
	public static final float		QUARTER_TURN_DEG = 		90f;
	
	/**
	 * First argument is a working directory
	 * @param str_args
	 */
	public static void main(final String[] str_args){
		try{
			String _directory = null;
			BufferedReader _buff = new BufferedReader(new InputStreamReader(System.in));
			if (str_args.length > 0){
				System.out.println("Output directory : " + str_args[0]);
				_directory = str_args[0];
			}
			else{
				System.out.print("Enter a working directory : ");
				_directory = _buff.readLine();
				if (_directory == null){
					errorExiting("null directory");
				}
				System.out.println("Working in : " + _directory);
			}
			File _file_dir = new File(_directory);
			if (_file_dir.exists()){
				if (_file_dir.isFile()){
					errorExiting(_directory + "exists and is not a directory !");
				}
			}
			else{
				if (!_file_dir.mkdir()){
					errorExiting("Cannot create the working directory !");
				}
			}
			
			CameraHead 			_camera 	= new CameraHead(BMP_IMAGE_FORMAT, BMP_IMAGE_FORMAT);
			MeasurementStrategy _strategy 	= new FileSupportHierarchicalMeasurementStrategy(MAX_C_DELTA, MAX_G_DELTA, _directory, _camera);
			MotionScheduler		_scheduler = new 
					ShortestTravelTimeMotionScheduler(	ARM_ACC_DEG_SEC_2, ARM_MAX_SPEED_DEG_SEC, 
														TURNTABLE_ACC_DEG_SEC_2, TURNTABLE_MAX_SPEED_DEG_SEC);
			
			XliControlledMotionEngine		_arm 		= new XliControlledMotionEngine(ARM_PORT, ARM_COUNT_PER_REV, ARM_REV_RATIO);
			XliControlledMotionEngine		_turntable	= new XliControlledMotionEngine(TURNTABLE_PORT, TURNTABLE_COUNT_PER_REV, TURNTABLE_REV_RATIO*TURNTABLE_BIG_GEAR/TURNTABLE_SMALL_GEAR);

			System.out.println("Need for homing motions (y n) ? [n]");
			String 	_str_need_for_homing = _buff.readLine();
			boolean	_need_for_homing = (_str_need_for_homing == null)? true : (_str_need_for_homing.compareTo("y") == 0);
			
			System.out.println("Performing arm homing");
			_arm.setAngularMaxVelocity(ARM_MAX_SPEED_DEG_SEC);
			_arm.setAngularAcceleration(ARM_ACC_DEG_SEC_2);
			_arm.setAngularDeceleration(ARM_ACC_DEG_SEC_2);
			_arm.setVelocityThreshold(0.0f);
			_arm.setHardLimitsAllowed(true);
			_arm.setHardLimitsNormalyOpen(true);
			_arm.setInvertMotionSense(false);
			if (_need_for_homing){
				_arm.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
				_arm.performHoming();
				_arm.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
				_arm.processRelativeMove(HOMING_SMALL_MOTION);
				_arm.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
				_arm.performHoming();
				_arm.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
				_arm.processRelativeMove(QUARTER_TURN_DEG);
				_arm.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
			}
			_arm.setToZeroPosition();
			_arm.setInvertMotionSense(true);
			_arm.setMinPosition(MIN_G_VALUE);
			_arm.setMaxPosition(MAX_G_VALUE);
			System.out.println("Arm Homing performed");

			System.out.println("Performing turntable homing");
			_turntable.setAngularMaxVelocity(TURNTABLE_MAX_SPEED_DEG_SEC);
			_turntable.setAngularAcceleration(TURNTABLE_ACC_DEG_SEC_2);
			_turntable.setAngularDeceleration(TURNTABLE_ACC_DEG_SEC_2);
			_turntable.setVelocityThreshold(0.0f);
			_turntable.setHardLimitsAllowed(true);
			_turntable.setHardLimitsNormalyOpen(true);
			_turntable.setInvertMotionSense(false);
			if (_need_for_homing){
				_turntable.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
				_turntable.performHoming();
				_turntable.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
				_turntable.processRelativeMove(HOMING_SMALL_MOTION);
				_turntable.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
				_turntable.performHoming();
				_turntable.waitForEndOfMotionAndSetTheoricalAbsolutePosition();
			}
			_turntable.setToZeroPosition();
			_turntable.setMinPosition(MIN_C_VALUE);
			_turntable.setMaxPosition(MAX_C_VALUE);
			System.out.println("Turntable Homing performed");
			
			Goniophotoradiometer	_gonio = new Goniophotoradiometer(_scheduler, _strategy, _arm, _turntable);
			_gonio.performMeasurement();
			
			System.exit(0);
			
		}
		catch(IOException _e){

		} catch (RadiometryException _e) {
			_e.printStackTrace();
			errorExiting("An issue with the measurement head arrised");
		} catch (GoniometryException _e2) {
			_e2.printStackTrace();
			errorExiting("An issue with the gonio arrised");
		}
	}
	
    private static void errorExiting(String error_msg){
    	System.out.println(error_msg);
    	System.out.println("Exiting with code 1");
    	System.exit(1);
    }
}
