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
                $this->$key = $value;
            }
        }
    }
}

class ArtistSongRelationshipTO extends Castable {
    public $id;
    public $oldArtistId;
    public $oldArtist;
    public $newMultiArtistId;
    public $newArtistId;
    public $newArtist;
    public $oldSong;
    public $newSong;
    public $exact;
    public $priority;
}

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

    function loadFullData(){
        if (CacheBO::isInCache(CacheBO::ARTISTSONG)){
            $list = CacheBO::getObject(CacheBO::ARTISTSONG);
        }
        else {
            $list = Array();
            ArtistBO:$artistBO = new ArtistBO();
            MultiArtistBO:$multiArtistBO = new MultiArtistBO();
            foreach ($this->artistSongRelationshipObj->items as $key => $item) {
                $artistSongRelationshipTO = new ArtistSongRelationshipTO($item);
                $artistTO = $artistBO->lookupArtist($artistSongRelationshipTO->oldArtistId);
                $artistSongRelationshipTO->oldArtist = isset($artistTO) ? $artistTO->name : "";
                if (isset($artistSongRelationshipTO->newArtistId)){
                    $artistTO = $artistBO->lookupArtist($artistSongRelationshipTO->newArtistId);
                    $artistSongRelationshipTO->newArtist = isset($artistTO) ? $artistTO->name : "";
                }
                else if (isset($artistSongRelationshipTO->newMultiArtistId)){
                    //$multiArtistTO = $multiArtistBO->
                }
                $list[] = $artistSongRelationshipTO;

            }
            CacheBO::saveObject(CacheBO::ARTISTSONG, $list);
        }
        return $list;
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

    function isArtistUsed($id){
        foreach ($this->artistSongRelationshipObj->items as $key => $value) {
            if (isset($value->oldArtistId) && $value->oldArtistId == $id){
                return true;
            }
            if (isset($value->newArtistId) && $value->newArtistId == $id){
                return true;
            }
        }
        return false;
    }

    function isMultiArtistUsed($id){
        foreach ($this->artistSongRelationshipObj->items as $key => $value) {
            if (isset($value->oldMultiArtistId) && $value->oldMultiArtistId == $id){
                return true;
            }
            if (isset($value->newMultiArtistId) && $value->newMultiArtistId == $id){
                return true;
            }
        }
        return false;
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