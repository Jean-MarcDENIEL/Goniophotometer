package inrs.goniophotoradiometer.motion.xliControlledImplementation;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

/*import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;*/

import inrs.goniophotoradiometer.motion.MotionEngine;

import c4sci.io.serial.SerialDevice;
import c4sci.io.serial.SerialStateDecoder;
import c4sci.io.serial.SerialStateParsingException;

/**
 * This class implements a motion engine that is composed of a stepper motor driven through a Parker Xli controller.<br>
 * The communication is done through RS232 links using EASI language.<br>
 * <b>Usage :</b><br>
 * All set/get methods modify the structure inner state without sending any order to the controller.<br>
 * Settings and orders are send through the 
 * 
 * @author jeanmarc.deniel
 *
 */
public final class XliControlledMotionEngine extends SerialDevice implements MotionEngine {

	//private String			serialPortName;
	private int 			engineNumber;
	private int				motorCurrent;
	private int				motorStandbyCurrent;
	private int 			motorResolution;
	private int				reducerRatio;
	//private SerialPort		serialPort;
	//private int				delayBetweenSendsMilliSec;
	private boolean			homingPositiveReferenceEdge;
	private boolean			homeSwitchNormallyClosed;
	private boolean			homePositiveSense;
	private float			homingVelocityRevPerSecond;
	private float			homingAccelerationDecelerationRevPerSecond;
	private boolean			invertMotionSense;



	private static final float	DEFAULT_ACCELERATION_DEGREES_PER_SQUARE_SECOND 	= 1f;
	private static final float	DEFAULT_DECELERATION_DEGREES_PER_SQUARE_SECOND 	= 1f;
	private static final float	DEFAULT_MAX_VELOCITY_DEGREE_PER_SECOND 			= 2f;
	private static final int	DEFAULT_DEGREE_LOWER_LIMIT						= 0;
	private static final int	DEFAULT_DEGREE_UPPER_LIMIT						= 10;
	private static final int	DEFAULT_DELAY_BETWEEN_SENDS_MILLISEC			= 350;
	private static final float	DEFAULT_DEGREES_PER_SECOND_VELOCITY_THRESHOLD	= 0.0f; 
	private static final int	DEFAULT_TIMEOUT_MILLISEC						= 100;
	private static final int	DEFAULT_MOTOR_CURRENT_PERCENT					= 50;

	private static final float	ONE_REV_DEGREES									= 360.0f;

	private boolean 		lowerHardLimitReached;
	private boolean			upperHardLimitReached;
	private boolean			hardLimitsNormalyOpen;
	private boolean			hardLimitsAllowed;

	private int				actualTheoricalCountPosition;
	private int				goalTheoricalCountPosition;
	private int				countDistance;
	private int 			lowerCountPositionLimit;
	private int 			upperCountPositionLimit;
	private float 			revPerSquareSecondAcceleration;
	private float 			revPerSquareSecondDeceleration;
	private float			revPerSecondMaxSpeed;
	private float 			velocityThreshold;
	private boolean			inPosition;
	private boolean			inMovement;
	private boolean			isBusy;

	private static final int 		FLAG_COUNT = 32; 

	private boolean[] 		driveFaultsTab;
	private boolean[]		statusBitsTab;
	private boolean[]		userFaultsTab;

	//private StringBuffer		controllerReturnToDecode;
	//private SerialStateDecoder	stateDecoderToUse;

	/**
	 * This class gets controller returns from the RS232 interface.
	 * It updates the {@link XliControlledMotionEngine}'s inner state by :
	 * <ol>
	 * <li> calling {@link XliControlledMotionEngine#setControllerReturnToDecode()} </li>
	 * <li> invoking the {@link XliControlledMotionEngine#getStateDecoderToUse()} result</li>
	 * <ol>
	 * @author jeanmarc.deniel
	 *
	 */
	/*
	private class EasiDecoder implements SerialPortEventListener {
		private InputStream			decodedStream;

		public EasiDecoder(InputStream decoded_stream){
			decodedStream = decoded_stream;
		}

		public void serialEvent(SerialPortEvent serial_event) {
			getControllerReturnToDecode().setLength(0);
			try{
				int _read_data = decodedStream.read();
				// first receives the message from the controller
				//
				while ((_read_data> -1)&& (_read_data != '\n')){
					getControllerReturnToDecode().append((char)_read_data);
					_read_data = decodedStream.read();
				}
				// if the message is a command result (i.e begins with '*') then it is decoded
				//
				if (getControllerReturnToDecode().charAt(0) == '*'){
					SerialStateDecoder _state_decoder = getStateDecoderToUse();
					if (_state_decoder != null){
						_state_decoder.decodeState(getControllerReturnToDecode().toString(), XliControlledMotionEngine.this);
					}
				}
			}
			catch(IOException _e){
				_e.printStackTrace();
			} catch (SerialStateParsingException _e) {
				_e.printStackTrace();
			}

		}
	};*/

	/**
	 * 
	 * @param serial_port_name "COM1", "COM2" etc.
	 * @param count_per_rev number of motor counts to have it make one round
	 * @param rev_ratio ratio between motor and reducer (1:rev_ratio)
	 */
	public XliControlledMotionEngine(String serial_port_name, int count_per_rev, int rev_ratio){

		super(serial_port_name, "\r\n", '\n', DEFAULT_DELAY_BETWEEN_SENDS_MILLISEC, DEFAULT_TIMEOUT_MILLISEC);
		
		driveFaultsTab	= new boolean[FLAG_COUNT];
		statusBitsTab	= new boolean[FLAG_COUNT];
		userFaultsTab	= new boolean[FLAG_COUNT];
		//serialPort		= null;

		//setControllerReturnToDecode(new StringBuffer());

		setEngineNumber(1);
		//setSerialPortName(serial_port_name);
		setMotorResolution(count_per_rev);
		setReducerRatio(rev_ratio);
		setMotorCurrent(DEFAULT_MOTOR_CURRENT_PERCENT);
		setRevPerSquareSecondAcceleration(convertFromDegreeToRev(DEFAULT_ACCELERATION_DEGREES_PER_SQUARE_SECOND));
		setRevPerSquareSecondDeceleration(convertFromDegreeToRev(DEFAULT_DECELERATION_DEGREES_PER_SQUARE_SECOND));
		setRevPerSecondMaxSpeed(convertFromDegreeToRev(DEFAULT_MAX_VELOCITY_DEGREE_PER_SECOND));
		setLowerCountPositionLimit(DEFAULT_DEGREE_LOWER_LIMIT * getCountPerDegree());
		setUpperCountPositionLimit(DEFAULT_DEGREE_UPPER_LIMIT * getCountPerDegree());
		setToZeroPosition();
		setInPosition(true);
		setHomePositiveSense(false);
		setHomeSwitchNormallyClosed(false);
		setHomingPositiveReferenceEdge(true);
		setHomingVelocityRevPerSecond(convertFromDegreeToRev(DEFAULT_MAX_VELOCITY_DEGREE_PER_SECOND));
		setHomingAccelerationDecelerationRevPerSecond(convertFromDegreeToRev(DEFAULT_ACCELERATION_DEGREES_PER_SQUARE_SECOND));
		

		//setDelayBetweenSendsMilliSec(DEFAULT_DELAY_BETWEEN_SENDS_MILLISEC);

/*
		try {
			CommPortIdentifier _com_id = CommPortIdentifier.getPortIdentifier(getSerialPortName());
			CommPort _com_port = _com_id.open("EngineMotion", DEFAULT_TIMEOUT_MILLISEC);

			if (_com_port instanceof SerialPort){
				serialPort = (SerialPort)_com_port;
			}
		}
		catch (NoSuchPortException _e) {
			_e.printStackTrace();
		}
		catch(Exception _ee){
			_ee.printStackTrace();
		}
		serialPort.notifyOnDataAvailable(true);
		try {
			serialPort.addEventListener(new EasiDecoder(serialPort.getInputStream()));
		} catch (TooManyListenersException _e) {
			_e.printStackTrace();
		} catch (IOException _e) {
			_e.printStackTrace();
		}*/

		sendOrderAndSetDecoder(EasiVocabulary.TURN_ON);
		sendOrderAndSetDecoder(EasiVocabulary.MODE_INCREMENTAL);
		sendOrderAndSetDecoder(EasiVocabulary.CONFIGURE_LIMITS);

		setAngularAcceleration(DEFAULT_ACCELERATION_DEGREES_PER_SQUARE_SECOND);
		setAngularDeceleration(DEFAULT_DECELERATION_DEGREES_PER_SQUARE_SECOND);
		setAngularMaxVelocity(DEFAULT_MAX_VELOCITY_DEGREE_PER_SECOND);
		setMaxPosition(DEFAULT_DEGREE_UPPER_LIMIT);
		setMinPosition(DEFAULT_DEGREE_LOWER_LIMIT);

		sendOrderAndSetDecoder(EasiVocabulary.START_STOP_VELOCITY_THRESHOLD);
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
		
		setHardLimitsAllowed(true);
		setHardLimitsNormalyOpen(true);
		setVelocityThreshold(convertFromDegreeToRev(DEFAULT_DEGREES_PER_SECOND_VELOCITY_THRESHOLD));

		setInvertMotionSense(false);

		setToZeroPosition();

	}
	
	@Override
	public boolean isACommandResult(String serial_return) {
		return serial_return.charAt(0) == '*';
	}
	
	private int getCountPerDegree() {
		return (int)(((float)(getReducerRatio()*getMotorResolution()))/ONE_REV_DEGREES);
	}
	/**
	 * Converts taking into account the reducer ratio.
	 * @param degree
	 * @return
	 */
	public float convertFromDegreeToRev(float degree_v){
		return degree_v / ONE_REV_DEGREES * (float) reducerRatio;
	}

	/**
	 * Sends an order through the serial port and prepare 
	 * the return signal decoding.
	 * 
	 * @param order_to_send
	 */
	/*public void sendOrderAndSetDecoder(EasiVocabulary order_to_send){
		setStateDecoderToUse(order_to_send.getResultDecoder());

		String _order_str = order_to_send.getCommandSequence(this);
		int[] _order_int_array = translateToPort(_order_str);
		for (int _msg_data : _order_int_array){
			try {
				serialPort.getOutputStream().write(_msg_data);
			} catch (IOException _e) {
				_e.printStackTrace();
			}
		}
		try {
			Thread.sleep(getDelayBetweenSendsMilliSec());
		} catch (InterruptedException _e) {
			_e.printStackTrace();
		}

	}*/

	/**
	 * Translates a {@link String} to an integer array. Appends "\r\n" in order to make the controller take into account this order.
	 * @param order_str An order in READI language.
	 * @return The corresponding integer array to send through the serial port.
	 */
	/*
	int[] translateToPort(String order_str){
		int[] _res = new int[order_str.length()+2];
		int _i=0;
		for (; _i<order_str.length(); _i++){
			_res[_i] = (int) order_str.charAt(_i);
		}
		_res[_i++] = '\r';
		_res[_i++] = '\n';
		return _res;
	}*/

	public float getVelocityThreshold() {
		return velocityThreshold;
	}
	public void setVelocityThreshold(float velocity_threshold) {
		this.velocityThreshold = velocity_threshold;
		// TODO : send
	}
	public boolean isHardLimitsNormalyOpen() {
		return hardLimitsNormalyOpen;
	}
	public void setHardLimitsNormalyOpen(boolean hard_limits_normaly_open) {
		this.hardLimitsNormalyOpen = hard_limits_normaly_open;
		sendOrderAndSetDecoder(EasiVocabulary.CONFIGURE_LIMITS);
	}
	public boolean isHardLimitsAllowed() {
		return hardLimitsAllowed;
	}
	public void setHardLimitsAllowed(boolean hard_limits_allowed) {
		this.hardLimitsAllowed = hard_limits_allowed;
		sendOrderAndSetDecoder(EasiVocabulary.CONFIGURE_LIMITS);
	}
	public int getCountDistance() {
		return countDistance;
	}
	public void setCountDistance(int count_distance) {
		this.countDistance = count_distance;
		// TODO : send
	}
	public int getEngineNumber() {
		return engineNumber;
	}
	public void setEngineNumber(int engine_number) {
		this.engineNumber = engine_number;
		// TODO : send
	}
	public boolean isLowerHardLimitReached() {
		return lowerHardLimitReached;
	}
	public void setLowerHardLimitReached(boolean lower_hard_limit_reached) {
		this.lowerHardLimitReached = lower_hard_limit_reached;
		// TODO : send
	}
	public boolean isUpperHardLimitReached() {
		return upperHardLimitReached;
	}
	public void setUpperHardLimitReached(boolean upper_hard_limit_reached) {
		upperHardLimitReached = upper_hard_limit_reached;
		// TODO : send
	}


	public boolean isLowerLimitReached() {
		return statusBitsTab[StatusBits.LOWER_LIMIT_SEEN.getBitIndex()];
	}

	public boolean isUpperLimitReached() {
		return statusBitsTab[StatusBits.UPPER_LIMIT_SEEN.getBitIndex()];
	}

	/**
	 * @return true if one or more user faults bits are non 0
	 */
	public boolean isFaulty() {
		for (UserFaultsBits _user_fault : UserFaultsBits.values()){
			if (userFaultsTab[_user_fault.getBitIndex()]){
				return true;
			}
		}
		return false;
	}

	public String getUserFaults() {
		StringBuffer _res = new StringBuffer();
		for (UserFaultsBits _user_fault : UserFaultsBits.values()){
			if (userFaultsTab[_user_fault.getBitIndex()]){
				_res.append(_user_fault.getBitMeaning());
				_res.append("\n");
			}
		}
		return _res.toString();
	}

	public String getStatus() {
		StringBuffer _res = new StringBuffer();
		for (StatusBits _status : StatusBits.values()){
			if(statusBitsTab[_status.getBitIndex()]){
				_res.append(_status.getBitMeaning());
				_res.append("\n");
			}
		}
		return _res.toString();
	}

	
	
	public void processRelativeMove(float deg_value) {


		setCountDistance((int)(((float)getCountPerDegree()) * deg_value));
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.MOVE);
		readEngineState();
	}

	private void readEngineState() {
		sendOrderAndSetDecoder(EasiVocabulary.READ_DRIVE_FAULT_STATUS);
		sendOrderAndSetDecoder(EasiVocabulary.READ_IN_POSITION_FLAG);
		sendOrderAndSetDecoder(EasiVocabulary.READ_MOVING);
		sendOrderAndSetDecoder(EasiVocabulary.READ_STATUS);
		sendOrderAndSetDecoder(EasiVocabulary.READ_READY_BUSY_FLAG);
		sendOrderAndSetDecoder(EasiVocabulary.READ_USER_PROGRAM_FAULT);
	}

	int translateFromDegreeToCount(float degree_value){
		return (int)((float)getCountPerDegree() * degree_value);
	}

	public void processAbsoluteMove(float deg_value) {
		if (isInvertAbsoluteMotionSense()){
			deg_value = -deg_value;
		}
		int _abs_count_value = (int)(((float)getCountPerDegree())*deg_value);
		setGoalTheoricalCountPosition(_abs_count_value);
		int _rel_count_value = _abs_count_value - getActualTheoricalCountPosition();
		/*System.out.println("processAbsoluteMove :");
		System.out.println("	Actual : " + getActualTheoricalCountPosition());
		System.out.println("	Dest :   " + _abs_count_value);
		System.out.println("    Rel  :   " + _rel_count_value);*/
		processRelativeMove((float)_rel_count_value / (float)getCountPerDegree());
	}

	public void processSoftStop() {
		sendOrderAndSetDecoder(EasiVocabulary.SMOOTH_STOP);
	}

	public void processEmergencystop() {
		sendOrderAndSetDecoder(EasiVocabulary.EMERGENCY_STOP);
	}

	public void setAngularMaxVelocity(float deg_per_second) {
		setRevPerSecondMaxSpeed(convertFromDegreeToRev(deg_per_second));
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
	}

	public void setAngularAcceleration(float deg_per_second_2) {
		setRevPerSquareSecondAcceleration(convertFromDegreeToRev(deg_per_second_2));
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
	}

	public void setAngularDeceleration(float deg_per_second_2) {
		setRevPerSquareSecondDeceleration(convertFromDegreeToRev(deg_per_second_2));
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
	}

	public void setToZeroPosition() {
		setActualTheoricalCountPosition(0);
		setGoalTheoricalCountPosition(0);
		setInPosition(true);
	}

	public void setMinPosition(float deg_value) {
		setLowerCountPositionLimit(translateFromDegreeToCount(deg_value));
	}

	public void setMaxPosition(float deg_value) {
		setUpperCountPositionLimit(translateFromDegreeToCount(deg_value));
	}

	public int getLowerCountPositionLimit() {
		return lowerCountPositionLimit;
	}

	public void setLowerCountPositionLimit(int lower_count_position_limit) {
		lowerCountPositionLimit = lower_count_position_limit;
	}

	public int getUpperCountPositionLimit() {
		return upperCountPositionLimit;
	}

	public void setUpperCountPositionLimit(int upper_count_position_limit) {
		upperCountPositionLimit = upper_count_position_limit;
	}

	public float getRevPerSquareSecondAcceleration() {
		return revPerSquareSecondAcceleration;
	}

	public void setRevPerSquareSecondAcceleration(float rev_per_square_acc) {
		revPerSquareSecondAcceleration = rev_per_square_acc;
	}

	public float getRevPerSquareSecondDeceleration() {
		return revPerSquareSecondDeceleration;
	}

	public void setRevPerSquareSecondDeceleration(float rev_par_square_second_dec) {
		revPerSquareSecondDeceleration = rev_par_square_second_dec;
	}

	/*public String getSerialPortName() {
		return serialPortName;
	}*/

	/*public void setSerialPortName(String serial_port_name) {
		serialPortName = serial_port_name;
	}*/

	public int getActualTheoricalCountPosition() {
		return actualTheoricalCountPosition;
	}

	public void setActualTheoricalCountPosition(int actual_count_position) {
		actualTheoricalCountPosition = actual_count_position;

	}

	public float getRevPerSecondMaxSpeed() {
		return revPerSecondMaxSpeed;
	}

	public void setRevPerSecondMaxSpeed(float rev_per_sec_max_speed) {
		revPerSecondMaxSpeed = rev_per_sec_max_speed;
	}


	public boolean[] getDriveFaultsTab() {
		return driveFaultsTab.clone();
	}


	public boolean[] getStatusBitsTab() {
		return statusBitsTab.clone();
	}


	public boolean[] getUserFaultsTab() {
		return userFaultsTab.clone();
	}


	public boolean isInPosition() {
		return inPosition;
	}


	public void setInPosition(boolean in_position) {
		inPosition = in_position;
	}


	public int getMotorCurrent() {
		return motorCurrent;
	}


	public void setMotorCurrent(int motor_current) {
		motorCurrent = motor_current;
	}


	public int getMotorResolution() {
		return motorResolution;
	}


	public int getReducerRatio() {
		return reducerRatio;
	}

	public void setReducerRatio(int reducer_ratio) {
		reducerRatio = reducer_ratio;
	}

	public void setMotorResolution(int motor_resolution) {
		motorResolution = motor_resolution;
	}


	public int getMotorStandbyCurrent() {
		return motorStandbyCurrent;
	}


	public void setMotorStandbyCurrent(int motor_standby_current) {
		motorStandbyCurrent = motor_standby_current;
	}


	public boolean isInMovement() {
		return inMovement;
	}


	public void setInMovement(boolean in_movement) {
		this.inMovement = in_movement;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean is_busy) {
		this.isBusy = is_busy;
	}


	/*public StringBuffer getControllerReturnToDecode() {
		return controllerReturnToDecode;
	}*/


	/*public void setControllerReturnToDecode(StringBuffer controller_return_to_decode) {
		this.controllerReturnToDecode = controller_return_to_decode;
	}*/

	/**
	 * 
	 * @return The {@link StateDecoder} to use to update the inner state, as soon as a return has been received from the controller.<br>
	 * Returns null is there is no inner state to update.
	 */
	/*public synchronized SerialStateDecoder getStateDecoderToUse() {
		return stateDecoderToUse;
	}*/

	/**
	 * 
	 * @param state_decoder_to_use null if there is no state to update.
	 */
	/*public synchronized void setStateDecoderToUse(StateDecoder state_decoder_to_use) {
		stateDecoderToUse = state_decoder_to_use;
	}*/

	/*public int getDelayBetweenSendsMilliSec() {
		return delayBetweenSendsMilliSec;
	}*/

	/*public void setDelayBetweenSendsMilliSec(int delay_between_sends_milli_sec) {
		delayBetweenSendsMilliSec = delay_between_sends_milli_sec;
	}*/

	public void waitForEndOfMotionAndSetTheoricalAbsolutePosition() {
		setInMovement(true);
		setInPosition(false);
		do{
			sendOrderAndSetDecoder(EasiVocabulary.READ_IN_POSITION_FLAG);
			sendOrderAndSetDecoder(EasiVocabulary.READ_MOVING);
		}
		while ((!isInPosition()) && isInMovement());
		setActualTheoricalCountPosition(getGoalTheoricalCountPosition());
	}
	public boolean isHomingPositiveReferenceEdge() {
		return homingPositiveReferenceEdge;
	}
	public void setHomingPositiveReferenceEdge(boolean homing_positive_reference_edge) {
		this.homingPositiveReferenceEdge = homing_positive_reference_edge;
	}
	public boolean isHomeSwitchNormallyClosed() {
		return homeSwitchNormallyClosed;
	}
	public void setHomeSwitchNormallyClosed(boolean home_switch_normally_closed) {
		this.homeSwitchNormallyClosed = home_switch_normally_closed;
	}
	public boolean isHomePositiveSense() {
		return homePositiveSense;
	}
	public void setHomePositiveSense(boolean homePositiveSense) {
		this.homePositiveSense = homePositiveSense;
	}
	public float getHomingVelocityRevPerSecond() {
		return homingVelocityRevPerSecond;
	}
	public void setHomingVelocityRevPerSecond(
			float homing_velocity_degree_per_second) {
		this.homingVelocityRevPerSecond = homing_velocity_degree_per_second;
	}
	public float getHomingAccelerationDecelerationRevPerSecond() {
		return homingAccelerationDecelerationRevPerSecond;
	}
	public void setHomingAccelerationDecelerationRevPerSecond(
			float homing_acceleration_deceleration_rev_per_second) {
		this.homingAccelerationDecelerationRevPerSecond = homing_acceleration_deceleration_rev_per_second;
	}

	public void performHoming(){
		sendOrderAndSetDecoder(EasiVocabulary.ARM_COMMAND);
		sendOrderAndSetDecoder(EasiVocabulary.CONFIGURE_HOMING);
		sendOrderAndSetDecoder(EasiVocabulary.GO_HOME);
	}
	public boolean isInvertAbsoluteMotionSense() {
		return invertMotionSense;
	}
	/**
	 * Changes the sense in which the moves are made. It corresponds to inverting the sign of absolute and relative moves.
	 * @param invert_motion_sense
	 */
	public void setInvertMotionSense(boolean invert_motion_sense) {
		this.invertMotionSense = invert_motion_sense;
	}
	public int getGoalTheoricalCountPosition() {
		return goalTheoricalCountPosition;
	}
	public void setGoalTheoricalCountPosition(int goal_theorical_count_position) {
		this.goalTheoricalCountPosition = goal_theorical_count_position;
	}



}
