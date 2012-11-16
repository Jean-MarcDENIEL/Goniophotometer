package inrs.goniophotometer.motion.XliControlled;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

import javax.swing.plaf.SliderUI;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import inrs.goniophotometer.motion.MotionEngine;

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
@SuppressWarnings("restriction")
public class XliControlledMotionEngine implements MotionEngine {

	private String			serialPortName;
	private int 			engineNumber;
	private int				motorCurrent;
	private int				motorStandbyCurrent;
	private int 			motorResolution;
	private int				reducerRatio;
	private SerialPort		serialPort;
	private int				delayBetweenSendsMilliSec;



	private static final float	DEFAULT_ACCELERATION_DEGREES_PER_SQUARE_SECOND 	= 1f;
	private static final float	DEFAULT_DECELERATION_DEGREES_PER_SQUARE_SECOND 	= 1f;
	private static final float	DEFAULT_MAX_VELOCITY_DEGREE_PER_SECOND 			= 2f;
	private static final int	DEFAULT_DEGREE_LOWER_LIMIT						= 0;
	private static final int	DEFAULT_DEGREE_UPPER_LIMIT						= 10;
	private static final int	DEFAULT_DELAY_BETWEEN_SENDS_MILLISEC			= 200;
	private static final float	DEFAULT_DEGREES_PER_SECOND_VELOCITY_THRESHOLD	= 0.5f; 

	private boolean 		lowerHardLimitReached;
	private boolean			upperHardLimitReached;
	private boolean			hardLimitsNormalyOpen;
	private boolean			hardLimitsAllowed;

	private int				actualCountPosition;
	private int				actualCountIncremental;
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

	private final int 		flagCount = 32; 

	private boolean[] 		driveFaultsTab;
	private boolean[]		statusBitsTab;
	private boolean[]		userFaultsTab;

	private StringBuffer	controllerReturnToDecode;
	private StateDecoder	stateDecoderToUse;

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
	private class EasiDecoder implements SerialPortEventListener {
		private InputStream			decodedStream;

		public EasiDecoder(InputStream decoded_stream){
			decodedStream = decoded_stream;
		}

		public void serialEvent(SerialPortEvent ev) {
			int _read_data;
			getControllerReturnToDecode().setLength(0);
			try{
				// first receives the message from the controller
				//
				while (((_read_data = decodedStream.read())> -1)&&
						(_read_data != '\n')){
					getControllerReturnToDecode().append((char)_read_data);
				}
				// if the message is a command result (i.e begins with '*') then it is decoded
				//
				if (getControllerReturnToDecode().charAt(0) == '*'){
					StateDecoder _state_decoder = getStateDecoderToUse();
					if (_state_decoder != null){
						_state_decoder.decodeState(getControllerReturnToDecode().toString(), XliControlledMotionEngine.this);
					}
				}
			}
			catch(IOException _e){
				_e.printStackTrace();
			} catch (StateParsingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	};

	/**
	 * 
	 * @param serial_port_name "COM1", "COM2" etc.
	 * @param count_per_rev number of motor counts to have it make one round
	 * @param rev_ratio ratio between motor and reducer (1:rev_ratio)
	 */
	public XliControlledMotionEngine(String serial_port_name, int count_per_rev, int rev_ratio){

		driveFaultsTab	= new boolean[flagCount];
		statusBitsTab	= new boolean[flagCount];
		userFaultsTab	= new boolean[flagCount];

		setControllerReturnToDecode(new StringBuffer());

		setEngineNumber(1);
		setSerialPortName(serial_port_name);
		setMotorResolution(count_per_rev);
		setReducerRatio(rev_ratio);
		setMotorCurrent(50);
		setRevPerSquareSecondAcceleration(convertFromDegreeToRev(DEFAULT_ACCELERATION_DEGREES_PER_SQUARE_SECOND));
		setRevPerSquareSecondDeceleration(convertFromDegreeToRev(DEFAULT_DECELERATION_DEGREES_PER_SQUARE_SECOND));
		setRevPerSecondMaxSpeed(convertFromDegreeToRev(DEFAULT_MAX_VELOCITY_DEGREE_PER_SECOND));
		setLowerCountPositionLimit(DEFAULT_DEGREE_LOWER_LIMIT * getCountPerDegree());
		setUpperCountPositionLimit(DEFAULT_DEGREE_UPPER_LIMIT * getCountPerDegree());
		setToZeroPosition();
		setInPosition(true);

		setDelayBetweenSendsMilliSec(DEFAULT_DELAY_BETWEEN_SENDS_MILLISEC);

		setHardLimitsAllowed(true);
		setHardLimitsNormalyOpen(true);
		setVelocityThreshold(convertFromDegreeToRev(DEFAULT_DEGREES_PER_SECOND_VELOCITY_THRESHOLD));

		try {
			CommPortIdentifier _com_id = CommPortIdentifier.getPortIdentifier(getSerialPortName());
			CommPort _com_port = _com_id.open("EngineMotion", 100);

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
		} catch (TooManyListenersException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

		setToZeroPosition();

	}
	private int getCountPerDegree() {
		return (int)(((float)(getReducerRatio()*getMotorResolution()))/360.0f);
	}
	/**
	 * Converts taking into account the reducer ratio.
	 * @param degree
	 * @return
	 */
	public float convertFromDegreeToRev(float degree_v){
		return degree_v / 360.0f * (float) reducerRatio;
	}

	/**
	 * Sends an order through the serial port and prepare 
	 * the return signal decoding.
	 * 
	 * @param order_to_send
	 */
	public void sendOrderAndSetDecoder(EasiVocabulary order_to_send){
		setStateDecoderToUse(order_to_send.getResultDecoder());

		String _order_str = order_to_send.getCommandSequence(this);
		// DEBUG
		System.out.println("             ->" + _order_str);

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

	}

	/**
	 * Translates a {@link String} to an integer array. Appends "\r\n" in order to make the controller take into account this order.
	 * @param order_str An order in READI language.
	 * @return The corresponding integer array to send through the serial port.
	 */
	int[] translateToPort(String order_str){
		int[] _res = new int[order_str.length()+2];
		int _i=0;
		for (; _i<order_str.length(); _i++){
			_res[_i] = (int) order_str.charAt(_i);
		}
		_res[_i++] = '\r';
		_res[_i++] = '\n';
		return _res;
	}

	public float getVelocityThreshold() {
		return velocityThreshold;
	}
	public void setVelocityThreshold(float velocity_threshold) {
		this.velocityThreshold = velocity_threshold;
	}
	public boolean isHardLimitsNormalyOpen() {
		return hardLimitsNormalyOpen;
	}
	public void setHardLimitsNormalyOpen(boolean hard_limits_normaly_open) {
		this.hardLimitsNormalyOpen = hard_limits_normaly_open;
	}
	public boolean isHardLimitsAllowed() {
		return hardLimitsAllowed;
	}
	public void setHardLimitsAllowed(boolean hard_limits_allowed) {
		this.hardLimitsAllowed = hard_limits_allowed;
	}
	public int getCountDistance() {
		return countDistance;
	}
	public void setCountDistance(int count_distance) {
		this.countDistance = count_distance;
	}
	public int getEngineNumber() {
		return engineNumber;
	}
	public void setEngineNumber(int engine_number) {
		this.engineNumber = engine_number;
	}
	public boolean isLowerHardLimitReached() {
		return lowerHardLimitReached;
	}
	public void setLowerHardLimitReached(boolean lower_hard_limit_reached) {
		this.lowerHardLimitReached = lower_hard_limit_reached;
	}
	public boolean isUpperHardLimitReached() {
		return upperHardLimitReached;
	}
	public void setUpperHardLimitReached(boolean upper_hard_limit_reached) {
		upperHardLimitReached = upper_hard_limit_reached;
	}


	public final boolean isLowerLimitReached() {
		return statusBitsTab[StatusBits.LOWER_LIMIT_SEEN.getBitIndex()];
	}

	public final boolean isUpperLimitReached() {
		return statusBitsTab[StatusBits.UPPER_LIMIT_SEEN.getBitIndex()];
	}

	/**
	 * @return true if one or more user faults bits are non 0
	 */
	public final boolean isFaulty() {
		for (UserFaultsBits _user_fault : UserFaultsBits.values()){
			if (userFaultsTab[_user_fault.getBitIndex()]){
				return true;
			}
		}
		return false;
	}

	public final String getUserFaults() {
		StringBuffer _res = new StringBuffer();
		for (UserFaultsBits _user_fault : UserFaultsBits.values()){
			if (userFaultsTab[_user_fault.getBitIndex()]){
				_res.append(_user_fault.getBitMeaning()+"\n");
			}
		}
		return _res.toString();
	}

	public final String getStatus() {
		StringBuffer _res = new StringBuffer();
		for (StatusBits _status : StatusBits.values()){
			if(statusBitsTab[_status.getBitIndex()]){
				_res.append(_status.getBitMeaning()+"\n");
			}
		}
		return _res.toString();
	}

	public final void processRelativeMove(float deg_value) {

		// TODO : ensure soft limits

		if (deg_value < 0.0){
			sendOrderAndSetDecoder(EasiVocabulary.CHANGE_DIRECTION);
		}

		setCountDistance((int)(((float)getCountPerDegree()) * deg_value));
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.MOVE);

		if (deg_value < 0.0){
			sendOrderAndSetDecoder(EasiVocabulary.CHANGE_DIRECTION);
		}

		readEnginState();
	}

	private void readEnginState() {
		sendOrderAndSetDecoder(EasiVocabulary.READ_DRIVE_FAULT_STATUS);
		sendOrderAndSetDecoder(EasiVocabulary.READ_IN_POSITION_FLAG);
		sendOrderAndSetDecoder(EasiVocabulary.READ_MOVING);
		sendOrderAndSetDecoder(EasiVocabulary.READ_POSITION_ABSOLUTE);
		sendOrderAndSetDecoder(EasiVocabulary.READ_POSITION_INCREMENTAL);
		sendOrderAndSetDecoder(EasiVocabulary.READ_STATUS);
		sendOrderAndSetDecoder(EasiVocabulary.READ_READY_BUSY_FLAG);
		sendOrderAndSetDecoder(EasiVocabulary.READ_USER_PROGRAM_FAULT);
	}

	int translateFromDegreeToCount(float degree_value){
		return (int)((float)getCountPerDegree() * degree_value);
	}

	public final void processAbsoluteMove(float deg_value) {
		int _abs_count_value = (int)(((float)getCountPerDegree())*deg_value);
		int _rel_count_value = getActualCountPosition()-_abs_count_value;
		processRelativeMove((float)_rel_count_value / (float)getCountPerDegree());
	}

	public final void processSoftStop() {
		sendOrderAndSetDecoder(EasiVocabulary.SMOOTH_STOP);
	}

	public final void processEmergencystop() {
		sendOrderAndSetDecoder(EasiVocabulary.EMERGENCY_STOP);
	}

	public final void setAngularMaxVelocity(float deg_per_second) {
		setRevPerSecondMaxSpeed(convertFromDegreeToRev(deg_per_second));
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
	}

	public final void setAngularAcceleration(float deg_per_second_2) {
		setRevPerSquareSecondAcceleration(convertFromDegreeToRev(deg_per_second_2));
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
	}

	public final void setAngularDeceleration(float deg_per_second_2) {
		setRevPerSquareSecondDeceleration(convertFromDegreeToRev(deg_per_second_2));
		sendOrderAndSetDecoder(EasiVocabulary.PROFILE_1);
		sendOrderAndSetDecoder(EasiVocabulary.USE_PROFILE_1);
	}

	public final void setToZeroPosition() {
		setActualCountPosition(0);
		setInPosition(true);
	}

	public final void setMinPosition(float deg_value) {
		setLowerCountPositionLimit(translateFromDegreeToCount(deg_value));
	}

	public final void setMaxPosition(float deg_value) {
		setUpperCountPositionLimit(translateFromDegreeToCount(deg_value));
	}

	public final int getLowerCountPositionLimit() {
		return lowerCountPositionLimit;
	}

	public final void setLowerCountPositionLimit(int lower_count_position_limit) {
		lowerCountPositionLimit = lower_count_position_limit;
	}

	public final int getUpperCountPositionLimit() {
		return upperCountPositionLimit;
	}

	public final void setUpperCountPositionLimit(int upper_count_position_limit) {
		upperCountPositionLimit = upper_count_position_limit;
	}

	public final float getRevPerSquareSecondAcceleration() {
		return revPerSquareSecondAcceleration;
	}

	public final void setRevPerSquareSecondAcceleration(float rev_per_square_acc) {
		revPerSquareSecondAcceleration = rev_per_square_acc;
	}

	public final float getRevPerSquareSecondDeceleration() {
		return revPerSquareSecondDeceleration;
	}

	public final void setRevPerSquareSecondDeceleration(float rev_par_square_second_dec) {
		revPerSquareSecondDeceleration = rev_par_square_second_dec;
	}

	public final String getSerialPortName() {
		return serialPortName;
	}

	public final void setSerialPortName(String serial_port_name) {
		serialPortName = serial_port_name;
	}

	public final int getActualCountPosition() {
		return actualCountPosition;
	}

	public final void setActualCountPosition(int actual_count_position) {
		actualCountPosition = actual_count_position;
	}

	public final float getRevPerSecondMaxSpeed() {
		return revPerSecondMaxSpeed;
	}

	public final void setRevPerSecondMaxSpeed(float rev_per_sec_max_speed) {
		revPerSecondMaxSpeed = rev_per_sec_max_speed;
	}


	public boolean[] getDriveFaultsTab() {
		return driveFaultsTab;
	}


	public boolean[] getStatusBitsTab() {
		return statusBitsTab;
	}


	public boolean[] getUserFaultsTab() {
		return userFaultsTab;
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


	public int getActualCountIncremental() {
		return actualCountIncremental;
	}


	public void setActualCountIncremental(int actual_count_incremental) {
		this.actualCountIncremental = actual_count_incremental;
	}


	public boolean isBusy() {
		return isBusy;
	}


	public void setBusy(boolean is_busy) {
		this.isBusy = is_busy;
	}


	public StringBuffer getControllerReturnToDecode() {
		return controllerReturnToDecode;
	}


	public void setControllerReturnToDecode(StringBuffer controller_return_to_decode) {
		this.controllerReturnToDecode = controller_return_to_decode;
	}

	/**
	 * 
	 * @return The {@link StateDecoder} to use to update the inner state, as soon as a return has been received from the controller.<br>
	 * Returns null is there is no inner state to update.
	 */
	public synchronized StateDecoder getStateDecoderToUse() {
		return stateDecoderToUse;
	}

	/**
	 * 
	 * @param state_decoder_to_use null if there is no state to update.
	 */
	public synchronized void setStateDecoderToUse(StateDecoder state_decoder_to_use) {
		stateDecoderToUse = state_decoder_to_use;
	}

	public int getDelayBetweenSendsMilliSec() {
		return delayBetweenSendsMilliSec;
	}

	public void setDelayBetweenSendsMilliSec(int delay_between_sends_milli_sec) {
		delayBetweenSendsMilliSec = delay_between_sends_milli_sec;
	}

	public void waitForEndOfMotion() {
		do{
			sendOrderAndSetDecoder(EasiVocabulary.READ_IN_POSITION_FLAG);
			sendOrderAndSetDecoder(EasiVocabulary.READ_MOVING);
		}
		while ((!isInPosition()) && isInMovement());
	}


}
