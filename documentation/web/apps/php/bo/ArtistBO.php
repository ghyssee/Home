<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");

$multiArtistConfig = getFullPath(JSON_MULTIARTIST);

class ArtistBO
{
    public $artistObj;
    public $file;

    function __construct() {
        $this->file = getFullPath(JSON_ARTISTS);
        $this->artistObj = readJSON( $this->file);
    }
    
    function getArtists(){
        return $this->artistObj->list;
    }

    function lookupArtist($id)
    {
        //$file = $GLOBALS['fileArtist'];
        //$obj = readJSON($file);
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
        //$file = $GLOBALS['fileArtist'];
        //$obj = readJSON($file);
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
        $counter = 0;
        foreach ($this->artistObj->list as $key => $value) {
            if (strcmp($value->id, $artist->id) == 0) {
                $this->artistObj->list[$counter] = $artist;
                writeJSON($this->artistObj, $this->file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addArtist($artist)
    {
        $artist->id = getUniqueId();
        array_push($this->artistObj->list, $artist);
        writeJSON($this->artistObj, $this->file);
    }

    function deleteArtist($id)
    {
        $array = $this->artistObj->list;
        $key = array_search($id, array_column($array, 'id'));
        if ($key === false || $key === NULL) {
            return false;

        } else {
            unset($this->artistObj->list[$key]);
            $array = array_values($this->artistObj->list);
            $this->artistObj->list = $array;
            writeJSON($this->artistObj, $this->file);
        }
        return true;

    }
}
class MultiArtistBO {
    public $multiArtistObj;
    public $file;

    function __construct() {
        $this->file = getFullPath(JSON_MULTIARTIST);
        $this->multiArtistObj = readJSON( $this->file);
    }
    function deleteMultiAristConfig($id){
        $array = $this->multiArtistObj->list;
        $key = array_search($id, array_column($array, 'id'));
        if ($key === false || $key === NULL) {
            return false;

        } else {
            unset( $this->multiArtistObj->list[$key]);
            $array = array_values( $this->multiArtistObj->list);
            $this->multiArtistObj->list = $array;
            writeJSON($this->multiArtistObj, $this->file);
        }
        return true;

    }

    function saveMultiAristConfig($multiArtistItem)
    {
        $array = $this->multiArtistObj->list;
        $key = array_search($multiArtistItem->id, array_column($array, 'id'));
        if ($key === false || $key === NULL) {
            return false;
        } else {
            $this->multiArtistObj->list[$key]->exactPosition = $multiArtistItem->exactPosition;
            writeJSON( $this->multiArtistObj, $this->file);
        }
        return true;
    }

    function getDelimterText(){
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
                    $artist = new Artist();
                    $artist->name = $value;
                    println("NOT FOUND: " . $value);
                    $artistArray[] = $artist;
                }
                else {
                    println("FOUND: " . $value);
                    $artistArray[] = $singleArtistObj;
                }
            }
        }
    }

    function buildDelimiters($multiArtistTxt, $errorObj, $artistArray, &$delimiterArray)
    {
        $searchStr = '/(';
        $first = true;
        foreach ($artistArray as $value) {
            $searchStr .= ($first ? '' : '|') . $value->name;
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
                        println("Delimiter found. ID: " . $delimiterObj->id);
                        $delimiterArray[] = $delimiterObj;
                    }
                }
                $delimiterArray[] = $this->lookupDelimiter($this->multiArtistObj->splitterEndId);
            }
        }
    }

        function lookupDelimiterByValue($delimiter)
        {
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
            //$file = $GLOBALS['fileArtist'];
            //$obj = readJSON($file);
            foreach ($this->multiArtistObj->splitters as $key => $value) {
                if (strcmp($value->id, $id) == 0) {
                    $delimiterObj = $value;
                    return $delimiterObj;
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

    function checkMultiArtistConfigExist($multiArtistLine){
        $nrOfItems = count($multiArtistLine->artists);
        foreach($this->multiArtistObj->list as $multiArtistItem){
            if (count($multiArtistItem->artists) == $nrOfItems){
                $diff = array_udiff($multiArtistItem->artists, $multiArtistLine->artists,
                    function ($obj_a, $obj_b) {
                        return strcmp($obj_a->id, $obj_b->id) ;
                    }
                );
                if (count($diff) == 0){
                    return true;
                }
            }
        }
        return false;
    }
    
    function getSplitters(){
        return $this->multiArtistObj->splitters;
    }



    function addMultiArtist($multiArtistItem){
        $multiArtistItem->id = getUniqueId();
        $this->multiArtistObj->list[] = $multiArtistItem;
        writeJSON($this->multiArtistObj, $this->file);
    }

        function addMultiAristConfig($multiArtistTxt)
        {
            $errorObj = new stdClass();
            $errorObj->success = false;
            $errorObj->errorFound = false;
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
                    $this->addMultiArtist($multiArtist);
                    $errorObj->multiArtist = $multiArtist;
                }
            }
            return $errorObj;
        }
    }

?>

