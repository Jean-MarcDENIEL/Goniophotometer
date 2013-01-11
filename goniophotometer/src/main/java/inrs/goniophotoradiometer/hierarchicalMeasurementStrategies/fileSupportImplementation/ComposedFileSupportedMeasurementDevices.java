package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.fileSupportImplementation;

import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.HierarchicalMeasurementStrategy.PatchSubdivision;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.IntegerBounds;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPatch;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import c4sci.math.geometry.plane.PlaneVector;
/**
 * This class is intended at encapsulating (through composition) several {@link FileSupportedMeasurementDevice}s.
 * @author jeanmarc.deniel
 *
 */
public class ComposedFileSupportedMeasurementDevices implements FileSupportedMeasurementDevice {

	private List<FileSupportedMeasurementDevice> measurementDevices;

	public ComposedFileSupportedMeasurementDevices(){
		measurementDevices = new ArrayList<FileSupportedMeasurementDevice>();
	}

	public void addMeasurementDevice(FileSupportedMeasurementDevice measurement_device){
		measurementDevices.add(measurement_device);
	}

	public boolean shouldCut(MeasurementPatch patch_,
			MeasurementPoint c_min_g_min_point,
			MeasurementPoint c_min_g_max_point,
			MeasurementPoint c_max_g_min_point,
			MeasurementPoint c_max_g_max_point) throws RadiometryException {
		try{
			ComposedMeasurementPoint _comp_c_min_gmin 	= (ComposedMeasurementPoint) c_min_g_min_point;
			ComposedMeasurementPoint _comp_c_min_gmax 	= (ComposedMeasurementPoint) c_min_g_max_point;
			ComposedMeasurementPoint _comp_c_max_gmin 	= (ComposedMeasurementPoint) c_max_g_min_point;
			ComposedMeasurementPoint _comp_c_max_gmax 	= (ComposedMeasurementPoint) c_max_g_max_point;
			ComposedMeasurementPatch _comp_patch		= (ComposedMeasurementPatch) patch_;
			for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				MeasurementPoint _cmin_g_min = _comp_c_min_gmin.getSubDeviceMeasurementPoint(_sub_device);
				MeasurementPoint _cmin_g_max = _comp_c_min_gmax.getSubDeviceMeasurementPoint(_sub_device);
				MeasurementPoint _cmax_g_min = _comp_c_max_gmin.getSubDeviceMeasurementPoint(_sub_device);
				MeasurementPoint _cmax_g_max = _comp_c_max_gmax.getSubDeviceMeasurementPoint(_sub_device);
				MeasurementPatch _subcomp_patch 	 = _comp_patch.getSubDeviceMeasurementPatch(_sub_device); 

				if (_it.next().shouldCut(_subcomp_patch, _cmin_g_min, _cmin_g_max, _cmax_g_min, _cmax_g_max)){
					return true;
				}
			}
			return false;
		}
		catch(ClassCastException _e){
			throw new RadiometryException("Bad Measurement point type", _e);
		}
	}

	public MeasurementPoint createMeasurementPoint(PlaneVector meas_point) {
		return new ComposedMeasurementPoint(meas_point, measurementDevices);
	}
	public MeasurementPatch createMeasurementPatch(int c_mid, int g_mid) {
		return new ComposedMeasurementPatch(c_mid, g_mid, measurementDevices);
	}

	/**
	 * In a {@link ComposedFileSupportedMeasurementDevices}, all {@link FileSupportedMeasurementDevice}s are tested through their own<br>
	 *  {@link FileSupportedMeasurementDevice#computeSubdivisionWay(MeasurementPatch, IntegerBounds, IntegerBounds)} method.
	 *  @return The maximum possible subdivision combination between all sub {@link FileSupportedMeasurementDevice}s results. 
	 */
	public PatchSubdivision computeSubdivisionWay(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		try{
			ComposedMeasurementPatch _comp_patch		= (ComposedMeasurementPatch) patch_to_subdivide;
			boolean _c_subdivide = false;
			boolean _g_subdivide = false;
			for (	Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); (
					_it.hasNext())&&((!_c_subdivide)||(!_g_subdivide));){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				MeasurementPatch _sub_patch = _comp_patch.getSubDeviceMeasurementPatch(_sub_device);
				PatchSubdivision _subdivision = _sub_device.computeSubdivisionWay(_sub_patch, patch_c_bounds, patch_g_bounds);
				switch(_subdivision){
				case NO_SUBDIVISION:
					break;
				case ON_C_ONLY:
					_c_subdivide = true;
					break;
				case ON_GAMMA_ONLY:
					_g_subdivide = true;
					break;
				case ON_C_AND_GAMMA:
					_c_subdivide = _g_subdivide = true;
					break;
				default:
					throw new RadiometryException("Unexpected subdivision");	
				}
			}
			if (_c_subdivide){
				if (_g_subdivide){
					return PatchSubdivision.ON_C_AND_GAMMA;
				}
				else{
					return PatchSubdivision.ON_C_ONLY;
				}
			}
			else{
				if (_g_subdivide){
					return PatchSubdivision.ON_GAMMA_ONLY;
				}
				else{
					return PatchSubdivision.NO_SUBDIVISION;
				}
			}
		}
		catch(ClassCastException _e){
			throw new RadiometryException("Bad Measurement point type", _e);
		}
	}

	/**
	 * @return The mean C value among all the composing {@link FileSupportedMeasurementDevice#computeSubpatchesCMiddleValues(MeasurementPatch, IntegerBounds, IntegerBounds)} method calls.
	 */
	public IntegerBounds computeSubpatchesCMiddleValues(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		int _sub_count = measurementDevices.size();
		if ( _sub_count == 0){
			throw new RadiometryException("no sub device");
		}
		IntegerBounds _res = new IntegerBounds(0, 0);
		try{
			ComposedMeasurementPatch _comp_patch = (ComposedMeasurementPatch) patch_to_subdivide;
			for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				MeasurementPatch _sub_patch = _comp_patch.getSubDeviceMeasurementPatch(_sub_device);
				IntegerBounds _sub_bounds = _sub_device.computeSubpatchesCMiddleValues(_sub_patch, patch_c_bounds, patch_g_bounds);
				_res.setLowerBound(_res.getLowerBound() + _sub_bounds.getLowerBound());
				_res.setUpperBound(_res.getUpperBound() + _sub_bounds.getUpperBound());
			}
			_res.setLowerBound(_res.getLowerBound()/_sub_count);
			_res.setUpperBound(_res.getUpperBound()/_sub_count);
		}
		catch(ClassCastException _e){
			throw new RadiometryException("bad MeasurementPatch type", _e);
		}
		return _res;
	}

	/**
	 * @return the mean value for all sub devices
	 */
	public IntegerBounds computeSubpatchesGammaMiddleValues(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		int _sub_count = measurementDevices.size();
		if ( _sub_count == 0){
			throw new RadiometryException("no sub device");
		}
		IntegerBounds _res = new IntegerBounds(0, 0);
		try{
			ComposedMeasurementPatch _comp_patch = (ComposedMeasurementPatch) patch_to_subdivide;
			for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				MeasurementPatch _sub_patch = _comp_patch.getSubDeviceMeasurementPatch(_sub_device);
				IntegerBounds _sub_bounds = _sub_device.computeSubpatchesGammaMiddleValues(_sub_patch, patch_c_bounds, patch_g_bounds);
				_res.setLowerBound(_res.getLowerBound() + _sub_bounds.getLowerBound());
				_res.setUpperBound(_res.getUpperBound() + _sub_bounds.getUpperBound());
			}
			_res.setLowerBound(_res.getLowerBound()/_sub_count);
			_res.setUpperBound(_res.getUpperBound()/_sub_count);
		}
		catch(ClassCastException _e){
			throw new RadiometryException("bad MeasurementPatch type", _e);
		}
		return _res;
	}
	/**
	 * @return The concatenation of all part names of all sub devices.
	 */
	public String[] getMeasurementPartsNames() throws RadiometryException {
		ArrayList<String> _res = new ArrayList<String>();
		
		for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
			FileSupportedMeasurementDevice _sub_device = _it.next();
			String[] _sub_parts_names_tab = _sub_device.getMeasurementPartsNames();
			for (String _sub_part_name : _sub_parts_names_tab){
				_res.add(_sub_part_name);
			}
		}
		return _res.toArray(new String[0]);
	}
	/**
	 * Begins the loading session for all sub devices.
	 */
	public void beginLoadingSession() throws RadiometryException {
		for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
			_it.next().beginLoadingSession();
		}
	}
	/**
	 * Search among the sub devices which has a corresponding part then make it load.
	 */
	public void loadMeasurementPart(MeasurementPoint meas_point,
			String part_name, InputStream part_stream)
					throws RadiometryException {
		try{
			ComposedMeasurementPoint _comp_point = (ComposedMeasurementPoint) meas_point;
			for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				String[] _sub_names = _sub_device.getMeasurementPartsNames();
				if (isAmong(part_name, _sub_names)){
					_sub_device.loadMeasurementPart(_comp_point.getSubDeviceMeasurementPoint(_sub_device), part_name, part_stream);
					return;
				}
			}
		}
		catch (ClassCastException _e){
			throw new RadiometryException("bad MeasurementPoint type", _e);
		}
	}
	/**
	 * For all the sub devices, call their {@link #endLoadingSession()} method
	 */
	public void endLoadingSession() throws RadiometryException {
		for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
			_it.next().endLoadingSession();
		}
	}

	/**
	 * For all the sub devices, call their {@link #beginSavingSession()} method.
	 */
	public void beginSavingSession() throws RadiometryException {
		for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
			_it.next().beginSavingSession();
		}
	}

	/**
	 * Find the sub device corresponding to the part_name parameter, then call its {@link #saveMeasurementPart(MeasurementPoint, String, OutputStream)} method.
	 */
	public void saveMeasurementPart(MeasurementPoint meas_point,
			String part_name, OutputStream part_stream)
					throws RadiometryException {
		try{
			ComposedMeasurementPoint _comp_point = (ComposedMeasurementPoint) meas_point;
			for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				String[] _sub_names = _sub_device.getMeasurementPartsNames();
				if (isAmong(part_name, _sub_names)){
					_sub_device.saveMeasurementPart(_comp_point.getSubDeviceMeasurementPoint(_sub_device), part_name, part_stream);
					return;
				}
			}
		}
		catch (ClassCastException _e){
			throw new RadiometryException("bad MeasurementPoint type", _e);
		}
	}
	/**
	 * For all sub devices, call their {@link #endSavingSession()} method.
	 */
	public void endSavingSession() throws RadiometryException {
		for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
			_it.next().endSavingSession();
		}
	}
	/**
	 * for all the sub devices call their {@link #performCompleteMeasurement(MeasurementPoint)} method.
	 */
	public void performCompleteMeasurement(MeasurementPoint meas_point)
			throws RadiometryException {
		try{
			ComposedMeasurementPoint _comp_point = (ComposedMeasurementPoint) meas_point;
			for (Iterator<FileSupportedMeasurementDevice> _it = measurementDevices.iterator(); _it.hasNext();){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				_sub_device.performCompleteMeasurement(_comp_point.getSubDeviceMeasurementPoint(_sub_device));
			}
		}
		catch (ClassCastException _e){
			throw new RadiometryException("bad MeasurementPoint type", _e);
		}
	}

	public class ComposedMeasurementPoint extends MeasurementPoint{

		private Map<FileSupportedMeasurementDevice, MeasurementPoint> measurementPoints;

		public ComposedMeasurementPoint(PlaneVector meas_point, List<FileSupportedMeasurementDevice> sub_device_list) {
			super(meas_point);
			measurementPoints = new ConcurrentHashMap<FileSupportedMeasurementDevice, MeasurementPoint>();
			for (Iterator<FileSupportedMeasurementDevice> _it = sub_device_list.iterator(); _it.hasNext();){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				measurementPoints.put(_sub_device, _sub_device.createMeasurementPoint(meas_point));
			}
		}
		public MeasurementPoint getSubDeviceMeasurementPoint(FileSupportedMeasurementDevice sub_device){
			return measurementPoints.get(sub_device);
		}
	}
	
	public class ComposedMeasurementPatch extends MeasurementPatch{
		private Map<FileSupportedMeasurementDevice, MeasurementPatch> measurementPatches;
		public ComposedMeasurementPatch(int c_mid, int g_mid, List<FileSupportedMeasurementDevice> sub_device_list){
			super(c_mid, g_mid);
			measurementPatches = new ConcurrentHashMap<FileSupportedMeasurementDevice, MeasurementPatch>();
			for (Iterator<FileSupportedMeasurementDevice> _it = sub_device_list.iterator(); _it.hasNext();){
				FileSupportedMeasurementDevice _sub_device = _it.next();
				measurementPatches.put(_sub_device, _sub_device.createMeasurementPatch(c_mid, g_mid));
			}
		}
		public MeasurementPatch getSubDeviceMeasurementPatch(FileSupportedMeasurementDevice sub_device){
			return measurementPatches.get(sub_device);
		}
	}

	private static boolean isAmong(String tested_elt, String[] str_tab){
		for (String _str : str_tab){
			if (_str.compareTo(tested_elt) == 0){
				return true;
			}
		}
		return false;
	}

}
