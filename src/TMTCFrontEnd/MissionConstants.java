package TMTCFrontEnd;
/**
 * This class stores all mission constants
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class MissionConstants {

	// the groundStation call sign
	public static String gsCallsign = "000000";
	
	// the groundStation SSID
	public static byte gsSSID = 0;
	
	// the satellite call sign
	public static String satCallsign = "000000";
	
	// the satellite SSID
	public static byte satSSID  = 0;
	
	// number of virtual channels
	public static int NoVCs = 8 ;
	
	// number of maximum resend attempts
	public static int MaxResends = 2;
	
	/**
	 *  Function to set the ground station call sign
	 *  @param callsign callsign of GS
	 */
	public static void SetgsCallsign(String callsign){
		gsCallsign = callsign;
	}
	
	/**
	 * Function to set the ground station SSID 
	 * @param ssid SSID of GS
	 */
	public static void SetgsSSID(byte ssid){
		gsSSID = ssid;
	}
	
	/**
	 * Function to set satellite call sign
	 * @param callsign callsign of Satellite.
	 */
	public static void SetsatCallsign(String callsign){
		satCallsign = callsign;
	}
	
	/**
	 * Function to set the satellite SSID
	 * @param ssid SSID of the satellite
	 */
	public static void SetsatSSID(byte ssid){
		satSSID = ssid;
	}
	
	/**
	 * Function to set number of virtual channels (by default 8 )
	 * @param num number of virtual channels
	 */
	public static void SetVirtualChannelNumber(int num){
		NoVCs = 8 ;
	}
	
	/**
	 * Funcion to set the maximum number of resends of a packet
	 * @param maxresends Number of maximum resend attempts
	 */
	public static void SetMaxResends(int maxresends){
		MaxResends = maxresends;
	}
}
