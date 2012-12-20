package inrs.goniophotoradiometer.motion;

import java.util.ArrayList;
import java.util.List;

import c4sci.math.geometry.plane.PlaneVector;
import inrs.goniophotoradiometer.MotionScheduler;
/**
 * This class implements the {@link MotionScheduler} interface so that
 * the chosen next position implies the shortest travel time  from the last one.
 * It supposes that arm and turn table are moved in parallel.
 * @author jeanmarc.deniel
 *
 */
public class ShortestTravelTimeMotionScheduler implements MotionScheduler {

	private	float					armAxeAccelerationDegSec2;
	private float					armAxeMaxSpeedDegSec;
	private float					turntableAccelerationDegSec2;
	private float					turntableMaxSpeedDegSec;
	private List<PlaneVector>		waitingMeasurements;
	private PlaneVector				lastPosition;
	
	public ShortestTravelTimeMotionScheduler(float arm_acc_deg_sec_2, float arm_max_speed_deg, float turntable_acc_deg_sec_2, float turntable_max_speed_deg){
		setArmAxeAccelerationDegSec2(arm_acc_deg_sec_2);
		setArmAxeMaxSpeedDegSec(arm_max_speed_deg);
		setTurntableAccelerationDegSec2(turntable_acc_deg_sec_2);
		setTurntableMaxSpeedDegSec(turntable_max_speed_deg);
		waitingMeasurements = new ArrayList<PlaneVector>();
		setLastPosition(new PlaneVector(0f,0f));
	}
	
	public void addMeasurementPosition(PlaneVector measurement_pos_c_g_rad) {
		getWaitingMeasurements().add(measurement_pos_c_g_rad);
	}

	public void addMeasurementPositions(
			PlaneVector[] measurement_pos_c_g_rad_tab) {
		for (PlaneVector _pos : measurement_pos_c_g_rad_tab){
			getWaitingMeasurements().add(_pos);
		}
	}

	public boolean hasWaitingMeasurementPositions() {
		return !getWaitingMeasurements().isEmpty();
	}

	public PlaneVector chooseNextMeasurementPosition() {
		int _res_index = 0;
		int _list_size = getWaitingMeasurements().size();
		float _min_duration = Float.MAX_VALUE;
		for (int _i=0; _i<_list_size; _i++){
			
			PlaneVector _dist= getWaitingMeasurements().get(_i).opMinus(lastPosition);
			float _duration = Math.max(_dist.getX() / getTurntableMaxSpeedDegSec() , _dist.getY() / getArmAxeMaxSpeedDegSec());
			
			if (_duration < _min_duration){
				_min_duration = _duration;
				_res_index = _i;
			}
		}
		setLastPosition(getWaitingMeasurements().get(_res_index));
		getWaitingMeasurements().remove(_res_index);
		return getLastPosition();
	}

	public final float getArmAxeAccelerationDegSec2() {
		return armAxeAccelerationDegSec2;
	}

	public final void setArmAxeAccelerationDegSec2(float arm_axe_acceleration_deg_sec_2) {
		this.armAxeAccelerationDegSec2 = arm_axe_acceleration_deg_sec_2;
	}

	public final float getArmAxeMaxSpeedDegSec() {
		return armAxeMaxSpeedDegSec;
	}

	public final void setArmAxeMaxSpeedDegSec(float arm_axe_max_speed_deg_sec) {
		this.armAxeMaxSpeedDegSec = arm_axe_max_speed_deg_sec;
	}

	public final float getTurntableAccelerationDegSec2() {
		return turntableAccelerationDegSec2;
	}

	public final void setTurntableAccelerationDegSec2(float turntable_acceleration_deg_sec_2) {
		this.turntableAccelerationDegSec2 = turntable_acceleration_deg_sec_2;
	}

	public final float getTurntableMaxSpeedDegSec() {
		return turntableMaxSpeedDegSec;
	}

	public final void setTurntableMaxSpeedDegSec(float turntable_max_speed_deg_sec) {
		this.turntableMaxSpeedDegSec = turntable_max_speed_deg_sec;
	}

	public final List<PlaneVector> getWaitingMeasurements() {
		return waitingMeasurements;
	}

	public PlaneVector getLastPosition() {
		return lastPosition;
	}

	public void setLastPosition(PlaneVector lastPosition) {
		this.lastPosition = lastPosition;
	}


}
