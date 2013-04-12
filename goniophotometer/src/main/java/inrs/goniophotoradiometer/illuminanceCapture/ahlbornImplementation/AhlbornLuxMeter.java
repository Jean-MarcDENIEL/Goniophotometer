package inrs.goniophotoradiometer.illuminanceCapture.ahlbornImplementation;

import c4sci.io.serial.SerialDevice;
import c4sci.math.algebra.Floatings;
import inrs.goniophotoradiometer.illuminanceCapture.LuxMeter;

public class AhlbornLuxMeter extends SerialDevice implements LuxMeter {

	private String				measurementChannel;
	private float				measurementValue;
	
	private static final float	LUX_LIMIT = 25000f; 
	
	
	public AhlbornLuxMeter(String serial_port_name, String command_string_end,
			char command_result_end, int delay_between_sends_millisec,
			int timeout_millisec, String measurement_channel) {
		super(serial_port_name, command_string_end, command_result_end,
				delay_between_sends_millisec, timeout_millisec);
		setMeasurementChannel(measurement_channel);
		
		sendOrderAndSetDecoder(AlmenoVocabulary.MEASUREMENT_CHANNEL_CHOICE);
	}

	public float captureIlluminance() throws IlluminanceException {
		sendOrderAndSetDecoder(AlmenoVocabulary.MEASUREMENT_VALUE);
		float _res = getMeasurementValue();
		if (Floatings.isGreaterEqual(_res, LUX_LIMIT)){
			throw new IlluminanceException("Illuminance limit reached!");
		}
		return _res;
	}

	@Override
	public boolean isACommandResult(String serial_return) {
		return true;
	}
	
	public String getMeasurementChannel() {
		return measurementChannel;
	}

	private void setMeasurementChannel(String measurement_channel) {
		this.measurementChannel = measurement_channel;
	}

	public static void main(String main_args[]){
		
		System.out.println("Ouverture du luxmetre");
		AhlbornLuxMeter _luxmeter = new AhlbornLuxMeter("COM2", "\r\n", '\n', 350, 100,"00");
		
		System.out.println("System overview");
		_luxmeter.sendOrderAndSetDecoder(AlmenoVocabulary.SYSTEM_OVERVIEW);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {}
		
		System.out.println("Measurement choice");
		_luxmeter.sendOrderAndSetDecoder(AlmenoVocabulary.MEASUREMENT_CHANNEL_CHOICE);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {}

		System.out.println("Measurement");
		System.out.println("before : " + _luxmeter.getMeasurementValue());
		try {
			System.out.println("after : " + _luxmeter.captureIlluminance());
		} catch (IlluminanceException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {}

		
		try {
			Thread.sleep(30);
		} catch (InterruptedException e) {}
		System.out.println("End");
		System.exit(0);
	}

	private float getMeasurementValue() {
		return measurementValue;
	}

	public void setMeasurementValue(float measurementValue) {
		this.measurementValue = measurementValue;
	}

}
