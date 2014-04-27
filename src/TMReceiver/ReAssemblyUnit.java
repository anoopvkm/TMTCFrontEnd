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
	public LinkedList<ReAssembleBuffer> Completed ;
	
	// to keep track of number of packets reassmebled
	public int reassembledPackets;
	// constructor
	public ReAssemblyUnit(int vcID){
		this.vcid = vcID;
		this.BufferMapMin = new HashMap<Integer,ReAssembleBuffer>();
		this.BufferMapMax = new HashMap<Integer,ReAssembleBuffer>();
		this.Completed = new LinkedList<ReAssembleBuffer>();
		this.reassembledPackets = 0;
	}
	
	/**
	 * Function which reassemble a packet
	 * @param frame
	 */
	public void ReassemblePacket(AX25Telemetry frame){
		
		int FirstHeaderPointer = BitOperations.UnsignedBytetoInteger8(frame.FirstHeaderPointer);
		if(FirstHeaderPointer == 255){
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
						reassembledPackets++;
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
							reassembledPackets++;
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
				byte [] tmpArray = new byte[frame.Data.length + nextBuffer.data.length];
				System.arraycopy(nextBuffer.data, 0, tmpArray, frame.Data.length,nextBuffer.data.length );
				System.arraycopy(frame.Data, 0 , tmpArray, 0, frame.Data.length);
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
				newBuffer.lengthRead = maxRead;
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
						reassembledPackets++;
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
		else if(FirstHeaderPointer == 254){
			// do nothing
		}
		else {
			
			int VcCounter = BitOperations.UnsignedBytetoInteger8(frame.VirtualChannelFrameCount);
			int PrevCounter = (VcCounter == 0) ? 255 : (VcCounter - 1);
			if(BufferMapMax.containsKey(PrevCounter)){
				ReAssembleBuffer bufferedData = BufferMapMax.get(PrevCounter);
				System.arraycopy(frame.Data, 0,bufferedData.data, bufferedData.lengthRead, FirstHeaderPointer);
				Completed.add(bufferedData);
				reassembledPackets++;
				BufferMapMax.remove(bufferedData.maxIdx);
				BufferMapMin.remove(bufferedData.minIdx);
				
			}
			else{
				ReAssembleBuffer newBuffer = new ReAssembleBuffer();
				newBuffer.minIdx = VcCounter;
				newBuffer.maxIdx  = VcCounter; // TODO
				newBuffer.lengthRead = FirstHeaderPointer;
				newBuffer.data = new byte[FirstHeaderPointer];
				System.arraycopy(frame.Data, 0,newBuffer.data , 0, FirstHeaderPointer);
				BufferMapMin.put(VcCounter, newBuffer);
				
				
			}
			
			int nextPointer = FirstHeaderPointer;
			boolean packetsLeft = true;
			while(packetsLeft){
				packetsLeft = false;
				ReAssembleBuffer newBuffer = new ReAssembleBuffer();
				newBuffer.minIdx = VcCounter;
				newBuffer.maxIdx = -1;
				if(frame.Data.length - nextPointer >= 6){
					byte [] pLength = new byte[2];
					pLength[0] = frame.Data[nextPointer + 4];
					pLength[1] = frame.Data[nextPointer + 5];
					newBuffer.PacketLength = BitOperations.UnsignedBytetoInteger16(pLength);
					if(frame.Data.length - nextPointer > newBuffer.PacketLength){
						newBuffer.data = new byte[newBuffer.PacketLength];
						newBuffer.lengthRead = newBuffer.PacketLength;
						System.arraycopy(frame.Data,nextPointer , newBuffer.data, 0, newBuffer.PacketLength);
						Completed.add(newBuffer);
						reassembledPackets++;
						packetsLeft = true;
						nextPointer = nextPointer + newBuffer.PacketLength;
					}
					else if (frame.Data.length - nextPointer == newBuffer.PacketLength){
						newBuffer.data = new byte[newBuffer.PacketLength];
						newBuffer.lengthRead = frame.Data.length - nextPointer;
						System.arraycopy(frame.Data, nextPointer, newBuffer.data, 0, newBuffer.lengthRead);
						Completed.add(newBuffer);
						reassembledPackets++;
					}
					else{
						int nextCounter = (VcCounter == 255 )? 0 : VcCounter + 1;
						
						if(BufferMapMin.containsKey(nextCounter)){
							ReAssembleBuffer tPac = BufferMapMin.get(nextCounter);
							if(newBuffer.PacketLength == (frame.Data.length - nextPointer + tPac.lengthRead )){
								byte [] tData = new byte[newBuffer.PacketLength];
								System.arraycopy(frame.Data, nextPointer, tData, 0, frame.Data.length - nextPointer);
								System.arraycopy(tPac.data, 0, tData, frame.Data.length - nextPointer, tPac.lengthRead);
								tPac.data =  new byte[newBuffer.PacketLength];
								tPac.PacketLength = newBuffer.PacketLength;
								System.arraycopy(tData, 0, tPac.data, 0, newBuffer.PacketLength);
								BufferMapMin.remove(nextCounter);
								Completed.add(tPac);
								reassembledPackets++;
							}
							else{
								byte [] tData = new byte[newBuffer.PacketLength];
								System.arraycopy(frame.Data, nextPointer, tData, 0, frame.Data.length - nextPointer);
							
								System.arraycopy(tPac.data, 0, tData, frame.Data.length - nextPointer, tPac.lengthRead);
								tPac.data = new byte[newBuffer.PacketLength];
								tPac.PacketLength = newBuffer.PacketLength;
								tPac.lengthRead = tPac.lengthRead + frame.Data.length - nextPointer;
								System.arraycopy(tData, 0, tPac.data, 0, newBuffer.PacketLength);
								BufferMapMin.remove(nextCounter);
								if(BufferMapMax.containsKey(tPac.maxIdx)){
									BufferMapMax.remove(tPac.maxIdx);
								}
								BufferMapMax.put(tPac.maxIdx, tPac);
							}
						}else{
							newBuffer.data = new byte[newBuffer.PacketLength];
							newBuffer.lengthRead = frame.Data.length - nextPointer;
							System.arraycopy(frame.Data, nextPointer, newBuffer.data, 0, newBuffer.lengthRead);
							BufferMapMax.put(VcCounter, newBuffer);
						}
					}
					
				}
				else{
					int nextCounter = (VcCounter == 255 )? 0 : VcCounter + 1;
					
					if(BufferMapMin.containsKey(nextCounter)){
						ReAssembleBuffer tPac = BufferMapMin.get(nextCounter);
						if( (frame.Data.length - nextPointer + tPac.lengthRead ) >= 6){
							byte [] tData = new byte[frame.Data.length - nextPointer + tPac.lengthRead ];
							System.arraycopy(frame.Data, nextPointer, tData, 0, frame.Data.length - nextPointer);
							System.arraycopy(tPac.data, 0, tData, frame.Data.length - nextPointer, tPac.lengthRead);
							
							byte [] lenbyte = new byte[2];
							lenbyte[0] = tData[4];
							lenbyte[1] = tData[5];
							int pclen = BitOperations.UnsignedBytetoInteger16(lenbyte);
							tPac.PacketLength = pclen;
							tPac.data =  new byte[pclen];
							System.arraycopy(tData, 0, tPac.data, 0, pclen);
						
							BufferMapMin.remove(nextCounter);
							
							if(pclen == (frame.Data.length - nextPointer + tPac.lengthRead )){
								Completed.add(tPac);
								reassembledPackets++;
							}
							else{
								tPac.lengthRead += (frame.Data.length - nextPointer);
								if(BufferMapMax.containsKey(tPac.maxIdx)){
									BufferMapMax.remove(tPac.maxIdx);
								}
								BufferMapMax.put(tPac.maxIdx, tPac);
							}
						}
						else{
							byte [] tData = new byte[newBuffer.PacketLength];
							System.arraycopy(frame.Data, nextPointer, tData, 0, frame.Data.length - nextPointer);
							System.arraycopy(tPac.data, 0, tData, frame.Data.length - nextPointer, tPac.lengthRead);
							tPac.data = new byte[newBuffer.PacketLength];
							System.arraycopy(tData, 0, tPac.data, 0, newBuffer.PacketLength);
							BufferMapMin.remove(nextCounter);
							if(BufferMapMax.containsKey(tPac.maxIdx)){
								BufferMapMax.remove(tPac.maxIdx);
							}
							BufferMapMax.put(tPac.maxIdx, tPac);
						}
					}else{
						newBuffer.data = new byte[newBuffer.PacketLength];
						newBuffer.lengthRead = frame.Data.length - nextPointer;
						System.arraycopy(frame.Data, nextPointer, newBuffer.data, 0, newBuffer.lengthRead);
						BufferMapMax.put(VcCounter, newBuffer);
					}
				}
			}
		}
	}
}
