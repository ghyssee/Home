<?php
include_once("../setup.php");

include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_BO, "ArtistBO.php");

$file = getFullPath(JSON_MP3PRETTIFIER);

$method = "addMulti";//htmlspecialchars($_REQUEST['method']);
try {
    switch ($method) {
        case "addMulti":
            addMultiArtist2();
            break;
    }
}
catch(Error $e) {
//    echo $e->getMessage();
    logError($e->getFile(), $e->getLine(), $e->getMessage());
}

function addErrorMsg($msg){
    return array('errorMsg'=>$msg);
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
?>
