import java.net.*;
import java.io.*;
import java.util.*;

//import Server.ClientSocket;

public class Client {

	public static Socket sock = null;
	public static void main(String[] args) throws InterruptedException, IOException {
		
		try {
			// make connection to server socket
			Socket socket = new Socket("127.0.0.1", 6008);
			sock = socket;

			Scanner scan = new Scanner(System.in);
			InputStream in = socket.getInputStream();
			//Server server = new Server();
			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
			PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
			ClientListener clientListener = new ClientListener(socket, bin);
			new Thread(clientListener).start();

			pout.println(scan.nextLine());
			//scan.nextLine();

			Thread.sleep(200);

			while (true) {

				String message = "";
				System.out.println("Please enter a 1 for general message or 2 for direct message:");
				if (scan.hasNextInt()) {
					int type = scan.nextInt();
					scan.nextLine();

					switch (type) {
					case 1:
						System.out.println("Enter general message:");
						message = scan.nextLine();
						pout.println(message);
						// generalMessage(message, pout);
						break;

					case 2:
						System.out.println("Enter direct message:");
						pout.println(scan.nextLine());
						// directMessage(message);
						break;
						
					default:
						break;
					}
					Thread.sleep(700);
				}
			}

			// close the socket connection
			// socket.close();
		} catch (IOException ioe) {
			sock.close();
			System.err.println(ioe);
			System.exit(0);
		}
	}

	public static class ClientListener implements Runnable {

		Socket socket = null;
		BufferedReader bin = null;

		public ClientListener(Socket socket, BufferedReader bin) {
			this.socket = socket;
			this.bin = bin;
		}

		@Override
		public void run() {
			while (true) {
				try {
					System.out.println(bin.readLine());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					System.out.println("Error, Closing");
					System.exit(0);
				}
			}
		}

	}
}
