import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

	// public static ArrayList<ClientConnection> clientList = new
	// ArrayList<ClientConnection>();
	// public static Queue<String> inputList = new LinkedList<String>();
	// public static Queue<String> outputList = new LinkedList<String>();

	public static ArrayList<ClientSocket> clientSockets = new ArrayList<ClientSocket>();
	public static ArrayList<Thread> clientThreads = new ArrayList<Thread>();
	public static ArrayList<Integer> clientIDs = new ArrayList<Integer>();
	public static Queue<String> messages = new LinkedList<String>();

	//public static ArrayList<String> messages = new ArrayList<String>();

	public static int con = 0;

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(6008);
			System.out.println("Server started at " + new Date() + '\n');
			// new Thread(new Connections(servSock)).start();
			// int con = 0;
			Thread mh = new Thread(new MessageHandler());
			mh.start();

			while (true) {
				con++;

				Socket clientSocket = serverSocket.accept();
				ClientSocket client = new ClientSocket(clientSocket, con);
				System.out.println("Connection " + con + " has been connected");
				// System.out.print(clientSocket);
				clientSockets.add(client);
				new Thread(client).start();
				// threadHandler.handleThread(new Thread(new ClientSocket(clientSocket, con)));

				clientIDs.add(con);
			}

			// PrintWriter pout = null;//new PrintWriter(socket.getOutputStream(), true);

			/*
			 * while (true) { while (inputList.peek() != null){ for(int i = 0; i <
			 * clientList.size() - 1; i++) { PrintWriter pw = new
			 * PrintWriter((clientList.get(i).getSocket()).getOutputStream(), true);
			 * pw.println(inputList.poll()); } } }
			 */

		} catch (IOException ioe) {
			//System.err.println(ioe);
			System.out.println(1);
		}
	}

	public static void addMessages(String message) {
		messages.add(message);
		//System.out.println(messages.poll());
		
	}

	public static class ClientSocket implements Runnable {

		Socket clientSocket = null;
		int clientID = 0;
		String name = "";
		PrintWriter pout = null;

		public ClientSocket(Socket clientSocket, int clientID) throws IOException {
			this.clientSocket = clientSocket;
			this.clientID = clientID;
			pout = new PrintWriter(clientSocket.getOutputStream(), true);
		}

		public PrintWriter getPrintWriter() {
			return pout;
		}

		public Socket getSocket() {
			return this.clientSocket;
		}
		
		public void sendMessage(String message) {
			pout.println(message);
			pout.flush();
		}

		@Override
		public void run() {
			// print message for name
			try {
				InputStream in = clientSocket.getInputStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in));
				// PrintWriter pout = new PrintWriter(clientSocket.getOutputStream(), true);

				sendMessage("Please enter a name:");
				pout.flush();
				this.name = bin.readLine();
				addMessages("<" + name + "> has entered the Matrix...");

				pout.println("****Welcome to the Matrix****");
				pout.flush();
				
				while(true) {
					String message = "<" + name + ">: " + bin.readLine();
					addMessages(message);
				}

			} catch (IOException e) {
				//clientSockets.remove(clientSocket);
				//clientIDs.remove(clientID);
				String disconnect = name + " has left the chat";
				System.out.println(disconnect);
				addMessages(disconnect);
				//e.printStackTrace();
			}
		}
	}

	public static class MessageHandler implements Runnable {

		// PrintWriter pout = new PrintWriter(clientSocket.getOutputStream(), true);

		@Override
		public void run() {
			
			while(true) {
				//System.out.println("I am here");
				if(!messages.isEmpty()) {
					//System.out.println("yo yo yo");
					String line = messages.poll();
					//System.out.println(line);
					for (ClientSocket client : clientSockets) {
						PrintWriter pw = null;
						client.sendMessage(line);
						/*try {
							
							pw = new PrintWriter((client.getSocket()).getOutputStream(), true);
							pw.println(line);
							client.getPrintWriter().flush();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}*/
					}
					//messages.remove(0);
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					
					System.out.println(2);
				}
			}
			
			
			
			/*while (true) {
				if (messages.size() > 0) {
					line = messages.get(0);
					while (line != null) {// .peek() != null) {
						// String line = messages.get(index)
						// String line = messages.poll();
						System.out.println(line);
						for (ClientSocket client : clientSockets) {
							PrintWriter pw;
							try {
								pw = new PrintWriter((client.getSocket()).getOutputStream(), true);
								pw.println(line);
								client.getPrintWriter().flush();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					}
				}
			}*/
		}
	}
}
