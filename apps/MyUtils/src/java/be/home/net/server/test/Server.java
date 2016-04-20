package be.home.net.server.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// declaration section:
		// declare a server socket and a client socket for the server
		// declare an input and an output stream
		ServerSocket echoServer = null;
		String line;
		PrintStream os = null;
		Socket clientSocket = null;
		// Try to open a server socket on port 9999
		// Note that we can't choose a port less than 1023 if we are not
		// privileged users (root)
		try {
			echoServer = new ServerSocket(9999);
		} catch (IOException e) {
			System.out.println(e);
		}
		// Create a socket object from the ServerSocket to listen and accept
		// connections.
		// Open input and output streams
		try {
			clientSocket = echoServer.accept();
			BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			//is = new DataInputStream(clientSocket.getInputStream());
			os = new PrintStream(clientSocket.getOutputStream());
			// As long as we receive data, echo that data back to the client.
			while (true) {
				line = br.readLine();
				if (line != null){
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
		if (os != null){
			os.close();
		}
	}

}
