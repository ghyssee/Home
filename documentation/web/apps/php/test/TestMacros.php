<?php
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_TEST, "Macros.php");
include_once documentPath (ROOT_PHP_TEST, "MafiaReloadedCommon.php");
include_once documentPath (ROOT_PHP_TEST, "FightBoss.php");

const LINE_BREAK = "<BR>";

$obj = (object) [
    'OPPONENT' => 'some string',
    'anArray' => [ 1, 2, 3 ]
];

$CONSTANTS = (object) array(
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


$globalSettingsOld = (object) array(
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

set_time_limit(0);

$fightersToExclude = readJSON(MR_FIGHTERS_EXCLUDE_FILE);
$friendObj = readJSON(MR_FRIENDS_FILE);
$fighterObj = readJSON(MR_FIGHTERS_FILE);
$configMRObj = readJSON(MR_CONFIG_FILE);

LogTest::$file = "C:\\My Programs\\iMacros\logs\\test2.txt";
LogTest::log(INFO,"TEST", "test1");

//$s = $iim1->iimSet("keyword", $_GET["keyword"]);
//$s2 = $iim1->iimPlay("MR\\Common\\01_Start.iim");
 $iim = new COM("iMacros");
iMacros::$iimHolder = $iim;
$s = iMacros::$iimHolder->iimOpen("-runner", true, 90);
echo "iimOpen=";
echo $s . LINE_BREAK;

try {
    $retCode = playMacro(iMacros::$iimHolder, "MR\\Common", "01_Start.iim", MACRO_INFO_LOGGING);
    /*
    if ($retCode === SUCCESS) {
        do {
            iMacros::$iimHolder->iimSet("FRAME", "0");
            $retCode = playMacro(iMacros::$iimHolder, "MR\\Jobs", "10_GetEnergy.iim");
            $energy = iMacros::$iimHolder->iimGetExtract(1);
            printAndFlush("Energy: " . $energy);
            wait(iMacros::$iimHolder, "10");
        } while (true);
    }*/
    //$energy = getExperience(iMacros::$iimHolder);
    //$energy = getEnergy(iMacros::$iimHolder);
    //$energy = getStamina(iMacros::$iimHolder);
    //waitTillEnoughStamina(iMacros::$iimHolder);
    /*
    $date = new DateTime();
    echo $date->format("Ymd His") . LINE_BREAK;
    $timeInterval = new DateInterval("P0DT0H30M5S");
    $date->add($timeInterval);
    echo $date->format("Ymd His") . LINE_BREAK;
    $str = "201711091514";
            20171109175931
    $date = DateTime::createFromFormat("YmdHi", $str);
    if (!$date){
       echo "Invalid format" . LINE_BREAK;
       echo serialize(DateTime::getLastErrors()) . LINE_BREAK;
    }
    else {
        echo $date->format("Ymd His") . LINE_BREAK;
    }
    */
    $bossFight = new BossFight($configMRObj);
    $bossFight->startFightBoss(iMacros::$iimHolder);
    echo "Done";

}
catch (UserCancelException $ex){
    printAndFlush("User Canceled 1");
}
iMacros::$iimHolder->iimClose();
//echo "extractTest=";
//echo $iim1->iimGetExtract;
//$s2 = $iim1->iimPlay("MR/Common/01_Start.iim");
//$s = $iim1->iimClose();

function waitTillEnoughStamina($iimHolder){
    $maxStamina = 200;
    $minStamina = 50;
    do {
        // refreshing stats (health / exp / stamina / energy)
        playMacro($iimHolder, FIGHT_FOLDER, "20_Extract_Start.iim", MACRO_INFO_LOGGING);
        $stamina = getStamina($iimHolder);
        $energy = getEnergy($iimHolder);
        $total = $stamina + $energy;
        $exp = getExperience($iimHolder);
        if ($exp > 0){
            $staminaNeeded = $exp / 4;
            LogTest::log(INFO, "WAIT", "Stamina Needed: " . $staminaNeeded);
            LogTest::log(INFO, "WAIT", "Total (Energy + Stamina available): " . $total);
            LogTest::log(INFO, "WAIT", "Stamina: " . $stamina);
            LogTest::log(INFO, "WAIT", "maxStamina: " . $maxStamina);
            // maxStamina = Math.min(maxStamina, staminaNeeded);
            if ($stamina >= $minStamina && ($stamina >= $maxStamina || $total >= $staminaNeeded)){
                break;
            }
            wait($iimHolder, "5");
        }
    }
        // wait till stamina > 100
        // or stamina + energy > (experience needed to level up / 4)
    while (true);
    LogTest::log(INFO, "WAIT", "Leaving wait");
}

function getLastExtract($iim, $nr, $title, $value){
    return $iim->iimGetExtract($nr);
}

function isNullOrBlank($value){
    if (isset($value)){
        if ($value == "#EANF#"){
            return true;
        }
        else {
            return false;
        }
    }
    return true;
}

function startFightBoss(){
    LogTest::log(INFO, "BOSS", "Start Boss Fight");
    /*
    $status = $CONSTANTS->ATTACKSTATUS->OK;
    if ($configMRObj.boss.defeatedOn !== null){
        var bossStartTime = formatStringYYYYMMDDHHMISSToDate(configMRObj.boss.defeatedOn);
        var currDate = new Date();
        if (bossStartTime < currDate) {
            status = fightBoss();
        }
        else {
            logV2(INFO, "BOSS", "Start Time is at: " + bossStartTime);
        }
    }
    else {
        status = fightBoss();
    }
    return status;
    */

}



?>