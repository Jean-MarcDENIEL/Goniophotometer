package inrs.goniophotoradiometer.illuminanceCapture.ahlbornImplementation;

import c4sci.io.serial.SerialDevice;
import inrs.goniophotoradiometer.illuminanceCapture.LuxMeter;

public class AhlbornLuxMeter extends SerialDevice implements LuxMeter {

	
	
	
	public AhlbornLuxMeter(String serial_port_name, String command_string_end,
			char command_result_end, int delay_between_sends_millisec,
			int timeout_millisec) {
		super(serial_port_name, command_string_end, command_result_end,
				delay_between_sends_millisec, timeout_millisec);
		// TODO Auto-generated constructor stub
	}

	public float captureIlluminance() throws IlluminanceException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isACommandResult(String serial_return) {
		return true;
	}
	
	public static void main(String main_args[]){
		System.out.println("Ouverture du luxmetre");
		AhlbornLuxMeter _luxmeter = new AhlbornLuxMeter("COM2", "\r\n", '\n', 350, 100);
		System.out.println("System overview");
		_luxmeter.sendOrderAndSetDecoder(AlmenoVocabulary.SYSTEM_OVERVIEW);

		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("End");
		System.exit(0);
	}

}
