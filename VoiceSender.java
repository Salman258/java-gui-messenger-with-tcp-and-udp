


import java.io.ByteArrayOutputStream;
import java.net.*;
import javax.sound.sampled.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class VoiceSender{
	public boolean stopaudioCapture = false;
	private AudioFormat adFormat;
	public TargetDataLine targetDataLine;
	private DatagramSocket broadcasterSocket;
	private String ipAddress;
	int port;
	
	public VoiceSender(String ipAddress, int port) throws Exception{
		this.ipAddress = ipAddress;
		this.port = port;
	    
	}
	
	private static AudioFormat getAudioFormat() {
	    float sampleRate = 8000.0F;
	    int sampleInbits = 16;
	    int channels = 2;
	    boolean signed = true;
	    boolean bigEndian = false;
	    return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
	}

	public void captureAudio() {
	    try {
	    	broadcasterSocket = new DatagramSocket();
	        adFormat = getAudioFormat();
	        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, adFormat);
	        targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
	        targetDataLine.open(adFormat);
	        targetDataLine.start();
	        Thread captureThread = new Thread(new CaptureThread());
	        captureThread.start();
	    } catch (Exception e) {
	        StackTraceElement stackEle[] = e.getStackTrace();
	        
	    }
	}
	
	private class CaptureThread extends Thread {
		
	    byte tempBuffer[] = new byte[9996];

	    public void run() {
	        stopaudioCapture = false;
	        DatagramPacket sendPacket;
	        try {
	            InetAddress IPAddress = InetAddress.getByName(ipAddress);
	            while (!stopaudioCapture) {
	                int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
	                if (cnt>0){
	                	sendPacket = new DatagramPacket(tempBuffer, tempBuffer.length, IPAddress, port);
	                    broadcasterSocket.send(sendPacket);
	                }
	                tempBuffer = new byte[9996];
	                    
	            }
	            
	        } catch (Exception e) {
	            e.printStackTrace();
	            
	        }
	    }
	}

}
