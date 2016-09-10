package chatrmi;

import static java.lang.System.exit;
import java.rmi.*;
import javax.swing.*;
import java.util.Scanner;
import java.lang.Thread.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Cliente extends Thread implements Runnable {

    private String nome;
    private static ServidorChat chat;

    private static Queue<String> mensagens = new LinkedList<String>();

    public Cliente(String nome) {
        this.nome = nome;

        try {
            if (this.chat == null) {
                this.chat = (ServidorChat) Naming.lookup("rmi://localhost:1099/ServidorChat");
            }
        } catch (NotBoundException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getNome() {
        return nome;
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
            int cont = chat.lerMensagem().size();

            ArrayList<String> msgArray = null;

            while (true) {
                
                Thread.sleep(500);

                synchronized (chat) {
                    msgArray = chat.lerMensagem();
                }

                int msgSize = msgArray.size();

                for (; msgSize > cont; cont++) {
                    this.armazenaMensagem(msgArray.get(msgSize - 1));
                }
            }

        } catch (RemoteException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void removeUsuarioOnline(String username) {
        try {
            chat.removeUsuarioOnline(username);
        } catch (RemoteException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void enviaMensagem(String msg) {
        try {
            if(msg.equalsIgnoreCase("whoisonline")) {
                String lista = "Safe Chat:\n";
                for(String user : chat.getUsuariosOnline())
                    lista += user + " está online.\n";
                
                mensagens.add(lista);
            } else
                chat.enviarMensagem(this.nome + ": " + msg + "\n");
        } catch (RemoteException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
