<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");

$fileArtist = getFullPath(JSON_ARTISTS);

class ArtistBO
{
    function lookupArtist($artists, $id)
    {
        //$file = $GLOBALS['fileArtist'];
        //$obj = readJSON($file);
        foreach ($artists as $key => $value) {
            if (strcmp($value->id, $id) == 0) {
                $artistObj = $value;
                return $artistObj;
            }
        }
        return null;
    }

    function lookupArtistByName($artists, $name)
    {
        //$file = $GLOBALS['fileArtist'];
        //$obj = readJSON($file);
        foreach ($artists as $key => $value) {
            if (strcmp($value->name, $name) == 0) {
                $artistObj = $value;
                return $artistObj;
            }
        }
        return null;
    }

    function saveArtist($artist)
    {
        $file = $GLOBALS['fileArtist'];
        $obj = readJSON($file);
        $counter = 0;
        foreach ($obj->list as $key => $value) {
            if (strcmp($value->id, $artist->id) == 0) {
                $obj->list[$counter] = $artist;
                writeJSON($obj, $file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addArtist($artist)
    {
        $file = $GLOBALS['fileArtist'];
        $obj = readJSON($file);
        $artist->id = getUniqueId();
        array_push($obj->list, $artist);
        writeJSON($obj, $file);
    }

    function deleteArtist($id)
    {
        $file = $GLOBALS['fileArtist'];
        $obj = readJSON($file);
        $array = $obj->list;
        $key = array_search($id, array_column($array, 'id'));
        if ($key === false || $key == NULL) {
            return false;

        } else {
            unset($obj->list[$key]);
            $array = array_values($obj->list);
            $obj->list = $array;
            writeJSON( $obj, $file);
        }
        return true;

    }
}
?>
