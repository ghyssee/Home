package be.home.mezzmo.domain.model;

/**
 * Created by ghyssee on 18/07/2016.
 */
public enum PlaylistType {
    NORMAL(16), EXTERNAL(32), SMART(64), FOLDER(128);

    private final int value;
    private PlaylistType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }


}
