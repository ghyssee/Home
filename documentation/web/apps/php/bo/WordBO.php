<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");

$file = getFullPath(JSON_MP3PRETTIFIER);
$backupFile = getFullPath(PATH_CONFIG_BACKUP) . "/MP3Prettiffier." . date("Ymd") . ".json";

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

    function backupGlobalWord($word, $type, $category, $mode){
        $file = $GLOBALS['backupFile'];
        $today = date("d/m/Y H:i:s");
        $word->type = $type;
        $word->category = $category;
        $word->timeStamp = $today;
        $word->mode = $mode;
        write($file, json_encode($word, JSON_PRETTY_PRINT + JSON_UNESCAPED_SLASHES, APPEND) . PHP_EOL);
    }

    function saveGlobalWord($word, $type, $category)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $counter = 0;
        foreach ($obj->{$type}->{$category} as $key => $value) {
            if (strcmp($value->id, $word->id) == 0) {
                $obj->{$type}->{$category}[$counter] = $word;
                writeJSON($obj, $file);
                $this->backupGlobalWord($word, $type, $category, "update");
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addGlobalWord($word, $type, $category)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $word->id = getUniqueId();
        array_push($obj->{$type}->{$category}, $word);
        writeJSON($obj, $file);
        $this->backupGlobalWord($word, $type, $category, "add");
    }

    function deleteGlobalWord($field, $id, $type, $category)
    {
        $file = $GLOBALS['file'];
        $obj = readJSON($file);
        $key = array_search($field, array_column($obj->{$type}->{$category}, $id));
        if ($key === false) {
            return false;

        } else {
            $this->backupGlobalWord($obj->{$type}->{$category}[$key], $type, $category, "delete");
            unset($obj->{$type}->{$category}[$key]);
            $array = array_values($obj->{$type}->{$category});
            $obj->{$type}->{$category} = $array;
            writeJSON($obj, $file);
        }
        return true;


    }

}
?>
