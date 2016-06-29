package be.home.main;

import be.home.common.constants.Constants;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class MovieSynchronizer extends BatchJobV2 {

    private static final String VERSION = "V1.0";

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(MovieSynchronizer.class);

    public static void main(String args[]) {

        MovieSynchronizer instance = new MovieSynchronizer();
        instance.printHeader("MOVIESYNCHRONIZER " + VERSION, "=");
        try {
            config = instance.init();
            instance.start();
        }
        catch (FileNotFoundException e){
           log.error(e);
        } catch (IOException e) {
            log.error(e);
        }
    }

    @Override
    public void run() {

    }

    public void start() throws FileNotFoundException {

        String configFile = config.movies.importFile;

        List <File> destMovies = new ArrayList<File>();
        List <MovieStatusTO> listStatusMovies = new ArrayList<MovieStatusTO>();

        // create new filename filter
        FilenameFilter fileNameFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                String cmpName = name.toUpperCase();
                for (ConfigTO.ExcludePaths excludePath : config.movies.excludePaths){
                    if (StringUtils.isNotBlank(excludePath.expression)){
                        return (!cmpName.matches(excludePath.path.toUpperCase() + " " + excludePath.expression));

                    }
                    if (excludePath.path.toUpperCase().compareTo(cmpName) == 0){
                        return false;
                    }
                }
                return true;
            }
        };
        File destinationDir = new File(config.movies.synchronizeDir);

        getListOfMovies(destinationDir, destMovies, fileNameFilter, 1);
        List<MovieTO> list = MovieBO.getListOfMoviesFromCSV(configFile);

        List<MovieTO> sourceFilteredMovieList = new ArrayList<MovieTO>();

        for (MovieTO movie : list){
            if (movie.getStockPlace() != null && config.movies.stockPlace.compareTo(movie.getStockPlace()) == 0){
                sourceFilteredMovieList.add(movie);
            }
        }
        synchronizeMovie(destMovies, sourceFilteredMovieList, listStatusMovies);
        for (MovieTO movie : sourceFilteredMovieList){
            if (!movie.isMovieSynchronized()){
                MovieStatusTO movieStatus = new MovieStatusTO();
                movieStatus.setMovie(movie);
                movieStatus.setStatus(Constants.Movies.Status.SOURCE);
                listStatusMovies.add(movieStatus);
            }
            else {
                String baseDir = movie.getSyncDir().getAbsolutePath();
                MovieStatusTO movieStatus = new MovieStatusTO();
                if (MovieBO.existNFOFile(movie, baseDir)){
                    movieStatus.setMovie(movie);
                    movieStatus.setStatus(Constants.Movies.Status.EXIST_NFO);
                    listStatusMovies.add(movieStatus);
                }
                else {
                    try {
                        MovieBO.makeNFOFile(movie, baseDir);
                        movieStatus.setMovie(movie);
                        movieStatus.setStatus(Constants.Movies.Status.NFO);
                        listStatusMovies.add(movieStatus);
                    } catch (FileNotFoundException e) {
                        movieStatus.setMovie(movie);
                        movieStatus.setStatus(Constants.Movies.Status.ERROR_NFO);
                        listStatusMovies.add(movieStatus);
                    }
                }
            }
        }

        final String batchJob = "MovieSynchronizer";
        log4GE = new Log4GE(config.logDir, config.movies.log);
        log4GE.start(batchJob);
        log4GE.emptyLine();
        log4GE.info("SOURCE:      " + configFile);
        log4GE.info("DESTINATION: " + destinationDir.getAbsolutePath());
        log4GE.emptyLine();
        log4GE.addColumn("Status", 15);
        log4GE.addColumn("Movie", 100);
        log4GE.addColumn("Description", 150);
        log4GE.printHeaders();

        Collections.sort(listStatusMovies, MovieBO.comparator);

        for (MovieStatusTO statusMovie : listStatusMovies) {
            String[] line =  new String[]{};
            switch (statusMovie.getStatus()){
                case SOURCE:
                    line = new String[]{statusMovie.getStatus().name(), MovieBO.getMovieTitle((statusMovie.getMovie())), "Movie found in Source, but not in destination"};
                    break;
                case DESTINATION:
                    line = new String[]{statusMovie.getStatus().name(), MovieBO.getMovieTitle((statusMovie.getMovie())), "Movie found in Destination, but not in source "
                            + "(" + statusMovie.getMovie().getSyncDir() + ")"};
                    break;
                case NFO:
                    line = new String[]{statusMovie.getStatus().name(), MovieBO.getMovieTitle((statusMovie.getMovie())), "NFO File created for movie " + "(" + statusMovie.getMovie().getSyncDir() + ")"};
                    break;
                case EXIST_NFO:
                    line = new String[]{statusMovie.getStatus().name(), MovieBO.getMovieTitle((statusMovie.getMovie())), "NFO File already exists " + "(" + statusMovie.getMovie().getSyncDir() + ")"};
                    break;
                case ERROR_NFO:
                    line = new String[]{statusMovie.getStatus().name(), MovieBO.getMovieTitle((statusMovie.getMovie())), "There was a problem with the creation of NFO File"};
                    break;
            }
            log4GE.printRow(line);
        }
        log4GE.endTable();
        log4GE.end();
    }

    public void synchronizeMovie(List <File> destMovies, List<MovieTO> list, List <MovieStatusTO> listMovieStatus){
        final String batchJob = "MovieSynchronizer";
        log4GE = new Log4GE(config.logDir, config.movies.log);
        log4GE.start(batchJob);
        for (File destMovie : destMovies){
            boolean found = false;
            String dest = destMovie.getName();
            for (MovieTO movie : list){
                if (dest.compareTo(MovieBO.getMovieTitle(movie)) == 0){
                    movie.setMovieSynchronized(true);
                    movie.setSyncDir(destMovie);
                    found = true;
                    break;
                }
            }
            if (!found){
                MovieStatusTO movieStatus = new MovieStatusTO();
                movieStatus.getMovie().setTitle(dest);
                movieStatus.getMovie().setSyncDir(destMovie);
                movieStatus.setStatus(Constants.Movies.Status.DESTINATION);
                listMovieStatus.add(movieStatus);
            }
        }

        log4GE.end();

    }


    private List getListOfMovies(File destinationDir, List <File> destMovies, FilenameFilter fileNameFilter, int level){
        if (destinationDir.isDirectory()){
            File[] files = destinationDir.listFiles(fileNameFilter);
            for (File movieDir : files){
                if (movieDir.isDirectory()){
                    if (level > 1) {
                        //System.out.println("movieDir = " + movieDir.getAbsolutePath() + " / level = " + level);
                        destMovies.add(movieDir);
                    }
                    getListOfMovies(movieDir, destMovies, fileNameFilter, level+1);
                }
            }
        }
        else {
            log.error("Destination Directory not found:" + destinationDir.getAbsolutePath());
        }
        return destMovies;
    }

}
