package be.home.picmgt.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.devlib.schmidt.imageinfo.ImageInfo;

public class ImageInfoMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		viewFileInfo();
	}
	
	public static void viewFileInfo(){
		File jpgFile = new File("C:/Projects/DEV/PicMgt/data/Alina-Serie03-PICT063.jpg");
		ImageInfo ii = new ImageInfo();
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(jpgFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 ii.setInput(fis); // in can be InputStream or RandomAccessFile
		 ii.setDetermineImageNumber(true); // default is false
		 ii.setCollectComments(true); // default is false
		 if (!ii.check()) {
		   System.err.println("Not a supported image file format.");
		   return;
		 }
		 System.out.println(ii.getFormatName() + ", " + ii.getMimeType() + 
		   ", " + ii.getWidth() + " x " + ii.getHeight() + " pixels, " + 
		   ii.getBitsPerPixel() + " bits per pixel, " + ii.getNumberOfImages() +
		   " image(s), " + ii.getNumberOfComments() + " comment(s).");
		
	}

}
