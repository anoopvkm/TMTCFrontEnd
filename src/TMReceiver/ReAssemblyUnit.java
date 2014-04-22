package TMReceiver;

import java.util.HashMap;
import java.util.LinkedList;

import AX25.AX25Telemetry;
import BitOperations.BitOperations;

/**
 * Class which handles reassembly of packets
 * @author anoop
 *
 */
public class ReAssemblyUnit {
	private int vcid ; 
	
	// HashMap of buffers
	HashMap<Integer,ReAssembleBuffer> BufferMapMin;
	
	HashMap<Integer,ReAssembleBuffer> BufferMapMax;
	
	// LinkedList of Completed Packet 
	LinkedList<ReAssembleBuffer> Completed ;
	
	// constructor
	public ReAssemblyUnit(int vcID){
		this.vcid = vcID;
		this.BufferMapMin = new HashMap<Integer,ReAssembleBuffer>();
		this.BufferMapMax = new HashMap<Integer,ReAssembleBuffer>();
		this.Completed = new LinkedList<ReAssembleBuffer>();
	}
	
	/**
	 * Function which reassemble a packet
	 * @param frame
	 */
	public void ReassemblePacket(AX25Telemetry frame){
		if(frame.FirstHeaderPointer == 0xFF){
			boolean minFlag = false;
			boolean maxFlag = false;
			int VcCounter = BitOperations.UnsignedBytetoInteger8(frame.VirtualChannelFrameCount);
			int PrevCounter = (VcCounter == 0) ? 255 : (VcCounter - 1);
			if(BufferMapMax.containsKey(PrevCounter)){
				maxFlag = true;
				ReAssembleBuffer temp = BufferMapMax.get(PrevCounter);
				if(temp.PacketLength != 0){
					int PacketLen = temp.PacketLength;
					int maxRead = (PacketLen-temp.lengthRead) < 502 ? PacketLen - temp.lengthRead : 502;
					
					System.arraycopy(frame.Data, 0 ,temp.data, temp.lengthRead, maxRead);
					temp.lengthRead = temp.lengthRead + maxRead;
					if(temp.lengthRead == PacketLen){
						Completed.add(temp);
						BufferMapMax.remove(PrevCounter);
						BufferMapMin.remove(temp.minIdx);
						
					}else{
						BufferMapMax.remove(PrevCounter);
						BufferMapMin.remove(temp.minIdx);
						temp.maxIdx = VcCounter;
						BufferMapMax.put(VcCounter, temp);
						BufferMapMin.put(temp.minIdx, temp);
					}
				}
				else{
					if(!temp.middle){
						byte [] byteLen = new byte[2];
						// TODO do array out of bound thing here
						byteLen[0] = frame.Data[4 - temp.lengthRead];
						byteLen[1] = frame.Data[5 - temp.lengthRead];
						temp.PacketLength = BitOperations.UnsignedBytetoInteger16(byteLen);
						
						int maxRead = (temp.PacketLength - temp.lengthRead) < 502 ? temp.PacketLength - temp.lengthRead : 502;
						byte [] tmpArray = new byte[temp.PacketLength];
						System.arraycopy(frame.Data, 0 ,tmpArray, temp.lengthRead, maxRead);
						System.arraycopy(temp.data, 0, tmpArray, 0, temp.lengthRead);
						temp.data = new byte[temp.PacketLength];
						System.arraycopy(tmpArray, 0, temp.data, 0, tmpArray.length);
						temp.lengthRead = temp.lengthRead + maxRead;
						if(temp.lengthRead == temp.PacketLength){
							Completed.add(temp);
							BufferMapMax.remove(PrevCounter);
							BufferMapMin.remove(temp.minIdx);
							
						}else{
							BufferMapMax.remove(PrevCounter);
							BufferMapMin.remove(temp.minIdx);
							temp.maxIdx = VcCounter;
							BufferMapMax.put(VcCounter, temp);
							BufferMapMin.put(temp.minIdx, temp);
						}
						
					}else{
						int maxRead = ( frame.Data.length < 502) ? frame.Data.length : 502 ; // TODO check this
						byte [] tmpArray = new byte[maxRead];
						System.arraycopy(frame.Data, 0 , tmpArray, 0, maxRead);
						byte [] tmpArray2 = new byte[temp.data.length + maxRead];
						System.arraycopy(temp.data, 0 , tmpArray2, 0, temp.data.length);
						System.arraycopy(tmpArray, 0, tmpArray2, temp.data.length, tmpArray.length);
						temp.data= new byte[temp.data.length + maxRead];
						temp.lengthRead = temp.data.length;
						System.arraycopy(tmpArray2, 0, temp.data, 0, tmpArray2.length);
						BufferMapMax.remove(PrevCounter);
						BufferMapMin.remove(temp.minIdx);
						temp.maxIdx = VcCounter;
						BufferMapMax.put(VcCounter, temp);
						BufferMapMin.put(temp.minIdx, temp);
						
					}
				}
			}
			int nextCounter = (VcCounter == 255 )? 0 : VcCounter + 1;
			if(BufferMapMin.containsKey(nextCounter)){
				ReAssembleBuffer nextBuffer = BufferMapMin.get(nextCounter);
				minFlag = true;
				byte [] tmpArray = new byte[502 + nextBuffer.data.length];
				System.arraycopy(nextBuffer.data, 0, tmpArray, 502,nextBuffer.data.length );
				System.arraycopy(frame.Data, 0 , tmpArray, 0, 502);
				nextBuffer.lengthRead = tmpArray.length;
				nextBuffer.data = new byte[tmpArray.length];
				System.arraycopy(tmpArray, 0, nextBuffer.data, 0, tmpArray.length);
				nextBuffer.minIdx = VcCounter;
				BufferMapMin.remove(nextCounter);
				BufferMapMax.remove(nextBuffer.maxIdx);
				BufferMapMin.put(VcCounter, nextBuffer);
				BufferMapMax.put(nextBuffer.maxIdx, nextBuffer);
			}
			
			if(!minFlag && !maxFlag ){
				ReAssembleBuffer newBuffer = new ReAssembleBuffer();
				newBuffer.minIdx = VcCounter;
				newBuffer.maxIdx = VcCounter;
				newBuffer.middle = true;
				int maxRead = ( frame.Data.length < 502) ? frame.Data.length : 502 ;
				newBuffer.data = new byte[maxRead];
				System.arraycopy(frame.Data, 0, newBuffer.data, 0,maxRead);
				BufferMapMin.put(VcCounter, newBuffer);
				BufferMapMax.put(VcCounter, newBuffer);
			}
			else if( minFlag && maxFlag){
				int maxRead = ( frame.Data.length < 502) ? frame.Data.length : 502 ;
				ReAssembleBuffer firstBuffer = BufferMapMax.get(VcCounter);
				ReAssembleBuffer secondBuffer = BufferMapMin.get(VcCounter);
				ReAssembleBuffer newBuffer = new ReAssembleBuffer();
				newBuffer.data = new byte[firstBuffer.data.length + secondBuffer.data.length];
				System.arraycopy(firstBuffer.data, 0 , newBuffer.data, 0, firstBuffer.data.length);
				System.arraycopy(secondBuffer.data, 0 , newBuffer.data, (firstBuffer.data.length - maxRead), secondBuffer.data.length);
				newBuffer.lengthRead = newBuffer.data.length;
				newBuffer.minIdx = firstBuffer.minIdx;
				newBuffer.maxIdx = secondBuffer.maxIdx;
				if(firstBuffer.PacketLength != 0 ){
					if(newBuffer.lengthRead == firstBuffer.PacketLength){
						newBuffer.PacketLength = firstBuffer.PacketLength;
						Completed.add(newBuffer);
						BufferMapMin.remove(VcCounter);
						BufferMapMax.remove(VcCounter);
					}else{
						newBuffer.PacketLength = firstBuffer.PacketLength;
						BufferMapMin.remove(VcCounter);
						BufferMapMax.remove(VcCounter);
						BufferMapMin.put(newBuffer.minIdx, newBuffer);
						BufferMapMax.put(newBuffer.maxIdx, newBuffer);
					}
				}else{
					BufferMapMin.remove(VcCounter);
					BufferMapMax.remove(VcCounter);
					BufferMapMin.put(newBuffer.minIdx, newBuffer);
					BufferMapMax.put(newBuffer.maxIdx, newBuffer);
				}
				
			}
		}
		else if(frame.FirstHeaderPointer == 0xFE){
			// do nothing
		}
		else {
			int FirstHeaderPointer = BitOperations.UnsignedBytetoInteger8(frame.FirstHeaderPointer);
			int VcCounter = BitOperations.UnsignedBytetoInteger8(frame.VirtualChannelFrameCount);
			int PrevCounter = (VcCounter == 0) ? 255 : (VcCounter - 1);
			if(BufferMapMax.containsKey(PrevCounter)){
				ReAssembleBuffer bufferedData = BufferMapMax.get(PrevCounter);
				System.arraycopy(frame.Data, 0,bufferedData.data, bufferedData.lengthRead, FirstHeaderPointer);
				Completed.add(bufferedData);
				BufferMapMax.remove(bufferedData.maxIdx);
				BufferMapMin.remove(bufferedData.minIdx);
				
			}
			else{
				ReAssembleBuffer newBuffer = new ReAssembleBuffer();
				newBuffer.minIdx = VcCounter;
				newBuffer.maxIdx  = VcCounter; // TODO
				newBuffer.lengthRead = FirstHeaderPointer;
				
				
			}
			
			int nextPointer = FirstHeaderPointer;
			boolean packetsLeft = true;
			while(packetsLeft){
				packetsLeft = false;
				ReAssembleBuffer newBuffer = new ReAssembleBuffer();
				newBuffer.minIdx = VcCounter;
				newBuffer.maxIdx = -1;
				if(502 - nextPointer >= 6){
					byte [] pLength = new byte[2];
					pLength[0] = frame.Data[nextPointer + 4];
					pLength[1] = frame.Data[nextPointer + 5];
					newBuffer.PacketLength = BitOperations.UnsignedBytetoInteger16(pLength);
					if(502 - nextPointer > newBuffer.PacketLength){
						newBuffer.data = new byte[newBuffer.PacketLength];
						newBuffer.lengthRead = newBuffer.PacketLength;
						System.arraycopy(frame.Data,nextPointer , newBuffer.data, 0, newBuffer.PacketLength);
						Completed.add(newBuffer);
						packetsLeft = true;
						nextPointer = nextPointer + newBuffer.PacketLength;
					}
					else if (502 - nextPointer == newBuffer.PacketLength){
						newBuffer.data = new byte[newBuffer.PacketLength];
						newBuffer.lengthRead = 502-nextPointer;
						System.arraycopy(frame.Data, nextPointer, newBuffer.data, 0, newBuffer.lengthRead);
						Completed.add(newBuffer);
					}
					else{
						newBuffer.data = new byte[newBuffer.PacketLength];
						newBuffer.lengthRead = 502 - nextPointer;
						System.arraycopy(frame.Data, nextPointer, newBuffer.data, 0, newBuffer.lengthRead);
						BufferMapMin.put(VcCounter, newBuffer);
					}
					
				}
				else{
					newBuffer.data = new byte[502 - nextPointer];
					newBuffer.lengthRead = 502 - nextPointer;
					System.arraycopy(frame.Data, nextPointer, newBuffer.data, 0,newBuffer.lengthRead);
					BufferMapMin.put(VcCounter, newBuffer);
				}
			}
		}
	}
}
