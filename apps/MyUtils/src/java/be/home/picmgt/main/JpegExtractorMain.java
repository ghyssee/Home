package be.home.picmgt.main;

import java.util.ArrayList;
import java.util.List;

public class JpegExtractorMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		List <String> arguments = new ArrayList<String>();
		arguments.add("C:/Projects/DEV/PicMgt/data/Alina-Serie03-PICT063.jpg");
		arguments.add("--outputdirectory");
		arguments.add("C:/Projects/DEV/PicMgt/output");
		arguments.add("--digits");
		arguments.add("3");
		arguments.add("--prefix");
		arguments.add("Alina-Serie03-PICT");
		arguments.add("--initialnumber");
		arguments.add("1");

//		String[] tst = (String[]) arguments.
//		JpegExtractor ext = new JpegExtractor(tst);
//		ext.run();

	}

}
