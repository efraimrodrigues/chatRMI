##An end-to-end encryption application using Java RMI

###Intro
Let's take the typical Alice and Bob scenario for instance. In this scenario Bob wants to communicate with Alice in a secure way. A client/server application is enough to satisfy Alice's and Bob's needs. However, end to end encryption will be used to ensure a man-in-the-middle atack is not feasible. So, this will be a real time chat which will have its messages centralized in the server.  The centralized messages must be encrypted in a way only the clients are able to decrypher them. Sender and receiver information will be encrypted too.

Server and clients are set with a symmetric key so they can safely exchange identity and asynmmetric keys. As a result of this it will be impossible for a sniffer to identify clients and public keys. To address the process of identifying and communication between users and servers, each user will generate a pair of assymetric keys locally and will share their public keys with the server service. It is important to mention that this communication will be done with a symmetric encryption technique. So, even if somehow a sniffer puts hands on the symmetric key, this sniffer might be able to decrypt one's public key. But will never be able to decrypt a message because the end user is the only one able to do that with its private key (which was never shared).

>Symmetric Encryption Technique: AES
>Assynmetric Encryption Technique: RSA

###Java RMI
All the communication is done over the Java RMI interface. RMI stands for Remote Method Invocation. This programming interface allows you to invoce remote classes' methods.  Most of Java RMI applications implement two applications, server and client. The server application will hold a server object and allow clients to use its methods. Client applications are required to have access to the objects interface, so they are oblivious to the way these methods are implemented. 

Java RMI classes must extends from the <code>java.rmi.Remote</code> class.

##Implementation
***
###Serverside
As stated before, Java RMI applications implement a client and server. Before proceeding to the central object of the server, let's lay eyes on <code>ChatConUserInterface</code>.  This interface represents a user.

	public interface ChatConUserInterface extends Remote,Serializable  {
		abstract protected void enviarMensagem(String mensagem) throws RemoteException;
		abstract protected ArrayList<String> getMensagens() throws RemoteException;
		abstract protected String getPublicKey() throws RemoteException;
		abstract protected Integer getContador() throws RemoteException;
	}
	
1.<code>abstract void enviarMensagem(String mensagem) throws RemoteException;</code>
	This method gets and adds a message to the user's mailbox or buffer represented by a list of strings. These strings shall be consumed by the client application. 
2. <code>abstract ArrayList<String> getMensagens() throws RemoteException;</code>
	This will simply return the buffer of messages.	
3. <code>abstract String getPublicKey() throws RemoteException;</code>
	User's public key will be retreived by other user's for encryption
4. <code>abstract Integer getContador() throws RemoteException;</code>
	Returns the current size of the buffer

In addition,the central object of the server is the class ChatCon which is an implementation of ChatConInterface as follows:

	public interface ChatConInterface extends Remote {
		abstract public void enviarMensagem(String user, String mensagem) throws RemoteException;
		abstract public ChatConUserInterface adicionaUsuario(String username, String publicKey) throws RemoteException;
		abstract public void removeUsuario(String username) throws RemoteException;
		abstract public ArrayList<String> getUsuarios() throws RemoteException;
		abstract public String getPublicKey(String username) throws RemoteException;
		abstract public boolean isOnline(String username) throws RemoteException;
	}
	
This interface supports simple operations:

1.<code>abstract public void enviarMensagem(String user, String mensagem) throws RemoteException;</code>
	Gets the username and encrypted message (receiver's private key) and forwards to the user
2. <code>abstract public ChatConUserInterface adicionaUsuario(String username, String publicKey) throws RemoteException;</code>
	Instantiates and returns a <code>ChatConUser</code>. Used for a user to login
3. <code>abstract public void removeUsuario(String username) throws RemoteException;</code>
	Removes the specific user from the list of users. It's used to logout
4. <code>abstract public ArrayList<String> getUsuarios() throws RemoteException;</code>
	Returns the list of logged in users.
5. <code>abstract public String getPublicKey(String username) throws RemoteException;</code>
	Returns the public key of a certain user
6. <code>abstract public boolean isOnline(String username) throws RemoteException;</code>
	Simply returns true if user is online, false otherwise
	
###Clientside
Perhaps the client side application is the simplest one because it will only use what is already implemented. For this application, it is required that <code>ChatConInterface</code> and <code>ChatConUserInterface</code> are visible. 
