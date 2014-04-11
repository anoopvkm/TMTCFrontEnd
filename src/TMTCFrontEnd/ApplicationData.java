package TMTCFrontEnd;
/**
 * This class deals with application data TMTC front end receives from MCS
 * @author Anoop R Santhosh
 * @version 1.0
 */
public class ApplicationData {
	private byte [] information;
	
	/*
	 * Function to set data 
	 * @param data , a byte array
	 */
	public void SetData(byte [] data){
		this.information = data;
	}
	
	/*
	 * Function to get data 
	 */
	public byte [] GetData(){
		return this.information;
				
	}
}
