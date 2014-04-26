package TMReceiver;
/**
 * Class to buffer data 
 * @author anoop
 *
 */
public class ReAssembleBuffer {

	public int minIdx = 0;
	public int maxIdx = 0;
	public int lengthRead = 0;
	public int PacketLength = 0;
	public boolean middle = false;
	public byte [] data ;
}
