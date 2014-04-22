package TMReceiver;
/**
 * Class to buffer data 
 * @author anoop
 *
 */
public class ReAssembleBuffer {

	int minIdx = 0;
	int maxIdx = 0;
	int lengthRead = 0;
	int PacketLength = 0;
	boolean middle = false;
	byte [] data ;
}
