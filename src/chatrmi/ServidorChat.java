package chatrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ServidorChat extends Remote {

    public void enviarMensagem(String mensagem) throws RemoteException;
    public ArrayList<String> lerMensagem() throws RemoteException;
    public void adicionarUsuarioOnline(String username) throws RemoteException;
    public void removeUsuarioOnline(String username) throws RemoteException;
    public ArrayList<String> getUsuariosOnline() throws RemoteException;
}
