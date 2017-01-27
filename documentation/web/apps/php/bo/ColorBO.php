<?php
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_HTML, "HTML.php");

$htmlFile = getFullPath(JSON_HTML);

class ColorBO
{
    function lookupColor($id)
    {
        $file = $GLOBALS['htmlFile'];
        $htmlObj = readJSON($file);
        foreach ($htmlObj->colors as $key => $value) {
            if (strcmp($value->id, $id) == 0) {
                $color = $value;
                return $color;
            }
        }
        return null;
    }

    function saveColor($color)
    {
        $file = $GLOBALS['htmlFile'];
        $htmlObj = readJSON($file);
        $counter = 0;
        foreach ($htmlObj->colors as $key => $value) {
            if (strcmp($value->id, $color->id) == 0) {
                $htmlObj->colors[$counter] = $color;
                writeJSON($htmlObj, $file);
                return true;
            }
            $counter++;
        }
        return false;
    }

    function addColor($color)
    {
        $file = $GLOBALS['htmlFile'];
        $htmlObj = readJSON($file);
        $color->id = getUniqueId();
        array_push($htmlObj->colors, $color);
        writeJSON($htmlObj, $file);
    }

    function deleteColor($id)
    {
        $file = $GLOBALS['htmlFile'];
        $htmlObj = readJSON($file);
        $key = array_search($id, array_column($htmlObj->colors, 'id'));
        if ($key === false) {
            return false;

        } else {
            unset($htmlObj->colors[$key]);
            writeJSON($htmlObj, $file);
        }
        return true;

    }

}
?>
