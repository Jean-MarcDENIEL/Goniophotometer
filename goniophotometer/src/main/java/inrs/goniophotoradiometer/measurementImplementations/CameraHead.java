package inrs.goniophotoradiometer.measurementImplementations;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import c4sci.math.algebra.Floatings;
import c4sci.math.geometry.plane.PlaneVector;

import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.HierarchicalMeasurementStrategy.PatchSubdivision;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.IntegerBounds;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPatch;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.MeasurementPoint;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.fileSupportImplementation.FileSupportedMeasurementDevice;
import inrs.goniophotoradiometer.imageCapture.GrayscaleImageCapture;
import inrs.goniophotoradiometer.imageCapture.GrayscaleImageCapture.CaptureException;
import inrs.goniophotoradiometer.imageCapture.xcd90Implementation.XCD90ImageCapture;

/**
 * This class is the basic implementation of the gonioradiophotometer head.<br>
 * It is composed of a single camera head. 
 * @author jeanmarc.deniel
 *
 */
public class CameraHead implements FileSupportedMeasurementDevice {

	private static final String	 PART_NAME = "Camera";
	
	private String 					imageFormatUnformalName;
	private String					imageSuffix;
	private GrayscaleImageCapture	captureDevice;
	private int[]					capturedImage;
	/**
	 * It is supposed that the format name parameter corresponds to the format suffix parameter.
	 * @param image_format_formal_name (@see {@link ImageIO#getWriterFormatNames()}
	 * @param image_format_suffix (@see {@link ImageIO#getWriterFileSuffixes()}
	 * @throws RadiometryException
	 */
	public CameraHead(String image_format_formal_name, String image_format_suffix) throws RadiometryException{
		captureDevice 			= new XCD90ImageCapture();
		imageFormatUnformalName = image_format_formal_name;
		imageSuffix 			= image_format_suffix;
		capturedImage			= null;
		try {
			captureDevice.initCapture();
		} catch (CaptureException _e) {
			throw new RadiometryException("cannot initialize camera", _e);
		}
	}
	
	public void finalize(){
		try {
			captureDevice.endCapture();
		} catch (CaptureException _e) {
			_e.printStackTrace();
		}
		finally{
			try {
				super.finalize();
			} catch (Throwable _e) {
				_e.printStackTrace();
			}
		}
	}
	
	public String[] getMeasurementPartsNames() {
		return new String[] {PART_NAME + "." + imageSuffix };
	}

	public void beginLoadingSession() throws RadiometryException {}

	public void loadMeasurementPart(MeasurementPoint meas_point,
			String part_name, InputStream part_stream)
			throws RadiometryException {
		try {
			if (!part_name.startsWith(PART_NAME)){
				throw new RadiometryException("Bad part name : "+ part_name + " instead of beginning with " + PART_NAME);
			}
			BufferedImage _read_img = ImageIO.read(part_stream);
			if (_read_img == null){
				throw new RadiometryException("Cannot read that kind of image");
			}
			if (_read_img.getType() != BufferedImage.TYPE_USHORT_GRAY){
				throw new RadiometryException("wrong measurement part image type");
			}
			((CameraHeadMeasurementPoint)meas_point).setMeasurementImage(_read_img);
			
		} catch (IOException _e) {
			throw new RadiometryException("cannot retrieve saved image", _e);
		}
		catch(ClassCastException _e){
			throw new RadiometryException("bad MeasurementPoint type : " +meas_point.getClass().getName());
		}
	}

	public void endLoadingSession() throws RadiometryException {}

	public void beginSavingSession() throws RadiometryException {}

	public void saveMeasurementPart(MeasurementPoint meas_point,
			String part_name, OutputStream part_stream)
			throws RadiometryException {
		if (!part_name.startsWith(PART_NAME)){
			throw new RadiometryException("Bad part name : "+ part_name + " instead of beginning with " + PART_NAME);
		}
		try {
			if (!ImageIO.write(((CameraHeadMeasurementPoint)meas_point).getMeasurementImage(), imageFormatUnformalName, part_stream)){
				throw new RadiometryException("Cannot image image : no appropriate writer");
			}
		} catch (IOException _e) {
			throw new RadiometryException("Cannot write image", _e);
		}
		catch(ClassCastException _e){
			throw new RadiometryException("Bad MeasurementPoint type : " +meas_point.getClass().getName());
		}
	}

	public void endSavingSession() throws RadiometryException {}

	public void performCompleteMeasurement(MeasurementPoint meas_point)
			throws RadiometryException {
		try{
			CameraHeadMeasurementPoint _camera_point = (CameraHeadMeasurementPoint)meas_point;
			int _width = captureDevice.getImageWidth();
			int _height = captureDevice.getImageHeight();
			_camera_point.setMeasurementImage(new BufferedImage(_width, _height, BufferedImage.TYPE_USHORT_GRAY));
			capturedImage = captureDevice.captureImage(capturedImage);
			int _capt_index = 0;
			WritableRaster _raster = _camera_point.getMeasurementImage().getRaster();
			
			for (int _row = 0; _row < _height; _row ++){
				for (int _col = 0; _col<_width; _col++){
					int _value = capturedImage[_capt_index ++];
					_raster.setSample(_col, _row, 0, _value);
				}
			}
		}
		catch(ClassCastException _e){
			throw new RadiometryException("Bad MeasurementPoint type : " + meas_point.getClass().getName(), _e);
		} catch (CaptureException _e) {
			throw new RadiometryException("Camera error", _e);
		}
		
	}

	public MeasurementPoint createMeasurementPoint(PlaneVector meas_point) {
		return new CameraHeadMeasurementPoint(meas_point);
	}
	/**
	 * {@link MeasurementPoint} created by the {@link CameraHead} class.<br>
	 * <br>
	 * The image model is {@link BufferedImage#TYPE_USHORT_GRAY}.
	 * @author jeanmarc.deniel
	 *
	 */
	public class CameraHeadMeasurementPoint extends MeasurementPoint{
		private BufferedImage measurementImage;
		
		public CameraHeadMeasurementPoint(PlaneVector meas_point) {
			super(meas_point);
			setMeasurementImage(null);
			setAsNotYetMeasured();
		}

		public BufferedImage getMeasurementImage() {
			return measurementImage;
		}

		public void setMeasurementImage(BufferedImage measurement_image) {
			this.measurementImage = measurement_image;
		}
	}
	public boolean shouldCut(MeasurementPatch patch_,
			MeasurementPoint c_min_g_min_point,
			MeasurementPoint c_min_g_max_point,
			MeasurementPoint c_max_g_min_point,
			MeasurementPoint c_max_g_max_point) throws RadiometryException {
		// for the moment there is no image analyze to decide whether to cut or not
		//
		return false;
	}

	public PatchSubdivision computeSubdivisionWay(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		// cut along the widest parameter range
		//
		if (patch_c_bounds.getWidth() > patch_g_bounds.getWidth()){
			return PatchSubdivision.ON_C_ONLY;
		}
		else{
			if (patch_c_bounds.getWidth() < patch_g_bounds.getWidth()){
				return PatchSubdivision.ON_GAMMA_ONLY;
			}
			else{
				return PatchSubdivision.ON_C_AND_GAMMA;
			}
		}
	}

	public IntegerBounds computeSubpatchesGammaMiddleValues(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		int _g_mid = patch_to_subdivide.getgMid();
		return new IntegerBounds((_g_mid+patch_g_bounds.getLowerBound())/2, (_g_mid+patch_g_bounds.getUpperBound())/2);
	}

	public IntegerBounds computeSubpatchesCMiddleValues(
			MeasurementPatch patch_to_subdivide, IntegerBounds patch_c_bounds,
			IntegerBounds patch_g_bounds) throws RadiometryException {
		int _c_mid = patch_to_subdivide.getcMid();
		return new IntegerBounds((_c_mid + patch_c_bounds.getLowerBound())/2, (_c_mid + patch_c_bounds.getUpperBound())/2);
	}

}