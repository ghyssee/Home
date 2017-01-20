<?php
require_once ("../config.php");
require_once ("../model/HTML.php");
$file = getFullPath(JSON_MP3PRETTIFIER);

class WordBO
{
    function lookupWord($words, $id)
    {
        foreach ($words as $key => $value) {
            if (strcmp($value->id, $id) == 0) {
                $word = $value;
                return $word;
            }
        }
        return null;
    }

    function saveGlobalWord($word)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $counter = 0;
        foreach ($obj->words as $key => $value) {
            if (strcmp($value->id, $word->id) == 0) {
                $obj->words[$counter] = $word;
                writeJSON($obj, $file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addGlobalWord($word)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $word->id = getUniqueId();
        array_push($obj->words, $word);
        writeJSON($obj, $file);
    }

    function deleteGlobalWord($field, $id)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file, JSON_ASSOCIATIVE);
        $array = $obj["words"];
        $key = array_search($field, array_column($array, 'id'));
        if ($key === false) {
            return false;

        } else {
            unset($obj["words"][$key]);
            $array["words"] = array_values($obj['words']);
            $obj["words"] = $array["words"];
            writeJSON( $obj, $file);
        }
        return true;

    }

    function deleteGlobalWord2($id)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $key = array_search($id, array_column($obj->words, 'id'));
        if ($key === false) {
            return false;

        } else {
            unset($obj->words[$key]);
            writeJSON($obj, $file);
        }
        return true;

    }
}
?>
