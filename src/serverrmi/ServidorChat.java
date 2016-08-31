package serverrmi;

import chatrmi.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
public interface ServidorChat extends Remote {
	public void enviarMensagem(String mensagem) throws RemoteException;
	public ArrayList<String> lerMensagem() throws RemoteException;
}