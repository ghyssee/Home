<?php
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");

const CANCELED_BY_USER = -101;
const LINE_BREAK = "<BR>";
const SUCCESS = 1;

set_time_limit(0);
$iim1 = new COM("imacros");
$s = $iim1->iimOpen("-runner", true, 90);
echo "iimOpen=";
echo $s . LINE_BREAK;

//$s = $iim1->iimSet("keyword", $_GET["keyword"]);
//$s2 = $iim1->iimPlay("MR\\Common\\01_Start.iim");
$retCode = playMacro($iim1, "MR\\Common", "01_Start.iim");
if ($retCode === SUCCESS){
    do {
        $iim1->iimSet("FRAME", "0");
        $retCode = playMacro($iim1, "MR\\Jobs", "10_GetEnergy.iim");
        $energy = $iim1->iimGetExtract;
        printAndFlush( "Energy: " . $energy);
        wait($iim1,"10");
    }
    while (true);
}

//echo "extractTest=";
//echo $iim1->iimGetExtract;
//$s2 = $iim1->iimPlay("MR/Common/01_Start.iim");
//$s = $iim1->iimClose();

function playMacro($iimHolder, $folder, $macro){
    $retCode = $iimHolder->iimPlay($folder . "\\" . $macro);
    if ($retCode == CANCELED_BY_USER){
        throw new Error("Canceled By User");
    }
    printAndFlush( "Macro: " . $macro);
    printAndFlush( "Return code: " . $retCode);
    return $retCode;
}

function wait($iimHolder, $seconds){
    $iimHolder->iimSet("seconds", $seconds);
    $retcode = $iimHolder->iimPlay("Wait.iim");
    if ($retcode == CANCELED_BY_USER){
        throw new UserCancelError();
    }
    return $retcode;
}

?>