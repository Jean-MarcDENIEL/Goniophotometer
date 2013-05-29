package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;

import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.HierarchicalMeasurementStrategy.PatchSubdivision;

public class DefaultHierarchicalStrategyBehavior {
	/**
	 * By default cuts along the widest parameter range
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return
	 */
	public static PatchSubdivision computeSubdivisionWay(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds){

		if (patch_c_bounds.getWidth() > patch_g_bounds.getWidth()){
			return PatchSubdivision.ON_C_ONLY;
		}
		else{
			if (patch_c_bounds.getWidth() < patch_g_bounds.getWidth()){
				return PatchSubdivision.ON_GAMMA_ONLY;
			}
			else{
				return PatchSubdivision.ON_C_AND_GAMMA;
			}
		}
	}

	public static IntegerBounds computeSubpatchesGammaMiddleValues(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) {
		int _g_mid = patch_to_subdivide.getgMid();
		return new IntegerBounds((_g_mid+patch_g_bounds.getLowerBound())/2, (_g_mid+patch_g_bounds.getUpperBound())/2);
	}

	public IntegerBounds computeSubpatchesCMiddleValues(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds){
		int _c_mid = patch_to_subdivide.getcMid();
		return new IntegerBounds((_c_mid + patch_c_bounds.getLowerBound())/2, (_c_mid + patch_c_bounds.getUpperBound())/2);
	}
}
