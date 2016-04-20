package be.home.model;

import be.home.common.constants.Constants;

/**
 * Created by ghyssee on 29/09/2015.
 */
public class MovieStatusTO {
    private Constants.Movies.Status status;
    private MovieTO movie;

    public Constants.Movies.Status getStatus() {
        return status;
    }

    public void setStatus(Constants.Movies.Status status) {
        this.status = status;
    }

    public MovieTO getMovie() {
        return movie;
    }

    public void setMovie(MovieTO movie) {
        this.movie = movie;
    }

    public MovieStatusTO(){
        movie = new MovieTO();
    }
}
