package inrs.goniophotometer.imageCapture;

/**
 * This interface represents video devices (camera) that capture grayscaled images. 
 * @author jeanmarc.deniel
 *
 */
public interface GrayscaleImageCapture {
	
	public class CaptureException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public CaptureException(String exception_message) {
			super(exception_message);
		}
		
	}
	
	/**
	 * 
	 * @return The height of the captured images in pixels
	 */
	int	getImageHeight() throws CaptureException;
	/**
	 * 
	 * @return The width of the captured images in pixels
	 */
	int getImageWidth() throws CaptureException;
	/**
	 * 
	 * @return The max number of useful bits composing the integer grey scale values of image pixels.
	 */
	int getImageDepth() throws CaptureException;
	/**
	 * Initiates the capture process.<br>
	 * This method should be called after creation, before any other method.
	 */
	void initCapture() throws CaptureException;
	/**
	 * Ends the capture process.<br>
	 * This methods releases all resources and should be called as soon as the capture device is no more used.
	 */
	void endCapture() throws CaptureException;
	/**
	 * Captures an images and stores it to be retrieved, depending on gain and exposure values.<br>
	 * @param image_buffer A pixel array whose dimensions corresponds to the image. May be null in order to create such an array as a return value.
	 * @return A new array if image_buffer = null, image_buffer otherwise. One integer value per pixel. <br>
	 * Pixels are stored in line first order.
	 */
	int[] captureImage(int[] image_buffer) throws CaptureException;
	
	/**
	 * Sets the exposure time for future {@link #captureImage()} method calls.
	 * @param seconds_of_exposure The exposure time, reduced to the {@link #getMinExposureTime()} to {@link #getMaxExposureTime()} range.
	 */
	void setExposureTime(float seconds_of_exposure) throws CaptureException;
	/**
	 * 
	 * @return The minimal exposure time, in seconds, that can be achieved by the device.
	 */
	float getMinExposureTime() throws CaptureException;
	/**
	 * 
	 * @return The maximal exposure time, in seconds, that can be achieved by the device.
	 */
	float getMaxExposureTime() throws CaptureException;
	/**
	 * Sets the gain. The results on captured images only depends on the camera. 
	 * @param gain_value The gain value that will be reduced to the {@link #getMinGain()} to {@link #getMaxGain()} range.
	 */
	void setGain(float gain_value) throws CaptureException;
	/**
	 * 
	 * @return The minimal gain set value.
	 */
	float getMinGain() throws CaptureException;
	/**
	 * 
	 * @return The maximal gain set value.
	 */
	float getMaxGain() throws CaptureException;
	
	
	
}
