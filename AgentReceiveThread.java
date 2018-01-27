
import java.io.DataInputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AgentReceiveThread extends Thread{
	private Socket socket;
	private static String myIP;
	private final static int LEFT_PORT = 9002;
	private final static int RIGHT_PORT = 9003;
	
	public AgentReceiveThread(Socket sock) {
		this.socket = sock;
	}
	
	@Override
	public void run(){
		String str = "";
		DataInputStream input = null;
		
		//let server ping agent to indicate connection established
		try{
			input = new DataInputStream(socket.getInputStream());
			str = input.readUTF();
			AgentChatArea.sceneTitle.setText("Welcome " + AgentChatArea.agentName.toUpperCase()+ "\n");
			myIP = InetAddress.getLocalHost().getHostAddress().toString();
			//myIP = "localhost";
		}
		catch(Exception e){
			
		}
		
		//keep reading string unless agent quit
		try{
			input = new DataInputStream(socket.getInputStream());
			while(AgentChatArea.quit != true){
					str = input.readUTF();
					receiveString(str);
			}
			input.close();
		}
		catch(Exception e){
			
		}
		
		
	}
	
	private void receiveString (String str) throws Exception{
		//process received string and decide what to do
		
		SimpleDateFormat fDate = new SimpleDateFormat ("hh:mm:ss aa ");
		String fromSocket = str.split("\t")[0];
		String strUser ="";
		String strAgent ="";
		int myPort=0;
		
		//if string received has command code 1
		//it means add new connected user into array
		//and send greeting
		if (str.split("\t")[1].equals("1")){
			for (int i=0; i<2; i++){
				if (AgentChatArea.connectedUser[i].equals("")){
					AgentChatArea.connectedUser[i] = fromSocket;
					strUser = fDate.format(new Date()) + " " + str.split("\t")[2] + " connected from socket " + 
					AgentChatArea.connectedUser[i] + "\nAsks : " + str.split("\t")[3] + "\n" ;
					
					
					//open voice to user
					AgentChatArea.voiceSender1 = new VoiceSender(str.split(",")[0].split("/")[1], Integer.valueOf(str.split(",")[2].split("=")[1].split("]")[0]));
					AgentChatArea.voiceSender2 = new VoiceSender(str.split(",")[0].split("/")[1], Integer.valueOf(str.split(",")[2].split("=")[1].split("]")[0]));
					
					if (i ==0){
						myPort = LEFT_PORT;
					}else if(i==1){
						myPort = RIGHT_PORT;
					}
					strAgent =  myIP + "@" + myPort + "@Hello my name is " + AgentChatArea.agentName +
							". Please hold on while we try to find the best answer for you.";
					if (i==0){
						
						
						AgentChatArea.txtArea1.clear();
						AgentChatArea.txtArea1.appendText(strUser);
						AgentChatArea.agentSend.send(fromSocket, strAgent);
					}else if (i==1){
						AgentChatArea.txtArea2.clear();
						AgentChatArea.txtArea2.appendText(strUser);
						AgentChatArea.agentSend.send(fromSocket, strAgent);
					}
					//start listening for voice
					new VoiceReceiver(i);
					break;
				}
			}
		//if command code is 2 means normal receiving message
		//just receive and display message to correct chat window
		}else if(str.split("\t")[1].equals("2")){
			for (int i=0; i<2; i++){
				if (AgentChatArea.connectedUser[i].equals(fromSocket)){
					strUser = fDate.format(new Date()) + " User >" + str.split("\t")[2] + "\n" ;
					if (i==0){
						AgentChatArea.txtArea1.appendText(strUser);
					}else if (i==1){
						AgentChatArea.txtArea2.appendText(strUser);
					}
					break;
				}
			}
		}else if(str.split("\t")[1].equals("3")){
			//command code 3 means user termination
			//delete correct user from chat window and save chat
			for (int i=0; i<2; i++){
				if (AgentChatArea.connectedUser[i].equals(fromSocket)){
					strUser = fDate.format(new Date()) + " User disconnected";
					AgentChatArea.stop3.fire();
					if (i==0){
						AgentChatArea.stop1.fire();
						AgentChatArea.connectedUser[i]="";
						AgentChatArea.txtArea1.appendText(strUser);
						AgentChatArea.saveChat(AgentChatArea.txtArea1.getText());
					}else if (i==1){
						AgentChatArea.stop2.fire();
						AgentChatArea.connectedUser[i]="";
						AgentChatArea.txtArea2.appendText(strUser);
						AgentChatArea.saveChat(AgentChatArea.txtArea2.getText());
					}
					break;
				}
			}
		}
	}
	
}
