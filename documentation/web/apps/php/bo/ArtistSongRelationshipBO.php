<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_HTML, "config.php");
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
    public $oldArtistList;
    public $oldMultiArtistId;
    public $oldSong;
    public $exact;
    public $newMultiArtistId;
    public $newArtistId;
    public $newSong;
    public $exactMatchTitle;
    public $indexTitle;
    public $priority;

}

class ArtistSongRelationshipExtendedTO extends ArtistSongRelationshipTO {
    public $oldArtist;
    public $newArtist;
}

class ArtistSongRelationshipCompositeTO extends ArtistSongRelationshipExtendedTO {
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
            if (isset($this->oldArtistList)) {
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

    function convertToArtistSongForm(MultiArtistBO $multiArtistBO, $item){
        $artistSongRelationshipExtendedTO = new ArtistSongRelationshipExtendedTO($item);
        $artistBO = $multiArtistBO->getArtistBO();
        if (isset($artistSongRelationshipExtendedTO->oldArtistList) && count($artistSongRelationshipExtendedTO->oldArtistList) > 0){
            $artistSongRelationshipExtendedTO->oldArtist = "";
            $first = true;
            /* @var $artist AristItemTO */
            foreach($artistSongRelationshipExtendedTO->oldArtistList as $artist){
                $artistSongRelationshipExtendedTO->oldArtist .= $first ? "" : "|";
                $first = false;
                if (isset($artist->id)){
                    $artistTO = $artistBO->lookupArtist($artist->id);
                    $artistSongRelationshipExtendedTO->oldArtist .= isset($artistTO) ? $artistTO->name : "";
                }
                else {
                    $artistSongRelationshipExtendedTO->oldArtist .= $artist->text;
                }
            }
        }
        else if (isset($artistSongRelationshipExtendedTO->oldMultiArtistId)){
            $multiArtist = $multiArtistBO->findMultiArtist($artistSongRelationshipExtendedTO->oldMultiArtistId);
            if (isset($multiArtist)){
                $multiArtistTO = $multiArtistBO->convertToMultiArtistTO($multiArtist);
                $artistSongRelationshipExtendedTO->oldArtist = $multiArtistTO->description2;
            }
        }

        if (isset($artistSongRelationshipExtendedTO->newArtistId)){
            $artistTO = $artistBO->lookupArtist($artistSongRelationshipExtendedTO->newArtistId);
            $artistSongRelationshipExtendedTO->newArtist = isset($artistTO) ? $artistTO->name : "";
        }
        else if (isset($artistSongRelationshipExtendedTO->newMultiArtistId)){
            $multiArtist = $multiArtistBO->findMultiArtist($artistSongRelationshipExtendedTO->newMultiArtistId);
            if (isset($multiArtist)){
                $multiArtistTO = $multiArtistBO->convertToMultiArtistTO($multiArtist);
                $artistSongRelationshipExtendedTO->newArtist = $multiArtistTO->description2;
            }
        }
        return $artistSongRelationshipExtendedTO;
    }

    function loadFullData($filterRules = null){
        $filterRuleSet = isSetFilterRule($filterRules);
        $normalList = Array();
        $cacheList = Array();
        if (CacheBO::isInCache(CacheBO::ARTISTSONG)){
            $cacheList = CacheBO::getObject(CacheBO::ARTISTSONG);
            if ($filterRuleSet){
                $filteredList = [];
                foreach($cacheList as $artistSongRelationshipTO){
                    if (!$this->isItemFiltered($artistSongRelationshipTO, $filterRules)){
                        $filteredList[] = $artistSongRelationshipTO;
                    }
                }
                $normalList = $filteredList;
            }
            else {
                $normalList = array_values($cacheList);
            }
        }
        else {
            $multiArtistBO = new MultiArtistBO();
            $multiArtistBO->loadData();
            $filteredList = [];
            foreach ($this->artistSongRelationshipObj->items as $key => $item) {
                $artistSongRelationshipExtendedTO = $this->convertToArtistSongForm($multiArtistBO, $item);
                $cacheList[$artistSongRelationshipExtendedTO->id] = $artistSongRelationshipExtendedTO;
                $normalList = $artistSongRelationshipExtendedTO;
                if (!$this->isItemFiltered($artistSongRelationshipExtendedTO, $filterRules)){
                    $filteredlist[] = $artistSongRelationshipExtendedTO;
                }
            }
            // Save The Full List In The Cache
            CacheBO::saveObject(CacheBO::ARTISTSONG, $cacheList);
            if ($filterRuleSet) {
                $normalList = $filteredList;
            }
        }
        return $normalList;
    }

    /* filterRules is an array of objects containging 2 fields:
       field = name of the field of the filter rule
       op = possible values: for the moment we threat everything as 'contains'
       value = the value of the filter field
    */
    function isItemFiltered(ArtistSongRelationshipTO $artistSongRelationshipTO, $filterRules) {
        $filtered = false;
        if ($filterRules != null) {
            $nr = 0;
            foreach ($filterRules as $item) {
                switch ($item->field){
                    case "newArtist":
                        if (strpos(strtoupper($artistSongRelationshipTO->newArtist), strtoupper($item->value)) !== false) {
                            $nr++;
                        }
                        break;
                    case "newSong":
                        if (strpos(strtoupper($artistSongRelationshipTO->newSong), strtoupper($item->value)) !== false) {
                            $nr++;
                        }
                        break;
                    default:
                        // filter Rule set but not checking on it, threat it as it does not have to be filtered
                        $nr++;
                        break;
                }
            }
            $filtered = $nr != count($filterRules);
        }
        return $filtered;
    }


    function saveArtistSong(ArtistSongRelationshipTO $artistSongRelationship)
    {
        $counter = 0;
        foreach ($this->artistSongRelationshipObj->items as $key => $value) {
            if (strcmp($value->id, $artistSongRelationship->id) == 0) {
                $this->artistSongRelationshipObj->items[$counter] = $artistSongRelationship;
                writeJSON($this->artistSongRelationshipObj, $this->file);
                $artistSongForm = $this->convertToArtistSongForm(new MultiArtistBO(), $artistSongRelationship);
                CacheBO::saveCacheObject(CacheBO::ARTISTSONG, $artistSongRelationship->id, $artistSongForm);
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
        $artistSongForm = $this->convertToArtistSongForm(new MultiArtistBO(), $artistSongRelationship);
        CacheBO::saveCacheObject(CacheBO::ARTISTSONG, $artistSongRelationship->id, $artistSongForm);
    }

    function isArtistUsed($id){
        foreach ($this->artistSongRelationshipObj->items as $key => $value) {
            if (isset($value->newArtistId) && $value->newArtistId == $id){
                return true;
            }
            if (isset($value->oldArtistList)) {
                foreach ($value->oldArtistList as $value) {
                    if ($value->id == $id) {
                        return true;
                    }
                }
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
                CacheBO::clearCacheObject(CacheBO::ARTISTSONG, $id);
                return true;
        }
        return false;
    }
}

?>