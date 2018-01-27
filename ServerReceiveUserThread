
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;


public class ServerReceiveUserThread extends Thread{
	private Socket fromSocket;
	private Socket destinationSocket;
	
	public ServerReceiveUserThread(Socket userSocket, Socket agentSocket) {
		this.fromSocket = userSocket;
		this.destinationSocket = agentSocket;
	}
	
	@Override
	public void run(){
		String str = "\t5\t";
		DataInputStream input = null;
		DataOutputStream output = null;
		try{
			input = new DataInputStream(fromSocket.getInputStream());
			output = new DataOutputStream(destinationSocket.getOutputStream());
			//continue as long as server does not receive termination code
			while(!str.split("\t")[0].equals("3")){
				try {
					str = input.readUTF();
					System.out.println("Server read from user :" + str);
					//send message to destination (agent) with addition of origin ip to the message
					output.writeUTF(fromSocket.toString() + "\t" + str);
					output.flush();
				} 
				catch (EOFException eof){
					//do nothing when user disconnected abruptly
					break;
				}
				catch (IOException e) {
					
				}
			}
			input.close();
			//delete user from map
			deleteUser:
				for (Agent agent:Server.agentUserMap.keySet()){
					for(ServerSendUser user:Server.agentUserMap.get(agent)){
						if(user.socket == fromSocket){
							Server.agentUserMap.get(agent).remove(user);
							System.out.println(user.socket.toString() + "User disconnected");
							break deleteUser;
						}
					}
				}
		}catch(NullPointerException npe){
			try(DataOutputStream out1 = new DataOutputStream(fromSocket.getOutputStream());){
				output.writeUTF("3\t\t");
			}catch (Exception e){
				
			}
			
		}
		catch(IOException ioe){
			ioe.printStackTrace();
		}
		
	}
}
