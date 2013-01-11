package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies;

import c4sci.math.geometry.plane.PlaneVector;
import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.HierarchicalMeasurementStrategy.PatchSubdivision;

/**
 * A {@link HierarchicalStrategy} is designed to 
 * <ol>
 * <li> create and measure {@link MeasurementPoint}s</li>
 * <li> decide on {@link MeasurementPatch}s' subdivision. </li>
 * </ol>
 * <b>Warning: </b> All methods accepting {@link MeasurementPoint}s must be fed with {@link MeasurementPoint}s created through this' {@link #createMeasurementPoint(PlaneVector)} method.<br>
 * Otherwise a {@link RadiometryException} will be raised.<br>
 * In the same way, {@link MeasurementPatch}s must be composed with such {@link MeasurementPoint}s otherwise a {@link RadiometryException} will be raised.
 * <br>
 * @author jeanmarc.deniel
 *
 */
public interface HierarchicalStrategy {
	/**
	 * Indicates whereas a patch should be cut in sub_patches.<br>
	 * Neither children nor parent patches will be explored.
	 * @param patch_ The patch that must be fully completed (not including sub patches).
	 * @return
	 */
	boolean shouldCut(MeasurementPatch patch_, MeasurementPoint c_min_g_min_point, MeasurementPoint c_min_g_max_point, MeasurementPoint c_max_g_min_point, MeasurementPoint c_max_g_max_point) throws RadiometryException;

	/**
	 * Creates a measurement point.<br>
	 * <b>Pattern</b> This method implements the <b>factory method</b> pattern.
	 * @param meas_point
	 * @return
	 */
	MeasurementPoint createMeasurementPoint(PlaneVector meas_point);
	
	MeasurementPatch createMeasurementPatch(int c_mid, int g_mid);

	/**
	 * Computes how to subdivide a patch in two or four sub patches.
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return
	 * @throws RadiometryException
	 */
	PatchSubdivision computeSubdivisionWay(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;

	/**
	 * Computes the C middle values of sub patches
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return Lower = lower child C middle value, Upper = upper child C middle value
	 * @throws RadiometryException
	 */
	IntegerBounds computeSubpatchesCMiddleValues(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;
	
	/**
	 * Computes the G middle values of sub patches
	 * @param patch_to_subdivide
	 * @param patch_c_bounds
	 * @param patch_g_bounds
	 * @return
	 * @throws RadiometryException
	 */
	IntegerBounds computeSubpatchesGammaMiddleValues(MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds, IntegerBounds patch_g_bounds) throws RadiometryException;

}
