package inrs.goniophotoradiometer.illuminanceCapture.ahlbornImplementation;

import c4sci.io.serial.SerialDevice;
import c4sci.math.algebra.Floatings;
import inrs.goniophotoradiometer.illuminanceCapture.LuxMeter;

public class AhlbornLuxMeter extends SerialDevice implements LuxMeter {

	private String				sensitiveMeasurementChannel;
	private String				insensitiveMeasurementChannel;
	private boolean				useSensitiveChannel;
	private float				measurementValue;
	
	public static final float	SENSITIVE_LUX_LIMIT 	= 25000f; 
	public static final float	INSENSITIVE_LUX_LIMIT 	= 250000f;
	public static final String 	COMMAND_STRING_END		= "\r\n";
	public static final char	COMMAND_RESULT_END		= '\n';		// should be ETX = 3
	public static final int		DEFAULT_DELAY_MILLISEC	= 350;
	public static final int		DEFAULT_TIMEOUT_MILLISEC = 200;
	private static final float	KLUX_TO_LUX_CONVERT		= 1000f;
	
	
	
	public AhlbornLuxMeter(String serial_port_name, String command_string_end,
			char command_result_end, int delay_between_sends_millisec,
			int timeout_millisec, String sensitive_measurement_channel, String insensitive_measurement_channel) {
		super(serial_port_name, command_string_end, command_result_end,
				delay_between_sends_millisec, timeout_millisec);
		useSensitiveChannel = true;
		setSensitiveMeasurementChannel(sensitive_measurement_channel);
		setInsensitiveMeasurementChannel(insensitive_measurement_channel);
		
		sendOrderAndSetDecoder(AlmenoVocabulary.MEASUREMENT_CHANNEL_CHOICE);
	}

	public float captureIlluminance() throws IlluminanceException {
		
		useSensitiveChannel = false;
		sendOrderAndSetDecoder(AlmenoVocabulary.MEASUREMENT_CHANNEL_CHOICE);
		sendOrderAndSetDecoder(AlmenoVocabulary.MEASUREMENT_VALUE);
		float _res = getMeasurementValue() * KLUX_TO_LUX_CONVERT;
		if (Floatings.isLess(_res, SENSITIVE_LUX_LIMIT)){
			
			useSensitiveChannel = true;
			sendOrderAndSetDecoder(AlmenoVocabulary.MEASUREMENT_CHANNEL_CHOICE);
			sendOrderAndSetDecoder(AlmenoVocabulary.MEASUREMENT_VALUE);
			_res = getMeasurementValue();
			
		}
		if (Floatings.isGreaterEqual(_res, INSENSITIVE_LUX_LIMIT)){
			throw new IlluminanceException("Illuminance limit reached!");
		}
		return _res;
	}

	@Override
	public boolean isACommandResult(String serial_return) {
		return true;
	}
	
	public String getSensitiveMeasurementChannel() {
		return sensitiveMeasurementChannel;
	}

	private void setSensitiveMeasurementChannel(String measurement_channel) {
		this.sensitiveMeasurementChannel = measurement_channel;
	}

	public static void main(String main_args[]){
		
		System.out.println("Ouverture du luxmetre");
		AhlbornLuxMeter _luxmeter = new AhlbornLuxMeter("COM3", 
				AhlbornLuxMeter.COMMAND_STRING_END, 
				AhlbornLuxMeter.COMMAND_RESULT_END, 350, 100,"00", "10");
		
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

	public String getInsensitiveMeasurementChannel() {
		return insensitiveMeasurementChannel;
	}

	public void setInsensitiveMeasurementChannel(
			String insensitive_measurement_channel) {
		this.insensitiveMeasurementChannel = insensitive_measurement_channel;
	}
	/**
	 * 
	 * @return The name of the currently in use measurement channel, that can be whereas the sensitive (0-26k) or insensitive (0-260k) one.
	 */
	public String getUsedMeasurementChannel(){
		if (useSensitiveChannel){
			return getSensitiveMeasurementChannel();
		}
		return getInsensitiveMeasurementChannel();
	}
	
}
