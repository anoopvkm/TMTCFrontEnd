/**
 * This class deals with the address field of the AX.25 frame
 * @author Anoop Santhosh
 * @version 1.0
 */
public class AX25AddressField {

	// Call sign string 6 letters
	public String CallSign;
	
	// Secondary Station Identifier
	public byte Ssid;
	
	/**
	 * AX.25 Adrress Field constructor 
	 */
	public AX25AddressField(){
		this.CallSign = "000000";
		this.Ssid = 0;
	}
	
	/**
	 * AX.25 Adrress Field constructor 
	 * @param callSign the call sign of the address field
	 * @param ssid Secondary Station Identifier
	 */
	public AX25AddressField(String callSign, byte ssid){
		this.CallSign = callSign;
		this.Ssid = ssid;
	}
	
	
	
}
