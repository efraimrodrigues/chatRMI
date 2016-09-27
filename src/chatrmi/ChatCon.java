package chatrmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.spec.RSAPublicKeySpec;
import java.util.ArrayList;

public interface ChatCon extends Remote {

    public void enviarMensagem(String mensagem) throws RemoteException;
    public ArrayList<String> lerMensagem() throws RemoteException;
    public void adicionarUsuarioOnline(String username) throws RemoteException;
    public void removeUsuarioOnline(String username) throws RemoteException;
    public ArrayList<String> getUsuariosOnline() throws RemoteException;
    public boolean isOnline(String username) throws RemoteException;
    public Integer getContador() throws RemoteException;
}