package chatrmi;

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
import javafx.application.Application;

public class Cliente extends Thread implements Runnable {

    private String nome;
    private static ServidorChat chat;

    private static Queue<String> mensagens = new LinkedList<String>();

    public Cliente(String nome) {
        this.nome = nome;

        try {
            this.chat = (ServidorChat) Naming.lookup("rmi://localhost:1098/ServidorChat");
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

    private void processaMsg() {
        if (mensagens.size() > 0) {
            System.out.println(mensagens.poll());
        }
    }
    
    public String getNewMessage() {
        String ret = "";
        if(mensagens.size() > 0) 
            ret = mensagens.poll();
        
        return ret;
    }

    @Override
    public void run() {
        try {
            int cont = chat.lerMensagem().size();

            System.out.println("Lendo mensagens.");

            while (true) {
                if (chat.lerMensagem().size() > cont) {
                    this.armazenaMensagem(chat.lerMensagem().get(chat.lerMensagem().size() - 1));
                    cont++;
                    System.out.println("Mensagem recebida.");
                }
            }

        } catch (RemoteException ex) {
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

    public void enviaMensagem(String msg) {
        try {
            chat.enviarMensagem(this.nome + ": " + msg + "\n");
        } catch (RemoteException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

public static void main(String args[]) {
        try {
            Cliente c = new Cliente("Teste");
            
            c.start();
            
            String msg = "";
            
            while(true) {
                Scanner scanner = new Scanner(System.in);
                
                msg = scanner.nextLine();
                
                c.enviaMensagem(msg);   
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
