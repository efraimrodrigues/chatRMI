package chatrmi;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.rmi.*;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.rmi.RemoteException;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import serverrmi.ChatConInterface;
import serverrmi.ChatConUserInterface;

/**
 *
 * @author Efraim Rodrigues
 */
public class ChatConCliente extends Thread implements Runnable, Serializable {

    private String nome;
    private static ChatConInterface chat;
    private static ChatConUserInterface user;

    private String encAlgo = "AES";
    private static Key key;
    private byte[] senha = new String("seasideseasideSS").getBytes();
    
    private KeyPair keyPair;

    private static final Queue<String> mensagens = new LinkedList<>();
 
    /**
     *
     */

    public ChatConCliente() {
        nome = "";

        try {
            if (this.chat == null) {
                this.chat = (ChatConInterface) Naming.lookup("rmi://127.0.0.1:1099/ServidorChat");
            }

            key = new SecretKeySpec(senha, encAlgo);

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     *
     * @return
     */
    public String getNome() {
        return nome;
    }

    /**
     *
     * @param nome
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     *
     * @return
     */
    public String getNewMessage() {
        String ret = "";
        if (mensagens.size() > 0) {
            ret = mensagens.poll();
            System.out.println(ret);
        }

        return ret;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        try {
            int cont = user.getContador();

            int contDiff = 0;

            ArrayList<String> msgArray = null;

            while (true) {

                Thread.sleep(100);

                synchronized (chat) {
                    contDiff = user.getContador();

                    msgArray = user.getMensagens();
                }

                //Pega os contDiff - cont últimos elementos para amarazenar
                for (; contDiff > cont; cont++) {
                    this.armazenaMensagem(decryptMessage(msgArray.get(msgArray.size() - (contDiff - cont))));
                }
            }

        } catch (RemoteException ex) {
            exit();
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            exit();
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     *
     * @param msg
     */
    public void enviaMensagem(String msg) {
        String msgFinal = this.nome + ": " + msg + "\n";

        try {
            if (msg.equalsIgnoreCase("@whoisonline")) {
                String lista = "Safe Chat:\n";
                for (String user : chat.getUsuarios()) {
                    lista += decrypt(user) + " está online.\n";
                }
                mensagens.add(lista);
            } else if (msg.equalsIgnoreCase("@tchau")) {
                exit();
            } else if (msg.equalsIgnoreCase("@help") || msg.equalsIgnoreCase("@")) {
                mensagens.add("Safe Chat: \nDigite @whoisonline para ver os usuários online.\nDigite @tchau para se despedir e sair do chat.");
            } else {
                for (String user : chat.getUsuarios()) {
                    chat.enviarMensagem(user, encryptMessage(nome + ": " + msg,getKeyFromString(chat.getPublicKey(user)))); //THIS WILL BE ENCRYPTED WITH PUBLIC KEY FROM USER
                }
            }
        } catch (RemoteException ex) {
            exit();
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void armazenaMensagem(String msg) {
        synchronized (mensagens) {
            if (!msg.equals("exit")) {
                mensagens.add(msg);
            }
        }
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getUsuariosOnline() {
        ArrayList<String> ret = new ArrayList<>();
        try {
            synchronized (chat) {
                ArrayList<String> temp = chat.getUsuarios();

                for (String user : temp) {
                    ret.add(decrypt(user));
                }
            }
        } catch (RemoteException ex) {
            exit();
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     *
     * @param username
     * @return
     */
    public boolean isOnline(String username) {
        boolean ret = false;
        try {
            ret = chat.isOnline(encrypt(username));
        } catch (RemoteException ex) {
            exit();
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     *
     * @param nome
     */
    public void login(String nome) {
        try {
            setNome(nome);

            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(1024);
            keyPair = generator.genKeyPair();
            
            KeyFactory fact = KeyFactory.getInstance("RSA");
            
            RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
            
            String pKey = publicKey.getModulus().toString() + "|" + publicKey.getPublicExponent().toString();
            
            user = chat.adicionaUsuario(encrypt(nome), encrypt(pKey)); //O segundo campo deve ser a chave publica deste usuario.

            enviaMensagem("Cheguei.");
        } catch (RemoteException | NoSuchAlgorithmException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void removeUsuario(String nome) {
        try {
            chat.removeUsuario(encrypt(nome));
        } catch (RemoteException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     */
    public void exit() {
        enviaMensagem("Fui.");

        removeUsuario(getNome());

        Platform.exit();
        System.exit(0);
    }

    /**
     *
     * @param encryptedMessage
     * @return
     */
    public String decrypt(String encryptedMessage) {
        String ret = "";

        try {
            Cipher cipher = Cipher.getInstance(encAlgo);

            cipher.init(Cipher.DECRYPT_MODE, key);

            ret = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage.getBytes())), Charset.forName("UTF8"));

        } catch (IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     *
     * @param message
     * @return
     */
    public String encrypt(String message) {
        String ret = "";

        try {
            Cipher cipher = Cipher.getInstance(encAlgo);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedMessage = cipher.doFinal(message.getBytes());

            ret = new String(Base64.getEncoder().encode(encryptedMessage), Charset.forName("UTF8"));

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    /**
     *
     * @param encryptedMessage
     * @return
     */
    public String decryptMessage(String encryptedMessage) {
        String ret = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            
            cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            
            ret = new String(cipher.doFinal(Base64.getDecoder().decode(encryptedMessage.getBytes())));
            
        } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    /**
     *
     * @param message
     * @param key
     * @return
     */
    public String encryptMessage(String message, RSAPublicKey key) {
        String ret = "";
        try {
            Cipher cipher = Cipher.getInstance("RSA");
            
            cipher.init(Cipher.ENCRYPT_MODE, key);
            
            ret = Base64.getEncoder().encodeToString(cipher.doFinal(message.getBytes("UTF-8")));
            
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
    
    private RSAPublicKey getKeyFromString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String [] partes = decrypt(key).split("\\|");
        
        RSAPublicKeySpec spec = new RSAPublicKeySpec(new BigInteger(partes[0]), new BigInteger(partes[1]));
        
        return (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(spec);
    }
    
    @Override
    protected void finalize() {
        try {
            exit();
            super.finalize();
        } catch (Throwable ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
