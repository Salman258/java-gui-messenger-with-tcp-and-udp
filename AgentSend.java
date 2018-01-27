
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class AgentSend {
	private Socket socket;
	private DataOutputStream output = null;
	
	AgentSend(Socket sock){
		this.socket = sock;
		try {
			output = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	void send(String destinationSocket, String str){
		//send message with command code 2 (normal message)
		//avoid sending nothing
		if (str.equals("")){
			str = " ";
		}
		try{
			output.writeUTF(destinationSocket + "\t2\t" + str);
			output.flush();
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
				
		
	}
	
	void sendTerminate(String destinationSocket){
		//send message with command code 3 (termination)
		//cannot close output as agent might need to call sendTerminate two times
		//to two users
			try{
				output.writeUTF(destinationSocket + "\t3\t");
				output.flush();
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
		
	}
	
	void close()throws Exception{
		//close the outputstream of the class
		output.close();
	}
}
