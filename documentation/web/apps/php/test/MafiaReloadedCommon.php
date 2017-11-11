<?php
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_TEST, "Macros.php");

/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 9/11/2017
 * Time: 11:37
 */
const OPPONENT_UNKNOWN = 0;
const OPPONENT_FRIEND = 1;
const OPPONENT_WON = 2;
const OPPONENT_LOST = 3;
const OPPONENT_DEAD = 4;

const ATTACKSTATUS_OK = 0;
const ATTACKSTATUS_PROBLEM = -1;
const ATTACKSTATUS_NOSTAMINA = 2;
const ATTACKSTATUS_BOSSDEFEATED = 1;
const ATTACKSTATUS_BOSSALREADYDEAD = 3;
const ATTACKSTATUS_UNKNOWN = 4;

Class GlobalSettings
{
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

function getStamina($iimHolder){
    playMacro($iimHolder, FIGHT_FOLDER, "52_GetStamina.iim", MACRO_INFO_LOGGING);
    $staminaInfo = getLastExtract($iimHolder, 1, "Stamina Left", "300/400");
    LogTest::log(INFO, "STAMINA", "stamina = " . $staminaInfo);
    if (!isNullOrBlank($staminaInfo)){
        $staminaInfo = removeComma($staminaInfo);
        $tmp = explode("/", $staminaInfo);
        $stamina = intval($tmp[0]);
        return $stamina;
    }
    return 0;
}

function getEnergy($iimHolder){
    playMacro($iimHolder, JOB_FOLDER, "10_GetEnergy.iim", MACRO_INFO_LOGGING);
    $energyInfo = getLastExtract($iimHolder,1, "Energy Left", "500/900");
    LogTest::log(INFO, "ENERGY", "energy = " . $energyInfo);
    if (!isNullOrBlank($energyInfo)){
        $energyInfo = removeComma($energyInfo);
        $tmp = explode("/", $energyInfo);
        $energy = intval($tmp[0]);
        return $energy;
    }
    return 0;
}

function getExperience($iimHolder){
    LogTest::log(INFO, "EXP", "Get Experience");
    $ret = playMacro($iimHolder,COMMON_FOLDER, "13_GetExperience.iim", MACRO_INFO_LOGGING);
    $exp = 0;
    if ($ret == SUCCESS){
        $msg = getLastExtract($iimHolder,1, "Experience Left", "5,886 (1,264 to level)");
        $exp = extractExperience($msg);
        LogTest::log(INFO, "EXP", "Experience Left: " . $exp);
    }
    return $exp;

}

function extractExperience($text){
    $text = strtoupper($text);
    $text = removeComma($text);
    $exp = 0;
    $regExp = "/(?:.*)[0-9]{1,10} \((.*) TO LEVEL/"; //5,886 (1,264 to level)
    preg_match($regExp, $text, $matches);
    if ($matches != null && count($matches) > 0){
        $exp = intval($matches[count($matches)-1]);
    }
    return $exp;

}

function checkHealth($iimHolder){
    LogTest::log(INFO, "FIGHT", "Checking Health");
    $health = getHealth($iimHolder);
    while ($health < 10){
        heal($iimHolder);
        $health = getHealth($iimHolder);
        if ($health > 10){
            GlobalSettings::$heals++;
        }
    }
}

function heal($iimHolder){
    LogTest::log(INFO, "TEST", "Healing...");
    $retCode = playMacro($iimHolder,FIGHT_FOLDER, "10_Heal.iim", MACRO_INFO_LOGGING);
    if ($retCode == SUCCESS) {
        closePopup($iimHolder);
    }
}

function getHealth($iimHolder){
    playMacro($iimHolder,FIGHT_FOLDER, "11_GetHealth.iim", MACRO_INFO_LOGGING);
    $healthInfo = getLastExtract($iimHolder,1, "Health", "50%");
    LogTest::log(INFO, "BOSS", "healthInfo = " . $healthInfo);
    if (!isNullOrBlank($healthInfo)){
        $healthInfo = removeComma($healthInfo);
        $tmp = explode("/", $healthInfo);
        $health = intval($tmp[0]);
        return $health;
    }
    return 0;
}

function checkSaldo($iimHolder){
    LogTest::log(INFO, "SALDO", "Get Saldo");
    $saldo = getSaldo($iimHolder);
    if ($saldo > 10){
        bank($iimHolder,$saldo);
    }
}

function bank($iimHolder, $saldo){
    playMacro($iimHolder,COMMON_FOLDER, "10_Bank.iim", MACRO_INFO_LOGGING);
    LogTest::log(INFO, "BANK", "Banking " . $saldo);
    GlobalSettings::$money += $saldo;
}

function getSaldo($iimHolder){
    playMacro($iimHolder,COMMON_FOLDER, "11_GetSaldo.iim", MACRO_INFO_LOGGING);
    $saldoInfo = getLastExtract($iimHolder,1, "Saldo", "$128");
    LogTest::log(INFO, "BANK", "saldoInfo = " . $saldoInfo);
    if (!isNullOrBlank($saldoInfo)){
        $saldoInfo = removeComma($saldoInfo);
        $saldoInfo = str_replace("$", "", $saldoInfo);
        $saldo = intval($saldoInfo);
        return $saldo;
    }
    return 0;
}

function removeComma($text){
    return str_replace(',', '', $text);
}

function closePopup($iimHolder){
    $retCode = playMacro($iimHolder, COMMON_FOLDER, "02_ClosePopup.iim", MACRO_INFO_LOGGING);
    if ($retCode == SUCCESS){
        LogTest::log(INFO, "POPUP", "Popup Closed");
    }
}

function checkIfLevelUp($iimHolder)
{
    $retCode = playMacro($iimHolder, COMMON_FOLDER, "12_GetLevel.iim", MACRO_INFO_LOGGING);
    if ($retCode == SUCCESS) {
        $msg = strtoupper(getLastExtract($iimHolder, 1, "LEVEL", "Level 300"));
        $msg = str_replace("LEVEL ", "", $msg);
        $level = intval($msg);
        if (GlobalSettings::$currentLevel == 0) {
            GlobalSettings::$currentLevel = $level;
        } else if ($level > GlobalSettings::$currentLevel) {
            LogTest::log(INFO, "LEVELUP", "New Level: " . $level . ". Checking For Dialog Box");
            $retCode = closePopup($iimHolder);
            if ($retCode == SUCCESS) {
                LogTest::log(INFO, "LEVELUP", "Dialog Box Closed");
            }
            GlobalSettings::$currentLevel = $level;
        }
    }
}

?>