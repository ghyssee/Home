<?php
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");

class UserCancelException extends Exception { }

const CANCELED_BY_USER = -101;
const LINE_BREAK = "<BR>";
const SUCCESS = 1;

set_time_limit(0);
$iim1 = new COM("imacros");
$s = $iim1->iimOpen("-runner", true, 90);
echo "iimOpen=";
echo $s . LINE_BREAK;

$obj = (object) [
    'OPPONENT' => 'some string',
    'anArray' => [ 1, 2, 3 ]
];

$array = (object) array(
    'OPPONENT' => (object) array(
        'UNKNOWN' => 0,
        'FRIEND' => 1,
		"WON" => 2,
		"LOST"=> 3,
		"DEAD"=> 4
    ),
    'ATTACKSTATUS' => (object) array(
        'OK' => 0,
        'PROBLEM' => -1,
		"NOSTAMINA"=> 2,
		"BOSSDEFATED"=> 1,
		"BOSSALREADYDEAD"=> 3,
		"UNKNOWN"=> 4    )
);

Const MACRO_FOLDER = "C:\\My Programs\\iMacros\\macros";
Const FIGHT_FOLDER = "MR\\Fight";
Const COMMON_FOLDER = "MR\\Common";
Const JOB_FOLDER = "MR\\Jobs";

define ("MR_PATH", getFullPath(PATH_MAIN_CONFIG) . "\\MR");
Const MR_FIGHTERS_EXCLUDE_FILE = MR_PATH . "\\" . "FightersToExclude.json";
Const MR_FRIENDS_FILE = MR_PATH . "\\" . "Friends.json";
Const MR_FIGHTERS_FILE = MR_PATH . "\\" . "Fighters.json";
Const MR_CONFIG_FILE = MR_PATH . "\\" . "MafiaReloaded.json";

$globalSettings = (object) array(
    'maxLevel' => 600,
    'iced' => 0,
    'money' => 0,
    'currentLevel' => 0,
    'nrOfAttacks' => 0,
    'stolenIces' => 0,
    'skippedHealth' => 0,
    'maxHealed' => 0,
    'heals' => 0,
    'boss' => (object) array(
        'attacks' => 0
    )
);

Class GlobalSettings {
    static $maxLevel = 600;
    static $iced = 0;
    static $money = 0;
    static $currentLevel = 0;
    static $nrOfAttacks = 0;
    static $stolenIces = 0;
    static $skippedHealth = 0;
    static $maxHealed = 0;
    static $heals = 0;
    static $bossAttacks = 0;
}

$fightersToExclude = readJSON(MR_FIGHTERS_EXCLUDE_FILE);
$friendObj = readJSON(MR_FRIENDS_FILE);
$fighterObj = readJSON(MR_FIGHTERS_FILE);
$configMRObj = readJSON(MR_CONFIG_FILE);

LogTest::$file = "C:\\My Programs\\iMacros\logs\\test2.txt";
LogTest::log("test1");

//$s = $iim1->iimSet("keyword", $_GET["keyword"]);
//$s2 = $iim1->iimPlay("MR\\Common\\01_Start.iim");

try {
    $retCode = playMacro($iim1, "MR\\Common", "01_Start.iim");
    if ($retCode === SUCCESS) {
        do {
            $iim1->iimSet("FRAME", "0");
            $retCode = playMacro($iim1, "MR\\Jobs", "10_GetEnergy.iim");
            $energy = $iim1->iimGetExtract(1);
            printAndFlush("Energy: " . $energy);
            wait($iim1, "10");
        } while (true);
    }
}
catch (UserCancelException $ex){
    printAndFlush("User Canceled 1");
}

//echo "extractTest=";
//echo $iim1->iimGetExtract;
//$s2 = $iim1->iimPlay("MR/Common/01_Start.iim");
//$s = $iim1->iimClose();

function waitTillEnoughStamina($iimHolder){
    $maxStamina = 200;
    $stamina = 0;
    $energy = 0;
    $total = 0;
    $minStamina = 50;
    do {
        // refreshing stats (health / exp / stamina / energy)
        playMacro($iimHolder, FIGHT_FOLDER, "20_Extract_Start.iim", true);
        $stamina = getStamina();
        $energy = getEnergy();
        $total = $stamina + $energy;
        $exp = getExperience();
        if ($exp > 0){
            $staminaNeeded = $exp / 4;
            LogTest::log(INFO, "WAIT", "Stamina Needed: " + $staminaNeeded);
            LogTest::log(INFO, "WAIT", "Total (Energy + Stamina available): " + $total);
            LogTest::log(INFO, "WAIT", "Stamina: " + $stamina);
            LogTest::log(INFO, "WAIT", "maxStamina: " + $maxStamina);
            // maxStamina = Math.min(maxStamina, staminaNeeded);
            if ($stamina >= $minStamina && ($stamina >= $maxStamina || $total >= $staminaNeeded)){
                break;
            }
            waitV2("5");
        }
    }
        // wait till stamina > 100
        // or stamina + energy > (experience needed to level up / 4)
    while (true);
    logV2(INFO, "WAIT", "Leaving wait");
}


function playMacro($iimHolder, $folder, $macro, $logging = true){
    if (!empty($folder)){
        $folder .= "\\";
    }
    echo $folder . $macro;
    $retCode = $iimHolder->iimPlay($folder . $macro);
    if ($retCode == CANCELED_BY_USER){
        throw new UserCancelException();
    }
    printAndFlush( "Macro: " . $macro);
    printAndFlush( "Return code: " . $retCode);
    return $retCode;
}

function wait($iimHolder, $seconds){
    $iimHolder->iimSet("seconds", $seconds);
    $retcode = $iimHolder->iimPlay("Wait.iim");
    if ($retcode == CANCELED_BY_USER){
        throw new UserCancelException();
    }
    return $retcode;
}

?>