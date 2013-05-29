package inrs.goniophotoradiometer.measurementImplementations;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import c4sci.math.geometry.plane.PlaneVector;
import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.HierarchicalMeasurementStrategy.PatchSubdivision;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.IntegerBounds;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPatch;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.fileSupportImplementation.FileSupportedMeasurementDevice;
import inrs.goniophotoradiometer.spectralIrradianceCapture.SpectralIrradiance;
import inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation.AvantesSpectorRadiometer;
import inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation.IrradianceType;

public class RadiospectrometerHead implements FileSupportedMeasurementDevice {

	private static final String 	PART_NAME = "spectralirradiance.xml";
	
	public class RadiospectrometryMeasurementPoint extends MeasurementPoint{

		private SpectralIrradiance irradianceValue;
		
		public RadiospectrometryMeasurementPoint(PlaneVector meas_point) throws RadiometryException {
			super(meas_point);
			irradianceValue = new SpectralIrradiance(captureDevice.getWavelengthCount());
		}

		public SpectralIrradiance getIrradianceValue() {
			return irradianceValue;
		}

		public void setIrradianceValue(SpectralIrradiance irradiance_value) {
			this.irradianceValue = irradiance_value;
		}
		
	}
	
	
	private  AvantesSpectorRadiometer captureDevice;
	/**
	 *
	 * @param device_name Device handle
	 * @param min_integration_time_millisec Minimum acceptable integration time (in milliseconds)
	 * @param max_integration_time_millisec Maximum acceptable integration time (in milliseconds)
	 * @param meas_time_millisec Measurement time (in milliseconds) used to define the number of measurements to be averaged.
	 * @throws RadiometryException 
	 */
	public RadiospectrometerHead(String device_name, float min_integration_time_millisec, float max_integration_time_millisec, float meas_time_millisec) throws RadiometryException{
		captureDevice = new AvantesSpectorRadiometer(device_name, min_integration_time_millisec, max_integration_time_millisec, meas_time_millisec);
	}
	
	public boolean shouldCut(MeasurementPatch patch_,
			MeasurementPoint c_min_g_min_point,
			MeasurementPoint c_min_g_max_point,
			MeasurementPoint c_max_g_min_point,
			MeasurementPoint c_max_g_max_point) throws RadiometryException {
		// nothing to decide for the moment
		return false;
	}

	public MeasurementPoint createMeasurementPoint(PlaneVector meas_point) throws RadiometryException {
		return new RadiospectrometryMeasurementPoint(meas_point);
	}

	public MeasurementPatch createMeasurementPatch(int c_mid, int g_mid) {
		return new MeasurementPatch(c_mid, g_mid);
	}

	public PatchSubdivision computeSubdivisionWay(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		// for the moment no heuristic
		return null;
	}

	public IntegerBounds computeSubpatchesCMiddleValues(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		// for the moment no heuristic
		return null;
	}

	public IntegerBounds computeSubpatchesGammaMiddleValues(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		// for the moment no heuristic
		return null;
	}

	public String[] getMeasurementPartsNames() throws RadiometryException {
		return new String[] {PART_NAME};
	}

	public void beginLoadingSession() throws RadiometryException {
		// nothing to do
	}

	public void loadMeasurementPart(MeasurementPoint meas_point,
			String part_name, InputStream part_stream)
			throws RadiometryException {
		if (!part_name.startsWith(PART_NAME)){
			throw new RadiometryException("Bad part name : "+ part_name + " instead of beginning with " + PART_NAME);
		}
		if (RadiospectrometryMeasurementPoint.class.isInstance(meas_point)){
			try {		
				BufferedReader _in = new BufferedReader(new InputStreamReader(part_stream));
				int _wavelength_count = Integer.parseInt(_in.readLine());
				SpectralIrradiance _spc = ((RadiospectrometryMeasurementPoint) meas_point).getIrradianceValue();
				if (_spc.getWavelengthNumber() != _wavelength_count){
					_spc = new SpectralIrradiance(_wavelength_count);
					((RadiospectrometryMeasurementPoint) meas_point).setIrradianceValue(_spc);
				}
				
				_spc.setAveragesCount(Integer.parseInt(_in.readLine()));
				_spc.setIntegrationTimeMs(Float.parseFloat(_in.readLine()));
				
				double[] 	_irradiance_tab = _spc.accessIrradianceData();
				boolean[] 	_saturation_tab = _spc.accessSaturationData();
				float[] 	_wavelength_tab = _spc.accessWavelengthData();
				for (int _lgn=0; _lgn<_wavelength_count; _lgn++){
					String _line = _in.readLine();
					int _first_whitespace_index = _line.indexOf(" ");
					int _second_whitespace_index = _line.lastIndexOf(" ");
					
					if ((_first_whitespace_index == -1) || (_second_whitespace_index == -1)){
						throw new RadiometryException("Malformed line : " + _line);
					}
					
					_wavelength_tab[_lgn] = Float.parseFloat(_line.substring(0, _first_whitespace_index));
					_irradiance_tab[_lgn] = Double.parseDouble(_line.substring(_first_whitespace_index, _second_whitespace_index));
					_saturation_tab[_lgn] = Boolean.parseBoolean(_line.substring(_second_whitespace_index));
					
				}
			} catch (NumberFormatException _e) {
				throw new RadiometryException("Error in format number", _e);
			} catch (IOException _e) {
				throw new RadiometryException("Error during input operations", _e);
			}

		}
		else{
			throw new RadiometryException("Bad MeasurementPoint subtype");
		}
		
	}

	public void endLoadingSession() throws RadiometryException {
		// nothing to do
	}

	public void beginSavingSession() throws RadiometryException {
		// nothing to do
	}

	public void saveMeasurementPart(MeasurementPoint meas_point,
			String part_name, OutputStream part_stream)
			throws RadiometryException {
		// TODO
		
		if (!part_name.startsWith(PART_NAME)){
			throw new RadiometryException("Bad part name : "+ part_name + " instead of beginning with " + PART_NAME);
		}
		if (RadiospectrometryMeasurementPoint.class.isInstance(meas_point)){
			
			BufferedWriter _out = new BufferedWriter(new OutputStreamWriter(part_stream));
			SpectralIrradiance _spc = ((RadiospectrometryMeasurementPoint) meas_point).getIrradianceValue();
			try {
				_out.write("" + _spc.getWavelengthNumber()	+"\n");
				_out.write("" + _spc.getAveragesCount() 	+"\n");
				_out.write("" + _spc.getIntegrationTimeMs() +"\n");
				
				float[] 	_wavelength_tab = _spc.accessWavelengthData();
				double[]	_irradiance_tab = _spc.accessIrradianceData();
				boolean[]	_saturation_tab = _spc.accessSaturationData();
				
				for (int _lgn=0; _lgn < _spc.getWavelengthNumber(); _lgn++){
					_out.write(""+_wavelength_tab[_lgn] + " " +
							_irradiance_tab[_lgn] + " " +
							_saturation_tab[_lgn] + "\n");
				}
				
			} catch (IOException _e) {
				throw new RadiometryException("Error during output operations", _e);
			}
		}
		else{
			throw new RadiometryException("Bad MeasurementPoint subtype");
		}		
	}

	public void endSavingSession() throws RadiometryException {
		// nothing to do
	}

	public void performCompleteMeasurement(MeasurementPoint meas_point)
			throws RadiometryException {
		if (RadiospectrometryMeasurementPoint.class.isInstance(meas_point)){
			((RadiospectrometryMeasurementPoint)meas_point).setIrradianceValue(captureDevice.captureIrradiance());
		}
		else{
			throw new RadiometryException("Bad MeasurementPoint subtype");
		}

	}

}
