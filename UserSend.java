
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class UserSend{
	private static Socket socket;
	private static DataOutputStream output = null;
	
	UserSend(Socket sock, String userName, String question){
		//upon instantiation, user will send its name and question 
		//as introduction to agent
		socket = sock;
		String str= "1\t" + userName + "\t" + question;
		try {
			output = new DataOutputStream(socket.getOutputStream());
			output.writeUTF(str);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void send(String str){
		//send normal message with code '2'
		//avoid sending nothing
		if (str.equals("")){
			str = " ";
		}
		try{
			output.writeUTF("2\t" + str);
			output.flush();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
	}
	
	public void sendTerminate(){
		//send terminate code '3' to server
		try{
			UserChatArea.quit = true;
			output.writeUTF("3\t");
			output.flush();
			output.close();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
	}
}
