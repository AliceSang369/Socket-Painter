/**
 * Yuwen Sang
 * March 14th, 2020
 * COMP 2355 - Project 3 - SocketPainter
 */
package yuwen.sang;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;


public class Hub {
	//Save each client's name and his/her related socket. Key = (String)name
	Hashtable hashtable = new Hashtable();
	//Save the hub account of each painter(client). The account include input/output stream, used for hub 
	//  to send information to/receive information from each painter(client)
	// **Each painter also has a local account to send information to/receive information from the hub
	Hashtable hashtable2 = new Hashtable();
	Account serveAccount;
	Account serveAccount2;
	Socket socket;
	Socket socket2;
	ArrayList<PaintingPrimitive> pntpmts = new ArrayList<>();
	ArrayList<String> strings = new ArrayList<>();
	
	/***MAIN METHOD***/
	public static void main(String[] args) {
		Hub hub = new Hub();
		hub.startServerSocket();	
	}/***MAIN METHOD End***/
	
	//Private method: Sharing Painting to other painters
	private void paintingSharing(PaintingPrimitive newPrimitive, Socket self) {
		Enumeration enumeration = hashtable.keys();
//		System.out.println("Currently, we have " + hashtable.size() + " painter in this hub");
		while(enumeration.hasMoreElements()) {
			String n = (String)enumeration.nextElement();
			socket2 = (Socket)hashtable.get(n);
			serveAccount = (Account)hashtable2.get(n);
			if(socket2 != self) {//Watch out double drawing here. if the socket is not itself, then sharing.
				try {
					serveAccount.getOOS().writeObject(newPrimitive);
				}catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}//paintingSharing end
	
	//Private method: sharing text message
	private void textSharing(String str, Socket self) {
		Enumeration enumeration = hashtable.keys();
		while(enumeration.hasMoreElements()) {
			String n = (String)enumeration.nextElement();
			socket2 = (Socket)hashtable.get(n);
			serveAccount2 = (Account)hashtable2.get(n);
			if(socket2 != self) {
				try {
					String newMessage = str;
					serveAccount2.getOOS().writeObject(newMessage);//share new message to everyone
				} catch (IOException e) {
                    e.printStackTrace();
                }
			}
		}
	}
	
	//****************************************************************//
	//ServerSocket created here, Always opening to wait new painter. The core code used in the main method.
	private void startServerSocket() {
		try {
			ServerSocket serverSocket = new ServerSocket(19999);
			while(true) {//always opening to waiting new painter come in 
				socket = serverSocket.accept();
				//when accept a new socket, generate a new thread to dealing the new socket's action
				HubService hService = new HubService(socket);
				Thread th = new Thread(hService);
				th.start();
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	//**************************************************************//
	
	//private HubService Thread class, inserted in the startServerSocket method to start a new thread when accept new painter
	private class HubService implements Runnable{
		Socket socket = null;
		String name;
		Account serverAccount;
		
		public HubService(Socket socket) {//*****************HubService Constructor
			this.socket = socket;
			try {
				ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				serverAccount = new Account();
				serverAccount.setOOS(oos);
				serverAccount.setOIS(ois);
				
				//send new painter the current paintingPrimitive arraylist record
				serverAccount.getOOS().writeObject(pntpmts);
				serverAccount.getOOS().writeObject(strings);
				//receive the new painter's name from the painter
				name = (String)serverAccount.getOIS().readObject();
				serverAccount.setName(name);
				hashtable.put(name, socket);//add new socket hashtable element
				hashtable2.put(name, serverAccount);//add new account hashtable element				
			}catch (IOException e) {
                e.printStackTrace();
            } 
			catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}//HubService Constructor end**************
		@Override//********************************THREAD************************//
		public void run() {
			synchronized(this){
				try {
					String newComer = "[" + name + "] comes to the hub.\n";
					System.out.println("[" + name + "] comes to the hub.");
					textSharing(newComer, socket);
					while(true) {		
						Object obj = serverAccount.getOIS().readObject();//read from painter
						if(obj instanceof PaintingPrimitive) {
							PaintingPrimitive newPrimitive = (PaintingPrimitive) obj;
							if(newPrimitive != null) {
								addPrimitive(newPrimitive);//add newPrimitive to hub pntpmts arraylist
								paintingSharing(newPrimitive, socket);
							}
						}else if(obj instanceof String) {
							String newString = (String)obj;
							if(newString.equals("s4524gda")) {
								String st = "[" + name + "] leaves the hub.\n";
								System.out.println("[" + name + "] leaves the hub.");
								strings.add(st);
								textSharing(st, socket);
								//Close the socket and related ois, oos
								serverAccount.getOIS().close();
								serverAccount.getOOS().close();
								socket.close();
								hashtable.remove(name);//remove it from the hashtable
								hashtable2.remove(name);
								break;
							}else {
								strings.add(newString);
								textSharing(newString, socket);
							}
						}	
					}
				}catch (IOException e) {
					e.printStackTrace();
            	} catch (ClassNotFoundException e) {
            		e.printStackTrace();
				}
			}
		}//run end
	}//private class end
	
	
	public void addPrimitive(PaintingPrimitive newPrimitive) {
		pntpmts.add(newPrimitive);
	}
	

}
