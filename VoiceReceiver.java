

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class VoiceReceiver extends Thread{
	ByteArrayOutputStream byteOutputStream;
	AudioFormat adFormat;
	TargetDataLine targetDataLine;
	
	static StereoInputStream inputStream;
	static SourceDataLine sourceLine;
	private final static int LEFT_PORT = 9002;
	private final static int RIGHT_PORT = 9003;
	private final static int USER_PORT = 9099;
	private int thisLRChannel;
	private static int bufferSize = 9996;
//	private static final int audioStreamBufferSize = bufferSize * 20;
//	static byte[] audioStreamBuffer = new byte[audioStreamBufferSize];

	
//    private static int audioStreamBufferIndex = 0;
	
	public VoiceReceiver(int LRchannel){
		this.thisLRChannel = LRchannel;
		this.start();
	}
	
//	private void bufferAudioForPlayback(byte[] buffer, int offset,int length) throws Exception {
//	    
//		for (int i = 0; i < length; i++) {
//			audioStreamBuffer[audioStreamBufferIndex] = buffer[i];
//			audioStreamBufferIndex++;
//			if (audioStreamBufferIndex == audioStreamBuffer.length - 1) {
//           	 	InputStream byteInputStream = new ByteArrayInputStream(audioStreamBuffer);
//                AudioFormat adFormat = getAudioFormat();
//                inputStream = new StereoInputStream(new AudioInputStream(byteInputStream, adFormat, audioStreamBuffer.length / adFormat.getFrameSize()));
//                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
//                sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
//                sourceLine.open(adFormat);
//                sourceLine.start();
//                
//               new PlayThread(thisLRChannel);
//               audioStreamBufferIndex = 0;
//               
//           }
//        }
//    }
	
	
	public void run(){
		int port=9001;
		if (thisLRChannel ==0 ){
			port = LEFT_PORT;
		}else if (thisLRChannel ==1 ){
			port = RIGHT_PORT;
		}else if (thisLRChannel ==3){
			port = USER_PORT;
		}
		
		try {
			DatagramSocket receiverSocket = new DatagramSocket(port);
	        byte[] receiveData = new byte[bufferSize];
	       
	        while (true) {
	            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	            receiverSocket.receive(receivePacket);
	            //bufferAudioForPlayback(receivePacket.getData(),receivePacket.getOffset(), receivePacket.getLength());
	            byte[] audioData = receivePacket.getData();
	            InputStream byteInputStream = new ByteArrayInputStream(audioData);
                AudioFormat adFormat = getAudioFormat();
                inputStream = new StereoInputStream(new AudioInputStream(byteInputStream, adFormat, audioData.length / adFormat.getFrameSize()));
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, adFormat);
                sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                sourceLine.open(adFormat);
                sourceLine.start();
               new PlayThread(thisLRChannel);
//                toSpeaker(audioStreamBuffer);
               receiveData = new byte[bufferSize];
	        }
	        
	        
        } catch (Exception e) {
        	
            e.printStackTrace();
            
        }
	}
	
	private static AudioFormat getAudioFormat() {
	    float sampleRate = 8000.0F;
	    int sampleInbits = 16;
	    int channels = 2;
	    boolean signed = true;
	    boolean bigEndian = false;
	    return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
	}

	
	static class PlayThread extends Thread {

		private int LRChannel;
	    private byte tempBuffer[] = new byte[bufferSize/3];
	    PlayThread(int channel){
	    	this.LRChannel=channel;
	    	this.start();
	    }
	    
	    
	    public void run() {
	    	int cnt;
	    	
	        try {
	            
	            if (LRChannel ==0){
		            while ((cnt = inputStream.readConvertLeftAudio(tempBuffer, 0, tempBuffer.length)) != -1) {
		                if (cnt > 0) {
		                	
	                		sourceLine.write(tempBuffer, 0, cnt);
		                	
		                }
		            }
	            }else if (LRChannel==1){
	            	while ((cnt = inputStream.readConvertRightAudio(tempBuffer, 0, tempBuffer.length)) != -1) {
		                if (cnt > 0) {
		                    sourceLine.write(tempBuffer, 0, cnt);
		                }
		            }
	            }else{
		            while ((cnt = inputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
		            	if (cnt > 0) {
		            		sourceLine.write(tempBuffer, 0, cnt);
		                }
		            }
	            }
	            sourceLine.drain();
	            sourceLine.close();
	            tempBuffer = new byte[bufferSize];
	        } catch (Exception e) {
	        	
	            
	        }
	    }
	    
	}
}


