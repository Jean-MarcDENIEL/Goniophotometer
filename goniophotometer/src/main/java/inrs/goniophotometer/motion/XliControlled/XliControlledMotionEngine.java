package inrs.goniophotometer.motion.XliControlled;

import java.io.IOException;
import java.io.InputStream;
import java.util.TooManyListenersException;

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

	private int 			countPerDegree;
	private String			serialPortName;
	private int 			engineNumber;
	private int				motorCurrent;
	private int				motorStandbyCurrent;
	private int 			motorResolution;
	private SerialPort		serialPort;
	private int				delayBetweenSendsMilliSec;
	
	

	private static final int	DEFAULT_ACCELERATION_DEGREES_PER_SQUARE_SECOND 	= 10;
	private static final int	DEFAULT_DECELERATION_DEGREES_PER_SQUARE_SECOND 	= 10;
	private static final int	DEFAULT_MAX_VELOCITY_DEGREE_PER_SECOND 			= 2;
	private static final int	DEFAULT_DEGREE_LOWER_LIMIT						= 0;
	private static final int	DEFAULT_DEGREE_UPPER_LIMIT						= 10;
	private static final int	DEFAULT_DELAY_BETWEEN_SENDS_MILLISEC			= 25;
	
	private boolean 		lowerHardLimitReached;
	private boolean			upperHardLimitReached;
	private boolean			hardLimitsNormalyOpen;
	private boolean			hardLimitsAllowed;
	
	private int				actualCountPosition;
	private int				actualCountIncremental;
	private int				countDistance;
	private int 			lowerCountPositionLimit;
	private int 			upperCountPositionLimit;
	private int 			countAcceleration;
	private int 			countDeceleration;
	private int				countMaxSpeed;
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
				while (((_read_data = decodedStream.read())> -1)&&
						(_read_data != '\n')){
					getControllerReturnToDecode().append((char)_read_data);
				}
				StateDecoder _state_decoder = getStateDecoderToUse();
				if (_state_decoder != null){
					_state_decoder.decodeState(getControllerReturnToDecode().toString(), XliControlledMotionEngine.this);
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
	
	public XliControlledMotionEngine(String serial_port_name, int count_per_degree){
		
		driveFaultsTab	= new boolean[flagCount];
		statusBitsTab	= new boolean[flagCount];
		userFaultsTab	= new boolean[flagCount];
		
		setSerialPortName(serial_port_name);
		setCountPerDegree(count_per_degree);
		setCountAcceleration(DEFAULT_ACCELERATION_DEGREES_PER_SQUARE_SECOND*getCountPerDegree());
		setCountDeceleration(DEFAULT_DECELERATION_DEGREES_PER_SQUARE_SECOND*getCountPerDegree());
		setCountMaxSpeed(DEFAULT_MAX_VELOCITY_DEGREE_PER_SECOND*getCountPerDegree());
		setLowerCountPositionLimit(DEFAULT_DEGREE_LOWER_LIMIT * getCountPerDegree());
		setUpperCountPositionLimit(DEFAULT_DEGREE_UPPER_LIMIT * getCountPerDegree());
		setToZeroPosition();
		setInPosition(true);
		setMotorCurrent(50);
		setMotorResolution(4000);
		setDelayBetweenSendsMilliSec(DEFAULT_DELAY_BETWEEN_SENDS_MILLISEC);
		
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
	}
	
	/**
	 * Sends an order through the serial port and prepare 
	 * the return signal decoding.
	 * 
	 * @param order_to_send
	 */
	public void sendOrderAndSetDecoder(EasiVocabulary order_to_send){
		setStateDecoderToUse(order_to_send.getResultDecoder());
		
		int[] _order_str = translateToPort(order_to_send.getCommandSequence(this));
		for (int _msg_data : _order_str){
			try {
				serialPort.getOutputStream().write(_msg_data);
			} catch (IOException _e) {
				_e.printStackTrace();
			}
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
		// TODO Auto-generated method stub

	}

	public final void processAbsoluteMove(float deg_value) {
		// TODO Auto-generated method stub

	}

	public final void processSoftStop() {
		// TODO Auto-generated method stub

	}

	public final void processEmergencystop() {
		// TODO Auto-generated method stub

	}

	public final void setMaxVelocity(float deg_per_second) {
		// TODO Auto-generated method stub

	}

	public final void setAcceleration(float deg_per_second_2) {
		// TODO Auto-generated method stub

	}

	public final void setDeceleration(float deg_per_second_2) {
		// TODO Auto-generated method stub

	}

	public final void setToZeroPosition() {
		// TODO Auto-generated method stub

	}

	public final void setMinPosition(float deg_value) {
		// TODO Auto-generated method stub

	}

	public final void setMaxPosition(float deg_value) {
		// TODO Auto-generated method stub

	}

	public final int getCountPerDegree() {
		return countPerDegree;
	}

	public final void setCountPerDegree(int count_per_degree) {
		countPerDegree = count_per_degree;
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

	public final int getCountAcceleration() {
		return countAcceleration;
	}

	public final void setCountAcceleration(int count_acceleration) {
		countAcceleration = count_acceleration;
	}

	public final int getCountDeceleration() {
		return countDeceleration;
	}

	public final void setCountDeceleration(int count_deceleration) {
		countDeceleration = count_deceleration;
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

	public final int getCountMaxSpeed() {
		return countMaxSpeed;
	}

	public final void setCountMaxSpeed(int count_max_speed) {
		countMaxSpeed = count_max_speed;
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


	public void setMotorResolution(int motor_resolution) {
		this.motorResolution = motor_resolution;
	}


	public int getMotorStandbyCurrent() {
		return motorStandbyCurrent;
	}


	public void setMotorStandbyCurrent(int motor_standby_current) {
		this.motorStandbyCurrent = motor_standby_current;
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
		this.stateDecoderToUse = state_decoder_to_use;
	}

	public int getDelayBetweenSendsMilliSec() {
		return delayBetweenSendsMilliSec;
	}

	public void setDelayBetweenSendsMilliSec(int delay_between_sends_milli_sec) {
		delayBetweenSendsMilliSec = delay_between_sends_milli_sec;
	}

}
