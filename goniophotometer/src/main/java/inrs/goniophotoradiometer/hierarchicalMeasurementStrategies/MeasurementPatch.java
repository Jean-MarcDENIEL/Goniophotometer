package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;

import inrs.goniophotoradiometer.exceptions.RadiometryException;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a two dimensional bounded areas in the (C,gamma) space.
 * C and Gamma values are integers. Surrounding values indexes are indicated below.
 * <pre>
 * 
 *         Cmax /---------o--------/
 *             /1        /       3/
 *            /         /        /
 *      Cmid o---------o--------o
 *          /         /        /
 *         /0        /       2/
 *   Cmin /---------o--------/
 *     Gmin      Gmid     Gmax
 * </pre>
 * 
 * Child patches and surrounding values indexes are indicated below :<br>
 * <pre>
 * Case of a four children patch
 *  
 *         Cmax /---------o--------/
 *             /    1    /   3    /
 *            /         /        /
 *      Cmid o---------o--------o
 *          /         /        /
 *         /    0    /  2     /
 *   Cmin /---------o--------/
 *     Gmin      Gmid     Gmax
 * 
 * </pre>
 * 
 *  * <pre>
 * Case of a two children patch cut along the C parameter : in this case mid C values are the same for the parent patch and the child patch.
 *  
 *         Cmax /---------o--------/
 *             /          1       /
 *            /                  /
 *      Cmid o------------------/
 *          /                  /
 *         /        0         /
 *   Cmin /---------o--------/
 *     Gmin      Gmid     Gmax
 * 
 * </pre>
 * 
 *  * <pre>
 * Case of a two children patch cut along the G parameter : in this case mid G values are the same for the parent patch and the child patch.
 *  
 *         Cmax /---------o--------/
 *             /         /        /
 *            /         /        /
 *      Cmid /    0    /    1   /
 *          /         /        /
 *         /         /        /
 *   Cmin /---------o--------/
 *     Gmin      Gmid     Gmax
 * 
 * </pre>
 * 
 * 
 * To these (C,Gamma) corresponds 5 {@link MeasurementPoint}s
 * @author jeanmarc.deniel
 *
 */
public class MeasurementPatch {



	private int cMid;
	private int gMid;
	private MeasurementPoint cMingMid;
	private MeasurementPoint cMidgMin;
	private MeasurementPoint cMidgMid;
	private MeasurementPoint cMidgMax;
	private MeasurementPoint cMaxgMid;

	private List<MeasurementPatch> subPatches;

	public enum BoundingIndexes{
		C_MIN_G_MIN(0),
		C_MIN_G_MAX(1),
		C_MAX_G_MIN(2),
		C_MAX_G_MAX(3);
		
		private int indexValue;
		public static final int INDEX_COUNT = 4; 
		private BoundingIndexes(int index_value){
			indexValue = index_value;
		}
		public int getIndex(){
			return indexValue;
		}
	}
	
	public enum ChildPatchIndexes {
		C_CUT_LOWER_CHILD(0),
		C_CUT_UPPER_CHILD(1),
		G_CUT_LOWER_CHILD(0),
		G_CUT_UPPER_CHILD(1),
		C_LOWER_AND_G_LOWER_CHILD(0),
		C_LOWER_AND_G_UPPER_CHILD(1),
		C_UPPER_AND_G_LOWER_CHILD(2),
		C_UPPER_AND_G_UPPER_CHILD(3);
		
		private int childIndex;
		private ChildPatchIndexes(int child_index){
			childIndex = child_index;
		}
		public int getChildIndex(){
			return childIndex;
		}
	}

	
	/**
	 * Creates an empty {@link MeasurementPatch} that has no {@link MeasurementPoint} inside : these must be created and set elsewhere.
	 * @param c_mid
	 * @param g_mid
	 */
	public MeasurementPatch(int c_mid, int g_mid){
		setcMid(c_mid);
		setgMid(g_mid);
		setcMidgMax(null);
		setcMidgMid(null);
		setcMidgMin(null);
		setcMingMid(null);
		setcMaxgMid(null);
		subPatches = new ArrayList<MeasurementPatch>();
	}

	/**
	 * 
	 * @return true is the patch has already been divided into sub patches.
	 */
	public boolean hasBeenSubdivided(){
		return subPatches.size() != 0;
	}

	public void subDivide(MeasurementPatch[] sub_patches){
		for (MeasurementPatch _patch : sub_patches){
			subPatches.add(_patch);
		}
	}

	public MeasurementPatch[] getSubPatches() throws RadiometryException {
		if (subPatches.size() == 0){
			throw new RadiometryException("trying to get subpatches of a non-subdivided patch");
		}
		return subPatches.toArray(new MeasurementPatch[1]);
	}

	/**
	 * 
	 * @return true if all the informations corresponding to the actual patch are non null and complete.
	 */
	public boolean isComplete(){
		return 	(getcMingMid() != null)&&
				getcMingMid().hasBeenYetMeasured()&&
				(getcMidgMin() != null)&&
				getcMidgMin().hasBeenYetMeasured()&&
				(getcMidgMid() != null)&&
				getcMidgMid().hasBeenYetMeasured()&&
				(getcMidgMax() != null)&&
				getcMidgMax().hasBeenYetMeasured()&&
				(getcMaxgMid() != null)&&
				getcMaxgMid().hasBeenYetMeasured();
	}
	/**
	 * 
	 * @param meas_point
	 * @return <i>true</i> if and only if the parameter corresponds to one of the {@link MeasurementPoint}s stored in this patch
	 */
	public boolean containsMeasurement(MeasurementPoint meas_point){
		return  (getcMingMid() == meas_point) ||
				(getcMidgMin() == meas_point) ||
				(getcMidgMid() == meas_point) ||
				(getcMidgMax() == meas_point) ||
				(getcMaxgMid() == meas_point);
	}

	/**
	 * Retrieves a {@link MeasurementPoint} if there exists on in a {@link MeasurementPatch} hierarchy
	 * @param current_patch	he patch that is tested
	 * @param current_c_bounds current patch bounds on the C axis
	 * @param current_g_bounds current patch bounds on the Gamma axis
	 * @param c_value	C value
	 * @param g_value	Gamma value
	 * @return the {@link MeasurementPoint} corresponding to (c_value, g_value) if there exists one in the patch or one of its children patches, or <i> null</i> otherwise.
	 */
	public static MeasurementPoint getExistingMeasurementPoint(MeasurementPatch current_patch, 
			IntegerBounds current_c_bounds, IntegerBounds current_g_bounds,
			int c_value, int g_value){
		if (c_value == current_patch.getcMid()){
			if (g_value == current_g_bounds.getLowerBound()){
				return current_patch.getcMidgMin();
			}
			if (g_value == current_g_bounds.getUpperBound()){
				return current_patch.getcMidgMax();
			}
			if (g_value == current_patch.getgMid()){
				return current_patch.getcMidgMid();
			}
		}
		if (g_value == current_patch.getgMid()){
			if (c_value == current_c_bounds.getLowerBound()){
				return current_patch.getcMingMid();
			}
			if (c_value == current_c_bounds.getUpperBound()){
				return current_patch.getcMaxgMid();
			}
		}

		try {
			for (MeasurementPatch _child_patch : current_patch.getSubPatches()){
				IntegerBounds _child_c_bounds = getChildPatchCBounds(current_patch, _child_patch, current_c_bounds);
				IntegerBounds _child_g_bounds = getChildPatchGammaBounds(current_patch, _child_patch, current_g_bounds);
				if (_child_c_bounds.boundsValue(c_value) && _child_g_bounds.boundsValue(g_value)){
					MeasurementPoint _meas_pt = getExistingMeasurementPoint(_child_patch, _child_c_bounds, _child_g_bounds, c_value, g_value);
					if (_meas_pt != null){
						return _meas_pt;
					}
				}
			}
		}catch (RadiometryException _e) {
			return null;
		}

		return null;
	}

	/**
	 * Computes the child bounds on the C space
	 * @param parent_patch
	 * @param child_patch
	 * @param parent_c_bounds X = min value, Y = max value
	 * @return
	 */
	public static IntegerBounds getChildPatchCBounds(MeasurementPatch parent_patch, MeasurementPatch child_patch, IntegerBounds parent_c_bounds){
		return getChildPatchBounds(parent_patch.getcMid(), child_patch.getcMid(), parent_c_bounds);
	}

	public static IntegerBounds getChildPatchGammaBounds(MeasurementPatch parent_patch, MeasurementPatch child_patch, IntegerBounds parent_g_bounds){
		return getChildPatchBounds(parent_patch.getgMid(), child_patch.getgMid(), parent_g_bounds);
	}

	private static IntegerBounds getChildPatchBounds(int parent_mid, int child_mid, IntegerBounds parent_bounds){
		if (child_mid < parent_mid){
			// this is a lower child of a subdivided parent
			//
			return new IntegerBounds(parent_bounds.getLowerBound(), parent_mid);
		}
		else{
			if (child_mid > parent_mid){
				// this is an upper child of a subdivided parent
				//
				return new IntegerBounds(parent_mid,  parent_bounds.getUpperBound());
			}
			else{
				// the parent has not been cut
				//
				return parent_bounds;
			}
		}
	}

	public final int getcMid() {
		return cMid;
	}
	public final void setcMid(int c_mid) {
		this.cMid = c_mid;
	}
	public final int getgMid() {
		return gMid;
	}
	public final void setgMid(int g_mid) {
		this.gMid = g_mid;
	}
	public final MeasurementPoint getcMingMid() {
		return cMingMid;
	}
	public final void setcMingMid(MeasurementPoint c_min_g_mid) {
		this.cMingMid = c_min_g_mid;
	}
	public final MeasurementPoint getcMidgMin() {
		return cMidgMin;
	}
	public final void setcMidgMin(MeasurementPoint c_mid_g_min) {
		this.cMidgMin = c_mid_g_min;
	}
	public final MeasurementPoint getcMidgMid() {
		return cMidgMid;
	}
	public final void setcMidgMid(MeasurementPoint c_mid_g_mid) {
		this.cMidgMid = c_mid_g_mid;
	}
	public final MeasurementPoint getcMidgMax() {
		return cMidgMax;
	}
	public final void setcMidgMax(MeasurementPoint c_mid_g_max) {
		this.cMidgMax = c_mid_g_max;
	}
	public final MeasurementPoint getcMaxgMid() {
		return cMaxgMid;
	}
	public final void setcMaxgMid(MeasurementPoint c_max_g_mid) {
		this.cMaxgMid = c_max_g_mid;
	}
	
	
	/**
	 * Computes the four surrounding measurement point of a patch.
	 * @param current_patch The patch whose surrounding measurements must be computed.
	 * @param parent_patch  The parent patch.
	 * @param c_min_g_min_point A parent patch surrounding patch.
	 * @param c_min_g_max_point A parent patch surrounding patch.
	 * @param c_max_g_min_point A parent patch surrounding patch.
	 * @param c_max_g_max_point A parent patch surrounding patch.
	 * @return surrounding measurement points of the current_patch parameter indexed by {@link MeasurementPatch.BoundingIndexes}
	 * @throws RadiometryException 
	 */
	public static MeasurementPoint[] getSurroundingMeasurements(MeasurementPatch current_patch, MeasurementPatch parent_patch, 
			MeasurementPoint c_min_g_min_point,
			MeasurementPoint c_min_g_max_point, 
			MeasurementPoint c_max_g_min_point,
			MeasurementPoint c_max_g_max_point) throws RadiometryException{
		MeasurementPoint[] _res = new MeasurementPoint[BoundingIndexes.INDEX_COUNT];
		if (current_patch.getcMid() < parent_patch.getcMid()){
			// left child of a C cut parent
			//
			if (current_patch.getgMid() < parent_patch.getgMid()){
				// lower child of a G cut parent
				//
				_res[BoundingIndexes.C_MIN_G_MIN.getIndex()] = c_min_g_min_point;
				_res[BoundingIndexes.C_MIN_G_MAX.getIndex()] = current_patch.getcMingMid();
				_res[BoundingIndexes.C_MAX_G_MIN.getIndex()] = current_patch.getcMidgMin();
				_res[BoundingIndexes.C_MAX_G_MAX.getIndex()] = current_patch.getcMidgMid();
			}
			else{
				if (current_patch.getgMid() > parent_patch.getgMid()){
					// upper child of a G cut parent
					//
					_res[BoundingIndexes.C_MIN_G_MIN.getIndex()] = current_patch.getcMingMid();
					_res[BoundingIndexes.C_MIN_G_MAX.getIndex()] = c_min_g_max_point;
					_res[BoundingIndexes.C_MAX_G_MIN.getIndex()] = current_patch.getcMidgMid();
					_res[BoundingIndexes.C_MAX_G_MAX.getIndex()] = current_patch.getcMidgMax();
				}
				else{
					// the parent has not been cut along G
					//
					_res[BoundingIndexes.C_MIN_G_MIN.getIndex()] = c_min_g_min_point;
					_res[BoundingIndexes.C_MIN_G_MAX.getIndex()] = c_min_g_max_point;
					_res[BoundingIndexes.C_MAX_G_MIN.getIndex()] = current_patch.getcMidgMin();
					_res[BoundingIndexes.C_MAX_G_MAX.getIndex()] = current_patch.getcMidgMax();
				}
			}
		}
		else{
			if (current_patch.getcMid() > parent_patch.getcMid()){
				// right child of a C cut parent
				//
				if (current_patch.getgMid() < parent_patch.getgMid()){
					// lower child of a G cut parent
					//
					_res[BoundingIndexes.C_MIN_G_MIN.getIndex()] = current_patch.getcMidgMin();
					_res[BoundingIndexes.C_MIN_G_MAX.getIndex()] = current_patch.getcMidgMid();
					_res[BoundingIndexes.C_MAX_G_MIN.getIndex()] = c_max_g_min_point;
					_res[BoundingIndexes.C_MAX_G_MAX.getIndex()] = current_patch.getcMaxgMid();
				}
				else{
					if (current_patch.getgMid() > parent_patch.getgMid()){
						// upper child of a G cut parent
						//
						_res[BoundingIndexes.C_MIN_G_MIN.getIndex()] = current_patch.getcMidgMid();
						_res[BoundingIndexes.C_MIN_G_MAX.getIndex()] = current_patch.getcMidgMax();
						_res[BoundingIndexes.C_MAX_G_MIN.getIndex()] = current_patch.getcMaxgMid();
						_res[BoundingIndexes.C_MAX_G_MAX.getIndex()] = c_max_g_max_point;
					}
					else{
						// the parent has not been cut along G
						//
						_res[BoundingIndexes.C_MIN_G_MIN.getIndex()] = current_patch.getcMidgMin();
						_res[BoundingIndexes.C_MIN_G_MAX.getIndex()] = current_patch.getcMidgMax();
						_res[BoundingIndexes.C_MAX_G_MIN.getIndex()] = c_max_g_min_point;
						_res[BoundingIndexes.C_MAX_G_MAX.getIndex()] = c_max_g_max_point;
					}
				}
			}
			else{
				// the parent has not been cut along C
				//
				if (current_patch.getgMid() < parent_patch.getgMid()){
					// lower child of a G cut parent
					//
					_res[BoundingIndexes.C_MIN_G_MIN.getIndex()] = c_min_g_min_point;
					_res[BoundingIndexes.C_MIN_G_MAX.getIndex()] = current_patch.getcMingMid();
					_res[BoundingIndexes.C_MAX_G_MIN.getIndex()] = c_max_g_min_point;
					_res[BoundingIndexes.C_MAX_G_MAX.getIndex()] = current_patch.getcMaxgMid();
				}
				else{
					if (current_patch.getgMid() > parent_patch.getgMid()){
						// upper child of a G cut parent
						//
						_res[BoundingIndexes.C_MIN_G_MIN.getIndex()] = current_patch.getcMingMid();
						_res[BoundingIndexes.C_MIN_G_MAX.getIndex()] = c_min_g_max_point;
						_res[BoundingIndexes.C_MAX_G_MIN.getIndex()] = current_patch.getcMaxgMid();
						_res[BoundingIndexes.C_MAX_G_MAX.getIndex()]= c_max_g_max_point;
					}
					else{
						// the parent has not been cut along G
						// nor along C : this is an error case
						//
						throw new RadiometryException("Trying to get a child patch surroundings of an uncut patch !");
					}
				}
			}

		}
		return _res;
	}
}
