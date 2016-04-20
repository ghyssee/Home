package be.home.common.utils;

public class StringUtilsExtended {

  public static String replicate(String string, int number){
    StringBuffer bf = new StringBuffer();
    for (int i=0; i < number; i++){
    	bf.append(string);
    }
    return bf.toString();
  }

}
