<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");
require_once documentPath (ROOT_PHP_BO, "ArtistSongRelationshipBO.php");

class ArtistTO{
    public $id;
    public $name;
    public $stageName;
    public $pattern;
    public $priority=1000;
    public $global=false;
    public function __construct()
    {
    }

    public function __construct_2($id, $name)
    {
        $this->id = $id;
        $this->name = $name;
    }
}

class MultiArtistListTO {
    public $id;
    public $exactPosition;
    public $master;
    public $description;
    public $description2;

    function __construct($id, $exactPosition, $master) {
        $this->id = $id;
        $this->exactPosition = $exactPosition;
        $this->master = $master;
    }
}

class ArtistBO
{
    public $artistObj;
    public $file;

    function __construct() {
        $this->file = getFullPath(JSON_ARTISTS);
        $this->artistObj = readJSON( $this->file);
        $this->loadFullData();
    }
    
    function getArtists($filterRules = null){
        $array = [];
        if ($filterRules != null){
            foreach($this->artistObj->list as $key => $value){
                foreach($filterRules as $item){
                    $test = $item;
                    if ($test->field == "name"){
                        if (strpos(strtoupper($value->name), strtoupper($item->value)) !== false) {
                            $array[] = $value;
                        }
                    }
                }

            }
        }
        else {
            $array = $this->artistObj->list;
        }
        return $array;
    }
    function loadFullData(){
        if (CacheBO::isInCache(CacheBO::ARTISTS)){
            $list = CacheBO::getObject(CacheBO::ARTISTS);
        }
        else {
            $list = Array();
            foreach ($this->artistObj->list as $key => $item) {
                $list[$item->id] = $item;
            }
            CacheBO::saveObject(CacheBO::ARTISTS, $list);
        }
        return $this->artistObj->list;
    }


    function lookupArtist($id)
    {
        $this->loadFullData();
        $list = CacheBO::getObject(CacheBO::ARTISTS);
        if (isset($list[$id])){
            return $list[$id];
        }
        return null;
    }

    function lookupArtist2($id)
    {
        foreach ($this->artistObj->list as $key => $value) {
            if (strcmp($value->id, $id) == 0) {
                $singleArtistObj = $value;
                return $singleArtistObj;
            }
        }
        return null;
    }
    function lookupArtistByName($name)
    {
        foreach ($this->artistObj->list as $key => $value) {
            if (strcmp($value->name, $name) == 0) {
                $singleArtistObj = $value;
                return $singleArtistObj;
            }
        }
        return null;
    }

    function saveArtist($artist)
    {
        $this->checkArtistRecord($artist);
        $counter = 0;
        foreach ($this->artistObj->list as $key => $value) {
            if (strcmp($value->id, $artist->id) == 0) {
                $this->artistObj->list[$counter] = $artist;
                writeJSON($this->artistObj, $this->file);
                CacheBO::saveCacheObject(CacheBO::ARTISTS, $artist->id,$artist);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function checkArtistRecord($artist){
        $artist->stageName = nullIfEmpty($artist->stageName);
    }

    function addArtist($artist)
    {
        $artist->id = getUniqueId();
        $this->checkArtistRecord($artist);
        array_push($this->artistObj->list, $artist);
        writeJSON($this->artistObj, $this->file);
        CacheBO::saveCacheObject(CacheBO::ARTISTS, $artist->id,$artist);
    }

    function deleteArtist($id)
    {
        $array = $this->artistObj->list;
        $key = array_search($id, array_column($array, 'id'));
        if ($key === false || $key === NULL) {
            return false;

        } else {
            $multiArtistBO = new MultiArtistBO();
            $artistSongRelationshipBO = new ArtistSongRelationshipBO();
            if (!$multiArtistBO->isArtistUsed($id) && !$artistSongRelationshipBO->isArtistUsed($id)) {
                unset($this->artistObj->list[$key]);
                $array = array_values($this->artistObj->list);
                $this->artistObj->list = $array;
                writeJSON($this->artistObj, $this->file);
                CacheBO::clearCacheObject(CacheBO::ARTISTS, $id);
                return true;
            }
        }
        return false;
    }
}

class ArtistItem{
    public $id;
    public function __construct($id)
    {
        $this->id = $id;
    }
}

class ArtistItemForm extends ArtistItem {
    public $name;
    public function __construct($id, $name)
    {
        $this->id = $id;
        $this->name = $name;
    }
}

class MultiArtist extends Castable {
    public $id;
    public $exactPosition;
    public $master;
    public $artists;
    public $artistSequence;

    public function __construct($object = null)
    {
        parent::__construct($object);
        if ($object == null) {
            $this->artists = array();
            $this->artistSequence = array();
            $this->exactPosition = false;
        }
    }
}

class ArtistSequence{
    public $artistId;
    public $splitterId;
    
    public function __construct($artistId, $splitterId)
    {
        $this->artistId = $artistId;
        $this->splitterId = $splitterId;
    }
}

class ArtistSequenceForm{
    public $artistName;
    public $splitterName;
    
    public function __construct($artistId, $artistName, $splitterId, $splitterName)
    {
        $this->artistId = $artistId;
        $this->splitterId = $splitterId;
        $this->artistName = $artistName;
        $this->splitterName = $splitterName;
    }
}

class Delimiter extends Castable {
    public $id;
    public $value1;
    public $value2;
}

abstract class MasterType
{
    const ARTIST = "artists";
    const SEQUENCE = "artistSequence";
}

class MultiArtistBO {
    public $multiArtistObj;
    public $file;
    public $artistBO;

    function __construct() {
    }
    
    function getArtistBO(){
        if (isset($this->artistBO)){
            return $this->artistBO;
        }
        else {
            $this->artistBO = new ArtistBO();
        }
        return $this->artistBO;
    }
    
    function loadData(){
        if (!isset($this->file)){
            $this->file = getFullPath(JSON_MULTIARTIST);
            logInfo(getCurrentTime() . " STARTED: Loading file " . $this->file);
            $this->multiArtistObj = readJSON($this->file);
            logInfo(getCurrentTime() . " ENDED: Loading file " . $this->file);
        }
        return $this->multiArtistObj;
    }

    function findMultiArtist($id){
        $multiArtistObj = $this->loadData();
        foreach ($multiArtistObj->list as $key => $item) {
            if ($item->id == $id){
                return new MultiArtist($item);
            }
        }
        return null;
    }

    function filterData($filterRules, $list){
        $array = [];
        if ($filterRules != null){
            foreach($list as $key => $value){
                foreach($filterRules as $item){
                    $test = $item;
                    if ($test->field == "description2"){
                        if (strpos(strtoupper($value->description2), strtoupper($item->value)) !== false) {
                            $array[] = $value;
                        }
                    }
                }

            }
        }
        return $array;
    }

    function loadFullData(){
        if (CacheBO::isInCache(CacheBO::MULTIARTIST2)){
            $list = CacheBO::getObject(CacheBO::MULTIARTIST2);
        }
        else {
            $multi = $this->loadData();
            $list = Array();
            foreach ($multi->list as $key => $item) {
                $multiArtistTO = $this->convertToMultiArtistTO($item);
                $list[$item->id] = $multiArtistTO;
            }
            CacheBO::saveObject(CacheBO::MULTIARTIST2, $list);
        }
        $list = array_values($list);
        return $list;
    }

    function isArtistUsed($artistId){
        $this->loadData();
        foreach($this->multiArtistObj->list as $mulitArtistItem){
            foreach($mulitArtistItem->artists as $artist){
                if ($artist->id == $artistId){
                    return true;
                }
                foreach($mulitArtistItem->artistSequence as $artist) {
                    if ($artist->artistId == $artistId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    function deleteMultiAristConfig($id){
        $this->loadData();
        $array = $this->multiArtistObj->list;
        $key = array_search($id, array_column($array, 'id'));
        if ($key === false || $key === NULL) {
            return false;

        } else {
            $artistSongRelationshipBO = new ArtistSongRelationshipBO();
            if (!$artistSongRelationshipBO->isMultiArtistUsed($id)) {
                unset($this->multiArtistObj->list[$key]);
                $array = array_values($this->multiArtistObj->list);
                $this->multiArtistObj->list = $array;
                writeJSON($this->multiArtistObj, $this->file);
                //CacheBO::clearCache(CacheBO::MULTIARTIST2);
                CacheBO::clearCacheObject(CacheBO::MULTIARTIST2, $id);
                return true;
            }
        }
        return false;

    }

    function saveMultiAristConfig($multiArtistItem)
    {
        $this->loadData();
        $array = $this->multiArtistObj->list;
        $key = array_search($multiArtistItem->id, array_column($array, 'id'));
        if ($key === false || $key === NULL) {
            return false;
        } else {
            $multiArtistItemtoUpdate = $this->multiArtistObj->list[$key];
            $multiArtistItemtoUpdate->exactPosition = $multiArtistItem->exactPosition;
            //CacheBO::clearCache(CacheBO::MULTIARTIST2);
            CacheBO::saveCacheObject(CacheBO::MULTIARTIST2, $multiArtistItem->id,$this->convertToMultiArtistTO($multiArtistItemtoUpdate));
            writeJSON( $this->multiArtistObj, $this->file);
        }
        return true;
    }

    function getDelimterText(){
        $this->loadData();
        $text = "/(";
        $first = true;
        foreach($this->multiArtistObj->splitters as $delimiter){
            if ($delimiter->id === $this->multiArtistObj->splitterEndId){
                continue;
            }
            $text .= ($first ? "" : "|") . $delimiter->value2;
            $first = false;
        }
        $text .= ")/";
        return $text;
    }

    function buildArtists($multiArtistTxt, $errorObj, &$artistArray){
        //$array = preg_split("/(, |Feat. )/", $multiArtistTxt);
        $text = $this->getDelimterText();
        $array = preg_split($text, $multiArtistTxt);
        if (count($array) == 1){
            $errorObj->errorMsg = 'Could not find a splitter in the config';
            $errorObj->errorFound = true;
        }
        else {
            $artistBO = new ArtistBO();
            foreach ($array as $key=>$value){
                // check if artist exist, if not add it;
                $value = trim($value);
                $singleArtistObj = $artistBO->lookupArtistByName($value);
                if ($singleArtistObj === null){
                    $artist = new ArtistTO();
                    $artist->name = $value;
                    $artistArray[] = $artist;
                }
                else {
                    $artistArray[] = $singleArtistObj;
                }
            }
        }
    }

    function buildDelimiters($multiArtistTxt, $errorObj, $artistArray, &$delimiterArray)
    {
        $this->loadData();
        $multiArtistTxt = $multiArtistTxt;
        $searchStr = '/(';
        $first = true;
        foreach ($artistArray as $value) {
            $searchStr .= ($first ? '' : '|') . preg_quote($value->name);
            $first = false;
        }
        $searchStr .= ')/';
        $array = preg_split($searchStr, $multiArtistTxt);
        if (count($array) < 2) {
            $errorObj->errorMsg = 'Could not find a Delimiter in the string or Problem with Config Text';
            $errorObj->errorFound = true;
        } else {
            array_shift($array); // remove the first element, always empty
            array_pop($array); // remove last element always empty
            if ((count($array) + 1) != count($artistArray)) {
                $errorObj->errorMsg = 'Number of delimiters and artists does not match';
                $errorObj->errorFound = true;
            } else {
                foreach ($array as $delimiter) {
                    $delimiterObj = $this->lookupDelimiterByValue($delimiter);
                    if ($delimiterObj === null) {
                        $errorObj->errorMsg = 'Delimiter not found: ' . $delimiter;
                        $errorObj->errorFound = true;
                        break;
                    } else {
                        $delimiterArray[] = $delimiterObj;
                    }
                }
                $delimiterArray[] = $this->lookupDelimiter($this->multiArtistObj->splitterEndId);
            }
        }
    }

        function lookupDelimiterByValue($delimiter)
        {
            $this->loadData();
            //$file = $GLOBALS['fileArtist'];
            //$obj = readJSON($file);
            foreach ($this->multiArtistObj->splitters as $key => $value) {
                if (strcmp($value->value2, $delimiter) == 0) {
                    $delimiterObj = $value;
                    return $delimiterObj;
                }
            }
            return null;
        }

        function lookupDelimiter($id)
        {
            $this->loadData();
            //$file = $GLOBALS['fileArtist'];
            //$obj = readJSON($file);
            foreach ($this->multiArtistObj->splitters as $key => $value) {
                if (strcmp($value->id, $id) == 0) {
                    return new Delimiter($value);
                }
            }
            return null;
        }

    function constructMultiArtistDescription($artistBO, $multiArtist){
        $description = '';
        $first = true;
        foreach($multiArtist->artists as $artistItem) {
            $artistObj = $artistBO->lookupArtist($artistItem->id);
            if ($artistObj === null){
                $description .= ($first ? '' : '|') ."UNKNOWN ARTIST - ID: " . $artistItem->id . '';
            }
            else {
                $description .= ($first ? '' : '|') . $artistObj->name;
            }
            $first = false;
        }
        return $description;
    }
    
    function constructMultiArtistSequeceDescription($artistBO, $multiArtist){
        $this->loadData();
        $description = "";
        foreach($multiArtist->artistSequence as $artistItem) {


            $artistObj = $artistBO->lookupArtist($artistItem->artistId);

            if ($artistObj === null){
                $description .= 'UNKNOWN ARTIST - ID: ' . $artistItem->artistId;
            }
            else {
                $description .= $artistObj->name;
            }

            $splitter = $this->lookupDelimiter($artistItem->splitterId);
            if (isset($splitter)){
                $description .= ($splitter->id === $this->multiArtistObj->splitterEndId ? '' : $splitter->value2);
            }
            else {
                $tmp = '';
            }
            //$description .= ($splitter->id === $this->multiArtistObj->splitterEndId ? '' : $splitter->value2);

        }
       return $description;
    }

    function checkMultiArtistConfigExist($multiArtistLine){
        $this->loadData();
        $nrOfItems = count($multiArtistLine->artists);
        foreach($this->multiArtistObj->list as $multiArtistItem){
            if (count($multiArtistItem->artists) == $nrOfItems){
                $diff = array_udiff($multiArtistItem->artists, $multiArtistLine->artists,
                    function ($obj_a, $obj_b) {
                        return strcmp($obj_a->id, $obj_b->id) ;
                    }
                );
                if (count($diff) == 0) {
                    if (!$multiArtistLine->exactPosition ||
                        $multiArtistLine->exactPosition != $multiArtistItem->exactPosition) {
                        return true;
                    }
                    else {
                        /* check if ordered artists are in the same order, i
                           If yes, than it already exist, else it can be added
                        */
                        foreach ($multiArtistLine->artistSequence as $key=>$item) {
                            if ($item->artistId != $multiArtistItem->artists[$key]->id) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    function getSplitters(){
        $this->loadData();
        return $this->multiArtistObj->splitters;
    }
    
    function convertToMultiArtistTO($object){
        $multiArtistTO = new MultiArtistListTO($object->id, $object->exactPosition, $object->master);
        $multiArtistTO->id = $object->id;

        //$item->description = $multiArtistBO->constructMultiArtistDescription($artistBO, $item);
        //$item->description2 = $multiArtistBO->constructMultiArtistSequeceDescription($artistBO, $item);
        $multiArtistTO->description = $this->constructMultiArtistDescription($this->getArtistBO(), $object);
        $multiArtistTO->description2 = $this->constructMultiArtistSequeceDescription($this->getArtistBO(), $object);
        return $multiArtistTO;
    }

    function fillMultiArtistInfo(MultiArtist $multiArtist){
        if (!isset($multiArtist)){
            return null;
        }
        $artistBO = $this->getArtistBO();
        $artists = Array();
        foreach ($multiArtist->artists as $item){
            $artist = $artistBO->lookupArtist($item->id);
            if ($artist != null) {
                $artistItemForm = new ArtistItemForm($artist->id, $artist->name);
            }
            else {
                $artistItemForm = new ArtistItemForm($artist->id, "UNKNOWN ARTIST");
            }
            $artists[] = $artistItemForm;
        }
        $artistSequence = Array();
        foreach ($multiArtist->artistSequence as $item){
            $artist = $artistBO->lookupArtist($item->artistId);
            $splitter = $this->lookupDelimiter($item->splitterId);
            if ($splitter == null){
                $splitter = new Delimiter();
                $splitter->id = "";
                $splitter->value1 = "UNKNOWN";
                $splitter->value2 = "UNKNOWN";
            }
            if ($artist != null) {
                $artistSequenceForm = new ArtistSequenceForm($artist->id, $artist->name, $splitter->id, $splitter->value2);
            }
            else {
                $artistSequenceForm = new ArtistSequenceForm($artist->id, "UNKNOWN ARTIST",  $splitter->id, $splitter->value2);
            }
            $artistSequence[] = $artistSequenceForm;
        }
        $multiArtist->artists = $artists;
        $multiArtist->artistSequence = $artistSequence;
        return $multiArtist;
    }

    function updateMultiArtist($multiArtistItem){
        $this->loadData();
        $counter = 0;
        foreach ($this->multiArtistObj->list as $key => $value) {
            if (strcmp($value->id, $multiArtistItem->id) == 0) {
                $this->multiArtistObj->list[$counter] = $multiArtistItem;
                $this->commit();
                CacheBO::saveCacheObject(CacheBO::MULTIARTIST2, $multiArtistItem->id, $this->convertToMultiArtistTO($multiArtistItem));
                return true;
            }
            $counter++;
        }
        return false;
    }
    
    function commit(){
        writeJSON($this->multiArtistObj, $this->file);
    }

    function addMultiArtist($multiArtistItem){
        $this->loadData();
        $multiArtistItem->id = getUniqueId();
        $this->multiArtistObj->list[] = $multiArtistItem;
        $this->commit();
        CacheBO::saveCacheObject(CacheBO::MULTIARTIST2, $multiArtistItem->id, $this->convertToMultiArtistTO($multiArtistItem));
        //CacheBO::clearCache(CacheBO::MULTIARTIST2);
    }

        function addMultiAristConfig($multiArtistTxt)
        {
            $this->loadData();
            $errorObj = new stdClass();
            $errorObj->success = false;
            $errorObj->errorFound = false;
            $errorObj->message = '';
            $errorObj->errorMsg = '';
            $errorObj->artistsAdded = [];
            $errorObj->multiArtist = null;
            $artistArray = '';
            $delimiterArray = '';
            $this->buildArtists($multiArtistTxt, $errorObj, $artistArray);
            if (!$errorObj->errorFound) {
                $this->buildDelimiters($multiArtistTxt, $errorObj, $artistArray, $delimiterArray);
            }
            if (!$errorObj->errorFound) {
                $multiArtist = new MultiArtist();
                $multiArtist->master = MULTIARTIST_RADIO_ARTISTSEQUENCE;
                $artistBO = new ArtistBO();
                foreach ($artistArray as $key=>$artist){
                    // check if artist exist, if not add it;
                    $singleArtistObj = $artistBO->lookupArtistByName($artist->name);
                    if ($singleArtistObj === null){
                        $artistBO->addArtist($artist);
                        $errorObj->artistsAdded[] = 'ADDED Artist: ' . $artist->name;
                    }
                    $artistItem = new ArtistItem($artist->id);
                    $artistSequence = new ArtistSequence($artist->id, $delimiterArray[$key]->id);
                    $multiArtist->artists[] = $artistItem;
                    $multiArtist->artistSequence[] = $artistSequence;
                }
                if ($this->checkMultiArtistConfigExist($multiArtist)){
                    $errorObj->errorMsg = 'Multi Artist Config Already Exists';
                    $errorObj->errorFound = true;
                }
                else {
                    //$this->addMultiArtist($multiArtist);
                    $errorObj->multiArtist = $multiArtist;
                    $errorObj->success = true;
                    $errorObj->message = "Multi Artist Configuration Added" . PHP_EOL;
                    $errorObj->message .= json_encode($errorObj->artistsAdded,JSON_PRETTY_PRINT) . PHP_EOL;
                    $errorObj->message .= json_encode($errorObj->multiArtist,JSON_PRETTY_PRINT);
                }
            }
            return $errorObj;
        }
    }

?>

