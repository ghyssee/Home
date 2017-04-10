<?php
include_once "../../setup.php";
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
sessionStart();

$method = htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "addMulti":
            addMultiArtist();
            break;
        case "batch":
            batchProcess();
        default :
            addMultiArtist2();
            break;
    }
}
catch(Exception $e){
    logError($e->getFile(), $e->getLine(), $e->getMessage());
}
catch(Error $e) {
//    echo $e->getMessage();
    logError($e->getFile(), $e->getLine(), $e->getMessage());
}

function addErrorMsg($msg){
    return array('errorMsg'=>$msg);
}

function addMultiArtist(){
    if (isset($_POST['config'])) {
        $config = json_decode($_POST['config']);
        $multiArtistBO = new MultiArtistBO();
        $result = $multiArtistBO->addMultiAristConfig($config->multiArtistConfig);
        echo json_encode($result);
    }
    else {
        echo json_encode(array('success' => false, 'message' => 'Config Object Not Found'));
    }
}

function addMultiArtist2(){
    $line = "Zucchero & Ilse DeLange";
    $multiArtistBO = new MultiArtistBO();
    $result = $multiArtistBO->addMultiAristConfig($line);
    if ($result->errorFound){
        println($result->errorMsg);
    }
    else {
        foreach ($result->artistsAdded as $item) {
            println($item);
        }
        println("ID: " . $result->multiArtist->id);
    }
}

function batchProcess(){
    $text = trim($_POST['multiArtistList']);
    $values = preg_split('/[\n\r]+/', $text);

    foreach ($values as $line) {
        $tmp = trim($line);
        $tmp2 = $line;
    }
    echo json_encode(array('success' => true));
}

?>
