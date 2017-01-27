<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_HTML, "HTML.php");

$mp3PreprocessOrObj = getFullPath(JSON_MP3PREPROCESSOR);

class MP3PreprocessorBO
{
    function lookupSplitter($id)
    {
        $file = getFullPath(JSON_MP3PREPROCESSOR);
        $mp3PreprocessOrObj = readJSON($file);
        foreach ($mp3PreprocessOrObj->splitters as $key => $value) {
            if (strcmp($value->id, $id) == 0) {
                $splitter = $value;
                return $splitter;
            }
        }
        return null;
    }

    function saveSplitter($splitter)
    {
        $file = getFullPath(JSON_MP3PREPROCESSOR);
        $mp3PreprocessorObj = readJSON($file);
        $counter = 0;
        foreach ($mp3PreprocessorObj->splitters as $key => $value) {
            if (strcmp($value->id, $splitter->id) == 0) {
                $mp3PreprocessorObj->splitters[$counter] = $splitter;
                writeJSON($mp3PreprocessorObj, $file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addSplitter($splitter)
    {
        $file = getFullPath(JSON_MP3PREPROCESSOR);
        $mp3PreprocessOrObj = readJSON($file);
        $splitter->id = getUniqueId();
        array_push($mp3PreprocessOrObj->splitters, $splitter);
        writeJSON($mp3PreprocessOrObj, $file);
    }

    function deleteSplitter($id)
    {
        $file = getFullPath(JSON_MP3PREPROCESSOR);
        $mp3PreprocessOrObj = readJSON($file);
        $key = array_search($id, array_column($mp3PreprocessOrObj->splitters, 'id'));
        if ($key === false) {
            return false;

        } else {
            unset($mp3PreprocessOrObj->splitters[$key]);
            writeJSON($mp3PreprocessOrObj, $file);
        }
        return true;

    }

}
?>
