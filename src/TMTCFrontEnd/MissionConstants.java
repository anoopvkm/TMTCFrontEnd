package TMTCFrontEnd;

public class MissionConstants {

	// the groundStation call sign
	public static String gsCallsign = "000000";
	// the groundStation SSID
	public static byte gsSSID = 0;
	
	// the satellite call sign
	public static String satCallsign = "000000";
	// the satellite SSID
	public static byte satSSID  = 0;
	
	public static int NoVCs = 8 ;
	
	public static int MaxResends = 5;
	
	/**
	 *  Function to set the ground station call sign
	 */
	public static void SetgsCallsign(String callsign){
		gsCallsign = callsign;
	}
	
	/**
	 * Function to set the ground station SSID 
	 */
	public static void SetgsSSID(byte ssid){
		gsSSID = ssid;
	}
	
	/**
	 * Function to set satellite call sign
	 * 
	 */
	public static void SetsatCallsign(String callsign){
		satCallsign = callsign;
	}
	
	/**
	 * Function to set the satellite SSID
	 */
	public static void SetsatSSID(byte ssid){
		satSSID = ssid;
	}
	
	/**
	 * Function to set number of virtual channels (by default 8 )
	 */
	public static void SetVirtualChannelNumber(int num){
		NoVCs = 8 ;
	}
	
	/**
	 * Funcion to set the maximum number of resends of a packet
	 */
	public static void SetMaxResends(int maxresends){
		MaxResends = maxresends;
	}
}
