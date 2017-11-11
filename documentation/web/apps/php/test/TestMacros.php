<?php
include("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_TEST, "Macros.php");
include_once documentPath (ROOT_PHP_TEST, "MafiaReloadedCommon.php");
include_once documentPath (ROOT_PHP_TEST, "FightBoss.php");
include_once documentPath (ROOT_PHP_TEST, "Fight.php");

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
    $fightObj = new Fight();
    $txt = "<a class=\"ajax_request\" href=\"#\" data-params=\"controller=profile&amp;action=view&amp;id=982390345205927\">cashqueen2</a>";
    echo $fightObj->extractIdFromString($txt) . LINE_BREAK;

    if ($retCode === SUCCESS) {
        //$bossFight = new BossFight();
        do  {
            waitTillEnoughStamina(iMacros::$iimHolder);
            $status = ATTACKSTATUS_OK;
/*
            if ($bossFight->isBossAvailable()) {
                $status = $bossFight->startFightBoss(iMacros::$iimHolder);
            }*/
            if ($status != ATTACKSTATUS_NOSTAMINA){
                $fightObj->fight($iim);
            }
        }
        while (true);
    }
}
catch (UserCancelException $ex){
    printAndFlush("User Canceled 1");
}
LogTest::log(INFO, "SUMMARY", "Total Iced: " . GlobalSettings::$iced);
LogTest::log(INFO, "SUMMARY", "Money Gained: " . GlobalSettings::$money);
LogTest::log(INFO, "SUMMARY", "Nr Of Attacks: " . GlobalSettings::$nrOfAttacks);
LogTest::log(INFO, "SUMMARY", "Stolen Ices: " . GlobalSettings::$stolenIces);
LogTest::log(INFO, "SUMMARY", "Skipped Health: " . GlobalSettings::$skippedHealth);
LogTest::log(INFO, "SUMMARY", "Max Healed: " . GlobalSettings::$maxHealed);
LogTest::log(INFO, "SUMMARY", "Heals: " . GlobalSettings::$heals);

iMacros::$iimHolder->iimClose();

function waitTillEnoughStamina($iimHolder){
    $maxStamina = 200;
    $minStamina = 30;
    do {
        // refreshing stats (health / exp / stamina / energy)
        playMacro($iimHolder, FIGHT_FOLDER, "20_Extract_Start.iim", MACRO_INFO_LOGGING);
        $stamina = getStamina($iimHolder);
        $energy = getEnergy($iimHolder);
        $total = $stamina + $energy;
        $exp = getExperience($iimHolder);
        if ($exp > 0){
            $staminaNeeded = $exp / 4.2;
            LogTest::log(INFO, "WAIT", "Stamina Needed: " . $staminaNeeded);
            LogTest::log(INFO, "WAIT", "Total (Energy + Stamina available): " . $total);
            LogTest::log(INFO, "WAIT", "Stamina: " . $stamina);
            LogTest::log(INFO, "WAIT", "maxStamina: " . $maxStamina);
            // maxStamina = Math.min(maxStamina, staminaNeeded);
            if ($total >= $staminaNeeded && ($stamina >= $minStamina || $exp < 300)) {
                LogTest::log(INFO, "WAIT", "Enough Stamina to level up");
                break;
            }
            elseif ($stamina >= $maxStamina){
                LogTest::log(INFO, "WAIT", "Enough Stamina to start fighting again");
                break;
            }
            wait($iimHolder, "60");
        }
    }
        // wait till stamina > 100
        // or stamina + energy > (experience needed to level up / 4)
    while (true);
    LogTest::log(INFO, "WAIT", "Leaving wait");
}

?>