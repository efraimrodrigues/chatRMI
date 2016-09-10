package serverrmi;

import chatrmi.ServidorChat;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServidorChatImpl extends java.rmi.server.UnicastRemoteObject implements ServidorChat {

    private ArrayList<String> mensagens;
    private HashMap<String,String> usuariosOnline;

    public ServidorChatImpl() throws RemoteException {
        super();
        this.mensagens = new ArrayList<String>();
        this.usuariosOnline = new HashMap<String,String>();
    }

    public void enviarMensagem(String mensagem) throws RemoteException {
        synchronized (mensagens) {
            mensagens.add(mensagem);
        }
    }

    public ArrayList<String> lerMensagem() throws RemoteException {
        return mensagens;
    }
    
    public void adicionarUsuarioOnline(String username) throws RemoteException {
        this.usuariosOnline.put(username,username);
        System.out.print(username + " logou.");
    }
    
    public void removeUsuarioOnline(String username) throws RemoteException {
        this.usuariosOnline.remove(username);
    }
    
    public ArrayList<String> getUsuariosOnline() throws RemoteException {
        ArrayList<String> ret = new ArrayList<String>();
        for(String key : usuariosOnline.values())
            ret.add(key);
        
        return ret;
    }
}
