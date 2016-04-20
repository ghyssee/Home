package be.home.test.main;

import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;

import be.home.common.utils.URLUtils;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public class MainTest {

		int x = 5;
		    public static void main(String s[])
		   {

		    	//Menu file = new Menu("File");
		    	//file.add (new MenuItem ("Print"));
		    	Character y = Character.valueOf('\u221a');
		    	System.out.println(y);
		    	
		   }
	
	
	}
	
		/*
		for (Locale locale : Locale.getAvailableLocales()) {
			final String contry = locale.getDisplayCountry();
			if (contry.length() > 0) {
				System.out.println("Country = " + contry + ". ISO code: "
						+ locale.getISO3Country());
			}
		}
		
		String[] countries = Locale.getISOCountries();
		for (int i=0; i < countries.length; i++){
			System.out.println(countries[i]);*/
		
		
		/*
		 * try { URLUtils.dump2("http://www.google.be"); } catch (IOException
		 * e1) { // TODO Auto-generated catch block e1.printStackTrace(); } //
		 * System.out.println("**************"); List<String> lines = null; try {
		 * lines = URLUtils .dump2("http://www.phazeddl.com"); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } for (String line : lines) {
		 * System.out.println(line); }
		 */
