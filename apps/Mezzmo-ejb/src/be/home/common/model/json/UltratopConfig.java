package be.home.common.model.json;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class UltratopConfig  {

    public boolean rename;
    public List <Year> years = new ArrayList <Year>();

    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("m3u rename : " + rename + "\n");
        return b.toString();
    }

    public class Year {
        public String year;
        public String listFile;
        public boolean enabled;
        public String relativePathId;
        public String filter;
        public List <Month> m3uMonth = new ArrayList <Month> ();

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Year id : " + year + "\n");
            b.append("Year listFile : " + listFile + "\n");
            b.append("Year enabled : " + enabled + "\n");
            return b.toString();
        }

    }

    public class Month {
        public String id;
        public String baseDir;
        public String inputFile;
        public boolean enabled;

        public String toString() {
            StringBuilder b = new StringBuilder();
            b.append("Year id : " + id + "\n");
            b.append("Year baseDir : " + baseDir + "\n");
            b.append("Year inputFile : " + inputFile + "\n");
            b.append("Year enabled : " + enabled + "\n");
            return b.toString();
        }
    }

    private static String getMessage(Object o){

        if (o == null){
            return "Error";
        }
        else {
            return o.toString();
        }
    }

}
