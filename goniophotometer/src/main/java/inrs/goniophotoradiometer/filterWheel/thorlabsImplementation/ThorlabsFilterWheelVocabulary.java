package inrs.goniophotoradiometer.filterWheel.thorlabsImplementation;

import c4sci.io.serial.SerialDevice;
import c4sci.io.serial.SerialStateDecoder;
import c4sci.io.serial.SerialStateParsingException;
import c4sci.io.serial.SerialVocabulary;

public enum ThorlabsFilterWheelVocabulary implements SerialVocabulary {

	MOVE_TO_POSITION("pos"){

		public String getCommandParameters(SerialDevice serial_device) {
			return "="+((ThorlabsFilterWheel)serial_device).getGoalFilterIndex();

		}},
	GET_ACTUAL_POSITION("pos?", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice serial_device)
				throws SerialStateParsingException {
			try{
				int _actual_position = Integer.parseInt(state_string);
				((ThorlabsFilterWheel)serial_device).setActualFilterIndex(_actual_position);
			}
			catch(NumberFormatException _e){
				throw new SerialStateParsingException("Cannot parse the filtering position", _e);
			}
			
		}}){

		public String getCommandParameters(SerialDevice serial_device) {
			return "";
		}
			
		},
	GET_POSITION_COUNT("pcount?", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice serial_device)
				throws SerialStateParsingException {
			try{
				int _filters_count = Integer.parseInt(state_string);
				((ThorlabsFilterWheel)serial_device).setHeldFiltersCount(_filters_count);
			}
			catch(NumberFormatException _e){
				throw new SerialStateParsingException("Cannot parse the number of filters", _e);
			}}}){
			public String getCommandParameters(SerialDevice serial_device) {
				return "";
			}
		},
	SET_SENSOR_MODE_OFF("sensors=0"){
		public String getCommandParameters(SerialDevice serial_device) {
			return "";
		}},
	SET_SENSOR_MODE_ACTIVE("sensors=1"){
		public String getCommandParameters(SerialDevice serial_device) {
			return "";
		}},
	SET_BAUD_RATE_9600("baud=0"){
		public String getCommandParameters(SerialDevice serial_device) {
			return "";
		}},
	SET_BAUD_RATE_115200("baud=1"){
		public String getCommandParameters(SerialDevice serial_device) {
			return "";
		}},
	SAVE_SETTINGS("save"){
		public String getCommandParameters(SerialDevice serial_device) {
			return "";
		}}
	;
	
	private String 				commandLabel;
	private SerialStateDecoder	resultDecoder;
	
	private ThorlabsFilterWheelVocabulary(String command_label){
		commandLabel 	= command_label;
		resultDecoder	= null; 
	}
	
	private ThorlabsFilterWheelVocabulary(String command_label, SerialStateDecoder result_decoder){
		commandLabel	= command_label;
		resultDecoder	= result_decoder;
	}

	public String getLabel() {
		return commandLabel;
	}

	public SerialStateDecoder getResultDecoder() {
		return resultDecoder;
	}

	public String getCommandState(SerialDevice serial_device) {
		return getLabel();
	}
}
