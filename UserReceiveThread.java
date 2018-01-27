
import java.io.DataInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class UserReceiveThread extends Thread{
	private Socket socket;
	
	public UserReceiveThread(Socket sock) {
		this.socket = sock;
	}
	
	@Override
	public void run(){
		SimpleDateFormat fDate = new SimpleDateFormat ("hh:mm:ss aa ");
		String str = "\t5\t";
		DataInputStream input= null;
		boolean firstMessage = true;
		try {
			input = new DataInputStream(socket.getInputStream());
			//while server does not send termination code from agent
			//read message and display it
			
			while(!str.split("\t")[0].equals("3")){
				str = input.readUTF();
				if (!str.split("\t")[0].equals("3"))
					if (firstMessage == true){
						UserChatArea.voiceSender = new VoiceSender(str.split("\t")[1].split("@")[0], Integer.valueOf(str.split("\t")[1].split("@")[1]));
						UserChatArea.msgArea.appendText(fDate.format(new Date()) + " Agent > " + str.split("\t")[1].split("@")[2] + "\n");
						firstMessage = false;
					}else{
						UserChatArea.msgArea.appendText(fDate.format(new Date()) + " Agent > " + str.split("\t")[1] + "\n");
					}
			}
			//received termination code and quit
			UserChatArea.stop.fire();
			UserChatArea.quit = true;
			socket.close();
			UserChatArea.msgArea.appendText(fDate.format(new Date()) + " Agent disconnected.\n");
		}
		catch (SocketException se){
			//do nothing when agent disconnect
		}
		catch (Exception e) {
			
		}
		
		
	}
}
