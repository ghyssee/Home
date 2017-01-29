<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");

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

    function saveGlobalWord2($word)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file, JSON_ASSOCIATIVE);
        $counter = 0;
        foreach ($obj['global']['words'] as $key => $value) {
            if (strcmp($value->id, $word->id) == 0) {
                $obj['global']['words'][$counter] = $word;
                writeJSON($obj, $file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function saveGlobalWord($word)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $counter = 0;
        foreach ($obj->{'global'}->{'words'} as $key => $value) {
            if (strcmp($value->id, $word->id) == 0) {
                $obj->{'global'}->{'words'}[$counter] = $word;
                writeJSON($obj, $file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addGlobalWord1($word)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file, JSON_ASSOCIATIVE);
        $word->id = getUniqueId();
        array_push($obj['global']['words'], $word);
        writeJSON($obj, $file);
    }

    function addGlobalWord($word)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $word->id = getUniqueId();
        array_push($obj->{'global'}->{'words'}, $word);
        writeJSON($obj, $file);
    }

    function deleteGlobalWord3($field, $id)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file, JSON_ASSOCIATIVE);
        $array = $obj['global']["words"];
        $key = array_search($field, array_column($array, 'id'));
        if ($key === false) {
            return false;

        } else {
            unset($obj['global']["words"][$key]);
            $array = array_values($obj['global']['words']);
            $obj['global']["words"] = $array;
            writeJSON( $obj, $file);
        }
        return true;

    }

    function deleteGlobalWord($field, $id)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $key = array_search($field, array_column($obj->{'global'}->{'words'}, 'id'));
        if ($key === false) {
            return false;

        } else {
            unset($obj->{'global'}->{'words'}[$key]);
            writeJSON($obj, $file);
        }
        return true;


    }

}
?>
