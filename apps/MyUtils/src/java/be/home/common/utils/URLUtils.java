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
}