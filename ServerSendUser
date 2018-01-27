
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerSendUser {
	public Socket socket;
	private DataOutputStream output;
	
	ServerSendUser(Socket sock){
		this.socket = sock;
		try {
			output = new DataOutputStream(socket.getOutputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void send(String str){
		//send normal message
		//activation();
		try{
			output.writeUTF(str);
			output.flush();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
				
	}
	
	public void sendTerminate(){
		//send message with command code 3 (termination)
		try{
			output.writeUTF("3\t");
			output.flush();
			output.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
			
		
	}
}
