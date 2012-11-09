package inrs.goniophotometer.motion.XliControlled;

public enum EasiVocabulary{
	
	
	ACCELERATION("AA"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return ""+motion_engine.getCountAcceleration();
		}
	},
	DECELERATION("AD"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return ""+motion_engine.getCountDeceleration();
		}
	},
	DISTANCE("D"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return ""+motion_engine.getCountDistance();
		}
	},
	MOVE("G"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},
	CHANGE_DIRECTION("H"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},
	EMERGENCY_STOP("K"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},
	CONFIGURE_LIMITS("LIMITS"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(" +  
				(motion_engine.isHardLimitsAllowed() ? "0" : "3")+ "," +
				(motion_engine.isHardLimitsNormalyOpen() ? "0" : "1") + "," +
				"0)" ;
		}
	},
	MODE("M"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "I";
		}
	},
	SHUTDOWN("OFF"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},
	TURN_ON("ON"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},
	PROFILE_1("PROFILE1"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(" +
					motion_engine.getCountAcceleration()	+ "," +
					motion_engine.getCountDeceleration()	+ "," +
					motion_engine.getCountDistance()		+ "," +
					motion_engine.getCountMaxSpeed()		+ "," +
					motion_engine.getVelocityThreshold()	+ ")";
		}
	},
	READ_DRIVE_FAULT_STATUS("R", new DriveFaultStateDecoder()){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(DF)";
		}
	},
	READ_ENCODER_POSITION("R"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(EP)";
		}
	},
	READ_IN_POSITION_FLAG("R"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(IP)";
		}
	},
	READ_MOTOR_CURRENT("R") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(MC)";
		}
	},
	READ_MOTOR_RESOLUTION("R") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(MR)";
		}
	},
	READ_MOTOR_STANDY_CURRENT("R") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(MS)";
		}
	},
	READ_MOVING("R") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(MV)";
		}
	},
	READ_POSITION_ABSOLUTE("R") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(PA)";
		}
	},
	READ_POSITION_INCREMENTAL("R") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(PI)";
		}
	},
	READ_READY_BUSY_FLAG("R") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(RB)";
		}
	},
	READ_STATUS("R", new StatusBitsStateDecoder()) {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(ST)";
		}
	},
	READ_USER_PROGRAM_FAULT("R", new UserFaultsStateDecoder()) {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(UF)";
		}
	},
	RETURN_TO_FACTORY_SETTINGS("RFS") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},
	SMOOTH_STOP("S") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},
	SAVE_CONFIGURATION("SV") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},
	USE_PROFILE_1("USE") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(1)";
		}
	},
	START_STOP_VELOCITY_THRESHOLD("VS") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "("  + motion_engine.getVelocityThreshold() + ")";
		}
	},
	RESET("Z") {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	}		
	;


	private String 			commandLabel;
	private StateDecoder	resultDecoder;
	
	EasiVocabulary(String cmd_label){
		commandLabel 	= cmd_label;
		resultDecoder	=	null;
	}
	private EasiVocabulary(String cmd_label, StateDecoder result_decoder) {
		commandLabel 	= cmd_label;
		resultDecoder	= result_decoder;
	}
	public String getLabel(){
		return commandLabel;
	}
	public StateDecoder getResultDecoder() {
		return resultDecoder;
	}
	public abstract String getCommandParameters(XliControlledMotionEngine motion_engine);
	public String getCommandState(XliControlledMotionEngine motion_engine){
		return ""+motion_engine.getEngineNumber()+getLabel();
	}
	public String getCommandSequence(XliControlledMotionEngine motion_engine){
		return getCommandState(motion_engine)+getCommandParameters(motion_engine);
	}
	public boolean isAFlagReaderCommand(){
		return false;
	}
};
