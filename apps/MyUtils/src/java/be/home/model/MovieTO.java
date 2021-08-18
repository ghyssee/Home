package be.home.model;

import com.opencsv.bean.CsvBindByPosition;

import java.io.File;

/**
 * Created by ghyssee on 16/03/2015.
 */
public class MovieTO {

    @CsvBindByPosition(position = 0)
    private String id;
    @CsvBindByPosition(position = 1)
    private String title;
    @CsvBindByPosition(position = 2)
    private String alternateTitle;
    @CsvBindByPosition(position = 3)
    private String year;
    @CsvBindByPosition(position = 4)
    private String genre;
    @CsvBindByPosition(position = 5)
    private String stockPlace;
    @CsvBindByPosition(position = 6)
    private String imdbId;
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
