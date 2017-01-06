package be.home.model;

import com.opencsv.CSVParser;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ghyssee on 29/09/2015.
 */
public class MovieBO {

    private static final String SEASON = " - Seizoen ";

    public static String formattedMovieTitle(String title){

        String tmp = title.replaceAll(" 1/3", "");
        tmp = tmp
                .replace('/',' ')
                .replace('\\',' ')
                .replace(":","")
                .replace("?", "");
        String suffix = ", The";
        if (tmp.endsWith(suffix)) {
            tmp = "The " + tmp.replace(suffix, "");
        }
        suffix = ", A";
        if (tmp.endsWith(suffix)) {
            tmp = "A " + tmp.replace(suffix, "");
        }
        suffix = ", Het";
        if (tmp.endsWith(suffix)) {
            tmp = "Het " + tmp.replace(suffix, "");
        }
        suffix = ", De";
        if (tmp.endsWith(suffix)) {
            tmp = "De " + tmp.replace(suffix, "");
        }
        int index = tmp.indexOf(SEASON);
        if (index >= 0){
           tmp = tmp.substring(0, index);
        }
        index = tmp.indexOf(" - Complete serie");
        if (index >= 0){
            tmp = tmp.substring(0, index);
        }        return tmp;

    }

    private static File getDestinationFile(MovieTO movie, String baseDir){
        File destinationFile = new File(baseDir + File.separator + getMovieTitle(movie) + ".nfo");
        return destinationFile;
    }

    public static String getMovieTitle (MovieTO movie){
        String suffix = "";
        if (StringUtils.isNotBlank(movie.getYear()) && !movie.getTitle().contains(SEASON)){
            suffix = " (" + movie.getYear() + ")";
        }
        String movieTitle = formattedMovieTitle(movie.getTitle()) + suffix;
        return movieTitle;
    }

    public static boolean existNFOFile(MovieTO movie, String baseDir){
        return getDestinationFile(movie, baseDir).exists();
    }

    public static void makeNFOFile(MovieTO movie, String baseDir) throws FileNotFoundException {

        //String baseDir = config.movies.outputDir + File.separator + movie.getStockPlace();
        File baseFolder = new File(baseDir);
        if (!baseFolder.exists()){
            baseFolder.mkdirs();
        }
        File destinationFile = getDestinationFile(movie, baseDir);
        movie.setSyncDir(destinationFile);
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(destinationFile);
            String line = "http://www.imdb.com/title/tt" + movie.getImdbId() + "/";
            writer.println(line);
        }
        finally{
            if (writer != null){
                writer.close();
            }
        }
    }

    public static List<MovieTO> getListOfMoviesFromCSV(String importFile) throws FileNotFoundException {
        ColumnPositionMappingStrategy mappingStrategy = new ColumnPositionMappingStrategy();
        mappingStrategy.setType(MovieTO.class);
        String[] columns = new String[] {"id", "title", "alternateTitle", "year", "genre", "stockPlace", "imdbId"}; // the fields to bind do in your JavaBean
        mappingStrategy.setColumnMapping(columns);

        CsvToBean csv = new CsvToBean();
        com.opencsv.CSVReader reader = null;
        List<MovieTO> list = null;
        reader = new com.opencsv.CSVReader(new FileReader(importFile), ';', CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER, 0, false, false);
        list = csv.parse(mappingStrategy, reader);
        if (list != null && !list.isEmpty() && "\0".compareTo(((MovieTO) list.get(list.size()-1)).getId()) == 0){
            list.remove(list.size()-1);
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getStatusDescription(MovieStatusTO movieStatus){
        String line =  "";
        switch (movieStatus.getStatus()){
            case SOURCE:
                line = "Movie found in Source, but not in destination";
                break;
            case DESTINATION:
                line = "Movie found in Destination, but not in source " + "(" + movieStatus.getMovie().getSyncDir() + ")";
                break;
            case NFO:
                line = "NFO File created for movie " + "(" + movieStatus.getMovie().getSyncDir() + ")";
                break;
            case ERROR_NFO:
                line = "There was a problem with the creation of NFO File";
                break;
            case DIFFERENT_TITLE:
                line = "Alternate Title: " + movieStatus.getMovie().getAlternateTitle();
                break;
        }
        return line;

    }
    public static Comparator<MovieStatusTO> comparator = new Comparator<MovieStatusTO>() {
        public int compare(MovieStatusTO c1, MovieStatusTO c2) {
            String cmp1 = c1.getStatus().name();
            String cmp2 = c2.getStatus().name();
            if (c1.getMovie().getSyncDir() != null){
                cmp1 += c1.getMovie().getSyncDir().getAbsolutePath();
            }
            else {
                cmp1 += MovieBO.getMovieTitle(c1.getMovie());
            }
            if (c2.getMovie().getSyncDir() != null){
                cmp2 += c2.getMovie().getSyncDir().getAbsolutePath();
            }
            else {
                cmp2 += MovieBO.getMovieTitle(c2.getMovie());
            }
            return cmp1.compareTo(cmp2); // use your logic
        }
    };

}
