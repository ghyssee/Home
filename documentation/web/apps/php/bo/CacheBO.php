<?php

/**
 * Created by PhpStorm.
 * User: Gebruiker
 * Date: 7/04/2017
 * Time: 18:56
 */
class CacheBO
{
    const MULTIARTIST2 = 'MULTIARTIST2';
    const ARTISTS = "ARTISTS";
    const ARTISTSONG = "ARTISTSONG";
    const MP3PRETTIFIER = "MP3PRETTIFIER";

    static function getObject ($id){
        $cache = $_SESSION["cache"];
        if (isset($cache[$id])){
            return $cache[$id];
        }
        else {
            return null;
        }
    }
    
    static function isInCache ($id){
        //return false;
        if (session_status() == PHP_SESSION_NONE) {
            return false;
        }
        if (isset($_SESSION["cache"])){
            $cache = $_SESSION["cache"];
            return isset($cache[$id]);
        }
        return false;
    }

    static function clearCache ($id){
        unset($_SESSION["cache"][$id]);
        logInfo("Clearing Cache " . $id);
    }

    static function clear (){
        unset($_SESSION["cache"]);
        logInfo("Clearing Cache...");
    }

    static function saveCacheObject($cacheId, $id, $object){
        if (self::isInCache($cacheId)) {
            $_SESSION["cache"][$cacheId][$id] = $object;
            logInfo("Save Cache Object " . $cacheId . " / Id = " . $id);
        }
    }

    static function clearCacheObject($cacheId, $id){
        if (self::isInCache($cacheId)) {
            unset($_SESSION["cache"][$cacheId][$id]);
            logInfo("Clear Cache Object " . $cacheId . " / Id = " . $id);
        }
    }

    static function saveObject($id, $object){
        $_SESSION["cache"][$id] = $object;
        logInfo("Saving Cache Object " . $id);
    }
}