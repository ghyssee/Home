<?php
include_once("../../setup.php");
include_once documentPath (ROOT_PHP_BO, "SessionBO.php");
include_once documentPath (ROOT_PHP_BO, "CacheBO.php");
sessionStart();
if(isset($_POST['clearCache'])){
    $button = $_POST['clearCache'];
    switch ($button) {
        case "clearCache":
            clearCache();
            break;
    }
    
}

function clearCache(){
    CacheBO::clear();
    logInfo("Cache Cleared");
    header("Location: " . $_SESSION["previous_location"]);

}


?>
