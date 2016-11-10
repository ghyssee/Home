<?php
require_once ("../config.php");
require_once ("../model/HTML.php");

class ColorBO
{
    function lookupColor($id)
    {
        $htmlObj = readJSON($GLOBALS['oneDrivePath'] . '/Config/Java/HTML.json');
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
        $file = $GLOBALS['oneDrivePath'] . '/Config/Java/HTML.json';
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

}
?>
