package be.home.mezzmo.domain.model;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MGOFileAlbumCompositeTO {

    MGOFileTO fileTO;
    MGOFileAlbumTO fileAlbumTO;
    MGOAlbumArtistTO albumArtistTO;
    MGOPlaylistTO playlistTO;
    MGOFileArtistTO fileArtistTO;

    public MGOFileAlbumCompositeTO() {
        fileTO = new MGOFileTO();
        fileAlbumTO = new MGOFileAlbumTO();
        albumArtistTO = new MGOAlbumArtistTO();
        playlistTO = new MGOPlaylistTO();
        fileArtistTO = new MGOFileArtistTO();
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

    public MGOPlaylistTO getPlaylistTO() {
        return playlistTO;
    }

    public void setPlaylistTO(MGOPlaylistTO playlistTO) {
        this.playlistTO = playlistTO;
    }

    public MGOFileArtistTO getFileArtistTO() {
        return fileArtistTO;
    }

    public void setFileArtistTO(MGOFileArtistTO fileArtistTO) {
        this.fileArtistTO = fileArtistTO;
    }
}
