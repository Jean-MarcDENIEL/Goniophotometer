package inrs.goniophotoradiometer.filterWheel.thorlabsImplementation;

import java.io.IOException;
import c4sci.io.serial.SerialDevice;
import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.filterWheel.FilterWheel;
import gnu.io.*;

public class ThorlabsFilterWheel extends SerialDevice implements FilterWheel{

	private static final int 	DEFAULT_DELAY_MILLISEC		= 500;
	private static final int	DEFAULT_TIMEOUT_MILLISEC	= 2000;
	private static final int	MAX_EVENT_COUNT				= 3;
	private static final String	CMD_STR_END					= "\r";
	private static final char 	CMD_RESULT_END				= (char)13;	
	private static final String	PROMPT_STR					= ">";


	private int	goalFilterIndex; 
	private int actualFilterIndex;
	private int holdedFiltersCount;
	private int serialEventCount;

	public int getMaxFilterCount() throws RadiometryException {
		return getHeldFiltersCount();
	}

	public void positionFilter(int filter_index) throws RadiometryException {
		if ((filter_index < 1) || (filter_index > getHeldFiltersCount())){
			throw new RadiometryException("Filter index out of index bounds : " + filter_index);
		}
		goalFilterIndex = filter_index;
		sendOrderAndSetDecoder(ThorlabsFilterWheelVocabulary.MOVE_TO_POSITION);
		try {
			Thread.sleep(DEFAULT_DELAY_MILLISEC);
		} catch (InterruptedException _e) {
			throw new RadiometryException("Interrupted", _e);
		}
		while (getGoalFilterIndex() != getActualFilterIndex()){
			retrieveActualFilterIndex();
		}
	}

	@SuppressWarnings("restriction")
	public ThorlabsFilterWheel(String com_port) throws RadiometryException{

		super(com_port, CMD_STR_END, CMD_RESULT_END, DEFAULT_DELAY_MILLISEC, DEFAULT_TIMEOUT_MILLISEC);
		try {
			getSerialPort().setSerialPortParams(115200, SerialPort.DATABITS_8 , SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException _e) {
			throw new RadiometryException("unsupported operation", _e);
		}
		serialEventCount = MAX_EVENT_COUNT - 1;
		goalFilterIndex = 0;
		actualFilterIndex = 0;
		
		sendOrderAndSetDecoder(ThorlabsFilterWheelVocabulary.SET_SENSOR_MODE_OFF);
		retrieveHeldFiltersCount();
		retrieveActualFilterIndex();
	}

	@Override
	public boolean isACommandResult(String serial_return) {
		serialEventCount = (serialEventCount + 1) % MAX_EVENT_COUNT;
		/*
		 * three case :
		 * 0 : received order
		 * 1 : prompt or info return
		 * 2 : prompt
		 */
		if ((serialEventCount == 0) || (serialEventCount == 2)){
			return false;
		}
		if (serial_return.startsWith(PROMPT_STR)){
			serialEventCount ++;
			return false;
		}
		return true;
	}

	public int getGoalFilterIndex() {
		return goalFilterIndex;
	}

	/**
	 * Sends order to retrieve the number of filter that can be held by the wheel.
	 * side effect : sets HeldFiltersCount returned by {@link #getHeldFiltersCount()}
	 */
	public void retrieveHeldFiltersCount() throws RadiometryException{
		sendOrderAndSetDecoder(ThorlabsFilterWheelVocabulary.GET_POSITION_COUNT);
		try {
			Thread.sleep(DEFAULT_DELAY_MILLISEC);
		} catch (InterruptedException _e) {
			throw new RadiometryException("Interrupted", _e);
		}
	}

	public final void setHeldFiltersCount(int filter_count) {
		this.holdedFiltersCount = filter_count;
	}
	
	public final int getHeldFiltersCount(){
		return this.holdedFiltersCount;
	}
	
	
	public void retrieveActualFilterIndex() throws RadiometryException{
		sendOrderAndSetDecoder(ThorlabsFilterWheelVocabulary.GET_ACTUAL_POSITION);
		try {
			Thread.sleep(DEFAULT_DELAY_MILLISEC);
		} catch (InterruptedException _e) {
			throw new RadiometryException("Interrupted", _e);
		}
	}
	public final int getActualFilterIndex() {
		return actualFilterIndex;
	}

	public final void setActualFilterIndex(int actual_filter_index) {
		this.actualFilterIndex = actual_filter_index;
	}

	@SuppressWarnings("restriction")
	public static void main(String[] args_tab) throws UnsupportedCommOperationException, IOException, InterruptedException, NoSuchPortException, PortInUseException, RadiometryException{

		ThorlabsFilterWheel _wheel = new ThorlabsFilterWheel("COM3");

		System.out.println("held = " + _wheel.getMaxFilterCount());
		System.out.println("actual = " + _wheel.getActualFilterIndex());

		for (int _i = 0; _i<10; _i++){
			int _goal_index = (_wheel.getActualFilterIndex() + 3)%(_wheel.getMaxFilterCount()+1);
			if (_goal_index == 0){
				_goal_index = 1;
			}
			System.out.println("Positionning to " + _goal_index);
			_wheel.positionFilter(_goal_index);


			_wheel.retrieveActualFilterIndex();
			System.out.println("actual = " + _wheel.getActualFilterIndex());
		}
		System.out.println("Waiting...");
		Thread.sleep(3000);


		System.out.println("Finished");
		System.exit(0);
	}


}
