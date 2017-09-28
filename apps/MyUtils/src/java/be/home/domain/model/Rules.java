package be.home.domain.model;

/**
 * Created by ghyssee on 26/09/2017.
 */
public enum Rules {
    AS_EXCEPTION("ARTIST/SONG EXCEPTION", "EXCEPTION"),
    AS_RELATION("ARTIST SONG RELATION", "RELATION"),
    ALBUM_RELATION("ALBUM/ARTIST RELATION", "RELATION"),
    MULTIARTIST("MULTIARTIST", "MULTIARTIST");

    private String message;
    private String code;

    Rules(String message, String code) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

}
