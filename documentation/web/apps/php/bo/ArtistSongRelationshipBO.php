<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");
require_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
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

abstract class ArtistType
{
    const ARTIST = "01";
    const MULTIARTIST = "02";
    const FREE = "03";
}


class ArtistSongRelationshipTO extends Castable {
    public $id;
    public $oldArtistId;
    public $oldArtist;
    public $oldArtistList;
    public $oldMultiArtistId;
    public $oldSong;
    public $exact;
    public $newMultiArtistId;
    public $newArtistId;
    public $newArtist;
    public $newSong;
    public $exactMatchTitle;
    public $indexTitle;
    public $priority;

}

class ArtistSongRelationshipCompositeTO extends ArtistSongRelationshipTO {
    public $multiArtistBO;
    public $oldArtistListObj;
    public $oldArtistType;
    public $newArtistType;
    function __construct($object)
    {
        parent::__construct($object);
        $this->oldArtistObj = new ArtistTO();
        //$this->oldMultiArtistObj = new MultiArtistListTO();
        $this->oldArtistListObj = array();
        if (isset($object)) {
            $this->multiArtistBO = new MultiArtistBO();
            if (isset($this->oldArtistId)) {
                $artistBO = $this->multiArtistBO->getArtistBO();
                $this->oldArtistListObj[] = $artistBO->lookupArtist($this->oldArtistId);
                $this->oldArtistType = ArtistType::ARTIST;
            } else if (isset($this->oldArtistList)) {
                $this->oldArtistType = ArtistType::ARTIST;
                $artistBO = $this->multiArtistBO->getArtistBO();
                foreach($this->oldArtistList as $item){
                    $tmp = new ArtistItemTO($item);
                    $this->oldArtistListObj[] =  $this->getArtist($artistBO, $tmp);
            }
            } else if (isset($this->oldMultiArtistId)) {
                $this->oldArtistType = ArtistType::MULTIARTIST;
                $multiArtistObj = $this->multiArtistBO->findMultiArtist($this->oldMultiArtistId);
                if (isset($multiArtistObj)){
                    $this->oldMultiArtistId = $multiArtistObj->id;
                }
            }
            if (isset($this->newArtistId)) {
                $this->newArtistType = ArtistType::ARTIST;
                $artistBO = $this->multiArtistBO->getArtistBO();
                $artistTO = $artistBO->lookupArtist($this->newArtistId);
                $this->newArtistId = $artistTO->id;
            } else if (isset($this->newMultiArtistId)) {
                $this->newArtistType = ArtistType::MULTIARTIST;
                $multiArtistObj = $this->multiArtistBO->findMultiArtist($this->newMultiArtistId);
                if (isset($multiArtistObj)){
                    $this->newMultiArtistId = $multiArtistObj->id;
                }
            }
        }
        else {
            $this->oldArtistObj = new ArtistTO();
        }
    }

    function getArtist(ArtistBO $artistBO, ArtistItemTO $artistItemTO){
        if (isset($artistItemTO->id)){
            return $artistBO->lookupArtist($artistItemTO->id);
        }
        else {
            $artist = new ArtistTO();
            $artist->name = $artistItemTO->text;
            return $artist;
        }

    }
  

}

class ArtistItemTO extends Castable {
    public $id;
    public $text;
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

    function findArtistSongRelationship($id){
        foreach ($this->artistSongRelationshipObj->items as $key => $item) {
            $artistSongRelationshipTO = new ArtistSongRelationshipTO($item);
            if ($artistSongRelationshipTO->id == $id){
                return $artistSongRelationshipTO;
            }
        }
        return null;
    }

    function loadFullData(){
        if (CacheBO::isInCache(CacheBO::ARTISTSONG)){
            $list = CacheBO::getObject(CacheBO::ARTISTSONG);
        }
        else {
            $list = Array();
            ArtistBO:$artistBO = new ArtistBO();
            $multiArtistBO = new MultiArtistBO();
            $multiArtistBO->loadData();
            foreach ($this->artistSongRelationshipObj->items as $key => $item) {
                $artistSongRelationshipTO = new ArtistSongRelationshipTO($item);
                if (isset($artistSongRelationshipTO->oldArtistId)){
                    $artistTO = $artistBO->lookupArtist($artistSongRelationshipTO->oldArtistId);
                    $artistSongRelationshipTO->oldArtist = isset($artistTO) ? $artistTO->name : "";
                }
                else if (isset($artistSongRelationshipTO->oldArtistList) && count($artistSongRelationshipTO->oldArtistList) > 0){
                    $artistSongRelationshipTO->oldArtist = "";
                    $first = true;
                    /* @var $artist AristItemTO */
                    foreach($artistSongRelationshipTO->oldArtistList as $artist){
                        $artistSongRelationshipTO->oldArtist .= $first ? "" : "|";
                        $first = false;
                        if (isset($artist->id)){
                            $artistTO = $artistBO->lookupArtist($artist->id);
                            $artistSongRelationshipTO->oldArtist .= isset($artistTO) ? $artistTO->name : "";
                        }
                        else {
                            $artistSongRelationshipTO->oldArtist .= $artist->text;
                        }
                    }
                }
                else if (isset($artistSongRelationshipTO->oldMultiArtistId)){
                    $multiArtist = $multiArtistBO->findMultiArtist($artistSongRelationshipTO->oldMultiArtistId);
                    if (isset($multiArtist)){
                        $multiArtistTO = $multiArtistBO->convertToMultiArtistTO($multiArtist);
                        $artistSongRelationshipTO->oldArtist = $multiArtistTO->description2;
                    }
                }

                if (isset($artistSongRelationshipTO->newArtistId)){
                    $artistTO = $artistBO->lookupArtist($artistSongRelationshipTO->newArtistId);
                    $artistSongRelationshipTO->newArtist = isset($artistTO) ? $artistTO->name : "";
                }
                else if (isset($artistSongRelationshipTO->newMultiArtistId)){
                    $multiArtist = $multiArtistBO->findMultiArtist($artistSongRelationshipTO->newMultiArtistId);
                    if (isset($multiArtist)){
                        $multiArtistTO = $multiArtistBO->convertToMultiArtistTO($multiArtist);
                        $artistSongRelationshipTO->newArtist = $multiArtistTO->description2;
                    }
                }
                $list[] = $artistSongRelationshipTO;

            }
            CacheBO::saveObject(CacheBO::ARTISTSONG, $list);
        }
        return $list;
    }


    function saveArtistSong(ArtistSongRelationshipTO $artistSongRelationship)
    {
        $counter = 0;
        foreach ($this->artistSongRelationshipObj->items as $key => $value) {
            if (strcmp($value->id, $artistSongRelationship->id) == 0) {
                $this->artistSongRelationshipObj->items[$counter] = $artistSongRelationship;
                writeJSON($this->artistSongRelationshipObj, $this->file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addArtistSong(ArtistSongRelationshipTO $artistSongRelationship)
    {
        $artistSongRelationship->id = getUniqueId();
        array_push($this->artistSongRelationshipObj->items, $artistSongRelationship);
        writeJSON($this->artistSongRelationshipObj, $this->file);
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