package serverrmi;

import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import chatrmi.ChatConInterface;

public class ChatConServidor {

    public ChatConServidor() {
        try {
            //System.setSecurityManager(new RMISecurityManager());
            //if (System.getSecurityManager() == null) {
            //    System.setSecurityManager(new SecurityManager());
            //}
            System.setProperty("java.rmi.server.hostname", "192.168.0.17");
            
            LocateRegistry.createRegistry(1099);
            
            ChatConInterface server = new ChatCon();

            //ServidorChat inter = (ServidorChat) UnicastRemoteObject.exportObject (server,1098);
            Naming.bind("ServidorChat", (Remote) server);
            System.out.println("Servidor online.");
        } catch (Exception e) {
            System.out.println("Trouble: " + e.toString());
        }
    }

    public static void main(String args[]) {
        new ChatConServidor();
    }
}
