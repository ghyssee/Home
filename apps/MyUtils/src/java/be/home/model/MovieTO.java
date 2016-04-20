package be.home.model;

import java.io.File;

/**
 * Created by ghyssee on 16/03/2015.
 */
public class MovieTO {

    private String id;
    private String title;
    private String alternateTitle;
    private String year;
    private String imdbId;
    private String genre;
    private String stockPlace;
    private boolean movieSynchronized = false;
    private File syncDir;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlternateTitle() {
        return alternateTitle;
    }

    public void setAlternateTitle(String alternateTitle) {
        this.alternateTitle = alternateTitle;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImdbId() {
        return imdbId;
    }

    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getStockPlace() {
        return stockPlace;
    }

    public void setStockPlace(String stockPlace) {
        this.stockPlace = stockPlace;
    }

    public boolean isMovieSynchronized() {
        return movieSynchronized;
    }

    public void setMovieSynchronized(boolean movieSynchronized) {
        this.movieSynchronized = movieSynchronized;
    }

    public File getSyncDir() {
        return syncDir;
    }

    public void setSyncDir(File syncDir) {
        this.syncDir = syncDir;
    }

    public MovieTO(){

    }
}
