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
}
