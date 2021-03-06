package inrs.goniophotoradiometer.motion.xliControlledImplementation;

import inrs.goniophotoradiometer.motion.MotionEngine;
import c4sci.io.serial.SerialDevice;
import c4sci.io.serial.SerialStateDecoder;
import c4sci.io.serial.SerialStateParsingException;
import c4sci.io.serial.SerialVocabulary;

public enum EasiVocabulary implements SerialVocabulary {



	ACCELERATION("AA"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return ""+limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getRevPerSquareSecondAcceleration(),2);
		}
	},
	ARM_COMMAND("ARM1"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}

	},
	DECELERATION("AD"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return ""+limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getRevPerSquareSecondDeceleration(),2);
		}
	},
	DISTANCE("D"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return ""+ ((XliControlledMotionEngine)motion_engine).getCountDistance();
		}
	},
	MOVE("G"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
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
	GO_HOME("GH") {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}
	},	
	CONFIGURE_HOMING("HOME1"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "("+
					(((XliControlledMotionEngine)motion_engine).isHomingPositiveReferenceEdge()?"+":"-") + "," +
					(((XliControlledMotionEngine)motion_engine).isHomeSwitchNormallyClosed()?"1":"0") + "," +
					(((XliControlledMotionEngine)motion_engine).isHomePositiveSense()?"":"-") + 
					limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getHomingVelocityRevPerSecond(),2) + "," +
					limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getHomingAccelerationDecelerationRevPerSecond(),2) + ",0)";
		}
	},

	EMERGENCY_STOP("K"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}
	},
	CONFIGURE_LIMITS("LIMITS"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(" +  
					(((XliControlledMotionEngine)motion_engine).isHardLimitsAllowed() ? "0" : "3")+ "," +
					(((XliControlledMotionEngine)motion_engine).isHardLimitsNormalyOpen() ? "0" : "1") + "," +
					"0)" ;
		}
	},
	MODE_INCREMENTAL("M"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "I";
		}
	},
	SHUTDOWN("OFF"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}
	},
	TURN_ON("ON"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}
	},
	PROFILE_1("PROFILE1"){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(" +
					limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getRevPerSquareSecondAcceleration(),2)	+ "," +
					limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getRevPerSquareSecondDeceleration(),2)	+ "," +
					((XliControlledMotionEngine)motion_engine).getCountDistance()											+ "," +
					limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getRevPerSecondMaxSpeed(),2)			+ "," +
					limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getVelocityThreshold(),2)				+ ")";
		}
	},
	READ_DRIVE_FAULT_STATUS("R", new DriveFaultStateDecoder()){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(DF)";
		}
	},
	READ_ENCODER_POSITION("R"){		// not supported by the product
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(EP)";
		}
	},

	READ_IN_POSITION_FLAG("R", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice motion_engine) throws SerialStateParsingException {

			final int _X_R_IR_LENGTH = 3;
			if (state_string.length() != _X_R_IR_LENGTH){
				throw new SerialStateParsingException("1R(IR) : bad length : " + state_string.length() + "instead of " + _X_R_IR_LENGTH);
			}
			((XliControlledMotionEngine)motion_engine).setInPosition(state_string.charAt(1) == '1');

		}}){
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(IP)";
		}
	},
	
	READ_MOTOR_CURRENT("R", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice motion_engine) throws SerialStateParsingException {
			String _value_str = state_string.substring(1, state_string.length()-2);
			try{
				((XliControlledMotionEngine)motion_engine).setMotorCurrent(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new SerialStateParsingException("R(MC) decoding " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(MC)";
		}
	},
	READ_MOTOR_RESOLUTION("R", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice motion_engine) throws SerialStateParsingException {
			String _value_str = state_string.substring(1);
			try{
				((XliControlledMotionEngine)motion_engine).setMotorResolution(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new SerialStateParsingException("R(MR) decoding " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(MR)";
		}
	},
	READ_MOTOR_STANDY_CURRENT("R", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice motion_engine) throws SerialStateParsingException {
			String _value_str = state_string.substring(1);
			try{
				((XliControlledMotionEngine)motion_engine).setMotorStandbyCurrent(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new SerialStateParsingException ("R(MS) decoding " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(MS)";
		}
	},
	READ_MOVING("R", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice motion_engine) throws SerialStateParsingException {
			String _value_str = state_string.substring(1);
			((XliControlledMotionEngine)motion_engine).setInMovement(_value_str.charAt(0) == '1');
		}}) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(MV)";
		}
	},
	READ_POSITION_ABSOLUTE("R", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice motion_engine) throws SerialStateParsingException {
			String _value_str = state_string.substring(1, state_string.length()-1);
			try{
				((XliControlledMotionEngine)motion_engine).setActualTheoricalCountPosition(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new SerialStateParsingException("R(PA) decoding : " + _value_str + "from : " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(PA)";
		}
	},
	READ_POSITION_INCREMENTAL("R", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice motion_engine) throws SerialStateParsingException {
			//String _value_str = state_string.substring(1, state_string.length()-2);
			try{
				//motion_engine.setActualCountIncremental(Integer.parseInt(_value_str));
			}
			catch(NumberFormatException _e){
				throw new SerialStateParsingException("R(PI) decoding " + state_string, _e);
			}
		}}) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(PI)";
		}
	},
	READ_READY_BUSY_FLAG("R", new SerialStateDecoder(){

		public void decodeState(String state_string, SerialDevice motion_engine) throws SerialStateParsingException {
			((XliControlledMotionEngine)motion_engine).setBusy(state_string.charAt(1) == '1');
		}}) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(RB)";
		}
	},
	READ_STATUS("R", new StatusBitsStateDecoder()) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(ST)";
		}
	},
	READ_USER_PROGRAM_FAULT("R", new UserFaultsStateDecoder()) {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(UF)";
		}
	},
	RETURN_TO_FACTORY_SETTINGS("RFS") {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}
	},
	SMOOTH_STOP("S") {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}
	},
	SAVE_CONFIGURATION("SV") {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}
	},
	USE_PROFILE_1("USE") {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(1)";
		}
	},
	START_STOP_VELOCITY_THRESHOLD("VS") {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return ""  + limitDecimalPrecision(((XliControlledMotionEngine)motion_engine).getVelocityThreshold(),2);
		}
	},
	SET_POSITION_ABSOLUTE("R") {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "(PA," + ((XliControlledMotionEngine)motion_engine).getActualTheoricalCountPosition()+")";
		}
	},
	RESET("Z") {
		@Override
		public String getCommandParameters(SerialDevice motion_engine) {
			return "";
		}
	}		
	;


	private String 				commandLabel;
	private SerialStateDecoder	resultDecoder;

	EasiVocabulary(String cmd_label){
		commandLabel 	= cmd_label;
		resultDecoder	=	null;
	}
	private EasiVocabulary(String cmd_label, SerialStateDecoder result_decoder) {
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
	public SerialStateDecoder getResultDecoder() {
		return resultDecoder;
	}
	/**
	 * 
	 * @param motion_engine The {@link MotionEngine} to take into account.
	 * @return The string making the parameters of the order.
	 */
	public abstract String getCommandParameters(SerialDevice motion_engine);
	/**
	 * 
	 * @param motion_engine The {@link MotionEngine} to take into account.
	 * @return The string corresponding to a state inquiry.
	 */
	public String getCommandState(SerialDevice motion_engine){
		return ""+((XliControlledMotionEngine)motion_engine).getEngineNumber()+getLabel();
	}

	private static final float DEC_TEN			= 10.0f; 

	public static float limitDecimalPrecision(float fl_value, int dec_count){
		return (float)(((float)((int)(fl_value*Math.pow(DEC_TEN, (double)dec_count)))) / Math.pow(DEC_TEN, (float)dec_count));
	}

};
