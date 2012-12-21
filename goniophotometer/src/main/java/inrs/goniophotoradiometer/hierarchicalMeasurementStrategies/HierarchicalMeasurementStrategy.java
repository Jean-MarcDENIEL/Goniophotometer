package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;

import java.util.ArrayList;
import java.util.List;

import c4sci.math.geometry.plane.PlaneVector;
import inrs.goniophotoradiometer.MeasurementStrategy;
import inrs.goniophotoradiometer.exceptions.RadiometryException;

public abstract class HierarchicalMeasurementStrategy implements MeasurementStrategy {

	private enum PatchSubdivision{
		ON_C_ONLY,
		ON_GAMMA_ONLY,
		ON_C_AND_GAMMA,
		NO_SUBDIVISION
	}	
	
	public static final int LOWER_C_BOUND	= 0;
	public static final int UPPER_C_BOUND	= 359;
	public static final int	LOWER_G_BOUND	= -90;
	public static final int UPPER_G_BOUND	= 90;
	
	public static final IntegerBounds C_BOUNDS = new IntegerBounds(LOWER_C_BOUND, UPPER_C_BOUND);
	public static final IntegerBounds G_BOUNDS = new IntegerBounds(LOWER_G_BOUND, UPPER_G_BOUND);
	
	private int maxCDelta;
	private int maxGDelta;
	private List<MeasurementPoint>	waitingPoints;
	protected MeasurementPatch rootPatch;
	
	/**
	 * 
	 * @param max_c_delta The maximum angular difference between two measurements on the C parameter
	 * @param max_g_delta The maximum angular difference between two measurements on the Gamma parameter
	 */
	public HierarchicalMeasurementStrategy(int max_c_delta, int max_g_delta) {
		maxCDelta = max_c_delta;
		maxGDelta = max_g_delta;
		rootPatch = null;
		waitingPoints = new ArrayList<MeasurementPoint>();
	}
	
	public PlaneVector[] getPrimaryMeasurementPositions() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void getPrimaryMeasurementPositions(MeasurementPatch current_patch, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException{
		boolean _should_cut_on_c = patch_c_bounds.getWidth() > maxCDelta;
		boolean _should_cut_on_g = patch_g_bounds.getWidth() > maxGDelta;
		PatchSubdivision _how_to_cut;
		if (_should_cut_on_c && _should_cut_on_g){
			_how_to_cut = PatchSubdivision.ON_C_AND_GAMMA;
		}
		else{
			if (_should_cut_on_c){
				_how_to_cut = PatchSubdivision.ON_C_ONLY;
			}
			else{
				if (_should_cut_on_g){
					_how_to_cut = PatchSubdivision.ON_GAMMA_ONLY;
				}
				else{
					_how_to_cut = PatchSubdivision.NO_SUBDIVISION;
				}
			}
		}
		if (_how_to_cut!= PatchSubdivision.NO_SUBDIVISION){
			subdividePatch(current_patch, patch_c_bounds, patch_g_bounds);
			MeasurementPatch[] _child_patches = current_patch.getSubPatches();
			for (MeasurementPatch _child_patch : _child_patches){
				IntegerBounds _child_c_bounds = MeasurementPatch.getChildPatchCBounds(current_patch, _child_patch, patch_c_bounds);
				IntegerBounds _child_g_bounds = MeasurementPatch.getChildPatchGammaBounds(current_patch, _child_patch, patch_g_bounds);
				getPrimaryMeasurementPositions(_child_patch, _child_c_bounds, _child_g_bounds);
			}
		}
	}

	public PlaneVector[] performMeasurement(PlaneVector measurement_pos_c_g_deg) throws RadiometryException {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * Indicates whereas a patch should be cut in sub_patches.
	 * @param patch_
	 * @return
	 */
	abstract boolean			shouldCut(MeasurementPatch patch_) throws RadiometryException;
	/**
	 * Creates a measurement point
	 * @return
	 */
	abstract MeasurementPoint	createMeasurementPoint();

	/**
	 * compute how to subdivide a patch in two or four sub patches.
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return The way to subdivide a patch in two or four sub patches.
	 */
	abstract PatchSubdivision computeSubdivision(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;
	/**
	 * Compute the C middle values of sub patches
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return Lower = lower child C middle value, Upper = upper child C middle value
	 */
	abstract IntegerBounds computeCSubdivision(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;
	abstract IntegerBounds computeGammaSubdivision(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;
	
	private void subdividePatch(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException{
		PatchSubdivision _subdivision_way = computeSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
		MeasurementPatch[] _child_patches;
		switch (_subdivision_way){
		case ON_C_ONLY:
			_child_patches = new MeasurementPatch[2];
			IntegerBounds _c_mids = computeCSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
			_child_patches[0] = new MeasurementPatch(_c_mids.getLowerBound(), patch_to_subdivide.getgMid());
			_child_patches[1] = new MeasurementPatch(_c_mids.getUpperBound(), patch_to_subdivide.getgMid());
			initializeMeasurementPatch(_child_patches[0], new IntegerBounds(patch_c_bounds.getLowerBound(), patch_to_subdivide.getcMid()), patch_g_bounds);
			initializeMeasurementPatch(_child_patches[1], new IntegerBounds(patch_to_subdivide.getcMid(), patch_c_bounds.getUpperBound()), patch_g_bounds);
			break;
		case ON_GAMMA_ONLY:
			_child_patches = new MeasurementPatch[2];
			IntegerBounds _g_mids = computeGammaSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
			_child_patches[0] = new MeasurementPatch(patch_to_subdivide.getcMid(), _g_mids.getLowerBound());
			_child_patches[1] = new MeasurementPatch(patch_to_subdivide.getcMid(), _g_mids.getUpperBound());
			initializeMeasurementPatch(_child_patches[0], patch_c_bounds, new IntegerBounds(patch_g_bounds.getLowerBound(), patch_to_subdivide.getgMid()));
			initializeMeasurementPatch(_child_patches[1], patch_c_bounds, new IntegerBounds(patch_to_subdivide.getgMid(), patch_g_bounds.getUpperBound()));
			break;
		case ON_C_AND_GAMMA:
			_child_patches = new MeasurementPatch[4];
			IntegerBounds _g_mids_2 = computeGammaSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
			IntegerBounds _c_mids_2 = computeCSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
			_child_patches[0] = new MeasurementPatch(_c_mids_2.getLowerBound(), _g_mids_2.getLowerBound());
			_child_patches[1] = new MeasurementPatch(_c_mids_2.getLowerBound(), _g_mids_2.getUpperBound());
			_child_patches[2] = new MeasurementPatch(_c_mids_2.getUpperBound(), _c_mids_2.getLowerBound());
			_child_patches[3] = new MeasurementPatch(_c_mids_2.getUpperBound(), _g_mids_2.getUpperBound());
			initializeMeasurementPatch(_child_patches[0], 
					new IntegerBounds(patch_c_bounds.getLowerBound(), patch_to_subdivide.getcMid()), 
					new IntegerBounds(patch_g_bounds.getLowerBound(), patch_to_subdivide.getgMid()));
			initializeMeasurementPatch(_child_patches[1], 
					new IntegerBounds(patch_c_bounds.getLowerBound(), patch_to_subdivide.getcMid()), 
					new IntegerBounds(patch_to_subdivide.getgMid(), patch_g_bounds.getUpperBound()));
			initializeMeasurementPatch(_child_patches[2], 
					new IntegerBounds(patch_to_subdivide.getcMid(), patch_c_bounds.getUpperBound()), 
					new IntegerBounds(patch_g_bounds.getLowerBound(), patch_to_subdivide.getgMid()));
			initializeMeasurementPatch(_child_patches[3], 
					new IntegerBounds(patch_to_subdivide.getcMid(), patch_c_bounds.getUpperBound()), 
					new IntegerBounds(patch_to_subdivide.getgMid(), patch_g_bounds.getUpperBound()));
			break;
		default:
			throw new RadiometryException("Cannot subdivide with NO_SUBDIVISON order");
		}
	}
	
	private void initializeMeasurementPatch(MeasurementPatch patch_to_initialize, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds){
		patch_to_initialize.setcMingMid(getMeasurementPoint(patch_c_bounds.getLowerBound(), patch_to_initialize.getgMid()));
		patch_to_initialize.setcMidgMin(getMeasurementPoint(patch_to_initialize.getcMid(), patch_g_bounds.getLowerBound()));
		patch_to_initialize.setcMidgMid(getMeasurementPoint(patch_to_initialize.getcMid(), patch_to_initialize.getgMid()));
		patch_to_initialize.setcMidgMax(getMeasurementPoint(patch_to_initialize.getcMid(), patch_g_bounds.getUpperBound()));
		patch_to_initialize.setcMaxgMid(getMeasurementPoint(patch_c_bounds.getUpperBound(), patch_to_initialize.getgMid()));
	}
	
	/**
	 * Search for an existing {@link MeasurementPoint} or creates and stores it has to be a point waiting for measurement,
	 * or retrieves an existing one.
	 * @return
	 */
	private MeasurementPoint getMeasurementPoint(int c_value, int g_value){
		MeasurementPoint _res = null;
		if (rootPatch != null){
			_res = MeasurementPatch.getExistingMeasurementPoint(rootPatch, C_BOUNDS, G_BOUNDS, c_value, g_value);
		}
		if (_res == null){
			_res = createMeasurementPoint();
		}
		return _res;
	}

}
