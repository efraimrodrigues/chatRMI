package serverrmi;

import java.nio.charset.Charset;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Efraim Rodrigues
 */
public class ChatCon extends java.rmi.server.UnicastRemoteObject implements ChatConInterface {

    private final HashMap<String, ChatConUserInterface> usuarios;

    public ChatCon() throws RemoteException {
        super();
        this.usuarios = new HashMap<String, ChatConUserInterface>();
    }

    public void enviarMensagem(String user, String mensagem) throws RemoteException {
        usuarios.get(decrypt(user)).enviarMensagem(mensagem);
    }

    @Override
    public ChatConUserInterface adicionaUsuario(String username, String publicKey) throws RemoteException {
        ChatConUserInterface user = new ChatConUser(decrypt(publicKey));
        this.usuarios.put(decrypt(username), user);
        System.out.print(decrypt(username) + " logou.\n");
        return user;
    }

    @Override
    public void removeUsuario(String username) throws RemoteException {
        this.usuarios.remove(decrypt(username));
        System.out.println(username + " saiu.\n");
    }
    
    @Override
    public ArrayList<String> getUsuarios() throws RemoteException {
        ArrayList<String> ret = new ArrayList<String>();
        usuarios.keySet().stream().forEach((key) -> {
            ret.add(encrypt(key));
        });
        return ret;
    }
    
    @Override
    public String getPublicKey(String username) throws RemoteException {
        return encrypt(usuarios.get(decrypt(username)).getPublicKey());
    }

    @Override
    public boolean isOnline(String username) throws RemoteException {
        return usuarios.containsKey(decrypt(username));
    }

    private String decrypt(String text) {
        String ret = "";
        try {
            byte[] senha = new String("seasideseasideSS").getBytes();

            Key key = new SecretKeySpec(senha, "AES");

            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);

            ret = new String(cipher.doFinal(Base64.getDecoder().decode(text.getBytes())), Charset.forName("UTF8"));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(ChatCon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private String encrypt(String text) {
        String ret = "";
        try {
            byte[] senha = new String("seasideseasideSS").getBytes();
            
            Key key = new SecretKeySpec(senha, "AES");
            
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            byte[] encryptedText = cipher.doFinal(text.getBytes());
            
            ret = new String(Base64.getEncoder().encode(encryptedText),Charset.forName("UTF8"));
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ChatCon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(ChatCon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(ChatCon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(ChatCon.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(ChatCon.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

}
