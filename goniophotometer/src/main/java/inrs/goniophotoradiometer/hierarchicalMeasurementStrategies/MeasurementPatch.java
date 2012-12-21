package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;

import inrs.goniophotoradiometer.exceptions.RadiometryException;

import java.util.ArrayList;
import java.util.List;

import c4sci.math.geometry.plane.PlaneVector;

/**
 * This class represents a two dimensional bounded areas in the (C,gamma) space.
 * C and Gamma values are integers :
 * 
 * 
 * 
 *         Cmax /---------o--------/
 *             /         /        /
 *            /         /        /
 *      Cmid o---------o--------o
 *          /         /        /
 *         /         /        /
 *   Cmin /---------o--------/
 *     Gmin      Gmid     Gmax
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
	
	/*public void computeSubpatchBounds(MeasurementPatch parent_patch, MeasurementPatch child_patch, 
			IntegerBounds parent_c_bounds, IntegerBounds parent_g_bound,
			IntegerBounds child_c_bounds, IntegerBounds child_g_bounds){
		if (child_patch.getcMid() < parent_patch.getcMid()){
			// left child of a C cut patch
			//
			child_c_bounds.setLowerBound(parent_c_bounds.getLowerBound());
			child_c_bounds.setUpperBound(parent_patch.getcMid());
		}
		else{
			if (child_patch.getcMid() > parent_patch.getcMid()){
				// right child of a C cut patch
				//
				child_c_bounds.setLowerBound(parent_patch.getcMid());
				child_c_bounds.setUpperBound(parent_c_bounds.getUpperBound());
			}
			else{
				// parent patch has not been cut on C
				//
				child_c_bounds.setLowerBound(parent_c_bounds.getLowerBound());
				child_c_bounds.setUpperBound(parent_c_bounds.getUpperBound());
			}
		}

		if (child_patch.getgMid() < parent_patch.getgMid()){
			// left child of a G cut patch
			//
			child_g_bounds.setLowerBound(parent_g_bound.getLowerBound());
			child_g_bounds.setUpperBound(parent_patch.getgMid());
		}
		else{
			if (child_patch.getgMid() > parent_patch.getgMid()){
				// right child of a G cut patch
				//
				child_g_bounds.setLowerBound(parent_patch.getgMid());
				child_g_bounds.setUpperBound(parent_g_bound.getUpperBound());
			}
			else{
				// the parent patch has not been cut on G
				//
				child_g_bounds.setLowerBound(parent_g_bound.getLowerBound());
				child_g_bounds.setUpperBound(parent_g_bound.getUpperBound());
			}
		}
	}*/
	
	
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
		}catch (RadiometryException e) {
			return null;
		}

		return null;
	}
	
	/**
	 * Computes the child bounds on the C space
	 * @param parent_patch
	 * @param child_patch
	 * @param parent_C_bounds X = min value, Y = max value
	 * @return
	 */
	public static IntegerBounds getChildPatchCBounds(MeasurementPatch parent_patch, MeasurementPatch child_patch, IntegerBounds parent_C_bounds){
		return getChildPatchBounds(parent_patch.getcMid(), child_patch.getcMid(), parent_C_bounds);
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
	
	public int getcMid() {
		return cMid;
	}
	public void setcMid(int c_mid) {
		this.cMid = c_mid;
	}
	public int getgMid() {
		return gMid;
	}
	public void setgMid(int g_mid) {
		this.gMid = g_mid;
	}
	public MeasurementPoint getcMingMid() {
		return cMingMid;
	}
	public void setcMingMid(MeasurementPoint c_min_g_mid) {
		this.cMingMid = c_min_g_mid;
	}
	public MeasurementPoint getcMidgMin() {
		return cMidgMin;
	}
	public void setcMidgMin(MeasurementPoint c_mid_g_min) {
		this.cMidgMin = c_mid_g_min;
	}
	public MeasurementPoint getcMidgMid() {
		return cMidgMid;
	}
	public void setcMidgMid(MeasurementPoint c_mid_g_mid) {
		this.cMidgMid = c_mid_g_mid;
	}
	public MeasurementPoint getcMidgMax() {
		return cMidgMax;
	}
	public void setcMidgMax(MeasurementPoint c_mid_g_max) {
		this.cMidgMax = c_mid_g_max;
	}
	public MeasurementPoint getcMaxgMid() {
		return cMaxgMid;
	}
	public void setcMaxgMid(MeasurementPoint c_max_g_mid) {
		this.cMaxgMid = c_max_g_mid;
	}
}
