package serverrmi;

import chatrmi.ServidorChat;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;

public class Servidor {

    public Servidor() {
        try {
            //System.setSecurityManager(new RMISecurityManager());
            //if (System.getSecurityManager() == null) {
            //    System.setSecurityManager(new SecurityManager());
            //}
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");
            
            LocateRegistry.createRegistry(1099);
            
            ServidorChat server = new ServidorChatImpl();

            //ServidorChat inter = (ServidorChat) UnicastRemoteObject.exportObject (server,1098);
            Naming.bind("ServidorChat", (Remote) server);
            System.out.println("Servidor online.");
        } catch (Exception e) {
            System.out.println("Trouble: " + e.toString());
        }
    }

    public static void main(String args[]) {
        new Servidor();
    }
}
