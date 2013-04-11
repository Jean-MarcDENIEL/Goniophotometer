package inrs.goniophotoradiometer.illuminanceCapture.ahlbornImplementation;

import c4sci.io.serial.SerialDevice;
import c4sci.io.serial.SerialStateDecoder;
import c4sci.io.serial.SerialStateParsingException;
import c4sci.io.serial.SerialVocabulary;

public enum AlmenoVocabulary implements SerialVocabulary {
	SYSTEM_OVERVIEW("P15", new SerialStateDecoder(){
		public void decodeState(String state_string, SerialDevice serial_device)
				throws SerialStateParsingException {
			System.out.println("Received ...");
			System.out.println(state_string);
		}}){
		public String getCommandParameters(SerialDevice serial_device) {
			return "";
		}
	};
	
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
		return "" + getLabel();
	}

}
