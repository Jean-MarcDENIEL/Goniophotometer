package inrs.goniophotoradiometer.spectralIrradianceCapture.avantesImplementation;

public class AvantesTesting {
	
	public static void main(String[] str_args){
		
		int _device_init;
		short _init_param = (short)0;
		testAVSFunction("AVS_Init("+_init_param+")", _device_init = AvantesLibrary.INSTANCE.AVS_Init(_init_param));
		
		int _device_count;
		testAVSFunction("AVS_GetNrOfDevice()", _device_count = AvantesLibrary.INSTANCE.AVS_GetNrOfDevices());
		
		testAVSFunction("AVS_Done()", AvantesLibrary.INSTANCE.AVS_Done());
		
	}
	
	public static void testAVSFunction(String function_name, int avs_value){
		try{
			AvantesReturnCode _ret_code = AvantesReturnCode.getCorrespondingReturnValue(avs_value);
			if (_ret_code != AvantesReturnCode.ERR_SUCCESS){
				System.out.println(function_name + " ... returned error : " + avs_value + " = "+ _ret_code.getReturnMeaning() + " meaning that " + _ret_code.getReturnDescription());
				System.out.println("Exiting with code 1");
				System.exit(1);
			}
			else{
				System.out.println(function_name + " ... passing with return code " + avs_value);
			}
		}
		catch(IllegalArgumentException _e){
			System.out.println(function_name + " ... passing with return code " + avs_value);
		}
	}
}
