/**
 * 
 */
package inrs.goniophotoradiometer.illuminanceCapture;

/**
 * This interface represents 
 * @author jeanmarc.deniel
 *
 */
public interface LuxMeter {
	public class IlluminanceException extends Exception{
		private static final long serialVersionUID = -1638263741366048141L;
		public IlluminanceException(String exc_msg){
			super(exc_msg);
		}
	}
	float captureIlluminance() throws IlluminanceException;
}
