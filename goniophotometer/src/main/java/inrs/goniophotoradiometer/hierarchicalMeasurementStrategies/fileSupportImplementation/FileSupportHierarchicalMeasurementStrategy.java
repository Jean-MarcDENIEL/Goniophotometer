package inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.fileSupportImplementation;

import java.io.File;
import c4sci.io.SecuredFile;
import c4sci.io.UncoherentStateFileException;
import c4sci.math.geometry.plane.PlaneVector;

import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.HierarchicalMeasurementStrategy;
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
public abstract class FileSupportHierarchicalMeasurementStrategy extends HierarchicalMeasurementStrategy {
	
	private static final String				SEPARATOR = "_"; 
	
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

}
