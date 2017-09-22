<?php
require_once("../setup.php");
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_DATABASE, "EricDatabase.php");
require_once "MyClasses.php";

/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 22/09/2017
 * Time: 8:54
 */
class EricBO
{
    public $db;
    function __construct() {
        $this->db = new EricSQLiteDatabase(ERIC);
    }
    
    function findMezzmoFileById($id)
    {
        $result = $this->db->findMezzmoFileById($id);
        $mezzmoFile = $this->db->convertToMezzmoFileTO($result);

        return $mezzmoFile;
    }


}