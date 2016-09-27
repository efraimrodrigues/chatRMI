package chatrmi;

import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;
import com.sun.org.apache.xml.internal.security.utils.Base64;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import static java.lang.System.exit;
import java.rmi.*;
import javax.swing.*;
import java.util.Scanner;
import java.lang.Thread.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class ChatConCliente extends Thread implements Runnable, Serializable {

    private String nome;
    private static ChatCon chat;

    private String encAlgo = "AES";
    private static Key key;
    private byte[] senha = new String("seasideseasideSS").getBytes();
    //private String encPublicKey;

    private static Queue<String> mensagens = new LinkedList<String>();

    public ChatConCliente() {
        nome = "";

        try {
            if (this.chat == null) {
                this.chat = (ChatCon) Naming.lookup("rmi://127.0.0.1:1099/ServidorChat");
            }

            key = new SecretKeySpec(senha, encAlgo);

            /*KeyFactory kf = KeyFactory.getInstance(encAlgo);

            X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encPublicKey);
            publicKey = kf.generatePublic(publicKeySpec);

            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encPrivateKey);
            privateKey = kf.generatePrivate(privateKeySpec);*/
        } catch (NotBoundException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNewMessage() {
        String ret = "";
        if (mensagens.size() > 0) {
            ret = mensagens.poll();
        }

        return ret;
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        try {
            int cont = chat.getContador();

            int contDiff = 0;

            ArrayList<String> msgArray = null;

            while (true) {

                Thread.sleep(100);

                synchronized (chat) {
                    contDiff = chat.getContador();

                    msgArray = chat.lerMensagem();
                }

                //Pega os contDiff - cont últimos elementos para amarazenar
                for (; contDiff > cont; cont++) {
                    this.armazenaMensagem(decryptMessage(msgArray.get(msgArray.size() - (contDiff - cont))));
                }
            }

        } catch (RemoteException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviaMensagem(String msg) {
        String msgFinal = this.nome + ": " + msg + "\n";

        try {
            if (msg.equalsIgnoreCase("@whoisonline")) {
                String lista = "Safe Chat:\n";
                for (String user : chat.getUsuariosOnline()) {
                    lista += user + " está online.\n";
                }
                mensagens.add(lista);
            } else if (msg.equalsIgnoreCase("@tchau")) {
                exit();
            } else if (msg.equalsIgnoreCase("@help")) {
                mensagens.add("Safe Chat: \nDigite @whoisonline para ver os usuários online.\nDigite @tchau para se despedir e sair do chat.");
            } else {
                chat.enviarMensagem(encryptMessage(msgFinal));
            }
        } catch (RemoteException ex) {
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

    public void adicionarUsuarioOnline(String username) {
        try {
            chat.adicionarUsuarioOnline(username);
        } catch (RemoteException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void removeUsuarioOnline(String username) {
        try {
            chat.removeUsuarioOnline(username);
        } catch (RemoteException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<String> getUsuariosOnline() {
        ArrayList<String> ret = null;
        try {
            synchronized (chat) {
                ret = chat.getUsuariosOnline();
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    public boolean isOnline(String username) {
        boolean ret = false;
        try {
            ret = chat.isOnline(username);
        } catch (RemoteException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    public void login(String nome) {
        setNome(nome);

        adicionarUsuarioOnline(nome);

        enviaMensagem("Cheguei.");
    }

    public void exit() {
        enviaMensagem("Fui.");

        removeUsuarioOnline(getNome());

        Platform.exit();
        System.exit(0);
    }

    public String decryptMessage(String encryptedMessage) {
        String ret = "";

        try {
            Cipher cipher = Cipher.getInstance(encAlgo);

            cipher.init(Cipher.DECRYPT_MODE, key);

            com.sun.org.apache.xml.internal.security.Init.init();

            ret = new String(cipher.doFinal(Base64.decode(encryptedMessage.getBytes())), Charset.forName("UTF8"));

        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Base64DecodingException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

    public String encryptMessage(String message) {
        String ret = "";

        try {
            Cipher cipher = Cipher.getInstance(encAlgo);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedMessage = cipher.doFinal(message.getBytes());

            ret = new String(Base64.encode(encryptedMessage));

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            Logger.getLogger(ChatConCliente.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
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