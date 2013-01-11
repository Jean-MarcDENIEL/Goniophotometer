package inrs.goniophotoradiometer;

import inrs.goniophotoradiometer.exceptions.RadiometryException;
import inrs.goniophotoradiometer.hierarchicalMeasurementStrategies.fileSupportImplementation.FileSupportHierarchicalMeasurementStrategy;
import inrs.goniophotoradiometer.measurementImplementations.CameraHead;
import inrs.goniophotoradiometer.motion.MotionEngine;
import inrs.goniophotoradiometer.motion.xliControlledImplementation.XliControlledMotionEngine;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;


public class GoniophotoradiometerApplication {
	
	public static final int 		MAX_C_DELTA = 15;
	public static final int 		MAX_G_DELTA = 20;
	public static final String 		ARM_PORT = "COM1";
	public static final int			ARM_COUNT_PER_REV = 200;
	public static final int			ARM_REV_RATIO = 500 ;
	public static final String 		TURNTABLE_PORT = "COM2";
	public static final int			TURNTABLE_COUNT_PER_REV = 200;
	public static final int			TURNTABLE_REV_RATIO = 64;
	
	/**
	 * First argument is a working directory
	 * @param args
	 */
	static public void main(String[] args){
		try{
			String _directory = null;
			BufferedReader _buff = new BufferedReader(new InputStreamReader(System.in));
			if (args.length > 0){
				System.out.println("Output directory : " + args[0]);
				_directory = args[0];
			}
			else{
				System.out.print("Enter a working directory : ");
				_directory = _buff.readLine();
				System.out.println("Working in : " + _directory);
			}
			File _file_dir = new File(_directory);
			if (_file_dir.exists()){
				if (_file_dir.isFile()){
					errorExiting(_directory + "exists and is not a directory !");
				}
			}
			else{
				if (!_file_dir.mkdir()){
					errorExiting("Cannot create the working directory !");
				}
			}
			
			String[] _image_formal_name_tab = uniqueNames(ImageIO.getWriterFormatNames());
			System.out.println("Choose an image format among the followings [0 - " + (_image_formal_name_tab.length-1) + "] (default 0) :" );
			for (int _i=0; _i<_image_formal_name_tab.length; _i++){
				System.out.println("\t [" +_i + "] : " + _image_formal_name_tab[_i] );
			}
			String _image_format_index_str = _buff.readLine();
			int _format_index = 0;
			if (_image_format_index_str.length()>0){
				try{
					_format_index = Integer.parseInt(_image_format_index_str);
				}
				catch(NumberFormatException _e){
					errorExiting("entry is not an integer.");
				}
			}
			else{
				System.out.println("Choosing default image format.");
			}
			if (_format_index >= _image_formal_name_tab.length-1){
				errorExiting("Out of format possible choices.");
			}
			
			CameraHead 			_camera 	= new CameraHead(_image_formal_name_tab[_format_index], _image_formal_name_tab[_format_index]);
			MeasurementStrategy _strategy 	= new FileSupportHierarchicalMeasurementStrategy(MAX_C_DELTA, MAX_G_DELTA, _directory, _camera);
			MotionEngine		_arm 		= new XliControlledMotionEngine(ARM_PORT, ARM_COUNT_PER_REV, ARM_REV_RATIO);
			MotionEngine		_turntable	= new XliControlledMotionEngine(TURNTABLE_PORT, TURNTABLE_COUNT_PER_REV, TURNTABLE_REV_RATIO);
			
			System.out.println("Moving the arm 5 degrees");
			_arm.processRelativeMove(5.0f);
			
		}
		catch(IOException _e){

		} catch (RadiometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorExiting("An issue with the measurement head arrised");
		}
	}
	
    private static String[] uniqueNames(String[] strings_tab) {
        Set<String> _set = new HashSet<String>();
        for (int i=0; i<strings_tab.length; i++) {
            String _name = strings_tab[i].toLowerCase();
            _set.add(_name);
        }
        return (String[])_set.toArray(new String[0]);
    }
    
    private static void errorExiting(String error_msg){
    	System.out.println(error_msg);
    	System.out.println("Exiting with code 1");
    	System.exit(1);
    }
}
