<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");
/**
 * Created by PhpStorm.
 * User: Gebruiker
 * Date: 28/05/2017
 * Time: 18:12
 */
class ArtistSongExceptionTO  {
    public $id;
    public $oldArtist;
    public $newArtist;
    public $oldSong;
    public $newSong;
    public $exactMatchTitle;
    public $exactMatchArtist;
    public $priority;
    public $indexTitle;
    public function __construct7($oldArtist, $newArtist, $oldSong, $newSong, $exactMatchArtist,$exactMatchTitle, $priority, $indexTitle)
    {
        $this->oldArtist = $oldArtist;
        $this->newArtist = $newArtist;
        $this->oldSong = $oldSong;
        $this->newSong = $newSong;
        $this->exactMatchTitle = $exactMatchTitle;
        $this->exactMatchArtist = $exactMatchArtist;
        $this->priority = $priority;
        $this->indexTitle = $indexTitle;
    }
}

class ArtistSongExceptionBO
{
    public $artistSongExceptionObj;
    public $file;

    function __construct()
    {
        $this->file = getFullPath(JSON_MP3PRETTIFIER);
        $this->artistSongExceptionObj = readJSON($this->file);
    }

    function getArtistSongExceptionList()
    {
        return $this->artistSongExceptionObj->artistSongExceptions->items;
    }

    function existArtistSongException($artist, $song, $id = null){
        foreach($this->getArtistSongExceptionList() as $key => $item){
            if ($item->oldArtist == $artist && $item->oldSong == $song){
                if ($id != null && $id == $item->id) {
                    continue;
                }
                else {
                    return true;
                }
            }
        }
        return false;
    }

}
?>