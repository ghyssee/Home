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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import be.home.common.utils.CSVReader;
import be.home.common.utils.CSVReaderImpl;

public class SQLInsert {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//ConvertTxtFile();
		ReadCSV("/Temp/eShopBankTransfers_Orderlines.txt", "SHO_ORDERLINE", 15);
		ReadCSV("/Temp/eShopBankTransfers.txt", "SHO_ORDER", 61);
		
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

	private static void ReadCSV(String file, String tableName, int columns)  throws IOException{
		File filename = new File(file);
		PrintWriter writer = new PrintWriter(file + ".SQL", "UTF-8");
		CSVReader input = new CSVReaderImpl(filename);
		List headers = null;
		input.setdelimiterChar(';');
		while(!input.isEof()){
			//System.out.println("Line Number :" + input.getLineNumber());
			String insert = "";
			List fields = input.readln();
			if (fields != null){
				if (fields.size() != columns){
					System.out.println(fields.size() + " / " + fields);
					continue;
				}
				if (input.getLineNumber() == 1){
					headers = fields;
				}
				else {
					insert = getColumnNames(tableName, headers);
					for (int i =0; i < fields.size(); i++){
						String field = (String) fields.get(i);
						String header = (String)headers.get(i);
						insert += i == 0 ? "" : ",";
						if (field.equals("null")){
							insert += field;
						}
						else if (isNumber((String)headers.get(i))){
							insert += checkField(header, field);
						}
						else if (isValidDate(field)){
							//System.out.println("Valid Date :" + field);
							insert += "TO_DATE('" + getSQLDate(field) + "','/DD/MM/YYYY HH24:MI:SS')";
						}
						else {
							insert += "'" + checkField(header, field) + "'";
						}
					}
					insert += ");";
					System.out.println(insert);
					writer.println(insert);
				}
			}
		}
		writer.close();
	}
	
	public static String checkField(String header, String field){
		String returnValue = field;
		if (header.toUpperCase().equals("@NUMBER@ORDER_ID")){
			long id = Long.parseLong(field);
			id += 161304;
			returnValue = Long.toString(id);
		}
		else if (header.toUpperCase().equals("@NUMBER@ORDERLINE_ID")){
			returnValue = "SHO_ORDERLINE_SEQ.NEXTVAL";
		}
		else if (header.toUpperCase().equals("PAYMENT_PROPOSAL_FILE_ID")){
			returnValue = "";
		}
		if (header.startsWith("@NUMBER@")){
			returnValue = returnValue.replaceAll(",", ".");
		}
		else {
			returnValue = returnValue.replaceAll("'", "''");
			returnValue = returnValue.replaceAll("&", " ");
			returnValue = returnValue.replaceAll(":", "-");
		}
		return returnValue;
	}
	
	public static String getColumnNames(String tableName, List headers){
		
		String insert = "INSERT INTO " + tableName + "(";
		for (int i =0; i < headers.size(); i++){
			String header = (String) headers.get(i);
			header = header.replaceAll("@NUMBER@", "");
			insert += (i==0 ? "" : ",") + header;
		}
		insert += ") VALUES (";
		return insert; 
	}

	public static boolean isNumber(String columnName) {
		if (columnName.startsWith("@NUMBER@")){
			return true;
		}
		return false;
	}
	public static boolean isValidDate(String dateString) {
	    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    try {
	        Date date = df.parse(dateString);
	        return true;
	    } catch (ParseException e) {
	        return false;
	    }
	}

	public static String getSQLDate(String dateString) {
	    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	    try {
	        Date date = df.parse(dateString);
	        return df.format(date);
	    } catch (ParseException e) {
	        return null;
	    }
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
