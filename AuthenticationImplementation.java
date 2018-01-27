
import java.io.File;
import java.io.FileNotFoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;

public class AuthenticationImplementation extends UnicastRemoteObject implements AuthenticationInterface{


	protected AuthenticationImplementation() throws Exception {
		
	}
	
	@Override
	public int userAuth(String username, String password) throws RemoteException{
		Scanner scan;
		String data = "";
		String filename = "Credentials.txt";
		try{
			scan = new Scanner(new File(filename));
		}catch(FileNotFoundException ex){
			return 0;
		}
		while (scan.hasNextLine()){
			data = scan.nextLine();
			if (username.equalsIgnoreCase(data.split("\t")[0])&&password.equals(data.split("\t")[1])){
				return 1;
			}
		}
		return 0;
		
	}
	
}
