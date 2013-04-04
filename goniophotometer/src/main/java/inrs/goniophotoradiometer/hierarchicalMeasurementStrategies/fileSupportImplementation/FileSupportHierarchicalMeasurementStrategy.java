package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.fileSupportImplementation;

import java.io.File;
import c4sci.io.SecuredFile;
import c4sci.io.UncoherentStateFileException;
import c4sci.math.algebra.Floatings;
import c4sci.math.geometry.plane.PlaneVector;

import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.HierarchicalMeasurementStrategy;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.IntegerBounds;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPatch;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;

/**
 * This class is intended at storing the measurement data in different files, so that
 * a measurement can be retrieved from disk even it has been made in a previous session.<br>
 * <br>
 * A {@link MeasurementPoint} is supposed to be loaded and saved in several files, each one corresponding to a "measurement part".<br>
 * The procedure in the following :
 * <ol>
 * <li>opening the save/load session,</li>
 * <li>loading/saving each measurement part,</li>
 * <li>closing the save/load session</li>
 * </ol>
 * @author jeanmarc.deniel
 *
 */
public  class FileSupportHierarchicalMeasurementStrategy extends HierarchicalMeasurementStrategy {
	
	private static final String				SEPARATOR = "_"; 
	private static final float 				MIN_CUTTABLE_RANGE_WIDTH = 8.0f;
	private static final int				MIN_SEPARATE_RANGE_WIDTH = 1;
	
	private String 							directoryAbsolutePath;
	private FileSupportedMeasurementDevice	measurementDevice; 

	public FileSupportHierarchicalMeasurementStrategy(int max_c_delta, int max_g_delta, String directory_absolute_path, FileSupportedMeasurementDevice meas_device) {
		super(max_c_delta, max_g_delta);
		directoryAbsolutePath = directory_absolute_path + File.pathSeparator;
		measurementDevice = meas_device;
	}

	public 	void completeMeasurementPoint(MeasurementPoint meas_point)
			throws RadiometryException {
		if (meas_point.hasBeenYetMeasured()){
			throw new RadiometryException("should not measure the same measurement point twice.");
		}
		// first scan whereas the data have already been saved
		//
		boolean _all_parts_in_files = true;
		String[] _parts_names = measurementDevice.getMeasurementPartsNames();
		for (String _part_name : _parts_names){
			SecuredFile _secured = new SecuredFile(getPartFileName(_part_name, meas_point));
			try {
				if (!_secured.existsInCoherentState()){
					_all_parts_in_files = false;
					break;
				}
			}
			catch (UncoherentStateFileException _e) {
				throw new RadiometryException("error verifying " + _part_name + " measurement part existance.", _e);
			}
		}
		if (_all_parts_in_files){
			measurementDevice.beginLoadingSession();
			for (String _part_name : _parts_names){
				try{
					SecuredFile _secured = new SecuredFile(getPartFileName(_part_name, meas_point));
					measurementDevice.loadMeasurementPart(meas_point, _part_name, _secured.readFile());
					_secured.closeFile();
				}
				catch (UncoherentStateFileException _e){
					throw new RadiometryException("error loading " + _part_name + " measurement part.", _e);
				}
			}
			measurementDevice.endLoadingSession();
		}
		else{
			measurementDevice.performCompleteMeasurement(meas_point);
			measurementDevice.beginSavingSession();
			for (String _part_name : _parts_names){
				try{
					SecuredFile _secured = new SecuredFile(getPartFileName(_part_name, meas_point));
					measurementDevice.saveMeasurementPart(meas_point, _part_name, _secured.createNewFile());
					_secured.closeFile();
				}
				catch(UncoherentStateFileException _e){
					throw new RadiometryException("error savign newly measured " + _part_name + " measurement part.", _e);
				}
			}
			measurementDevice.endSavingSession();
		}
		meas_point.setAsMeasured();
	}
	
	private String getPartFileName(String part_name, MeasurementPoint meas_point){
		int _c_val = (int) meas_point.getMeasurementPosition().getX();
		int _g_val = (int) meas_point.getMeasurementPosition().getY();
		return directoryAbsolutePath + _c_val + SEPARATOR + _g_val + SEPARATOR + part_name;
	}


	public MeasurementPoint createMeasurementPoint(PlaneVector meas_point) {
		return measurementDevice.createMeasurementPoint(meas_point);
	}

	public boolean shouldCut(MeasurementPatch patch_,
			MeasurementPoint c_min_g_min_point,
			MeasurementPoint c_min_g_max_point,
			MeasurementPoint c_max_g_min_point,
			MeasurementPoint c_max_g_max_point) throws RadiometryException {
		PlaneVector _c_g_width = c_max_g_max_point.getMeasurementPosition().opMinus(c_min_g_min_point.getMeasurementPosition());
		
		boolean _cutable_on_c = Floatings.isGreaterEqual(_c_g_width.getX(), MIN_CUTTABLE_RANGE_WIDTH);
		boolean _cutable_on_g = Floatings.isGreaterEqual(_c_g_width.getY(), MIN_CUTTABLE_RANGE_WIDTH);
		if ((!_cutable_on_c) && (!_cutable_on_g)){
			return false;
		}
		return measurementDevice.shouldCut(patch_, c_min_g_min_point, c_min_g_max_point, c_max_g_min_point, c_max_g_max_point);
	}

	public PatchSubdivision computeSubdivisionWay(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		
		boolean _cutable_on_c = Floatings.isGreaterEqual(patch_c_bounds.getWidth(), MIN_CUTTABLE_RANGE_WIDTH);
		boolean _cutable_on_g = Floatings.isGreaterEqual(patch_g_bounds.getWidth(), MIN_CUTTABLE_RANGE_WIDTH);
		
		PatchSubdivision _res = measurementDevice.computeSubdivisionWay(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
		switch(_res){
		case NO_SUBDIVISION :
			return _res;
		case ON_C_ONLY:
			if (_cutable_on_c){
				return _res;
			}
			break;
		case ON_GAMMA_ONLY:
			if (_cutable_on_g){
				return _res;
			}
			break;
		case ON_C_AND_GAMMA:
			if (_cutable_on_c && _cutable_on_g){
				return _res;
			}
			break;
		default:
			throw new RadiometryException("Unexpected patch subdivision from measurement device");	
		}
		// otherwise it is not possible to cut as demanded by the measurement device
		// in that case there is no cut
		//
		return PatchSubdivision.NO_SUBDIVISION;
	}

	public IntegerBounds computeSubpatchesCMiddleValues(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		IntegerBounds _res = measurementDevice.computeSubpatchesCMiddleValues(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
		// ensure that C values are separate under the integer form
		//
		int _c_mid = patch_to_subdivide.getcMid();
		int _c_min = patch_c_bounds.getLowerBound();
		int _c_max = patch_c_bounds.getUpperBound();
		int _lower_child_c_mid = _res.getLowerBound();
		int _upper_child_c_mid = _res.getUpperBound();
		
		_res.setLowerBound(ensureValueIsSeparateFromBoundValue(_lower_child_c_mid, _c_min, _c_mid));
		_res.setUpperBound(ensureValueIsSeparateFromBoundValue(_upper_child_c_mid, _c_mid, _c_max));
		
		return _res;
	}

	public IntegerBounds computeSubpatchesGammaMiddleValues(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		IntegerBounds _res = measurementDevice.computeSubpatchesGammaMiddleValues(patch_to_subdivide, patch_c_bounds, patch_g_bounds);
		// ensure that the G values are separate under the integer form
		//
		int _g_mid = patch_to_subdivide.getgMid();
		int _g_min = patch_g_bounds.getLowerBound();
		int _g_max = patch_g_bounds.getUpperBound();
		int _lower_child_g_mid = _res.getLowerBound();
		int _upper_child_g_mid = _res.getUpperBound();
		
		_res.setLowerBound(ensureValueIsSeparateFromBoundValue(_lower_child_g_mid, _g_min, _g_mid));
		_res.setUpperBound(ensureValueIsSeparateFromBoundValue(_upper_child_g_mid, _g_mid, _g_max));
		
		return _res;
	}
	
	private int ensureValueIsSeparateFromBoundValue(int child_value, int bounds_lower_value, int bounds_upper_value){
		boolean _separate = (child_value - bounds_lower_value > MIN_SEPARATE_RANGE_WIDTH) && (bounds_upper_value - child_value > MIN_SEPARATE_RANGE_WIDTH);
		if (_separate){
			return child_value;
		}
		else{
			return (bounds_lower_value + bounds_upper_value)/2;
		}
	}

	public MeasurementPatch createMeasurementPatch(int c_mid, int g_mid) {
		return measurementDevice.createMeasurementPatch(c_mid, g_mid);
	}

}
