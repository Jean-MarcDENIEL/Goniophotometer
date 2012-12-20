package inrs.goniophotoradiometer.motion.xliControlled;

enum DriveFaultsBits {
	COMPOSITE_FAULT 				(0, "Composite Fault"),
	OUTPUT_STAGE_OVER_CURRENT		(1, "Output stage over current"),
	SUPPLY_RAIL_FAILURE				(2, "Supply rail failure"),
	AMBIENT_OVER_TEMPERATURE		(3, "Ambient over temperature"),
	DRIVE_OVER_TEMPERATURE			(4, "Drive over temperature"),
	CONFIGURATION_ERROR				(5, "Configuration error"),
	MOTOR_HIGH_VOLTAGE_RAIL_FAILURE	(6, "Motor high voltage rail failure"),
	OUTPUT_FAULT					(7, "Output fault")
	;
	private int 	bitIndex;
	private String	bitMeaning;
	
	public int getBitIndex() {
		return bitIndex;
	}
	public String getBitMeaning() {
		return bitMeaning;
	}
	private DriveFaultsBits(int bit_index, String meaning_str) {
		bitIndex	= bit_index;
		bitMeaning	= meaning_str;
	}
}
