package inrs.goniophotoradiometer.motion.xliControlledImplementation;

enum StatusBits {
	COMMAND_PROCESSING_PAUSED	(0, "Command processing paused"),
	LOOPING						(1, "Looping"),
	WAIT_FOR_TRIGGER			(2,"Wait for trigger"),
	RUNNING_PROGRAM				(3, "Runing program"),
	GOING_HOME					(4, "Going home"),
	WAITING_FOR_DELAY_OUTPUT	(5, "Waiting for delay output"),
	REGISTRATION_IN_PROGRESS	(6, "Registration in progress"),
	MOTOR_ENERGISED				(8, "Motor energised"),
	EVENT_TRIGGERED				(10,"Event triggered - active until trigger inputs are reset"),
	INPUT_NOT_MATCHING_LABEL	(11,"Input in LSEL not matching label"),
	LOWER_LIMIT_SEEN			(12,"-ve limit seen during last move"),
	UPPER_LIMIT_SEEN			(13,"+ve limit seen during last move"),
	MOVING						(18,"Moving (in motion)"),
	STATIONARY					(19,"Stationary (in position)"),
	NO_REGISTRATION_SIGNAL		(20,"No registration signal seen in registration window"),
	CANNOT_STOP_WITHIN_DISTANCE	(21,"Cannot stop within the defined registration distance")
	;
	private int		bitIndex;
	private String	bitMeaning;
	public int getBitIndex() {
		return bitIndex;
	}
	public String getBitMeaning() {
		return bitMeaning;
	}		
	private StatusBits(int bit_index, String bit_meaning){
		bitIndex	= bit_index;
		bitMeaning	= bit_meaning;
	}
}
