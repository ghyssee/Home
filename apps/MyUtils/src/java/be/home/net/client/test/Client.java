package be.home.net.client.test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// declaration section:
		// smtpClient: our client socket
		// os: output stream
		// is: input stream
		Socket clientSocket = null;
		DataOutputStream os = null;
		DataInputStream is = null;
		// Initialization section:
		// Try to open a socket on port 25
		// Try to open input and output streams
		try {
			clientSocket = new Socket("W200101", 9999);
			os = new DataOutputStream(clientSocket.getOutputStream());
			is = new DataInputStream(clientSocket.getInputStream());
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: hostname");
		} catch (IOException e) {
			System.err
					.println("Couldn't get I/O for the connection to: hostname");
		}
		// If everything has been initialized then we want to write some data
		// to the socket we have opened a connection to on port xxxx
		if (clientSocket != null && os != null && is != null) {
			try {
				os.writeBytes("DIT IS EEN TEST");
				// clean up:
				// close the output stream
				// close the input stream
				// close the socket
				os.close();
				is.close();
				clientSocket.close();
			} catch (UnknownHostException e) {
				System.err.println("Trying to connect to unknown host: " + e);
			} catch (IOException e) {
				System.err.println("IOException:  " + e);
			}
		}
	}

}
