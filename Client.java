import java.net.*;
import java.io.*;
import java.util.*;

public class Client {

	public static Socket sock = null;

	public static void main(String[] args) throws InterruptedException, IOException {

		try {
			// make connection to server socket
			Socket socket = new Socket("127.0.0.1", 6008);
			sock = socket;

			Scanner scan = new Scanner(System.in);
			InputStream in = socket.getInputStream();
			BufferedReader bin = new BufferedReader(new InputStreamReader(in));
			PrintWriter pout = new PrintWriter(socket.getOutputStream(), true);
			ClientListener clientListener = new ClientListener(socket, bin);

			new Thread(clientListener).start();

			pout.println(scan.nextLine()); //sends username

			Thread.sleep(200);
			System.out.println("Surround Username with [ and ] to send DM \\nExample( [Zack] Hello )");

			while (true) {
				
				//***
				System.out.println("Type your message:");
				pout.println(scan.nextLine());

				Thread.sleep(700);
			}

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

					System.out.println("Server Disconnected, Closing");
					System.exit(0);
				}
			}
		}
	}
}
