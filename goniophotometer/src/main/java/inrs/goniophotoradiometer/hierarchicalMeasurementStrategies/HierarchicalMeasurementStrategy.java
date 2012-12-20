package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;

import c4sci.math.geometry.plane.PlaneVector;
import inrs.goniophotoradiometer.MeasurementStrategy;

public abstract class HierarchicalMeasurementStrategy implements MeasurementStrategy {

	public PlaneVector[] getPrimaryMeasurementPositions() {
		// TODO Auto-generated method stub
		return null;
	}

	public PlaneVector[] performMeasurement(PlaneVector measurement_pos_c_g_deg) {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Indicates whereas a patch should be cut in sub_patches.
	 * @param patch_
	 * @return
	 */
	abstract boolean			shouldCut(MeasurementPatch patch_);
	//abstract 
	abstract MeasurementPoint	createMeasurement();
	
	

}
