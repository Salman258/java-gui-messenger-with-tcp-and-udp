import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthenticationInterface extends Remote {
	public int userAuth(String username, String password)throws	RemoteException;
}
