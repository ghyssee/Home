<?php
require_once("../setup.php");
require_once documentPath (ROOT_PHP_DATABASE, "Database.php");
include_once documentPath (ROOT_PHP_BO, "MyClasses.php");

const MEZZMO = "MEZZMO";
const MEZZMOV2 = "MEZZMOV2";

/* Just extend the class, add our method */
class MezzmoSQLiteDatabase extends CustomDatabase {

    public $db;

    function __construct($id) {
        parent::__construct($id);
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

    public function convertToSongObj($result){
        $songObj = new SongCorrection();
        $songObj->fileId = $result['ID'];
        $songObj->title = $result['TITLE'];
        $songObj->artist = $result['ARTIST'];
        $songObj->artistId = $result['ARTISTID'];
        $songObj->albumId = $result['ALBUMID'];
        $songObj->album = $result['ALBUM'];
        $songObj->file = $result['FILE'];
        return $songObj;
    }

    public function joinAlbum(){
        $join = "INNER JOIN mgofilealbum AS FILEALBUM " .
                 "ON ( FILEALBUM.id = FILEALBUMREL.id ) " .
                 "INNER JOIN mgofilealbumrelationship AS FILEALBUMREL " .
                 "ON ( FILEALBUMREL.fileid = FILE.id ) ";
        return $join;
    }

    public function joinArtist(){
        $join = "INNER JOIN mgofileartist AS FILEARTIST " .
                "ON ( FILEARTIST.id = FILEARTISTREL.id ) " .
                "INNER JOIN mgofileartistrelationship AS FILEARTISTREL " .
                "ON ( FILEARTISTREL.fileid = FILE.id ) " ;
        return $join;
    }

    public function joinAlbumArtist(){
        $join = "INNER JOIN mgoalbumartist AS ALBUMARTIST " .
                "ON ( ALBUMARTIST.id = ALBUMARTISTREL.id ) ".
                "INNER JOIN mgoalbumartistrelationship AS ALBUMARTISTREL " .
                "ON ( ALBUMARTISTREL.fileid = FILEALBUMREL.fileid ) " ;
        return $join;
    }

    public function joinFileExtension(){
        $join = "INNER JOIN mgofileextension AS FILEEXTENSION " .
                "ON ( FILEEXTENSION.id = FILE.extensionid ) ";
        return $join;
    }

    public function getMezzmoSong($id){

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
                 "AND FILE.id = :id ";
//        $result = $this->query($query);
        $sth = $this->prepare($query, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
        $sth->execute(array(':id' => $id));
        $result = $sth->fetchAll();
        $songRec = null;
        $nr = count($result);
        switch ($nr) {
            case 0 :
                throw new ApplicationException("No result found for id " .$id);
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

    public function searchSong($song){

        $query = "SELECT " .
            $this->getFileColumns() . "," .
            $this->getAlbumColumns() . "," .
            $this->getArtistColumns() .
            "FROM MGOFile AS FILE " .
            $this->joinAlbum() .
            $this->joinArtist() .
            $this->joinAlbumArtist() .
            $this->joinFileExtension() .
            "WHERE  FILEEXTENSION.data = 'mp3' ";
//        $result = $this->query($query);
        $array = Array();
        if (isset($song->artistId)){
            $query .= "AND FILEARTIST.id = :id ";
            $array[":id"] =  $song->artistId;
        }
        else if (isset($song->artist)){
            $query .= "AND FILEARTIST.data = :artist ";
            $array[":artist"] =  $song->artist;
        }
        $sth = $this->prepare($query, array(PDO::ATTR_CURSOR => PDO::CURSOR_FWDONLY));
        $sth->execute($array);
        $result = $sth->fetchAll();
        return $result;
    }


}

?>