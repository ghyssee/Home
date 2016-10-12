package be.home.mezzmo.domain.model;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class MGOFileAlbumCompositeTO {

    private MGOFileTO fileTO;
    private MGOFileAlbumTO fileAlbumTO;
    private MGOAlbumArtistTO albumArtistTO;
    private MGOPlaylistTO playlistTO;
    private MGOFileArtistTO fileArtistTO;
    private String filename = null;

    private boolean currentlyPlaying = false;

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

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public boolean isCurrentlyPlaying() {
        return currentlyPlaying;
    }

    public void setCurrentlyPlaying(boolean currentlyPlaying) {
        this.currentlyPlaying = currentlyPlaying;
    }


}
