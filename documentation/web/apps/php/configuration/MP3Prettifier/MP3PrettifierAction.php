<?php
chdir("..");
include_once "../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_BO, "WordBO.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
include_once documentPath (ROOT_PHP_BO, "CacheBO.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
sessionStart();

$file = getFullPath(JSON_MP3PRETTIFIER);

$method = htmlspecialchars($_REQUEST['method']);
if (isset($_REQUEST['type'])){
    $type = htmlspecialchars($_REQUEST['type']);
}
if (isset($_REQUEST['category'])) {
    $category = htmlspecialchars($_REQUEST['category']);
}
try {
    switch ($method) {
        case "getListArtists":
            getListArtists();
            break;
        case "addArtist":
            addArtist();
            break;
        case "updateArtist":
            updateArtist();
            break;
        case "deleteArtist":
            deleteArtist();
            break;
        case "list":
            getList($type, $category);
            break;
        case "update":
            update($file, $type, $category);
            break;
        case "add":
            add($file, $type, $category);
            break;
        case "delete":
            delete($type, $category);
            break;
        case "listMultiArtists":
            listMultiArtists();
            break;
        case "listArtists":
            listArtists();
            break;
        case "listSplitters":
            listSplitters();
            break;
        case "deleteMultiArtist":
            deleteMultiArtist();
            break;
        case "saveMulti":
            saveMulti();
            break;
        case "updateMultiArtist":
            updateMultiArtist();
            break;
        case "updateArtistSong":
            updateArtistSong($file, $type, $category);
            break;
        case "addArtistSong":
            addArtistSong($file, $type, $category);
            break;
        case "deleteArtistSong":
            deleteArtistSong($type, $category);
            break;
    }
}
catch(Error $e) {
//    echo $e->getMessage();
    logError($e->getFile(), $e->getLine(), $e->getMessage());
}
catch(ApplicationException $e) {
//    echo $e->getMessage();
    logError($e->getFile(), $e->getLine(), $e->getMessage());
    echo 'Caught exception: ',  $e->getMessage(), "\n";
}
exit(0);

function getListArtists(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $artists = readJSONWithCode(JSON_ARTISTS);
    //if (isset($_POST['sort'])){
    $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'name';
    $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
    $sort = new CustomSort();
    $array = $sort->sortObjectArrayByField($artists->list, "name", $order);
    $artists->list = $array;
    //}
    $array = array_slice($artists->list, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($artists->list);
    $result["rows"] = $array;
    echo json_encode($result);
}


function getList($type, $category){

    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    $mp3Prettifier = readJSONWithCode(JSON_MP3PRETTIFIER);
    if (isset($_POST['sort'])){
        $field = isset($_POST['sort']) ? strval($_POST['sort']) : '';
        $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
        $sort = new CustomSort();
        $array = $sort->sortObjectArrayByField($mp3Prettifier->{$type}->{$category}, $field, $order);
        $mp3Prettifier->{$type}->{$category} = $array;
    }
    $array = array_slice($mp3Prettifier->{$type}->{$category}, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($mp3Prettifier->{$type}->{$category});
    $result["rows"] = $array;
    echo json_encode($result);
}

function addErrorMsg($msg){
    return array('errorMsg'=>$msg);
}

function isExtWord($type, $category){
    /*
    if ($type == "song" && $category == "replacements"){
        return true;
    }
    else if ($type == "artist" && $category == "names"){
        return true;
    }*/
    return true;

    }

function update($file, $type, $category){
    $id = $_REQUEST['id'];
    $obj = readJSON($file);
    if (isExtWord($type, $category)){
        $word = new ExtWord();
        assignCheckbox($word->parenthesis, "parenthesis", !HTML_SPECIAL_CHAR);
        assignCheckbox($word->exactMatch, "exactMatch", !HTML_SPECIAL_CHAR);
        assignCheckbox($word->beginOfWord, "beginOfWord", !HTML_SPECIAL_CHAR);
        assignNumber($word->endOfWord, "endOfWord", !HTML_SPECIAL_CHAR);
        if (isset($_POST["priority"])){
            $word->priority = (int) $_POST["priority"];
        }
    }
    else {
        $word = new Word();
    }
    assignField($word->oldWord, "oldWord", !HTML_SPECIAL_CHAR);
    assignField($word->newWord, "newWord", !HTML_SPECIAL_CHAR);
    $word->id = $id;
    $save = true;
    if (objectExist($obj->{$type}->{$category}, "oldWord", $word->oldWord, true, "id", $word->id)) {
        //addError('colorCode', "Color Code already exist: " . $color->code);
        $errors = addErrorMsg($type . ' ' . $category . ' already exist: ' . $word->oldWord);
        $save = false;
    }
    if ($save) {
        $wordBO = new WordBO();
        $wordBO->saveGlobalWord($word, $type, $category);
        $items = array();
        array_push($items, $word);
        echo json_encode(array('success'=>true));
    }
    else {
        //write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
        echo json_encode($errors);
    }
}

function add($file, $type, $category)
{

    $obj = readJSON($file);
    if (isExtWord($type, $category)){
        $word = new ExtWord();
        assignCheckbox($word->parenthesis, "parenthesis", !HTML_SPECIAL_CHAR);
        assignCheckbox($word->exactMatch, "exactMatch", !HTML_SPECIAL_CHAR);
        assignCheckbox($word->beginOfWord, "beginOfWord", !HTML_SPECIAL_CHAR);
        assignNumber($word->endOfWord, "endOfWord", !HTML_SPECIAL_CHAR);
    }
    else {
        $word = new Word();
    }
    if (isset($_POST["priority"])){
        $word->priority = (int) $_POST["priority"];
    }

    assignField($word->oldWord, "oldWord", !ESCAPE_HTML);
    assignField($word->newWord, "newWord", !ESCAPE_HTML);
    $save = true;
    If (objectExist($obj->{$type}->{$category}, "oldWord", $word->oldWord, true)) {
        $errors = addErrorMsg($type . ' ' . $category . ' already exist: ' . $word->oldWord);
        $save = false;
    }
    if ($save) {
        $wordBO = new WordBO();
        $wordBO->addGlobalWord($word, $type, $category);
        $items = array();
        array_push($items, $word);
        echo json_encode($items);
    } else {
        echo json_encode($errors);
    }
    exit();
}

function delete($type, $category)
{
    $id = $_REQUEST['id'];
    $wordBO = new WordBO();
    $success = $wordBO->deleteGlobalWord($id, 'id', $type, $category);
    echo json_encode(array('success'=>$success));
}

function updateArtist(){
    $id = $_REQUEST['id'];
    $artistBO = new ArtistBO();
    $artist = new Artist();
    assignField($artist->name, "name", !HTML_SPECIAL_CHAR);
    assignField($artist->stageName, "stageName", !ESCAPE_HTML);
    $artist->id = $id;
    $save = true;
    if (objectExist($artistBO->getArtists(), "name", $artist->name, true, "id", $artist->id)) {
        //addError('colorCode', "Color Code already exist: " . $color->code);
        $errors = addErrorMsg('Artist already exist: ' . $artist->name);
        $save = false;
    }
    if ($save) {
        $artistBO->saveArtist($artist);
        $items = array();
        array_push($items, $artist);
        echo json_encode(array('success'=>true));
    }
    else {
        //write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
        echo json_encode($errors);
    }
}

function addArtist()
{

    $artistBO = new ArtistBO();
    $artist = new Artist();
    assignField($artist->name, "name", !ESCAPE_HTML);
    assignField($artist->stageName, "stageName", !ESCAPE_HTML);
    $save = true;
    If (objectExist($artistBO->getArtists(), "name", $artist->name, true)) {
        $errors = addErrorMsg('Artist already exist: ' . $artist->name);
        $save = false;
    }
    if ($save) {
        $artistBO->addArtist($artist);
        $items = array();
        array_push($items, $artist);
        echo json_encode($items);
    } else {
        echo json_encode($errors);
    }
    exit();
}

function deleteArtist()
{
    $id = $_REQUEST['id'];
    $artistBO = new ArtistBO();
    $success = $artistBO->deleteArtist($id);
    $returnObj = array('success' => $success);
    if (!$success) {
        $returnObj['errorMessage'] = "Artist can not be deleted!";
    }
    echo json_encode($returnObj);
}

function listMultiArtists(){
    $page = isset($_POST['page']) ? intval($_POST['page']) : 1;
    $rows = isset($_POST['rows']) ? intval($_POST['rows']) : 10;
    if (CacheBO::isInCache(CacheBO::MULTIARTIST2)){
       //$multi = CacheBO::getObject(CacheBO::MULTIARTIST);
        $list = CacheBO::getObject(CacheBO::MULTIARTIST2);
    }
    else {
        $multiArtistBO = new MultiArtistBO();
        $multi = $multiArtistBO->loadData();
        $list = Array();

        foreach ($multi->list as $key => $item) {
            //$multiArtistTO = new MultiArtistListTO($item->id, $item->exactPosition, $item->master);
            //$item->description = $multiArtistBO->constructMultiArtistDescription($artistBO, $item);
            //$item->description2 = $multiArtistBO->constructMultiArtistSequeceDescription($artistBO, $item);
            //$multiArtistTO->description = $multiArtistBO->constructMultiArtistDescription($artistBO, $item);
            //$multiArtistTO->description2 = $multiArtistBO->constructMultiArtistSequeceDescription($artistBO, $item);
            $multiArtistTO = $multiArtistBO->convertToMultiArtistTO($item);
            $list[$item->id] = $multiArtistTO;
        }
        //$cacheBO = new CacheBO();
        //$cacheBO->saveObject(CacheBO::MULTIARTIST, $multi);
        CacheBO::saveObject(CacheBO::MULTIARTIST2, $list);
    }
    $newArray = array_values($list);

    //if (isset($_POST['sort'])){
    $field = isset($_POST['sort']) ? strval($_POST['sort']) : 'description';
    if ($field != 'description') {
        $order = isset($_POST['order']) ? strval($_POST['order']) : 'asc';
        $sort = new CustomSort();
        //$array = $sort->sortObjectArrayByField($multi->list, $field, $order);
        $array = $sort->sortObjectArrayByField($newArray, $field, $order);
    }
    else {
        //$array = $multi->list;
        $array = $newArray;
    }
    //}
    $array = array_slice($array, ($page-1)*$rows, $rows);

    $result = array();
    $result["total"] = count($newArray);
    $result["rows"] = $array;
    echo json_encode($result);
}

function listArtists(){
    $artistBO = new ArtistBO();
    $sort = new CustomSort();
    $array = $sort->sortObjectArrayByField($artistBO->getArtists(), "name");
    echo json_encode($array);
}

function listSplitters(){
    $multiArtistBO = new MultiArtistBO();
    echo json_encode($multiArtistBO->getSplitters());

}

function saveMulti(){
    $msg = '';
    if (isset($_POST['config'])){
        $config = json_decode($_POST['config']);
        $saveConfig = new MultiArtist();
        $saveConfig->id = getUniqueId();
        $multiArtistLine = new MultiArtist();
        $multiArtistLine->id = getUniqueId();
        $multiArtistLine->exactPosition = $config->exactPosition;
        $multiArtistLine->master = $config->master;
        foreach ($config->artists as $value){
            $multiArtistLine->artists[] = new ArtistItem($value->id);
        }
        foreach ($config->artistSequence as $value){
            $multiArtistLine->artistSequence[] = new ArtistSequence($value->artistId, $value->splitterId);
        }
        $multiArtistBO = new MultiArtistBO();
        $existAlready = $multiArtistBO->checkMultiArtistConfigExist($multiArtistLine);
        if ($existAlready){
            $success = false;
            $msg = 'Multi Artist Config exist already';
        }
        else {
            $multiArtistBO->addMultiArtist($multiArtistLine);
            $success = true;
        }
    }
    else {
        $success = false;
    }
    echo json_encode(array('success'=>$success,'message'=>$msg));

}

function deleteMultiArtist(){
    $id = $_REQUEST['id'];
    $multiArtistBO = new MultiArtistBO();
    $success = $multiArtistBO->deleteMultiAristConfig($id);
    $returnObj = array('success' => $success);
    if (!$success) {
        $returnObj['errorMessage'] = "There was a problem trying to delete the MultiArtist!";
    }
    echo json_encode($returnObj);

}

function updateMultiArtist(){
    $multiArtist = new MultiArtist();
    $multiArtist->id = $_REQUEST['id'];
    assignCheckbox($multiArtist->exactPosition, "exactPosition", !HTML_SPECIAL_CHAR);
    $multiArtistBO = new MultiArtistBO();
    $success = $multiArtistBO->saveMultiAristConfig($multiArtist);
    if ($success) {
        echo json_encode(array('success' => true));
    }
    else {
        echo json_encode(addErrorMsg("There was a problem updating the Multi Artist with ID " . $multiArtist->id));
    }
}

function updateArtistSong($file, $type, $category){
    $id = $_REQUEST['id'];
    $word = new ArtistSongException();
    assignField($word->oldArtist, "oldArtist", !ESCAPE_HTML);
    assignField($word->newArtist, "newArtist", !ESCAPE_HTML);
    assignField($word->oldSong, "oldSong", !ESCAPE_HTML);
    assignField($word->newSong, "newSong", !ESCAPE_HTML);
    $obj = readJSON($file);
    $word->id = $id;
    $save = true;
    If (objectExist($obj->{$type}->{$category}, "oldArtist", $word->oldArtist, true, "id", $word->id) &&
        objectExist($obj->{$type}->{$category}, "oldSong", $word->oldSong, true,  "id", $word->id)
    ) {
        $errors = addErrorMsg($type . ' ' . $category . ' already exist: ' . $word->oldArtist . ' / ' . $word->oldSong);
        $save = false;
    }
    if ($save) {
        $wordBO = new WordBO();
        $wordBO->saveGlobalWord($word, $type, $category);
        $items = array();
        array_push($items, $word);
        echo json_encode(array('success'=>true));
    }
    else {
        //write($file, json_encode(array('errorMsg'=>'Some errors occured.')));
        echo json_encode($errors);
    }
}

function addArtistSong($file, $type, $category)
{

    $obj = readJSON($file);
    $word = new ArtistSongException();
    assignField($word->oldArtist, "oldArtist", !ESCAPE_HTML);
    assignField($word->newArtist, "newArtist", !ESCAPE_HTML);
    assignField($word->oldSong, "oldSong", !ESCAPE_HTML);
    assignField($word->newSong, "newSong", !ESCAPE_HTML);

    $save = true;
    If (objectExist($obj->{$type}->{$category}, "oldArtist", $word->oldArtist, true) &&
        objectExist($obj->{$type}->{$category}, "oldSong", $word->oldSong, true)
    ) {
        $errors = addErrorMsg($type . ' ' . $category . ' already exist: ' . $word->oldArtist . ' / ' . $word->oldSong);
        $save = false;
    }
    if ($save) {
        $wordBO = new WordBO();
        $wordBO->addGlobalWord($word, $type, $category);
        $items = array();
        array_push($items, $word);
        echo json_encode($items);
    } else {
        echo json_encode($errors);
    }
    exit();
}

function deleteArtistSong($type, $category)
{
    $id = $_REQUEST['id'];
    $wordBO = new WordBO();
    $success = $wordBO->deleteGlobalWord($id, 'id', $type, $category);
    echo json_encode(array('success'=>$success));
}


?>

