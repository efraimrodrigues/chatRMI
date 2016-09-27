package serverrmi;

import chatrmi.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
public interface ChatCon extends Remote {
	abstract public void enviarMensagem(String mensagem) throws RemoteException;
	abstract public ArrayList<String> lerMensagem() throws RemoteException;
        abstract public void adicionarUsuarioOnline(String username) throws RemoteException;
        abstract public void removeUsuarioOnline(String username) throws RemoteException;
        abstract public ArrayList<String> getUsuariosOnline() throws RemoteException;
        abstract public boolean isOnline(String username) throws RemoteException;
        abstract public Integer getContador() throws RemoteException;
}