import java.io.DataOutputStream;
import java.net.Socket;

public class Agent {

	public Socket socket;
	
	Agent(Socket sock){
		this.socket = sock;
		pingAgent();
	}
	private void pingAgent(){
		DataOutputStream output=null;
		try {
			output = new DataOutputStream(socket.getOutputStream());
			output.writeUTF("ping ping");
			output.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
