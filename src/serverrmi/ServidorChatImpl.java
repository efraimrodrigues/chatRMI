package serverrmi;

import chatrmi.ServidorChat;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
public class ServidorChatImpl extends java.rmi.server.UnicastRemoteObject implements ServidorChat {
	//StringBuffer mensagens;
	ArrayList<String> mensagens;
	int nMensagens;
	public ServidorChatImpl() throws RemoteException {
		super();
		this.mensagens = new ArrayList<String>();
		//this.mensagens = new StringBuffer();
	}
	
	public void enviarMensagem(String mensagem) throws RemoteException{
		//mensagens.append(mensagem+�\n�);
		mensagens.add(mensagem);
	}
	/*
	public String lerMensagem() throws RemoteException{
	return new String(mensagens);
	}
	* */
	public ArrayList<String> lerMensagem() throws RemoteException{
		return mensagens;
	}
}