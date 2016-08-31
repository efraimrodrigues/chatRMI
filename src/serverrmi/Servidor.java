package serverrmi;

import chatrmi.ServidorChat;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Servidor {

    public Servidor() {
        try {
            //System.setSecurityManager(new RMISecurityManager());
            //if (System.getSecurityManager() == null) {
            //    System.setSecurityManager(new SecurityManager());
            //}
            Registry registry = LocateRegistry.createRegistry(1098);
            ServidorChat server = new ServidorChatImpl();

            //ServidorChat inter = (ServidorChat) UnicastRemoteObject.exportObject (server,1098);
            Naming.bind("rmi://127.0.0.1:1098/ServidorChat", server);
            System.out.println("Servidor online.");
        } catch (Exception e) {
            System.out.println("Trouble: " + e.toString());
        }
    }

    public static void main(String args[]) {
        new Servidor();
    }
}
