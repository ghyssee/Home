package be.home.mezzmo.domain.model;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MGOFileAlbumCompositeTO {

    MGOFileTO fileTO;
    MGOFileAlbumTO fileAlbumTO;
    MGOAlbumArtistTO albumArtistTO;

    public MGOFileAlbumCompositeTO() {
        fileTO = new MGOFileTO();
        fileAlbumTO = new MGOFileAlbumTO();
        albumArtistTO = new MGOAlbumArtistTO();
    }


    public MGOFileTO getFileTO() {
        return fileTO;
    }

    public void setFileTO(MGOFileTO fileTO) {
        this.fileTO = fileTO;
    }

    public MGOFileAlbumTO getFileAlbumTO() {
        return fileAlbumTO;
    }

    public void setFileAlbumTO(MGOFileAlbumTO fileAlbumTO) {
        this.fileAlbumTO = fileAlbumTO;
    }

    public MGOAlbumArtistTO getAlbumArtistTO() {
        return albumArtistTO;
    }

    public void setAlbumArtistTO(MGOAlbumArtistTO albumArtistTO) {
        this.albumArtistTO = albumArtistTO;
    }
}
