<?php
class Castable
{
    public function __construct($object = null)
    {
        $this->cast($object);
    }

    public function cast($object)
    {
        if (is_array($object) || is_object($object)) {
            foreach ($object as $key => $value) {
                if (property_exists($this, $key)) {
                    $this->$key = $value;
                }
            }
        }
    }
}

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
    public $status;
    public $ruleId;
    public $newArtist;
    public $newTitle;
}

class MezzmoFileTO {
    public $id;
    public $artistId;
    public $artistName;
    public $status;
    public $isNew;
    public $ruleId;
    public $newArtist;
    public $newTitle;
}

class VersionTO {
    public $version;
    public $lastUpdated;
}

class FeedBackTO {
    public $success;
    public $errorFound;
    public $message;
    public $errorMsg;
    public $artistsAdded;
    public $multiArtist;
}

?>