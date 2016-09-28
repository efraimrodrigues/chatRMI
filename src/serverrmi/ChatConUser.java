package serverrmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Efraim Rodrigues
 */
public class ChatConUser extends java.rmi.server.UnicastRemoteObject implements Serializable, ChatConUserInterface {

    private final String publicKey;
    private Integer contador;
    private final ArrayList<String> mensagens;
    
    ChatConUser (String publicKey) throws RemoteException {
        super();
        mensagens = new ArrayList<>();
        contador = 0;
        this.publicKey = publicKey;
    }

    @Override
    public void enviarMensagem(String mensagem) throws RemoteException {
        synchronized (mensagens) {
            if (mensagens.size() > 20) { //Isto garante que a ArrayList<String> terá no máximo 20 mensagens. Isto é extremamente importante porque evita que o tamanho do objeto cresça indefinidamente.
                mensagens.remove(0);
            }

            this.contador++;
            mensagens.add(mensagem);
            System.out.println(mensagem);
        }
    }
    
    @Override
    public ArrayList<String> getMensagens() throws RemoteException {
        return mensagens;
    }

    @Override
    public String getPublicKey() throws RemoteException {
        return publicKey;
    }

    @Override
    public Integer getContador() throws RemoteException {
        return contador;
    }

    

}
