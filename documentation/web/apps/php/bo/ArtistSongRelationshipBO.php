<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 28/04/2017
 * Time: 15:02
 */


class ArtistSongRelationshipBO
{
    public $artistSongRelationshipObj;
    public $file;

    function __construct() {
        $this->file = getFullPath(JSON_ARTISTSONGRELATIONSHIP);
        $this->artistSongRelationshipObj = readJSON( $this->file);
    }

    function getArtistSongRelationshipList(){
        return $this->artistSongRelationshipObj->items;
    }

    function saveArtistSong($artistSongRelationship)
    {
        $counter = 0;
        foreach ($this->artistSongRelationshipObj->items as $key => $value) {
            if (strcmp($value->id, $artistSongRelationship->id) == 0) {
                $this->artistSongRelationshipObj->items[$counter] = $artistSongRelationship;
                writeJSON($this->artistObj, $this->file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addArtistSong($artistSongRelationship)
    {
        $artistSongRelationship->id = getUniqueId();
        array_push($this->artistSongRelationshipObj->items, $artistSongRelationship);
        writeJSON($this->artistSongRelationship, $this->file);
    }

    function deleteArtistSong($id)
    {
        $array = $this->artistSongRelationshipObj->items;
        $key = array_search($id, array_column($array, 'id'));
        if ($key === false || $key === NULL) {
            return false;

        } else {
                unset($this->artistSongRelationshipObj->items[$key]);
                $array = array_values($this->artistSongRelationshipObj->items);
                $this->artistSongRelationshipObj->items = $array;
                writeJSON($this->artistSongRelationshipObj, $this->file);
                return true;
        }
        return false;
    }
}

?>