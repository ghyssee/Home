<?php
require_once("../setup.php");
require_once documentPath (ROOT_PHP_DATABASE, "Database.php");
include_once documentPath (ROOT_PHP_BO, "MyClasses.php");

const ERIC = "ERIC";

/* Just extend the class, add our method */
class EricSQLiteDatabase extends CustomDatabase {

    public $db;

    function __construct($id) {
        parent::__construct($id);
    }



    public function getMezzmoFileColumns(){
        $cols = "FILE.Id                 AS ID, ".
                "FILE.ArtistId           AS ARTISTID, ".
                "FILE.ArtistName         AS ARTISTNAME, " .
                "FILE.IsNew              AS ISNEW, " .
                "FILE.STATUS             AS STATUS ";
        return $cols;
    }

    public function convertToMezzmoFileTO($result){
        if ($result == null) return null;
        $mezzmoFileTO = new MezzmoFileTO();
        $mezzmoFileTO->id = $result['ID'];
        $mezzmoFileTO->artistId = $result['ARTISTID'];
        $mezzmoFileTO->artistName = $result['ARTISTNAME'];
        $mezzmoFileTO->isNew = getBoolean($result, 'ISNEW');
        $mezzmoFileTO->status = $result['STATUS'];
        return $mezzmoFileTO;
    }

    public function findMezzmoFileById($id){

        $query = "SELECT " .
            $this->getMezzmoFileColumns() .
            "FROM MezzmoFile AS FILE " .
            "WHERE FILE.id = :id ";
        $sth = $this->prepare($query, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
        $sth->execute(array(':id' => $id));
        $result = $sth->fetchAll();
        $songRec = null;
        $nr = count($result);
        switch ($nr) {
            case 0 :
                //throw new ApplicationException("No result found for id " .$id);
                break;
            case 1:
                $songRec = $result[0];
                break;
            default:
                throw new ApplicationException("Multiple results found for id " . $id);
                break;
        }
        return $songRec;
    }

    public function searchSong($id){

        $query = "SELECT " .
            $this->getFileColumns() . "," .
            $this->getAlbumColumns() . "," .
            $this->getArtistColumns() .
            "FROM MGOFile AS FILE " .
            $this->joinAlbum() .
            $this->joinArtist() .
            $this->joinAlbumArtist() .
            $this->joinFileExtension() .
            "WHERE  FILEEXTENSION.data = 'mp3' " .
            "AND FILEARTIST.id = :id ";
//        $result = $this->query($query);
        $sth = $this->prepare($query, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
        $sth->execute(array(':id' => $id));
        $result = $sth->fetchAll();
        return $result;
    }


}

?>