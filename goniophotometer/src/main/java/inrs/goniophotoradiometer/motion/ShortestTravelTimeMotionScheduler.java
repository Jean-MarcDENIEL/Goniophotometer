package inrs.goniophotoradiometer.motion;

import java.util.ArrayList;
import java.util.List;

import c4sci.math.geometry.plane.PlaneVector;
import inrs.goniophotoradiometer.MotionScheduler;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;
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
	private List<MeasurementPoint>	waitingMeasurements;
	private MeasurementPoint		lastPosition;
	
	public ShortestTravelTimeMotionScheduler(float arm_acc_deg_sec_2, float arm_max_speed_deg, float turntable_acc_deg_sec_2, float turntable_max_speed_deg){
		setArmAxeAccelerationDegSec2(arm_acc_deg_sec_2);
		setArmAxeMaxSpeedDegSec(arm_max_speed_deg);
		setTurntableAccelerationDegSec2(turntable_acc_deg_sec_2);
		setTurntableMaxSpeedDegSec(turntable_max_speed_deg);
		waitingMeasurements = new ArrayList<MeasurementPoint>();
		setLastPosition(null);
	}
	
	public void addMeasurementPosition(MeasurementPoint measurement_pos_c_g_rad) {
		getWaitingMeasurements().add(measurement_pos_c_g_rad);
	}

	public void addMeasurementPositions(MeasurementPoint[] measurement_pos_c_g_rad_tab) {
		for (MeasurementPoint _pos : measurement_pos_c_g_rad_tab){
			getWaitingMeasurements().add(_pos);
		}
	}

	public boolean hasWaitingMeasurementPositions() {
		return !getWaitingMeasurements().isEmpty();
	}

	public MeasurementPoint chooseNextMeasurementPosition() {
		int _res_index = 0;
		int _list_size = getWaitingMeasurements().size();
		if (_list_size == 0){
			return null;
		}
		if (getLastPosition() == null){
			_res_index = 0;
		}
		else{
			float _min_duration = Float.MAX_VALUE;
			for (int _i=0; _i<_list_size; _i++){
				PlaneVector _dist= getWaitingMeasurements().get(_i).getMeasurementPosition().opMinus(getLastPosition().getMeasurementPosition());
				float _duration = Math.max(_dist.getX() / getTurntableMaxSpeedDegSec() , _dist.getY() / getArmAxeMaxSpeedDegSec());

				if (_duration < _min_duration){
					_min_duration = _duration;
					_res_index = _i;
				}
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

	public final List<MeasurementPoint> getWaitingMeasurements() {
		return waitingMeasurements;
	}

	public final MeasurementPoint getLastPosition() {
		return lastPosition;
	}

	public final void setLastPosition(MeasurementPoint last_position) {
		this.lastPosition = last_position;
	}


}
