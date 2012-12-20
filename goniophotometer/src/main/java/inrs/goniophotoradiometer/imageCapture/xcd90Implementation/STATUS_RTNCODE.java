package inrs.goniophotoradiometer.imageCapture.xcd90Implementation;

public    enum STATUS_RTNCODE
{
    STATUSZCL_NO_ERROR			(0, "No error"),
    STATUSZCL_COMPLETE			(0, "Success"),
    STATUSZCL_PARAMETER_ERROR	(1, "Invalid parameters"),
    STATUSZCL_BUFFER_SHORT		(2, "Buffer is short"),
    
    STATUSZCL_OPEN_ERROR		(3, "Failed to open the camera"),
    STATUSZCL_OPENED			(4, "The camera is already opened"),
    STATUSZCL_CANNOT_FOUND		(5, "Camera not found"),
    STATUSZCL_NO_OPEN			(6, "The camera is not opened"),

    STATUSZCL_COMMUNICATE_ERROR	(7, "Communication error"),

    STATUSZCL_DATA_INACCURACY	(8, "Retrieved data is not accurate"),
    STATUSZCL_NO_SUPPORT		(9, "Feature is not supported"),
    STATUSZCL_VMODE_ERROR		(10, "Video Mode setting error"),
    STATUSZCL_FEATURE_ERROR		(11, "Feature Control error"),
    STATUSZCL_VALUE_ERROR		(12, "Camera Parameter setting error"),
    STATUSZCL_SELFCLEAR_ERROR	(13, "Selfclear bit is not cleared"),
    STATUSZCL_IMAGE_ERROR		(14, "Image Size setting error"),
    STATUSZCL_RESOURCE_ERROR	(15, "Isochronous Resource error"),
    STATUSZCL_NOTRESOURCE_ERROR	(16, "Isochronous Resource is not allocated"),
    STATUSZCL_ALLOCATE_ERROR	(17, "Failed to allocate the Isochronous Resource"),
    STATUSZCL_STARTED_ERROR		(18, "Camera is already started"),
    STATUSZCL_NOTSTART_ERROR	(19, "Camera is not started"),
    STATUSZCL_REQUEST_ERROR		(20, "Image Request failed"),
    STATUSZCL_REQUEST_TIMEOUT	(21, "Image Request timeout"),

    STATUSZCL_SOFTTRIGGER_BUSY	(22, "Software Trigger is busy"),

    STATUSZCL_UNDEF_ERROR		(99, "Undefined error");
    
    private int		statusCode;
    private String	statusMessage;
    private STATUS_RTNCODE(int status_code, String status_msg){
    	statusCode 		= status_code;
    	statusMessage 	= status_msg;
    }
    public int	getStatusCode(){
    	return statusCode;
    }
    public String getStatusMessage(){
    	return statusMessage;
    }
    
    static public String decodeStatus(int status_code){
    	for (STATUS_RTNCODE _status : STATUS_RTNCODE.values()){
    		if (_status.getStatusCode() == status_code){
    			return _status.getStatusMessage();
    		}
    	}
    	return "Unknown status code";
    }
} ;


