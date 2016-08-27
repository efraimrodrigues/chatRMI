package chatrmi;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Servidor {
	public Servidor(){
		try {
			Registry registry = LocateRegistry.createRegistry(1098);
			ServidorChat server = new ServidorChatImpl();
			Naming.rebind("rmi://127.0.0.1:1098/ServidorChat",server);
                        System.out.println("Servidor online.");
		} catch (Exception e){
		System.out.println("Trouble: "+e.toString());
		}
	}
	public static void main (String args[]){
		new Servidor();
	}
}