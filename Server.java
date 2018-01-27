
import java.net.*;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class Server {
	private static final int AGENT_THREAD_POOL_SIZE = 3;
	private static final int AGENT_QUEUE_CAPACITY =2;
	private static final int USER_QUEUE_CAPACITY = 2;
	
	private static int userPort=9099;
	private static int agentPort=9088;
	
	private static ArrayBlockingQueue<Socket> agentConnectionQueue;
	private static ArrayBlockingQueue<Socket> userConnectionQueue;
	
	public static HashMap<Agent, ArrayList<ServerSendUser>> agentUserMap = new HashMap<Agent, ArrayList<ServerSendUser>>();
	
	public static void main(String []args){
		//main method of server. does not have GUI
		
		//listen for agent and user connection
		new AgentConnect();
		new UserConnect();
	}
	
	private static class AgentConnect extends Thread{
		AgentConnect(){
			start();
		}
		
		public void run(){
			
			
			try{
				LocateRegistry.createRegistry(1099);
				AuthenticationInterface login = new AuthenticationImplementation();
				Naming.rebind("loginObject", login);
				System.out.println("--SERVER STARTS--");
				
				ServerSocket ss = new ServerSocket(agentPort);
				agentConnectionQueue = new ArrayBlockingQueue<Socket>(AGENT_QUEUE_CAPACITY);
				new AgentConnectionHandler(); //Create the thread; it starts itself
				while (true){
					Socket s = ss.accept();
					try{
						agentConnectionQueue.put(s);
						//Blocks if queue is full
					}
					catch (InterruptedException e){
						e.printStackTrace();
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	private static class UserConnect extends Thread{
		UserConnect(){
			start();
		}
		
		public void run(){
			try{
				ServerSocket ss = new ServerSocket(userPort);
				userConnectionQueue = new ArrayBlockingQueue<Socket>(USER_QUEUE_CAPACITY);
				new UserConnectionHandler(); //Create the thread; it starts itself
				while (true){
					Socket s = ss.accept();
					try{
						userConnectionQueue.put(s);
						//Blocks if queue is full
					}
					catch (Exception e){
						e.printStackTrace();
					}
				}
			}
			catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	private static class AgentConnectionHandler extends Thread{
		AgentConnectionHandler(){
			start();
		}
		
		public void run(){
			while (true){
				Socket s;
				try{
					if (agentUserMap.size()<AGENT_THREAD_POOL_SIZE){
						s = agentConnectionQueue.take();
						System.out.println(s.toString() + " Agent connected");

						//add agent into map and start listening for message
						//increase thread pool for users to connect
						agentUserMap.put(new Agent(s), new ArrayList<ServerSendUser>());
						try{
							ServerReceiveAgentThread receive = new ServerReceiveAgentThread(s);
							receive.start();
							
						} catch (Exception e){
							e.printStackTrace();
						}
					}
					
					sleep(1000);
					
				}
				catch (InterruptedException e){
					continue; //If interrupted, just go back to start of while loop
					
				}
			}
		}
	}
	
	private static class UserConnectionHandler extends Thread{
		UserConnectionHandler(){
			start();
		}
		
		public void run(){
			while (true){
				
				Socket userSocket=null;
				Socket agentSocket = null;
				try{
					if (mapIsFull()==false){
						userSocket = userConnectionQueue.take();
						System.out.println(userSocket.toString() + " User connected");
						
	
						//add connected user
						//loop each agents 2 times to populate each first empty slot first
						// 2 times > check size 0 or size 1
						addUserLoop:{
							for (int i =0; i<2; i++){
								for(Agent key : agentUserMap.keySet()){
									if(i==0 && agentUserMap.get(key).size()==i){
										addUser(key, userSocket);
										agentSocket = key.socket;
										break addUserLoop;
									}else if(i==1 && agentUserMap.get(key).size()==i){
										addUser(key,userSocket);
										agentSocket = key.socket;
										break addUserLoop;
									}
								}
							}
						}
					}
					//start listening for message from user
					
					ServerReceiveUserThread receive = new ServerReceiveUserThread(userSocket, agentSocket);
					receive.start();
					
				}
				catch (InterruptedException e){
					//If interrupted, just rest awhile and go back to start of while loop
					try {
						sleep(150);
					} catch (InterruptedException e1) {
						
					}
					continue; 
				}
				
			}
		}
	}
	
	private static boolean mapIsFull(){
		boolean full = true;
		try{
			//check if map is full
			full = true;
			for(Agent key : agentUserMap.keySet()){
				if (agentUserMap.get(key).size()<2){
					full = false;
					break;
				}
			}
			return full;
		}
		catch (ConcurrentModificationException cme){
			return false;
		}
	}
	
	private static void addUser(Agent key, Socket s){
		//add instance of server to user outputstream into the map 
		agentUserMap.get(key).add(new ServerSendUser(s));
	}
}
