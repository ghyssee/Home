<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 9/11/2017
 * Time: 11:33
 */
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_TEST, "Macros.php");
include_once documentPath (ROOT_PHP_TEST, "MafiaReloadedCommon.php");

Class BossFight {

    public $configMRObj;
    public $iimHolder;

    public function __construct($config)
    {
        $this->configMRObj = $config;
    }

function startFightBoss($iimHolder){
    LogTest::log(INFO, "BOSS", "Start Boss Fight");
    $status = ATTACKSTATUS_OK;
    $this->configMRObj->boss->defeatedOn = null;
    if ($this->configMRObj->boss->defeatedOn != null){
        $bossStartTime = DateTime::createFromFormat("YmdHis", $this->configMRObj->boss->defeatedOn);
        if (!$bossStartTime){
            LogTest::log(WARN, "BOSS", "Invalid Time in config: " . $this->configMRObj->defeatedOn);
        }
        else {
            $currDate = new DateTime();
            if ($bossStartTime < $currDate) {
                $status = fightBoss();
            } else {
                LogTest::log(INFO, "BOSS", "Start Time is at: " . $bossStartTime->format("Ymd His"));
            }
        }
    }
    else {
        $status = $this->fightBoss($iimHolder);
    }
    return $status;

}

function fightBoss($iimHolder){
    $retCode = playMacro($iimHolder,FIGHT_FOLDER, "70_Boss_Start.iim", MACRO_INFO_LOGGING);
    if ($retCode == SUCCESS) {
        $bossObj = $this->evaluateBossMessage($iimHolder);
        LogTest::log(INFO, "BOSS", "Status: " . $bossObj->status);
        switch ($bossObj->status){
            case ATTACKSTATUS_OK:
                $this->attackBoss();
                break;
            case ATTACKSTATUS_PROBLEM:
                break;
            case ATTACKSTATUS_NOSTAMINA:
                break;
            case ATTACKSTATUS_BOSSDEFEATED:
                break;
            default:
                break;
        }
    }
    else {
        LogTest::log(INFO, "BOSS", "Problem Starting Boss Fight");
    }
}

function evaluateBossResult($iimHolder){
    $retCode = playMacro($iimHolder,FIGHT_FOLDER, "75_Boss_Attack_Result.iim", MACRO_INFO_LOGGING);
    if ($retCode == SUCCESS){
        $msg = getLastExtract($iimHolder,1, "Boss Attack Result", 'You WON the fight');
        $msg = strtoupper($msg);
        LogTest::log(INFO, "BOSS", "Boss Result: " + $msg);
        if (startsWith($msg, 'YOU WON THE FIGHT')){
        }
        else if (startsWith($msg,"You DO NOT FEEL HEALTHY")){
        }
    }
    else {
        LogTest::log(WARN, "BOSS", "Problem Getting Boss Result");
    }
}

function attackBoss($iimHolder){
    $bossHealth = -1;
    $retCode = playMacro($iimHolder,FIGHT_FOLDER, "73_Boss_StartAttack.iim", MACRO_INFO_LOGGING);
    if ($retCode == SUCCESS) {
        do {
            $stamina = getStamina();
            if ($stamina >= 5) {
                checkHealth();
                if ($retCode == SUCCESS) {
                    $retCode = playMacro($iimHolder,FIGHT_FOLDER, "74_Boss_Attack.iim", MACRO_INFO_LOGGING);
                    $bossHealth = $this->getBossHealth($iimHolder);
                    if ($bossHealth == 0) {
                        LogTest::log(INFO, "BOSS", "Boss is dead!!!");
                        break;
                    }
                    else if ($bossHealth < 0) {
                        break;
                    }
                    else {
                        $this->evaluateBossResult($iimHolder);
                    }
                    GlobalSettings::$bossAttacks++;
                }
                else
                {
                    LogTest::log(INFO, "BOSS", "Problem With Attacking boss");
                    break;
                }
            }
            else {
                LogTest::log(INFO, "BOSS", "Not Enough Stamina");
                break;
            }
        }
        while ($bossHealth > 0);
    }
    else {
        LogTest::log(INFO, "BOSS", "Problem With Start Attacking boss");
    }
}

function getBossHealth($immHolder){
    LogTest::log(INFO, "FIGHT", "Checking Health");
    $health = -1;
    $retCode = playMacro($immHolder, FIGHT_FOLDER, "72_Boss_Health", MACRO_INFO_LOGGING);
    if ($retCode == SUCCESS) {
        $healthMsg = getLastExtract($immHolder,1, "Boss Health", "27,356/34,775");
        if (!isNullOrBlank($healthMsg)) {
            $healthMsg = removeComma($healthMsg);
            $list = explode('/', $healthMsg);
            LogTest::log(INFO, "BOSS", "Boss Health: " + $healthMsg);
            if ($list != null && count($list) == 2) {
                $health = intval($list[0]);
            }
            else {
                LogTest::log(INFO, "BOSS", "Problem Parsing health");
            }
        }
        else {
            LogTest::log(INFO, "BOSS", "Problem Extracting health");
        }
    }
    else {
        LogTest::log(INFO, "BOSS", "Problem Getting Boss Health");
    }
    return $health;
}

function evaluateBossMessage($iimHolder) {
    $retCode = playMacro($iimHolder,FIGHT_FOLDER, "71_Boss_Message.iim", MACRO_INFO_LOGGING);
    $bossObj = new Boss(ATTACKSTATUS_UNKNOWN);
    if ($retCode == SUCCESS){
        $msg = getLastExtract($iimHolder,1, "Boss Message", "There are no bosses available to fight. Please try coming back in 20 hours, 57 minutes.");
        if (!isNullOrBlank($msg)){
            $msg = strtoupper($msg);
            LogTest::log(INFO, "BOSS", "Boss Message: " + $msg);
            if (startsWith($msg,"THERE ARE NO BOSSES AVAILABLE")){
                $regExp = "/BACK IN ([0-9]{1,2}) HOURS, ([0-9]{1,2}) MINUTES/";
                preg_match($regExp, $msg, $matches);
                if ($matches != null && count($matches) > 1){
                    $minutes = $matches[2];
                    $hours = $matches[1];
                    $bossObj->status = ATTACKSTATUS_OK;
                    $date = new DateTime();
                    $dateInterval = new DateInterval("P0DT" . $hours . "H" . $minutes . "M");
                    $date->add($dateInterval);
                    $formattedDate = $date->format("YmdHis");
                    $this->configMRObj->boss->defeatedOn = $formattedDate;
                    //writeObject(configMRObj, MR_CONFIG_FILE);
                    $bossObj->status = ATTACKSTATUS_BOSSALREADYDEAD;
                }
                else {
                    $bossObj->status = ATTACKSTATUS_PROBLEM;
                    LogTest::log(INFO, "BOSS", "No Time Found");
                }
            }
            else if (startsWith($msg,"ANTON")) {
                $bossObj->status = ATTACKSTATUS_OK;
                LogTest::log(INFO, "BOSS", "BOSS AVAILABLE ???");
            }
        }
        else {
            $bossObj->status = ATTACKSTATUS_PROBLEM;
            LogTest::log(INFO, "BOSS", "Problem Extracting Boss Message");
        }
    }
    else {
        $bossObj->status = ATTACKSTATUS_PROBLEM;
        LogTest::log(INFO, "BOSS", "Problem Getting Boss Message");
    }
    return $bossObj;
}
}

class Boss
{
    public $status = ATTACKSTATUS_UNKNOWN;

    public function __construct($status)
    {
        $this->status = $status;
    }
}
?>