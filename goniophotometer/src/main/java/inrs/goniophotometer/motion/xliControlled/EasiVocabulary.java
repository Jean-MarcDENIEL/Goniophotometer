package inrs.goniophotometer.motion.xliControlled;

public enum EasiVocabulary{


	ACCELERATION("AA"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return ""+limitDecimalPrecision(motion_engine.getRevPerSquareSecondAcceleration(),2);
		}
	},
	DECELERATION("AD"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return ""+limitDecimalPrecision(motion_engine.getRevPerSquareSecondDeceleration(),2);
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
	// "H" should not be used as it blocks R(IP) and R(MV) return values that get queued until end of move.
	/*CHANGE_DIRECTION("H"){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "";
		}
	},*/
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
	MODE_INCREMENTAL("M"){
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
					limitDecimalPrecision(motion_engine.getRevPerSquareSecondAcceleration(),2)	+ "," +
					limitDecimalPrecision(motion_engine.getRevPerSquareSecondDeceleration(),2)	+ "," +
					motion_engine.getCountDistance()											+ "," +
					limitDecimalPrecision(motion_engine.getRevPerSecondMaxSpeed(),2)			+ "," +
					limitDecimalPrecision(motion_engine.getVelocityThreshold(),2)				+ ")";
		}
	},
	READ_DRIVE_FAULT_STATUS("R", new DriveFaultStateDecoder()){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(DF)";
		}
	},
	READ_ENCODER_POSITION("R"){		// not supported by the product
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(EP)";
		}
	},
	READ_IN_POSITION_FLAG("R", new StateDecoder(){

		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
						throws StateParsingException {
			final int X_R_IR_LENGTH = 3;
			if (state_string.length() != X_R_IR_LENGTH){
				throw new StateParsingException("1R(IR) : bad length : " + state_string.length() + "instead of " + X_R_IR_LENGTH);
			}
			motion_engine.setInPosition(state_string.charAt(1) == '1');
			
		}}){
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(IP)";
		}
	},
	READ_MOTOR_CURRENT("R", new StateDecoder(){

		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
						throws StateParsingException {
			String _value_str = state_string.substring(1, state_string.length()-2);
			try{
				motion_engine.setMotorCurrent(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new StateParsingException("R(MC) decoding " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(MC)";
		}
	},
	READ_MOTOR_RESOLUTION("R", new StateDecoder(){

		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
						throws StateParsingException {
			String _value_str = state_string.substring(1);
			try{
				motion_engine.setMotorResolution(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new StateParsingException("R(MR) decoding " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(MR)";
		}
	},
	READ_MOTOR_STANDY_CURRENT("R", new StateDecoder(){

		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
						throws StateParsingException {
			String _value_str = state_string.substring(1);
			try{
				motion_engine.setMotorStandbyCurrent(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new StateParsingException ("R(MS) decoding " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(MS)";
		}
	},
	READ_MOVING("R", new StateDecoder(){

		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
						throws StateParsingException {
			String _value_str = state_string.substring(1);
			motion_engine.setInMovement(_value_str.charAt(0) == '1');
		}}) {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(MV)";
		}
	},
	READ_POSITION_ABSOLUTE("R", new StateDecoder(){

		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
						throws StateParsingException {
			String _value_str = state_string.substring(1, state_string.length()-2);
			try{
				motion_engine.setActualCountPosition(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new StateParsingException("R(PA) decoding " + state_string + ": " + _value_str, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(PA)";
		}
	},
	READ_POSITION_INCREMENTAL("R", new StateDecoder(){

		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
						throws StateParsingException {
			String _value_str = state_string.substring(1, state_string.length()-2);
			try{
				motion_engine.setActualCountIncremental(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new StateParsingException("R(PI) decoding " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(
				XliControlledMotionEngine motion_engine) {
			return "(PI)";
		}
	},
	READ_READY_BUSY_FLAG("R", new StateDecoder(){

		public void decodeState(String state_string,
				XliControlledMotionEngine motion_engine)
						throws StateParsingException {
			motion_engine.setBusy(state_string.charAt(1) == '1');
		}}) {
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
			return ""  + limitDecimalPrecision(motion_engine.getVelocityThreshold(),2);
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
	/**
	 * 
	 * @return The Easi code of the command
	 */
	public String getLabel(){
		return commandLabel;
	}
	/**
	 * 
	 * @return The decoder that can be used to decode this order's result and update a {@link MotionEngine}.<br>
	 * <i>null</i> if there is nothing to decode. 
	 */
	public StateDecoder getResultDecoder() {
		return resultDecoder;
	}
	/**
	 * 
	 * @param motion_engine The {@link MotionEngine} to take into account.
	 * @return The string making the parameters of the order.
	 */
	public abstract String getCommandParameters(XliControlledMotionEngine motion_engine);
	/**
	 * 
	 * @param motion_engine The {@link MotionEngine} to take into account.
	 * @return The string corresponding to a state inquiry.
	 */
	public String getCommandState(XliControlledMotionEngine motion_engine){
		return ""+motion_engine.getEngineNumber()+getLabel();
	}
	/**
	 * 
	 * @param motion_engine the {@link MotionEngine} to take into account.
	 * @return The complete "set" sequence.
	 */
	public String getCommandSequence(XliControlledMotionEngine motion_engine){
		return getCommandState(motion_engine)+getCommandParameters(motion_engine);
	}

	private static float DEC_TEN			= 10.0f; 
	
	public static float limitDecimalPrecision(float fl_value, int dec_count){
		return (float)(((float)((int)(fl_value*Math.pow(DEC_TEN, (double)dec_count)))) / Math.pow(DEC_TEN, (float)dec_count));
	}

};
