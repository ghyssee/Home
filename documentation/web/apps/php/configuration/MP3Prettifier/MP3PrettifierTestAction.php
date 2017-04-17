<?php
chdir("..");
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_BO, "SongBO.php");
require_once documentPath (ROOT_PHP_BO, "MyClasses.php");

session_start();

if (isset($_REQUEST['method'])) {
    $method = htmlspecialchars($_REQUEST['method']);
    try {
        switch ($method) {
            case "list":
                getList();
                break;
        }
    } catch (Error $e) {
        logError($e->getFile(), $e->getLine(), $e->getMessage());
    } catch (ApplicationException $e) {
        logError($e->getFile(), $e->getLine(), $e->getMessage());
        echo 'Caught exception: ', $e->getMessage(), "\n";
    }
}
else if(isset($_POST['settings'])){
    $button = $_POST['settings'];
    switch ($button){
        case "flush":
            flushTestFile();
            break;
        case "add":
            add();
            break;
    }
}
exit(0);

function getList()
{
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $mp3Prettifier = readJSONWithCode(JSON_ARTISTSONGTEST);
    if (isset($_POST['sort'])) {
        $field = isset($_POST['sort']) ? strval($_POST['sort']) : '';
        $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
        $sort = new CustomSort();
        $array = $sort->sortObjectArrayByField($mp3Prettifier->items, $field, $order);
    }
    else {
        $array = $mp3Prettifier->items;
    }
    $array = array_slice($array, ($page - 1) * $rows, $rows);

    $result = array();
    $result["total"] = count($mp3Prettifier->items);
    $result["rows"] = $array;
    echo json_encode($result);
}

function flushTestFile(){
    assignField($number, "number");
    $artistSongObj = readJSONWithCode(JSON_ARTISTSONGTEST);
    $array =  array_slice($artistSongObj->items, $number);
    $artistSongObj->items = $array;
    writeJSONWithCode($artistSongObj, JSON_ARTISTSONGTEST);
    header("Location: " . $_SESSION["previous_location"]);
}

function add(){
    assignField($artist, "artist", !HTML_SPECIAL_CHAR);
    assignField($song, "song", !HTML_SPECIAL_CHAR);
    $save = true;
    if (empty($artist) && empty($song)) {
        addError ('Add', 'At least one field must be filled in');
        $save = false;
    }
    if ($save){
        $artistSongTO = new AristSongTestTO();
        $artistSongTO->oldArtist = $artist;
        $artistSongTO->oldSong = $song;
        $songBO = new SongBO();
        $songBO->saveArtistSongTestItem($artistSongTO);
        
    }
    header("Location: " . $_SESSION["previous_location"]);
}

?>