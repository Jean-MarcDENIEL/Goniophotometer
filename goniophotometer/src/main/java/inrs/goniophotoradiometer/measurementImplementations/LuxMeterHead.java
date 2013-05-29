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
import inrs.goniophotoradiometer.illuminanceCapture.LuxMeter;
import inrs.goniophotoradiometer.illuminanceCapture.LuxMeter.IlluminanceException;
import inrs.goniophotoradiometer.illuminanceCapture.ahlbornImplementation.AhlbornLuxMeter;
/**
 * This class encapsulates an {@link AhlbornLuxMeter}.
 * @author jeanmarc.deniel
 *
 */
public class LuxMeterHead implements FileSupportedMeasurementDevice {

	private static final String	PART_NAME = "LuxMeter.txt";
	
	private LuxMeter 	captureDevice;
	
	public class LuxMeterMeasurementPoint extends MeasurementPoint{

		private 	float illuminanceValue;
		
		public LuxMeterMeasurementPoint(PlaneVector meas_point) {
			super(meas_point);
			// TODO Auto-generated constructor stub
		}

		public float getIlluminanceValue() {
			return illuminanceValue;
		}

		public void setIlluminanceValue(float illuminance_value) {
			this.illuminanceValue = illuminance_value;
		}
		
	}
	
	public LuxMeterHead(String serial_port_name, String sensitive_measurement_channel, String insensitive_measurement_channel){
		captureDevice = new AhlbornLuxMeter(serial_port_name, 
				AhlbornLuxMeter.COMMAND_STRING_END, AhlbornLuxMeter.COMMAND_RESULT_END, 
				AhlbornLuxMeter.DEFAULT_DELAY_MILLISEC, AhlbornLuxMeter.DEFAULT_TIMEOUT_MILLISEC, 
				sensitive_measurement_channel, insensitive_measurement_channel);
	}
	
	public boolean shouldCut(MeasurementPatch patch_,
			MeasurementPoint c_min_g_min_point,
			MeasurementPoint c_min_g_max_point,
			MeasurementPoint c_max_g_min_point,
			MeasurementPoint c_max_g_max_point) throws RadiometryException {
		// for the moment there is no heuristic to decide whether to cut or not
		//
		return false;
	}

	public MeasurementPoint createMeasurementPoint(PlaneVector meas_point) throws RadiometryException {
		return new LuxMeterMeasurementPoint(meas_point);
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
		try {
			if (!part_name.startsWith(PART_NAME)){
				throw new RadiometryException("Bad part name : "+ part_name + " instead of beginning with " + PART_NAME);
			}
			
			String _line_to_parse = new BufferedReader(new InputStreamReader(part_stream)).readLine();
			float _read_illuminance;
			try{
				 _read_illuminance = Float.parseFloat(_line_to_parse);

			} catch (NumberFormatException _e){
				throw new RadiometryException("cannot parse entry : "  + _line_to_parse, _e);
			}
			if (LuxMeterMeasurementPoint.class.isInstance(meas_point)){
				((LuxMeterMeasurementPoint)meas_point).setIlluminanceValue(_read_illuminance);
			}
			else{
				throw new RadiometryException("bad MeasurementPoint type : " +meas_point.getClass().getName());
			}

		} catch (IOException _e) {
			throw new RadiometryException("cannot retrieve saved illuminance", _e);
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
		if (LuxMeterMeasurementPoint.class.isInstance(meas_point)){
			BufferedWriter _out = new BufferedWriter(new OutputStreamWriter(part_stream));
			try {
				_out.write(""+ ((LuxMeterMeasurementPoint)meas_point).getIlluminanceValue());
			} catch (IOException _e) {
				throw new RadiometryException("output error", _e);
			}
		}
		else{
			throw new RadiometryException("bad MeasurementPoint type : " +meas_point.getClass().getName());
		}
	}

	public void endSavingSession() throws RadiometryException {
		// nothing to do
	}

	public void performCompleteMeasurement(MeasurementPoint meas_point)
			throws RadiometryException {
		if (LuxMeterMeasurementPoint.class.isInstance(meas_point)){
			try {
				((LuxMeterMeasurementPoint)meas_point).setIlluminanceValue(captureDevice.captureIlluminance());
			} catch (IlluminanceException _e) {
				throw new RadiometryException("Error with the luxmeter", _e);
			}
		}
		else{
			throw new RadiometryException("bad MeasurementPoint type : " +meas_point.getClass().getName());
		}

	}

}
