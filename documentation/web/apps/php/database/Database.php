<?php
require_once("../setup.php");
include_once documentPath (ROOT_PHP_BO, "MyClasses.php");

/* Just extend the class, add our method */

function lookupDatabase ($list, $id){
    foreach($list as $dbObj){
        if ($dbObj->id == $id){
            return $dbObj;
        }
    }
    return null;
}

function openDatabase($id)
{
    $dbFile = getFullPath(JSON_DATABASE);
    $dbLocal = getFullPath(JSON_LOCAL_DATABASE);
    $hostname = gethostname();
    $dbLocal = str_replace("%HOST%", $hostname, $dbLocal);
    $dbObj = null;
    if (file_exists($dbLocal)){
        $localDbConfig = readJSON($dbLocal);
        $dbObj = lookupDatabase($localDbConfig->databases, $id);
    }
    if ($dbObj == null){
        $localDbConfig = readJSON($dbFile);
        $dbObj = lookupDatabase($localDbConfig->databases, $id);
    }
    if ($dbObj == null){
        throw new ApplicationException("NO DB Definition found for " . $id);
        exit(0);
    }
    $database = $dbObj->path . $dbObj->name;

    if (file_exists($database)) {
        $db = new MezzmoSQLiteDatabase("sqlite:" . $database);
        return $db;
    }
    else {
        throw new ApplicationException("DB does not exist: " . $database);
    }
}

?>