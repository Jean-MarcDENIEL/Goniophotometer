package inrs.goniophotoradiometer.motion.xliControlled;

enum UserFaultsBits {
	VALUE_OUT_OF_RANGE				(0, "Value is out of range"),
	INCORRECT_COMMAND_SYNTAX		(1, "Incorrect command syntax"),
	LAST_LABEL_ALREADY_IN_USE		(2, "Last label already in use"),
	LABEL_UNDEFINED					(3, "Label of this namenot defined"),
	MISSING_Z_PULSE_WHEN_HOMING		(4, "Missing Z pulse when homing"),
	HOMING_FAILED					(5, "Homing failed - no signal detected"),
	HOME_SIGNAL_TOO_NARROW			(6, "Home signal too narrow"),
	DRIVE_DE_ENERGISED				(7, "Drive de-energised"),
	CANNOT_RELATE_END_TO_LABEL		(8, "Cannot relate END statement to a label"),
	PROGRAM_MEMORY_BUFFER_FULL		(9, "Program memory buffer full"),
	NO_MORE_PROFILE_AVAILABLE		(10,"No more motion profiles available"),
	NO_MORE_LABEL_AVAILABLE			(11,"No more sequence labels available"),
	END_OF_TRAVEL_LIMIT_HIT			(12,"End of travail limit hit"),
	STILL_MOVING					(13,"Still moving"),
	DECELERATION_ERROR				(14,"Deceleration error"),
	TRANSMIT_BUFFER_OVERFLOW		(15,"Transmit buffer overflow"),
	USER_PROGRAM_NESTING_OVERFLOW	(16,"User program nesting overflow"),
	CANNOT_USE_UNDEFINED_PROFILE	(17,"Cannot use an undefined profile"),
	DRIVE_NOT_READY					(18,"Drive not ready"),
	SAVE_ERROR						(21,"Save error"),
	COMMAND_NOT_SUPPORTED			(22,"Command nopt supported by this product"),
	;
	private int		bitIndex;
	private String	bitMeaning;
	public int getBitIndex() {
		return bitIndex;
	}
	public String getBitMeaning() {
		return bitMeaning;
	}
	private UserFaultsBits(int bit_index, String bit_meaning) {
		bitIndex	= bit_index;
		bitMeaning	= bit_meaning;
	}
}
