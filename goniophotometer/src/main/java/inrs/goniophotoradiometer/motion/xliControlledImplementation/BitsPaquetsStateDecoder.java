package inrs.goniophotoradiometer.motion.xliControlledImplementation;

abstract class BitsPaquetsStateDecoder implements StateDecoder{
	
	private static final int	PAQUET_LENGTH			= 4;
	private static final int	PAQUET_COUNT			= 8;
	private static final char	STATE_HEADER			= '*';
	private static final char	PAQUET_SEPARATOR		= '_';
	
	public void decodeState(String state_string, boolean[] flag_tab) throws StateParsingException{
		// first ensure that the state string is a 32 bit info message :
		// *0000_000...._0000
					
		if (state_string.length() != PAQUET_COUNT*(PAQUET_LENGTH+1)+1){
			throw new StateParsingException("bad state message length : " + state_string.length() + " instead of " + PAQUET_COUNT*(PAQUET_LENGTH+1) + "decoding " + state_string);
		}
		
		if (state_string.charAt(0) != STATE_HEADER){
			throw new StateParsingException("bad state header");
		}
		
		// verify and set flags that begin at index 1
		//
		int _index = 0;
		for (int _paquet_index = 0; _paquet_index < PAQUET_COUNT; _paquet_index ++){
			for (int _bit_index_in_paquet = 0; _bit_index_in_paquet<PAQUET_LENGTH; _bit_index_in_paquet ++){
				char _bit_char = state_string.charAt(_index+1);
				if ((_bit_char != '0')&&(_bit_char != '1')){
					throw new StateParsingException("bad bit char at index " + _index+1 + " : " + state_string.charAt(_index+1));
				}
				_index ++;
			}
			if (_paquet_index < PAQUET_COUNT-1){
				if (state_string.charAt(_index+1)!=PAQUET_SEPARATOR){
					throw new StateParsingException("bad separator at index " + (_index+1) + ": " + state_string.charAt(_index+1) + "paquet_index = "+ _paquet_index  + " / " + PAQUET_COUNT);
				}
				_index ++;
			}
		}
		
		// set flags
		//
		_index = 0;
		for (int _paquet_index = 0; _paquet_index < PAQUET_COUNT; _paquet_index ++){
			for (int _bit_index_in_paquet = 0; _bit_index_in_paquet<PAQUET_LENGTH; _bit_index_in_paquet ++){
				flag_tab[_index] = (state_string.charAt(_index+1) == '1');
				_index ++;
			}
			_index ++;
		}
	}
};
