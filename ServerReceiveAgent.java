
import java.io.DataInputStream;
import java.net.Socket;


public class ServerReceiveAgentThread extends Thread{
	private Socket fromSocket;
	
	public ServerReceiveAgentThread(Socket sock) {
		this.fromSocket = sock;
	}
	
	@Override
	public void run(){
		String str = "\t5\t";
		DataInputStream input = null;
		
		try {
			input = new DataInputStream(fromSocket.getInputStream());
			while(!str.split("\t")[1].equals("3")){
				try{
					str = input.readUTF();
					System.out.println("Server read from agent :" + str);
				}
				catch(Exception e){
					e.printStackTrace();
				}
				
				//set destination according to IP from message received
				//if meant for server only then IP would be "server"
				if (!str.split("\t")[0].equals("server")){
					sendUser(str);

					//if agent terminates, read one more line if there is two users connected
					//to terminate both users
					if (str.split("\t")[1].equals("3")){
						for (Agent key: Server.agentUserMap.keySet()){
							if ((key.socket == fromSocket)&&(Server.agentUserMap.get(key).size() ==2)){
								try{
									str = input.readUTF();
									System.out.println("server read agent " + str);
									sendUser(str);
									break;
								}catch(Exception e){
									e.printStackTrace();
								}
								
							}
						}
					}
				}
			}
			input.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		//delete agent from map
		for (Agent agent:Server.agentUserMap.keySet()){
			if(agent.socket == fromSocket){
				Server.agentUserMap.remove(agent);
				System.out.println(agent.socket.toString() + "Agent disconnected & users removed");
				break;
			}
		}
		
	}
	
	private void sendUser(String str){
		//send string to user according to the ip in front of the message
		//message will be truncated before sending to user
		sendUser:
		for (Agent key: Server.agentUserMap.keySet()){
			for (ServerSendUser user:Server.agentUserMap.get(key)){						
				if(str.split("\t")[0].equals(user.socket.toString())){
					user.send(str.substring(str.indexOf("\t")+1));
					break sendUser;
				}
			}
		}
	}
}
