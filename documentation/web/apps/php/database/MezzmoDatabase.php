<?php
include_once("../model/HTML.php");

/* Just extend the class, add our method */
class MezzmoSQLiteDatabase extends PDO {

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

    public function getFileColumns(){
        $cols = "FILE.id             AS ID, ".
                "FILE.FILE           AS FILE, ".
                "FILE.title          AS TITLE, ".
                "FILE.playcount      AS PLAYCOUNT, ".
                "FILE.ranking        AS RANKING, ".
                "FILE.disc           AS DISC, ".
                "FILE.track          AS TRACK, ".
                "FILE.filetitle      AS FILETITLE, ".
                "FILE.datelastplayed AS DATELASTPLAYED, ".
                "FILE.duration       AS DURATION, ".
                "FILE.extensionid    AS EXTENSION_ID, ".
                "FILE.year           AS YEAR, ".
                "FILE.sorttitle      AS SORTTILE ";
        return $cols;
    }

    public function getAlbumColumns(){
        $cols = "FILEALBUM.id        AS ALBUMID, ".
                "FILEALBUM.data      AS ALBUM ";
        return $cols;
    }

    public function getArtistColumns(){
        $cols = "FILEARTIST.id       AS ARTISTID, ".
                "FILEARTIST.data     AS ARTIST ";
            ;
        return $cols;
    }
    public function convertToSongUpdateObj($result){
        $songObj = new SongCorrection();
        $songObj->fileId = $result['ID'];
        $songObj->title = $result['TITLE'];
        $songObj->artist = $result['ARTIST'];
        $songObj->track = $result['TRACK'];
        return $songObj;
    }

    public function getMezzmoSong($id){

        $query = "SELECT " .
                  $this->getFileColumns() . "," .
                  $this->getAlbumColumns() . "," .
                  $this->getArtistColumns() .
                 "FROM MGOFile AS FILE " .
                 "INNER JOIN mgofilealbum AS FILEALBUM " .
                 "ON ( FILEALBUM.id = FILEALBUMREL.id ) ".
                 "INNER JOIN mgofilealbumrelationship AS FILEALBUMREL " .
                 "ON ( FILEALBUMREL.fileid = FILE.id ) ".
                 "INNER JOIN mgofileartist AS FILEARTIST " .
                 "ON ( FILEARTIST.id = FILEARTISTREL.id ) " .
                 "INNER JOIN mgofileartistrelationship AS FILEARTISTREL " .
                 "ON ( FILEARTISTREL.fileid = FILE.id ) " .
                 "INNER JOIN mgoalbumartist AS ALBUMARTIST " .
                 "ON ( ALBUMARTIST.id = ALBUMARTISTREL.id ) ".
                 "INNER JOIN mgoalbumartistrelationship AS ALBUMARTISTREL " .
                 "ON ( ALBUMARTISTREL.fileid = FILEALBUMREL.fileid ) " .
                 "INNER JOIN mgofileextension AS FILEEXTENSION " .
                 "ON ( FILEEXTENSION.id = FILE.extensionid ) ".
                 "WHERE  FILEEXTENSION.data = 'mp3' " .
                 "AND FILE.id = :id ";
//        $result = $this->query($query);
        $sth = $this->prepare($query, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
        $sth->execute(array(':id' => $id));
        $result = $sth->fetchAll();
        $songRec = null;
        $nr = count($result);
        switch ($nr) {
            case 0 :
                throw new Exception("No result found for id " .$id);
                break;
            case 1:
                $songRec = $result[0];
                break;
            default:
                throw new Exception("Multiple results found for id " . $id);
                break;
        }
        return $songRec;
    }


}

function openDatabase()
{
    $database = "C:/My Data/Mezzmo/mezzmo.db";

    if (file_exists($database)) {
        $db = new MezzmoSQLiteDatabase("sqlite:" . $database);
        return $db;
    }
    else {
        throw error("DB does not exist: " . $database);
    }
}

?>