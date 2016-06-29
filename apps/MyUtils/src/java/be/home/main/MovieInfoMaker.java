package be.home.main;

import be.home.model.ConfigTO;
import be.home.model.MovieBO;
import be.home.model.MovieStatusTO;
import be.home.model.MovieTO;
import be.home.common.constants.Constants;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MovieInfoMaker extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MovieInfoMaker.class);



    public static void main(String args[]) {

        MovieInfoMaker instance = new MovieInfoMaker();
        instance.printHeader("MovieInfoMaker " + VERSION, "=");
        try {
            config = instance.init();
            instance.start();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

    }

    public void start() throws FileNotFoundException {

        String configFile = config.movies.importFile;

        List <MovieStatusTO> movieStatusList = new ArrayList<MovieStatusTO>();
        List<MovieTO> list = MovieBO.getListOfMoviesFromCSV(configFile);

        for (MovieTO movie : list){
            processMovie(movie, movieStatusList);
        }

        Collections.sort(movieStatusList, MovieBO.comparator);
        final String batchJob = "MovieInfoMaker";
        log4GE = new Log4GE(config.logDir, config.movies.log);
        log4GE.start(batchJob);
        log4GE.addColumn("Status", 20);
        log4GE.addColumn("Movie", 100);
        log4GE.addColumn("Description", 200);
        log4GE.printHeaders();

        for (MovieStatusTO movieStatus : movieStatusList){
            String line[] = new String[]{movieStatus.getStatus().name(), MovieBO.getMovieTitle((movieStatus.getMovie())), MovieBO.getStatusDescription((movieStatus))};
            log4GE.printRow(line);
        }
        log4GE.endTable();
        log4GE.end();

    }

    public void processMovie(MovieTO movie, List <MovieStatusTO> movieStatusList){
        if (StringUtils.isNotBlank(movie.getAlternateTitle())) {
            MovieStatusTO movieStatus = new MovieStatusTO();
            movieStatus.setMovie(movie);
            movieStatus.setStatus(Constants.Movies.Status.DIFFERENT_TITLE);
            movieStatusList.add(movieStatus);
        }
        MovieStatusTO movieStatus = new MovieStatusTO();
        movieStatus.setMovie(movie);
        try {
            MovieBO.makeNFOFile(movie, config.movies.outputDir + File.separator + movie.getStockPlace());
            movieStatus.setStatus(Constants.Movies.Status.NFO);

        } catch (FileNotFoundException e) {
            movieStatus.setStatus(Constants.Movies.Status.ERROR_NFO);
        }
        movieStatusList.add(movieStatus);

    }

}
