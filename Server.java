import java.net.*;
import java.io.*;
import java.util.*;

public class Server {

	public static ArrayList<ClientSocket> clientSockets = new ArrayList<ClientSocket>();
	public static Queue<String> messages = new LinkedList<String>();
	public static Queue<String> dMessages = new LinkedList<String>();

	public static void main(String[] args) {

		int con = 0;
		try {

			ServerSocket serverSocket = new ServerSocket(6008);
			System.out.println("Server started at " + new Date() + '\n');

			Thread mh = new Thread(new MessageHandler());
			mh.start();

			while (true) {
				con++;

				Socket clientSocket = serverSocket.accept();
				ClientSocket client = new ClientSocket(clientSocket);

				System.out.println("Connection " + con + " has been connected");

				clientSockets.add(client);
				new Thread(client).start();
			}

		} catch (IOException ioe) {

			System.out.println(1);

		}
	}

	public static void addMessages(String message) {
		messages.add(message);
	}

	public static void addDMessages(String name, String message) {
		dMessages.add(name);
		dMessages.add(message);
	}

	public static class ClientSocket implements Runnable {

		Socket clientSocket = null;
		String name = "";
		PrintWriter pout = null;

		public ClientSocket(Socket clientSocket) throws IOException {
			this.clientSocket = clientSocket;
			pout = new PrintWriter(clientSocket.getOutputStream(), true);
		}

		public String getName() {
			return name;
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

			try {

				InputStream in = clientSocket.getInputStream();
				BufferedReader bin = new BufferedReader(new InputStreamReader(in));

				sendMessage("Please enter a username:");
				pout.flush();
				this.name = bin.readLine();
				addMessages("<" + name + "> has entered the lobby...");

				pout.println("****Welcome to the lobby****");
				pout.flush();

				while (true) {

					String userName = "<" + name + ">: ";
					String message = bin.readLine();

					if (message.contains("[") && message.contains("]")) {
						addDMessages(message.substring(message.indexOf("[") + 1, message.indexOf("]")),
								"DM" + userName + message.substring(message.indexOf("]") + 1));
					} else {
						addMessages(userName + message);
					}
				}

			} catch (IOException e) {

				String disconnect = name + " has left the chat";
				System.out.println(disconnect);
				addMessages(disconnect);

			}
		}
	}

	public static class MessageHandler implements Runnable {

		@Override
		public void run() {

			while (true) {

				if (!messages.isEmpty()) {

					String line = messages.poll();

					for (ClientSocket client : clientSockets) {

						PrintWriter pw = null;
						client.sendMessage(line);

					}

				} else if (!dMessages.isEmpty()) {

					String name = dMessages.poll();
					String message = dMessages.poll();

					for (ClientSocket client : clientSockets) {

						if (client.getName().equals(name)) {
							client.sendMessage(message);
						}
					}
				}
				try {

					Thread.sleep(200);

				} catch (InterruptedException e) {

					System.out.println("Error... but continuing.");

				}
			}
		}
	}
}
