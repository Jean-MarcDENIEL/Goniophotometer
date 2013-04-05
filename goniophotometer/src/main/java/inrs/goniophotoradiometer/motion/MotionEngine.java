package inrs.goniophotoradiometer.motion;
/**
 * This interface is aimed at piloting a motion engine, decoding and representing its state.
 * 
 * @author jeanmarc.deniel
 *
 */
public interface MotionEngine {

	/**
	 * 
	 * @return true is the -Limit has been reached.
	 */
	boolean isLowerLimitReached();
	/**
	 * 
	 * @return true if the +Limit has been reached.
	 */
	boolean isUpperLimitReached();
	
	/**
	 * 
	 * @return true if the engine is in an error state.
	 */
	boolean isFaulty();
	
	/**
	 * 
	 * @return Last error messages due to the engine user. Each message is separated by an end of line.
	 */
	String 	getUserFaults();
	/**
	 * 
	 * @return Last messages corresponding to the engine inner state. Each message is separated by an end of line.
	 */
	String 	getStatus();
	
	/**
	 * Moves the engine is a number of degrees.
	 * @param deg_value This value (in degree) can be positive or negative.
	 */
	void 	processRelativeMove(float deg_value);
	/**
	 * Moves the engine to achieve a certain position relatively to "0" position set by the {@link #setToZeroPosition()} method.
	 * @param deg_value The degree position. It can be positive or negative.
	 */
	void	processAbsoluteMove(float deg_value);
	/**
	 * Stops smoothly the engine by applying its deceleration that has been set through the {@link #setAngularDeceleration(float)} method.
	 */
	void	processSoftStop();
	/**
	 * Stops abruptly the motion.
	 */
	void	processEmergencystop();
	
	/**
	 * Sets the max velocity that can be achieved along a move.
	 * @param deg_per_second Degree per second angular speed.
	 */
	void	setAngularMaxVelocity(float deg_per_second);
	/**
	 * Sets the motion acceleration.
	 * @param deg_per_second_2 Degree per square second angular acceleration. Negative values are ignored.
	 */
	void	setAngularAcceleration(float deg_per_second_2);
	/**
	 * Set the motion deceleration.
	 * @param deg_per_second_2 Degree per square second angular deceleration. Negative values are ignored.
	 */
	void	setAngularDeceleration(float deg_per_second_2);
	
	/**
	 * Sets the zero reference position. This position is used by the {@link #processAbsoluteMove(float)} method.<br>
	 * This method should be called before the first call to {@link #processAbsoluteMove(float)} method.
	 */
	void	setToZeroPosition();
	/**
	 * Limits the motion range. <br>
	 * Needs the {@link #setToZeroPosition()} method to have set a reference zero position to be useful.<br>
	 * This lower limit should be set lower than {@link #setMaxPosition(float)} limit.
	 * @param deg_value Lower absolute position. It can be negative or positive.
	 */
	void	setMinPosition(float deg_value);
	/**
	 * Limits the motion range.<br>
	 * Needs the {@link #setToZeroPosition()} method to have set a reference zero position to be useful.<br>
	 * This upper limit should be set greater than {@link #setMinPosition(float)} limit.
	 * @param deg_value
	 */
	void	setMaxPosition(float deg_value);
	/**
	 * Waits for the motion engine to be in position or stops moving.
	 */
	void	waitForEndOfMotionAndSetTheoricalAbsolutePosition();
}
