<?php

/**
 * Created by PhpStorm.
 * User: Gebruiker
 * Date: 21/10/2017
 * Time: 14:03
 */
class AlbumErrorsBO
{
    public $albumErrorsObj;
    public $file;

    function __construct() {
        $this->file = getFullPath(JSON_ALBUMERRORS);
        $this->albumErrorsObj = readJSON( $this->file);
    }

    function getAlbumErrors(){
        $filteredArray = [];
        foreach ($this->albumErrorsObj->items as $key => $value) {
            if (!$value->done) { //} && !$value->process) {
                array_push($filteredArray, $value);
            }
        }
        return $filteredArray;
    }

    public function countSelectedAlbumErrors()
    {
        return count($this->getSelectedAlbumErrors());
    }

    public function getSelectedAlbumErrors2(){
        $filteredArray = [];
        foreach ($this->albumErrorsObj->items as $key => $value) {
            if (!$value->done && $value->process) { //} && !$value->process) {
                array_push($filteredArray, $value);
            }
        }
        return $filteredArray;
    }

    public function getSelectedAlbumErrors(){
        $array = array_filter($this->albumErrorsObj->items, array($this, 'filterNotSelectedRecords'));
        return array_values($array);
    }

    protected function filterNotSelectedRecords($element)
    {
        return !$element->done && $element->process;
    }

}