package be.home.picmgt.main;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

import jonelo.jacksum.algorithm.Cksum;

import org.apache.commons.lang3.StringUtils;

public class HelloWorld {

	public static void main(String args[]) throws IOException {
		HelloWorld instance = new HelloWorld();
//		instance.initialize(args);
		//instance.generateOgm("10364005");
//		instance.generateOgm("97");
		//instance.decodeOrderId("001/0363/99328");
		//instance.decodeOrderId("000/0000/09797");
		long tst = instance.checksumInputStream("C:\\Projects\\pmt\\DEV_V0006.00.00\\db\\V0006.00.00\\Scripts\\FILE_OBJ.tpb");
        System.out.println(tst);
    	CRC32 crc = new CRC32();	
        System.out.println(crc.getValue());
        Cksum ck = new Cksum();
        ck.readFile("C:\\Projects\\pmt\\DEV_V0006.00.00\\db\\V0006.00.00\\Scripts\\FILE_OBJ.tpb");
        System.out.println(ck.getValue());



	}
	
	    private static long doChecksum(String fileName) {

	        long checksum = 0;
	    	try {

	            CheckedInputStream cis = null;
	            long fileSize = 0;
	            try {
	                // Computer CRC32 checksum
	                cis = new CheckedInputStream(
	                        new FileInputStream(fileName), new CRC32());

	                fileSize = new File(fileName).length();
	               
	            } catch (FileNotFoundException e) {
	                System.err.println("File not found.");
	                System.exit(1);
	            }

	            byte[] buf = new byte[128];
	            while(cis.read(buf) >= 0) {
	            }

	            checksum = cis.getChecksum().getValue();
	            System.out.println(checksum + " " + fileSize + " " + fileName);

	        } catch (IOException e) {
	            e.printStackTrace();
	            System.exit(1);
	        }
	        
	        return checksum;


	    }

	    private static long doChecksumV2(String fileName) {

	        long checksum = 0;
	        byte[] result = read(fileName);
	        Checksum checksum2 = new CRC32();
	        checksum2.update(result, 0, result.length);
	        checksum = checksum2.getValue();
            System.out.println(result.length);
            System.out.println(checksum);
	        
	        
	        return checksum;


	    }
	
	    private static long checksumInputStream(String filepath) throws IOException {
	    		 
	    		  InputStream inputStreamn = new FileInputStream(filepath);
	    	
	    	CRC32 crc = new CRC32();	
	    	int cnt;
	    	
	    	
	    	
	    	while ((cnt = inputStreamn.read()) != -1) {	    
	    		crc.update(cnt);
	    		}
	    	crc.update(8905);
	    		
	    		return crc.getValue();
	    }
	    
	    /** Read the given binary file, and return its contents as a byte array.*/ 
	    private static byte[] read(String aInputFileName){
	      File file = new File(aInputFileName);
	      byte[] result = new byte[(int)file.length()];
	      try {
	        InputStream input = null;
	        try {
	          int totalBytesRead = 0;
	          input = new BufferedInputStream(new FileInputStream(file));
	          while(totalBytesRead < result.length){
	            int bytesRemaining = result.length - totalBytesRead;
	            //input.read() returns -1, 0, or more :
	            int bytesRead = input.read(result, totalBytesRead, bytesRemaining); 
	            if (bytesRead > 0){
	              totalBytesRead = totalBytesRead + bytesRead;
	            }
	          }
	          /*
	           the above style is a bit tricky: it places bytes into the 'result' array; 
	           'result' is an output parameter;
	           the while loop usually has a single iteration only.
	          */
	        }
	        finally {
	          input.close();
	        }
	      }
	      catch (FileNotFoundException ex) {
	      }
	      catch (IOException ex) {
	      }
	      return result;
	    }


	public void roundProblem(){
	      double tmp = 0.785; 
	       BigDecimal a = new BigDecimal(tmp);
	       a = a.setScale(2, BigDecimal.ROUND_HALF_DOWN);
	 
	       
	       System.out.println("tmp = " + tmp);
	       System.out.println("Amount = " + a.doubleValue());
	  		
	}
	
	public String generateOgm(final String number) {
		StringBuffer str = new StringBuffer();
		//calculation the ogm
		String ogmValueStr = StringUtils.leftPad(number, 12, "0");

		long ogmValue = Long.parseLong(ogmValueStr);

		int modulo97 = (int) (ogmValue % 97);
		if (modulo97 == 0) {
			modulo97 = 97;
		}
		//formatting the OGM
		str.append(ogmValueStr);

		if (modulo97 < 10) {
			str.append(0);
		}
		str.append(modulo97);

		//str.insert(3, '/');
		//str.insert(8, '/');

		System.out.println(str.toString());

		return str.toString();
	}

	public String decodeOrderId(final String number) {
		
		if (number == null || number.length() < 2){
			return number;
		}
		String str = number.replaceAll("/", ""); 
	    str = str.substring(0, str.length()-2); // remove modulo 97 numbers
	    str = str.replaceFirst("^0+(?!$)", ""); // remove leading zeros
		
		System.out.println(str.toString());
		
		return str.toString();
	}
	
	
	public void run5(String args[]){
		
		float price = 77f;
		DecimalFormat dec = new DecimalFormat("##########.#####");
		//System.out.println(new Float(price).toString());

		Float tst = new Float(price);
		MathContext mc = new MathContext(5, RoundingMode.HALF_EVEN);
		BigDecimal bd = new BigDecimal(price, mc);
		//System.out.printf("%5f", price);
		//System.out.println(tst.toString());
		//System.out.println(dec.format(price));
		System.out.println(dec.format(bd).replace(',', '.'));
		//System.out.println(new Float(price).toString());
		//System.out.println(bd.stripTrailingZeros().toString());
	}
	
	public void run4(String args[]){
		
		// try parsing the date in dd/MM/yyyy format
		SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
		Date before = null;
		try {
			before = parser.parse("2008-11-13T15:06:26+0100");
			System.out.println("Date = " + before);
		} catch (ParseException ex2) {
			// try parsing the date in yyyyMMdd format
			System.out.println("problem parsing");
		}
	}
	
	public void run2(String args[]){
		File outputFile = new File("c:/temp/test.txt");
		if (outputFile.exists()) {
			System.out.println("File already exist and will be overwritten : " + outputFile);
		}
		OutputStream out = null;
		try {
			out = new FileOutputStream(outputFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    if (out != null) {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	public void run3(String args[]){
		
		String tmp = "";
		String[] tmpArray = tmp.split(" - ");
		System.out.println(tmpArray.length);
		for (int i=0; i < tmpArray.length; i++){
			System.out.println(tmpArray[i]);
		}
	}
	
	public void run(String args[]) {

		Hashtable params = new Hashtable();
		for (int i = 0; i < args.length; i++) {
			String param = args[i];
			if (param != null) {
				System.out.println(param);
				String key = "";
				String value = "";
				if (param.startsWith("-D")) {
					StringTokenizer st = new StringTokenizer(param.substring(2), "=");
					boolean first = true;
					while (st.hasMoreTokens()) {
						String token = st.nextToken(); 
						if (first) {
							key = token;
							System.out.println(key);
							first = false;
						}
						else {
							value = value + token; 
						}
					}
					params.put(key, value);
				}
				else {
					System.out.println("invalid param " + param);
				}
			}
		}
		Enumeration e = params.keys();
		for (; e.hasMoreElements() ;) {
	        String key = (String) e.nextElement(); 
			System.out.println("key = " + key);
	         System.out.println("value = " + params.get(key));
	     }
		
		String line = unescape("DownloadPath=E:/nbpro/DOWNLOAD/");
		System.out.println(line);
		
	}

	public void run(Map batchArgs){
		
	}

    public String unescape(String line)
    {
        int n = line.length();
        StringBuilder buffer = new StringBuilder(n);
        
        for(int i = 0; i < n; )
        {
            char c = line.charAt(i++);
            
            if ( c == '\\' )
            {
                c = line.charAt(i++);
                
                if ( c == 'u' )
                {
                    try
                    {
                        c = (char) Integer.parseInt(line.substring(i,i+=4), 16);
                    }
                    catch (RuntimeException x)
                    {
                        throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                    }
                }
                else
                {
                    int idx = "\\tnf".indexOf(c);
                    
                    if ( idx >= 0 )
                    {
                        c = "\\\t\n\f".charAt(idx);
                    }
                }
            }
            
            buffer.append(c);
        }
        
        return buffer.toString();
    }	
	
}
