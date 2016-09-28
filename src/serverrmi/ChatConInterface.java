package serverrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Efraim Rodrigues
 */
public interface ChatConInterface extends Remote {
	abstract public void enviarMensagem(String user, String mensagem) throws RemoteException;
        abstract public ChatConUserInterface adicionaUsuario(String username, String publicKey) throws RemoteException;
        abstract public void removeUsuario(String username) throws RemoteException;
        abstract public ArrayList<String> getUsuarios() throws RemoteException;
        abstract public String getPublicKey(String username) throws RemoteException;
        abstract public boolean isOnline(String username) throws RemoteException;
}