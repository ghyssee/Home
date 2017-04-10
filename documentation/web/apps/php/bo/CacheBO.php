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
        $cache = $_SESSION["cache"];
        return isset($cache[$id]);
    }

    static function clearCache ($id){
        unset($_SESSION["cache"][$id]);
        $tmp = "blabla";
    }

    static function clear (){
        unset($_SESSION["cache"]);
    }

    static function saveCacheObject($cacheId, $id, $object){
        $_SESSION["cache"][$cacheId][$id] = $object;
    }

    static function clearCacheObject($cacheId, $id){
        unset($_SESSION["cache"][$cacheId][$id]);
    }

    static function saveObject($id, $object){
        $_SESSION["cache"][$id] = $object;
    }
}