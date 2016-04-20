package be.home.picmgt.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class ReadTextFile {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//ConvertTxtFile();
		ReadAmounts();
		
	}
		
		
	private static void ConvertTxtFile()  throws IOException{
		File filename = new File("/tmp/woordenbot.txt");
		BufferedReader in = new BufferedReader(new FileReader(filename));
		PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/tmp/woordentmp.txt")));
		String line = null;
		do {
			line = in.readLine();
			if (line != null && line.length() > 2 && line.length() < 7){
				System.out.println(line);
				out.write(line + "\r\n");
			}
		}
		while (line != null);
		in.close();
	}

	private static void ReadAmounts()  throws IOException{
		File filename = new File("/Temp/eshop/ESH20120814A001/Amount.txt");
		BufferedReader in = new BufferedReader(new FileReader(filename));
		BigDecimal total = new BigDecimal("0.00");
		total.setScale(2, BigDecimal.ROUND_HALF_UP);
		String line = null;
		do {
			line = in.readLine();
			if (line != null){
				//Double amount = new Double(line);
				BigDecimal amount = new BigDecimal(line).setScale(2, BigDecimal.ROUND_HALF_UP);
                //BigDecimal a = new BigDecimal(amount);
				total = total.add(amount);
				System.out.println("Amount = " + amount);
				System.out.println("total = " + total.doubleValue());
				double tst = total.doubleValue();
				System.out.println("tst = " + tst);
				System.out.println("---------------------------------");
				//total += a.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		}
		while (line != null);
		in.close();
	}

	private static void ReadAmounts2()  throws IOException{
		File filename = new File("/Temp/eshop/ESH20120814A001/Amount.txt");
		BufferedReader in = new BufferedReader(new FileReader(filename));
		//double total = 0.0;
		Double total = new Double ("0.0");
		String line = null;
		do {
			line = in.readLine();
			if (line != null){
				Double amount = new Double(line);
				new Double(total.doubleValue() + amount);
	            total = new Double(total.doubleValue() + amount);
				System.out.println("Amount = " + amount);
				System.out.println("total = " + total);
				System.out.println("---------------------------------");
				//total += a.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
		}
		while (line != null);
		in.close();
	}


}
