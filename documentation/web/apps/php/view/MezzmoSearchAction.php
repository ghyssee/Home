<?php
session_start();
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SongBO.php");
include_once documentPath (ROOT_PHP_BO, "EricBO.php");
$albumSave = 'MezzmoSearchAction.php';

$mp3Settings = readJSONWithCode(JSON_MP3SETTINGS);
$songBO =SongBO::db(MEZZMOV3);

$data = array();
if (isset($_POST['submit'])) {
    $data = array();
    $song = new SongTO();
    assignNumber($artistId, "cmbArtistId");
    assignNumber($song->artistId, "artistId");
    assignNumber($song->fileId, "fileId");
    assignField($song->artist, "artistName");
    $listboxArtistId = false;
    if (empty($song->artistId) && empty($song->fileId) && empty($song->artist)){
        $song->artistId = $artistId;
        $listboxArtistId = true;
        writeJSONWithCode($mp3Settings, JSON_MP3SETTINGS);
    }
    if (empty($song->artistId) && empty($song->fileId) && empty($song->artist)){
        echo "all empty";
    }
    else {
        if ($listboxArtistId) {
            $mp3Settings->mezzmo->artistId = $song->artistId;
            writeJSONWithCode($mp3Settings, JSON_MP3SETTINGS);
            $artistArray = $songBO->loadArtistIdsFile();
            $mp3Settings->mezzmo->artistId = getNextArtistId($artistArray, $mp3Settings->mezzmo->artistId);
        }
        $data = $songBO->searchSong($song);
        $songBO->saveArtistSongTest($data);
        $result = array();
        $result["total"] = count($data);
        $result["rows"] = $data;
        echo json_encode($result);    }
}
else if (isset($_REQUEST['method'])) {
    {
        $method = htmlspecialchars($_REQUEST['method']);
        try {
            switch ($method) {
                case "list":
                    getList();
                    break;
                case "search":
                    search();
                    break;
            }
        } catch (Error $e) {
            logError($e->getFile(), $e->getLine(), $e->getMessage());
        } catch (ApplicationException $e) {
            logError($e->getFile(), $e->getLine(), $e->getMessage());
            echo 'Caught exception: ', $e->getMessage(), "\n";
        }
    }
}

function search(){
    $data = array();
    $song = new SongTO();
    assignNumber($artistId, "cmbArtistId");
    assignNumber($song->artistId, "artistId");
    assignNumber($song->fileId, "fileId");
    assignField($song->artist, "artistName");
    $mp3Settings = readJSONWithCode(JSON_MP3SETTINGS);
    $listboxArtistId = false;
    if (empty($song->artistId) && empty($song->fileId) && empty($song->artist)){
        $song->artistId = $artistId;
        $listboxArtistId = true;
        writeJSONWithCode($mp3Settings, JSON_MP3SETTINGS);
    }
    if (empty($song->artistId) && empty($song->fileId) && empty($song->artist)){
        echo "all empty";
    }
    else {
        if ($listboxArtistId) {
            $mp3Settings->mezzmo->artistId = $song->artistId;
            writeJSONWithCode($mp3Settings, JSON_MP3SETTINGS);
            $songBO =SongBO::db(MEZZMOV3);
            $artistArray = $songBO->loadArtistIdsFile();
            $mp3Settings->mezzmo->artistId = getNextArtistId($artistArray, $mp3Settings->mezzmo->artistId);
        }
        $data = $songBO->searchSong($song);
        $songBO->saveArtistSongTest($data);
        $result = array();
        $result["total"] = count($data);
        $result["rows"] = $data;
        echo json_encode($data);
    }
}

    function getList()
    {
        $data = array();
        $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
        $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
        //$mp3Prettifier = readJSONWithCode(JSON_ARTISTSONGTEST);
        if (isset($_POST['sort'])) {
            $field = isset($_POST['sort']) ? strval($_POST['sort']) : '';
            $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
            $sort = new CustomSort();
            //$array = $sort->sortObjectArrayByField($mp3Prettifier->items, $field, $order);
        } else {
            //$array = $mp3Prettifier->items;
        }
        //$array = array_slice($array, ($page - 1) * $rows, $rows);

        $result = array();
        $result["total"] = count($data);
        $result["rows"] = $data;
        echo json_encode($result);
    }

    function getLatestVersion()
    {
        $tmpBO = SongBO::db(MEZZMO);
        $version = $tmpBO->findLatestVersion();
        return $version;
    }

    function getNextArtistId($artistArray, $artistId)
    {
        $next = false;
        foreach ($artistArray as $song) {
            if ($next) {
                $artistId = $song->artistId;
                break;
            }
            if ($song->artistId == $artistId) {
                $next = true;;
            }
        }
        return $artistId;

        function showVersion()
        {
            $version = getLatestVersion();
            $txt = '';
            if ($version != null) {
                $txt = "(Version: " . $version->version . ", " . $version->lastUpdated . ")";
            }
            return $txt;
        }

    }

?>