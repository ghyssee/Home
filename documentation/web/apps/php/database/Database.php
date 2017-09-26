<?php
require_once("../setup.php");
include_once documentPath (ROOT_PHP_BO, "MyClasses.php");

/* Just extend the class, add our method */

class CustomDatabase extends PDO
{
    public $dbName;

    function __construct($id) {
        //parent::__construct();
        $this->dbName = $this->openDatabase($id);
        parent::__construct('sqlite:' . $this->dbName);
    }

    /* A neat way to see which tables are inside a valid sqlite file */
    public function getTables()  {
        $tables=array();
        $q = $this->query(sprintf("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name"));
        $result = $q->fetchAll();
        foreach($result as $tot_table) {
            $tables[]=$tot_table['name'];
        }
        return($tables);
    }


    function lookupDatabase($list, $id)
    {
        foreach ($list as $dbObj) {
            if ($dbObj->id == $id) {
                return $dbObj;
            }
        }
        return null;
    }
    function openDatabase2($id)
    {
        $dbFile = getFullPath(JSON_DATABASE);
        $dbLocal = getFullPath(JSON_LOCAL_DATABASE);
        $hostname = gethostname();
        $dbLocal = str_replace("%HOST%", $hostname, $dbLocal);
        $dbObj = null;
        if (file_exists($dbLocal)) {
            $localDbConfig = readJSON($dbLocal);
            $dbObj = lookupDatabase($localDbConfig->databases, $id);
        }
        if ($dbObj == null) {
            $localDbConfig = readJSON($dbFile);
            $dbObj = lookupDatabase($localDbConfig->databases, $id);
        }
        if ($dbObj == null) {
            throw new ApplicationException("NO DB Definition found for " . $id);
            exit(0);
        }
        $database = $dbObj->path . $dbObj->name;

        if (file_exists($database)) {
            $db = new MezzmoSQLiteDatabase("sqlite:" . $database);
            return $db;
        } else {
            throw new ApplicationException("DB does not exist: " . $database);
        }
    }
    
    function openDatabase($id)
    {
        $dbFile = getFullPath(JSON_DATABASE);
        $dbLocal = getFullPath(JSON_LOCAL_DATABASE);
        $hostname = gethostname();
        $dbLocal = str_replace("%HOST%", $hostname, $dbLocal);
        $dbObj = null;
        if (file_exists($dbLocal)) {
            $localDbConfig = readJSON($dbLocal);
            $dbObj = $this->lookupDatabase($localDbConfig->databases, $id);
        }
        if ($dbObj == null) {
            $localDbConfig = readJSON($dbFile);
            $dbObj = $this->lookupDatabase($localDbConfig->databases, $id);
        }
        if ($dbObj == null) {
            throw new ApplicationException("NO DB Definition found for " . $id);
            exit(0);
        }
        $database = $dbObj->path . $dbObj->name;
        if (!file_exists($database)) {
            throw new ApplicationException("DB does not exist: " . $database);
        }
        return $database;
    }
}

function getBoolean($result, $field){
    $boolean = $result[$field];
    if (!isset($boolean)){
        return false;
    }
    return ($boolean == 1);
}


?>