package serverrmi;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import chatrmi.ChatCon;
import java.security.spec.RSAPublicKeySpec;

public class ChatConImpl extends java.rmi.server.UnicastRemoteObject implements ChatCon {

    private ArrayList<String> mensagens;
    private HashMap<String,String> usuariosOnline;
    private Integer contador;

    public ChatConImpl() throws RemoteException {
        super();
        this.mensagens = new ArrayList<String>();
        this.usuariosOnline = new HashMap<String,String>();
        this.contador = 0;
    }

    public void enviarMensagem(String mensagem) throws RemoteException {
        synchronized (mensagens) {
            if(mensagens.size() > 20)
                mensagens.remove(0);
            
            this.contador++;
            mensagens.add(mensagem);
            
        }
    }

    public ArrayList<String> lerMensagem() throws RemoteException {
        return mensagens;
    }
    
    public void adicionarUsuarioOnline(String username) throws RemoteException {
        this.usuariosOnline.put(username,username);
        System.out.print(username + " logou.\n");
    }
    
    public void removeUsuarioOnline(String username) throws RemoteException {
        this.usuariosOnline.remove(username);
        System.out.println(username + " saiu.\n");
    }
    
    public ArrayList<String> getUsuariosOnline() throws RemoteException {
        ArrayList<String> ret = new ArrayList<String>();
        for(String key : usuariosOnline.keySet())
            ret.add(key);
        
        return ret;
    }
    public boolean isOnline(String username) throws RemoteException {
        return usuariosOnline.containsKey(username);
    }
    
    public Integer getContador() throws RemoteException {
        return contador;
    }

    
}
