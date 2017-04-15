<?php
class SongTO {
    public $artistId;
    public $artist;
    public $title;
    public $fileId;
    public $albumId;
    public $album;
    public $file;

}

class AristSongTestTO {
    public $fileId;
    public $oldArtist;
    public $oldSong;
    public $newArtist;
    public $newSong;
}

class SongCorrection{
    public $fileId;
    public $title;
    public $artist;
    public $track;
    public $source;
}
?>