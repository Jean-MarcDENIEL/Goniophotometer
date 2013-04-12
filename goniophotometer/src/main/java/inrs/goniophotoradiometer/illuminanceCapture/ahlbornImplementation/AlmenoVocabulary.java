package inrs.goniophotoradiometer.illuminanceCapture.ahlbornImplementation;

import c4sci.io.serial.SerialDevice;
import c4sci.io.serial.SerialStateDecoder;
import c4sci.io.serial.SerialStateParsingException;
import c4sci.io.serial.SerialVocabulary;

public enum AlmenoVocabulary implements SerialVocabulary {
	SYSTEM_OVERVIEW("P15"){
		public String getCommandParameters(SerialDevice serial_device) {
			return "";
		}
	},
	MEASUREMENT_CHANNEL_CHOICE("M"){
		public String getCommandParameters(SerialDevice serial_device) {
			return ((AhlbornLuxMeter)serial_device).getMeasurementChannel();
		}
	},
	MEASUREMENT_VALUE("P01", new SerialStateDecoder(){
		private final int DATE_LENGTH 				= 9;
		private final int BETWEEN_CHANNEL_AND_VALUE	= 2;
		public void decodeState(String state_string, SerialDevice serial_device)
				throws SerialStateParsingException {
			try{
				System.out.println(": " + state_string);
				int _value_index = this.DATE_LENGTH + ((AhlbornLuxMeter)serial_device).getMeasurementChannel().length() + this.BETWEEN_CHANNEL_AND_VALUE;
				String _to_parse = state_string.substring(_value_index);
				int _point_index = _to_parse.indexOf(" ");
				if (_point_index != -1){
					_to_parse = _to_parse.substring(0, _point_index);
					try{
						((AhlbornLuxMeter)serial_device).setMeasurementValue(Float.parseFloat(_to_parse));
					}
					catch(NumberFormatException _e){
						throw new SerialStateParsingException("Cannot read illuminance value " + _e.getMessage(), _e);
					}
				}
			}
			catch(IndexOutOfBoundsException _e){}

		}}){
		public String getCommandParameters(SerialDevice serial_device) {
			return null;
		}
	}
	;

	private String 				commandLabel;
	private SerialStateDecoder	resultDecoder;

	private AlmenoVocabulary(String cmd_label){
		commandLabel 	= cmd_label;
		resultDecoder	=	null;
	}
	private AlmenoVocabulary(String cmd_label, SerialStateDecoder result_decoder) {
		commandLabel 	= cmd_label;
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
