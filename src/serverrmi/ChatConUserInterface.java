package serverrmi;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Efraim Rodrigues
 */
public interface ChatConUserInterface extends Remote,Serializable {
    abstract void enviarMensagem(String mensagem) throws RemoteException;
    abstract ArrayList<String> getMensagens() throws RemoteException;
    abstract String getPublicKey() throws RemoteException;
    abstract Integer getContador() throws RemoteException;
}
