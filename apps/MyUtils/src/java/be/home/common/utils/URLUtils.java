package be.home.common.utils;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class URLUtils {

	public static void dump(String URLName) {
		try {
			DataInputStream di = null;
			byte[] b = new byte[1];

			// PROXY
			Properties systemSettings = System.getProperties();
			systemSettings.put("http.proxyHost", "proxy.pxpost.netpost");
			systemSettings.put("http.proxyPort", "8080");

			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("POST\\ghyssee",
							"XXX".toCharArray());
				}
			});

			URL u = new URL(URLName);
			HttpURLConnection con = (HttpURLConnection) u.openConnection();
			di = new DataInputStream(con.getInputStream());
			while (-1 != di.read(b, 0, 1)) {
				System.out.print(new String(b));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> dump2(String URLName)
			throws IOException {

		List<String> lines = new ArrayList <String> ();
		BufferedReader di = null;

		// PROXY
		Properties systemSettings = System.getProperties();
		systemSettings.put("http.proxyHost", "proxy.pxpost.netpost");
		systemSettings.put("http.proxyPort", "8080");

		URL u = new URL(URLName);
		HttpURLConnection con = (HttpURLConnection) u.openConnection();

		//
		// it's not the greatest idea to use a sun.misc.* class
		// Sun strongly advises not to use them since they can
		// change or go away in a future release so beware.
		//
		sun.misc.BASE64Encoder encoder = new sun.misc.BASE64Encoder();
		String encodedUserPwd = encoder.encode("POST\\ghyssee:XXX"
				.getBytes());
		con
				.setRequestProperty("Proxy-Authorization", "Basic "
						+ encodedUserPwd);
		// PROXY ----------

		di = new BufferedReader(new InputStreamReader(con.getInputStream()));
		// while(-1 != di.read(b,0,1)) {
		String line = null;
		line = di.readLine();
		while (line != null) {

			lines.add(line);
			line = di.readLine();
		}
		con.disconnect();
		di.close();
		return lines;
	}
}