package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;

import java.util.ArrayList;
import java.util.List;

import inrs.goniophotoradiometer.MeasurementStrategy;
import inrs.goniophotoradiometer.exceptions.RadiometryException;

public abstract class HierarchicalMeasurementStrategy implements MeasurementStrategy {

	private enum PatchSubdivision{
		ON_C_ONLY (2),
		ON_GAMMA_ONLY (2),
		ON_C_AND_GAMMA (4),
		NO_SUBDIVISION (0);
		
		private int childCount;
		private PatchSubdivision(int child_count){
			childCount = child_count;
		}
		public int getChildCount(){
			return childCount;
		}
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
	private MeasurementPatch rootPatch;
	private MeasurementPoint cMingMinMeasurement;
	private MeasurementPoint cMingMaxMeasurement;
	private MeasurementPoint cMaxgMinMeasurement;
	private MeasurementPoint cMaxgMaxMeasurement;

	/**
	 * 
	 * @param max_c_delta The maximum angular difference between two measurements on the C parameter
	 * @param max_g_delta The maximum angular difference between two measurements on the Gamma parameter
	 */
	public HierarchicalMeasurementStrategy(int max_c_delta, int max_g_delta) {
		maxCDelta = max_c_delta;
		maxGDelta = max_g_delta;
		setRootPatch(new MeasurementPatch((LOWER_C_BOUND+UPPER_C_BOUND)/2, (LOWER_G_BOUND+UPPER_G_BOUND)/2));
		initializeMeasurementPatch(getRootPatch(), C_BOUNDS, G_BOUNDS);
		waitingPoints = new ArrayList<MeasurementPoint>();
		setcMingMinMeasurement(createMeasurementPoint());
		setcMingMaxMeasurement(createMeasurementPoint());
		setcMaxgMinMeasurement(createMeasurementPoint());
		setcMaxgMaxMeasurement(createMeasurementPoint());
		waitingPoints.add(getcMingMinMeasurement());
		waitingPoints.add(getcMingMaxMeasurement());
		waitingPoints.add(getcMaxgMinMeasurement());
		waitingPoints.add(getcMaxgMaxMeasurement());
	}

	public MeasurementPoint[] getPrimaryMeasurementPositions() throws RadiometryException {
		getPrimaryMeasurementPositions(getRootPatch(), C_BOUNDS, G_BOUNDS);
		return convertWaitingPointsToArray();
	}
	/**
	 * Indicates whereas a patch should be cut in sub_patches.
	 * @param patch_ The patch that must be fully completed (not including sub patches).
	 * @return
	 */
	abstract boolean shouldCut(MeasurementPatch patch_, MeasurementPoint c_min_g_min_point, MeasurementPoint c_min_g_max_point, MeasurementPoint c_max_g_min_point, MeasurementPoint c_max_g_max_point) throws RadiometryException;
	/**
	 * Creates a measurement point.<br>
	 * <b>Pattern</b> This method implements the <b>factory method</b> pattern.
	 * @return
	 */
	abstract MeasurementPoint createMeasurementPoint();
	/**
	 * Performs necessary measurement and completes the parameter {@link MeasurementPoint}.
	 * @param meas_point a {@link MeasurementPoint} got through this' {@link #createMeasurementPoint()} method call. 
	 * @throws RadiometryException
	 */
	abstract void completeMeasurementPoint(MeasurementPoint meas_point) throws RadiometryException;

	/**
	 * Computes how to subdivide a patch in two or four sub patches.
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return The way to subdivide a patch in two or four sub patches.
	 */
	abstract PatchSubdivision computeSubdivision(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;
	/**
	 * Computes the C middle values of sub patches
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return Lower = lower child C middle value, Upper = upper child C middle value
	 */
	abstract IntegerBounds computeCSubdivision(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;
	/**
	 * Computes the G middle values of sub patches
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return
	 * @throws RadiometryException
	 */
	abstract IntegerBounds computeGammaSubdivision(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;

	public MeasurementPoint[] performMeasurement(MeasurementPoint measurement_pos_c_g_deg) throws RadiometryException {
		// first measures
		//
		completeMeasurementPoint(measurement_pos_c_g_deg);
		// then inspects corresponding patches and eventually adds MeasurementPoints of waitingPoints  
		//
		inspectPatch(getRootPatch(), getcMingMinMeasurement(), getcMingMaxMeasurement(), 
				getcMaxgMinMeasurement(), getcMaxgMaxMeasurement(),
				measurement_pos_c_g_deg, C_BOUNDS, G_BOUNDS);
		// translates waitingPoints to a resulting array
		//
		return convertWaitingPointsToArray();
	}	

	private void inspectPatch(MeasurementPatch meas_patch, 
			MeasurementPoint c_min_g_min_point,
			MeasurementPoint c_min_g_max_point, 
			MeasurementPoint c_max_g_min_point,
			MeasurementPoint c_max_g_max_point,
			MeasurementPoint meas_point, 
			IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException{
		
		if (meas_patch.hasBeenSubdivided()){
			// inspects the children whose C/G ranges contain the measurement point
			//
			MeasurementPatch[] _sub_patches = meas_patch.getSubPatches();
			for (MeasurementPatch _child_patch : _sub_patches){
				IntegerBounds _child_c_bounds = MeasurementPatch.getChildPatchCBounds(meas_patch, _child_patch, patch_c_bounds);
				IntegerBounds _child_g_bounds = MeasurementPatch.getChildPatchGammaBounds(meas_patch, _child_patch, patch_g_bounds);
				if (	_child_c_bounds.boundsValue((int)meas_point.getMeasurementPosition().getX()) &&
						_child_g_bounds.boundsValue((int)meas_point.getMeasurementPosition().getY())){
					MeasurementPoint[] _bounds = MeasurementPatch.getSurroundingMeasurements(_child_patch, meas_patch, 
							c_min_g_min_point, c_min_g_max_point, c_max_g_min_point, c_max_g_max_point);
					inspectPatch(_child_patch, 
							_bounds[MeasurementPatch.BoundingIndexes.C_MIN_G_MIN.getIndex()], 
							_bounds[MeasurementPatch.BoundingIndexes.C_MIN_G_MAX.getIndex()], 
							_bounds[MeasurementPatch.BoundingIndexes.C_MAX_G_MIN.getIndex()], 
							_bounds[MeasurementPatch.BoundingIndexes.C_MAX_G_MAX.getIndex()], 
							meas_point, _child_c_bounds, _child_g_bounds);
				}
			}
		}
		else{
			if (	meas_patch.isComplete() &&
					c_min_g_min_point.hasBeenYetMeasured() &&
					c_min_g_max_point.hasBeenYetMeasured() &&
					c_max_g_min_point.hasBeenYetMeasured() &&
					c_max_g_max_point.hasBeenYetMeasured() &&
					shouldCut(meas_patch, c_min_g_min_point, c_min_g_max_point, c_max_g_min_point, c_max_g_max_point)){
				subdividePatch(meas_patch, patch_c_bounds, patch_g_bounds);
			}
		}
	}


	
	private MeasurementPoint[] convertWaitingPointsToArray(){
		MeasurementPoint[] _res = waitingPoints.toArray(new MeasurementPoint[0]);
		waitingPoints.clear();
		return _res;
	}
	
	private void getPrimaryMeasurementPositions(MeasurementPatch current_patch, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException{
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

	private void subdividePatch(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException{
		PatchSubdivision _subdivision_way = computeSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
		MeasurementPatch[] _child_patches = new MeasurementPatch[_subdivision_way.getChildCount()];
		switch (_subdivision_way){
		case ON_C_ONLY:
			IntegerBounds _c_mids = computeCSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
			_child_patches[MeasurementPatch.ChildPatchIndexes.C_CUT_LOWER_CHILD.getChildIndex()] = new MeasurementPatch(_c_mids.getLowerBound(), patch_to_subdivide.getgMid());
			_child_patches[MeasurementPatch.ChildPatchIndexes.C_CUT_UPPER_CHILD.getChildIndex()] = new MeasurementPatch(_c_mids.getUpperBound(), patch_to_subdivide.getgMid());
			initializeMeasurementPatch(_child_patches[MeasurementPatch.ChildPatchIndexes.C_CUT_LOWER_CHILD.getChildIndex()], new IntegerBounds(patch_c_bounds.getLowerBound(), patch_to_subdivide.getcMid()), patch_g_bounds);
			initializeMeasurementPatch(_child_patches[MeasurementPatch.ChildPatchIndexes.C_CUT_UPPER_CHILD.getChildIndex()], new IntegerBounds(patch_to_subdivide.getcMid(), patch_c_bounds.getUpperBound()), patch_g_bounds);
			break;
		case ON_GAMMA_ONLY:
			IntegerBounds _g_mids = computeGammaSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
			_child_patches[MeasurementPatch.ChildPatchIndexes.G_CUT_LOWER_CHILD.getChildIndex()] = new MeasurementPatch(patch_to_subdivide.getcMid(), _g_mids.getLowerBound());
			_child_patches[MeasurementPatch.ChildPatchIndexes.G_CUT_UPPER_CHILD.getChildIndex()] = new MeasurementPatch(patch_to_subdivide.getcMid(), _g_mids.getUpperBound());
			initializeMeasurementPatch(_child_patches[MeasurementPatch.ChildPatchIndexes.G_CUT_LOWER_CHILD.getChildIndex()], patch_c_bounds, new IntegerBounds(patch_g_bounds.getLowerBound(), patch_to_subdivide.getgMid()));
			initializeMeasurementPatch(_child_patches[MeasurementPatch.ChildPatchIndexes.G_CUT_UPPER_CHILD.getChildIndex()], patch_c_bounds, new IntegerBounds(patch_to_subdivide.getgMid(), patch_g_bounds.getUpperBound()));
			break;
		case ON_C_AND_GAMMA:
			IntegerBounds _g_mids_2 = computeGammaSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
			IntegerBounds _c_mids_2 = computeCSubdivision(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
			_child_patches[MeasurementPatch.ChildPatchIndexes.C_LOWER_AND_G_LOWER_CHILD.getChildIndex()] = new MeasurementPatch(_c_mids_2.getLowerBound(), _g_mids_2.getLowerBound());
			_child_patches[MeasurementPatch.ChildPatchIndexes.C_LOWER_AND_G_UPPER_CHILD.getChildIndex()] = new MeasurementPatch(_c_mids_2.getLowerBound(), _g_mids_2.getUpperBound());
			_child_patches[MeasurementPatch.ChildPatchIndexes.C_UPPER_AND_G_LOWER_CHILD.getChildIndex()] = new MeasurementPatch(_c_mids_2.getUpperBound(), _c_mids_2.getLowerBound());
			_child_patches[MeasurementPatch.ChildPatchIndexes.C_UPPER_AND_G_UPPER_CHILD.getChildIndex()] = new MeasurementPatch(_c_mids_2.getUpperBound(), _g_mids_2.getUpperBound());
			initializeMeasurementPatch(_child_patches[MeasurementPatch.ChildPatchIndexes.C_LOWER_AND_G_LOWER_CHILD.getChildIndex()], 
					new IntegerBounds(patch_c_bounds.getLowerBound(), patch_to_subdivide.getcMid()), 
					new IntegerBounds(patch_g_bounds.getLowerBound(), patch_to_subdivide.getgMid()));
			initializeMeasurementPatch(_child_patches[MeasurementPatch.ChildPatchIndexes.C_LOWER_AND_G_UPPER_CHILD.getChildIndex()], 
					new IntegerBounds(patch_c_bounds.getLowerBound(), patch_to_subdivide.getcMid()), 
					new IntegerBounds(patch_to_subdivide.getgMid(), patch_g_bounds.getUpperBound()));
			initializeMeasurementPatch(_child_patches[MeasurementPatch.ChildPatchIndexes.C_UPPER_AND_G_LOWER_CHILD.getChildIndex()], 
					new IntegerBounds(patch_to_subdivide.getcMid(), patch_c_bounds.getUpperBound()), 
					new IntegerBounds(patch_g_bounds.getLowerBound(), patch_to_subdivide.getgMid()));
			initializeMeasurementPatch(_child_patches[MeasurementPatch.ChildPatchIndexes.C_UPPER_AND_G_UPPER_CHILD.getChildIndex()], 
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
		if (getRootPatch() != null){
			_res = MeasurementPatch.getExistingMeasurementPoint(getRootPatch(), C_BOUNDS, G_BOUNDS, c_value, g_value);
		}
		if (_res == null){
			_res = createMeasurementPoint();
			waitingPoints.add(_res);
		}
		return _res;
	}

	public final MeasurementPatch getRootPatch() {
		return rootPatch;
	}

	public final void setRootPatch(MeasurementPatch root_patch) {
		this.rootPatch = root_patch;
	}

	public final MeasurementPoint getcMingMinMeasurement() {
		return cMingMinMeasurement;
	}

	public final void setcMingMinMeasurement(MeasurementPoint c_min_g_min_measurement) {
		this.cMingMinMeasurement = c_min_g_min_measurement;
	}

	public final MeasurementPoint getcMingMaxMeasurement() {
		return cMingMaxMeasurement;
	}

	public final void setcMingMaxMeasurement(MeasurementPoint c_min_g_max_measurement) {
		this.cMingMaxMeasurement = c_min_g_max_measurement;
	}

	public final MeasurementPoint getcMaxgMinMeasurement() {
		return cMaxgMinMeasurement;
	}

	public final void setcMaxgMinMeasurement(MeasurementPoint c_max_g_min_measurement) {
		this.cMaxgMinMeasurement = c_max_g_min_measurement;
	}

	public final MeasurementPoint getcMaxgMaxMeasurement() {
		return cMaxgMaxMeasurement;
	}

	public final void setcMaxgMaxMeasurement(MeasurementPoint c_max_g_max_measurement) {
		this.cMaxgMaxMeasurement = c_max_g_max_measurement;
	}

}
