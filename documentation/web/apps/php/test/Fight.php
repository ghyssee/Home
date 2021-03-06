<?php
/**
 * Created by PhpStorm.
 * User: Gebruiker
 * Date: 10/11/2017
 * Time: 14:35
 */
include_once("../setup.php");
include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MODEL, "HTML.php");
include_once documentPath (ROOT_PHP_HTML, "config.php");
include_once documentPath (ROOT_PHP_TEST, "Macros.php");
include_once documentPath (ROOT_PHP_TEST, "MafiaReloadedCommon.php");

const RIVAL_MOBSTER = true;

class Fight
{
    public $fightersToExclude;
    public $friendObj;
    public $fighterObj;

    public function __construct()
    {
        $this->fightersToExclude = readJSON(MR_FIGHTERS_EXCLUDE_FILE);
        $this->friendObj = readJSON(MR_FRIENDS_FILE);
        $this->fighterObj = readJSON(MR_FIGHTERS_FILE);
        $this->configMRObj = readJSON(MR_CONFIG_FILE);
    }

    public function fight($iimHolder)
    {
        $exitLoop = false;
        $counter = 0;
        $status = ATTACKSTATUS_OK;
        do {
            $counter++;
            $rival = 0;

            do {

                $this->configMRObj = readJSON(MR_CONFIG_FILE);
                $rival = $this->extractRivalMobster($iimHolder);
                if ($rival > 0) {
                    $fighter = new Fighter("RIVAL", "RIVAL " . $rival, "0");
                    $list[] = $fighter;
                    $status = $this->processList($iimHolder, $list, RIVAL_MOBSTER);
                    LogTest::log(INFO, "FIGHT", "Status: " . $status);
                    if ($status == ATTACKSTATUS_NOSTAMINA) {
                        LogTest::log(INFO, "FIGHT", "Exit Fight V1...");
                        $exitLoop = true;
                        break;
                    }
                }
            } while ($rival > 0);
            if ($status != ATTACKSTATUS_NOSTAMINA) {

                $fighters = $this->getFightList($iimHolder);
                $filteredFightersList = $this->filterFightList($fighters);
                $minFightList = isNullOrBlank($this->configMRObj->fight->minLengthOfFightList) ? 0: $this->configMRObj->fight->minLengthOfFightList;
                logV2(INFO, "FIGHT", "Min Fighters on Fight List: " + $minFightList);
                if (count($filteredFightersList) > 2) {
                    $status = $this->processList($iimHolder, $filteredFightersList, !RIVAL_MOBSTER);
                }
                else {
                    $status = $this->startProfileAttack($iimHolder);
                }
                LogTest::log(INFO, "FIGHT", "Status: " . $status);
                if ($status == ATTACKSTATUS_NOSTAMINA) {
                    LogTest::log(INFO, "FIGHT", "Exit Fight V2...");
                    $exitLoop = true;
                    break;
                }
            }
        } while (!$exitLoop && $counter < 100000);
    }

    function processList($iimHolder, $list, $rivalMobster)
    {
        $refresh = false;
        $status = ATTACKSTATUS_OK;
        $length = count($list);
        for ($i = 0; $i < $length; $i++) {
            $arrayItem = $list[$i];
            if (!$arrayItem->skip) {
                LogTest::log(INFO, "FIGHT", "Fighting Player " . $arrayItem->id . " - " . $arrayItem->name);
                $statusObj = $this->attack($iimHolder, $arrayItem, $rivalMobster);
                switch ($statusObj->status) {
                    case ATTACKSTATUS_OK :
                        // do nothing, continue with next fighter
                        break;
                    case ATTACKSTATUS_PROBLEM :
                        LogTest::log(INFO, "FIGHT", "Problem With Fightlist. Refreshing...");
                        $refresh = true;
                        break;
                    case ATTACKSTATUS_NOSTAMINA :
                        LogTest::log(INFO, "FIGHT", "Out Of Stamina. Exiting processList");
                        $status = ATTACKSTATUS_NOSTAMINA;
                        $refresh = true;
                        break;
                }
            } else {
                LogTest::log(INFO, "FIGHT", "Skipping Stronger Opponent: " . $arrayItem->id);
            }
            if ($refresh) break;
        };
        LogTest::log(INFO, "FIGHT", "ProcessList Return Status: " . $status);
        return $status;
    }

    function extractRivalMobster($iimHolder)
    {
        //Rival mobsters alive: 18 / 20
        LogTest::log(INFO, "FIGHT", "Rival Mobsters");
        $retCode = playMacro($iimHolder, FIGHT_FOLDER, "20_Extract_Start.iim", MACRO_INFO_LOGGING);
        if ($retCode == SUCCESS) {
            $retCode = playMacro($iimHolder, FIGHT_FOLDER, "22_Extract_Rival.iim", MACRO_INFO_LOGGING);
            $mob = 0;
            if ($retCode == SUCCESS) {
                $msg = getLastExtract($iimHolder, 1, "Rival", "20 / 20");
                LogTest::log(INFO, "FIGHT", "MSG: " . $msg);
                $msg = strtoupper($msg);
                $msg = str_replace("RIVAL MOBSTERS ALIVE: ", "", $msg);
                $msg = str_replace("/ 20", "", $msg);
                $msg = trim($msg);
                LogTest::log(INFO, "FIGHT", "MSG PROCESSED: " . $msg);
                $mob = intval($msg);
            }
            return $mob;
        } else {
            LogTest::log(INFO, "FIGHT", "Problem With Extract Start");
        }
        return 0;
    }

    function attack($iimHolder, $fighter, $rivalMobster, $profileFighter)
    {
        $statusObj = new FightStatus(ATTACKSTATUS_OK);
        $retCode = SUCCESS;
        LogTest::log(INFO, "FIGHT", "Attacking " . $fighter->id);
        if (!$profileFighter) {
            $retCode = playMacro($iimHolder, FIGHT_FOLDER, "20_Extract_Start.iim", MACRO_INFO_LOGGING);
        }
        if ($retCode != SUCCESS) {
            LogTest::log(INFO, "FIGHT", 'Problem With Extract Start');
            $statusObj->status = ATTACKSTATUS_PROBLEM;
            return $statusObj;
        }
        checkHealth($iimHolder);
        if ($profileFighter){
            $retCode = playMacro($iimHolder,FIGHT_FOLDER, "81_Profile_Attack_Start.iim", MACRO_INFO_LOGGING);
        }
        else if ($rivalMobster) {
            $retCode = playMacro($iimHolder, FIGHT_FOLDER, "32_AttackRivalMobster_start.iim", MACRO_INFO_LOGGING);
        } else {
            addMacroSetting($iimHolder, "ID", $fighter->id);
            $retCode = playMacro($iimHolder, FIGHT_FOLDER, "30_Attack_Start", MACRO_INFO_LOGGING);
        }
        $statusObj->status = ATTACKSTATUS_OK;
        checkIfLevelUp($iimHolder);
        if ($retCode == SUCCESS) {
            $retCode = playMacro($iimHolder, FIGHT_FOLDER, "31_Attack_Status", MACRO_INFO_LOGGING);
            if ($retCode == SUCCESS) {
                $msg = getLastExtract($iimHolder, 1, "Attack Status", "You WON The Fight");
                //$msg = prompt("FIRST ATTACK","You WON");
                $status = $this->evaluateAttackMessage($msg);
                switch ($status) {
                    case OPPONENT_FRIEND :
                        LogTest::log(INFO, "FIGHT", "Add Friend: " . $fighter->id);
                        $fighter->skip = true;
                        $this->addFriend($fighter);
                        if ($profileFighter){
                            $this->removeItemFromArray(MR_FIGHTERS_FILE, $fighter->id);
                            logV2(INFO, "FIGHT", "Remove Fighter + Add Friend: " . $fighter->id);
                        }
                        $statusObj->status = ATTACKSTATUS_OK;
                        break;
                    case OPPONENT_WON :
                        if (!$rivalMobster && !$profileFighter) {
                            $this->addFighter($fighter);
                        }
                        /*
                        if ($this->getVictimHealth($iimHolder) == 0) {
                            $statusObj->status = ATTACKSTATUS_OK;
                            break;
                        }*/
                        $attackStatusObj = $this->attackTillDeath($iimHolder, $fighter, $rivalMobster);
                        checkIfLevelUp($iimHolder);
                        if ($attackStatusObj->status == ATTACKSTATUS_NOSTAMINA) {
                            // no stamina
                            $statusObj->status = ATTACKSTATUS_NOSTAMINA;
                        } else {
                            $statusObj->status = ATTACKSTATUS_OK;
                        }
                        break;
                    case OPPONENT_DEAD :
                        LogTest::log(INFO, "FIGHT", "Opponent is dead. Move on to the next one");
                        $statusObj->status = ATTACKSTATUS_OK;
                        GlobalSettings::$stolenIces++;
                        break;
                    case OPPONENT_LOST :
                        $this->getVictimHealth($iimHolder);
                        LogTest::log(INFO, "FIGHT", "Add Stronger Opponent: " . $fighter->id);
                        $this->addStrongerOpponent($fighter);
                        if ($profileFighter){
                            removeItemFromArray(MR_FIGHTERS_FILE, $fighter->id);
                            logV2(INFO, "FIGHT", "Remove Fighter + Add Stronger Opponent: " . $fighter->id);
                        }
                        $fighter->skip = true;
                        $statusObj->status = ATTACKSTATUS_OK;
                        break;
                    default :
                        $statusObj->status = ATTACKSTATUS_PROBLEM;
                        LogTest::log(INFO, "FIGHT", "Attack First Time Problem");
                        break;
                }
            } else {
                LogTest::log(INFO, "FIGHT", "Problem getting status for Fighter: " . $fighter->id);
                $statusObj->status = ATTACKSTATUS_PROBLEM;
            }
        } else {
            LogTest::log(INFO, "FIGHT", "Fighter Not Found: " . $fighter->id . " / Fight List Refreshed???");
            $statusObj->status = ATTACKSTATUS_PROBLEM;
        }
        return $statusObj;
    }

    function getVictimHealth($iimHolder)
    {
        $health = -1;
        $retCode = playMacro($iimHolder, FIGHT_FOLDER, "40_Victim_Health", MACRO_INFO_LOGGING);
        if ($retCode == SUCCESS) {
            $healthMsg = getLastExtract($iimHolder, 1, "Victim Health", "50%");
            if (!isNullOrBlank($healthMsg)) {
                $healthMsg = str_replace("%", "", $healthMsg);
                LogTest::log(INFO, "ATTACK", "Victim Health: " . $healthMsg);
                $health = intval($healthMsg);
                if ($health == 0) {
                    wait($iimHolder, "0.5");
                    $this->checkIfIced($iimHolder);
                }
            } else {
                LogTest::log(INFO, "ATTACK", "Problem extracting Victim Health (Empty))");
            }
        }
        return $health;

    }

    function checkForAttackButton($iimHolder){
        $btnAvailable = false;
        $retCode = playMacro($iimHolder,FIGHT_FOLDER, "43_Check_Attack_Button.iim", MACRO_INFO_LOGGING);
        $btn = getLastExtract($iimHolder,1, "ATTACK BUTTON", "Power Attack");
        if ($retCode == SUCCESS && !isNullOrBlank($btn)){
            $btnAvailable = true;
        }
        LogTest::log(INFO, "ATTACK", "Check Attack Button: " . $btnAvailable);
        return $btnAvailable;

    }

    function attackTillDeath($iimHolder, $fighter, $rivalMobster)
    {
        LogTest::log(INFO, "ATTACK", "Attack Figther " . $fighter->id);
        $alive = true;
        $retCode = 0;
        $previousHealth = 1000;
        $nrOfAttacks = 0;
        $statusObj = new FightStatus(ATTACKSTATUS_PROBLEM);
        $firstAttack = true;
        $nrOfHeals = 0;
        $originalHealth = 0;
        $health = 0;
        do {
            if ($health > -1) {
                if ($firstAttack) {
                    $originalHealth = $health;
                    $health = $this->getVictimHealth($iimHolder);
                }
                if ($previousHealth < $health) {
                    LogTest::log(INFO, "ATTACK", "Victim healed: " . $fighter->id);
                    $nrOfHeals++;
                    $originalHealth = $health;
                    $previousHealth = $health;
                }

                $victimIsDeath = false;
                if ($health == 0){
                    // check if attack button available (if health is 0, he can still be alive)
                    if ($this->checkForAttackButton($iimHolder)){
                        LogTest::log(INFO, "ATTACK", "Victim is not dead yet. Continue Attacking...");
                        $victimIsDeath = false;
                        $alive = true;
                    }
                    else {
                        LogTest::log(INFO, "ATTACK", "Victim is dead: " . $fighter->id);
                        $alive = false;
                        $statusObj->status = ATTACKSTATUS_OK;
                        $victimIsDeath = true;
                        break;
                    }
                }
                if (!$victimIsDeath) {
                    $deltaHealth = 0;
                    if (!$firstAttack) {
                        $deltaHealth = $previousHealth - $health;
                        LogTest::log(INFO, "ATTACK", "Victim Health changed: " . $deltaHealth);
                    }
                    $previousHealth = $health;
                    if ($nrOfAttacks > 50 && $health > 10) {
                        LogTest::log(INFO, "ATTACK", "Max. Nr Of Attacks Reached. Skipping...");
                        $statusObj->status = ATTACKSTATUS_OK;
                        break;
                    } else if ($nrOfHeals > 22 && $health > 2) {
                        LogTest::log(INFO, "ATTACK", "Victim Heals too fast. Skipping...");
                        GlobalSettings::$maxHealed++;
                        $statusObj->status = ATTACKSTATUS_OK;
                        break;
                    } else if (!$firstAttack && $deltaHealth > 0 && $deltaHealth < 2 && $health > 25 && $nrOfAttacks > 20) {
                        LogTest::log(INFO, "ATTACK", "Victim has too much health. Skipping...");
                        LogTest::log(INFO, "ATTACK", "Orignal Health: " . $originalHealth);
                        LogTest::log(INFO, "ATTACK", "Current Health: " . $health);
                        LogTest::log(INFO, "ATTACK", "Nr of Attacks: " . $nrOfAttacks);
                        GlobalSettings::$skippedHealth++;
                        $statusObj->status = ATTACKSTATUS_OK;
                        break;
                    } else {
                        checkHealth($iimHolder);
                        $stamina = getStamina($iimHolder);
                        if ($stamina < 5) {
                            $statusObj->status = ATTACKSTATUS_NOSTAMINA;
                            break;
                        }
                        if ($rivalMobster) {
                            $retCode = playMacro($iimHolder, FIGHT_FOLDER, "42_VictimRivalMobster_Attack.iim", MACRO_INFO_LOGGING);
                        } else {
                            addMacroSetting($iimHolder, "ID", $fighter->id);
                            $retCode = playMacro($iimHolder, FIGHT_FOLDER, "41_Victim_Attack", MACRO_INFO_LOGGING);
                        }
                        $firstAttack = false;
                        $statusObj->totalStamina += 5;
                        $nrOfAttacks++;
                        checkSaldo($iimHolder);
                        // maybe todo: check status of fight. If Message starts with "It looks like"
                        // Opponent was already dead and no stamina is spent
                        // maybe also check if is iced by you
                        $health = $this->getVictimHealth($iimHolder);
                        if ($retCode != SUCCESS) {
                            $statusObj->status = ATTACKSTATUS_PROBLEM;
                            break;
                        }
                    }
                }
            } else {
                // Problem with script
                $statusObj->status = ATTACKSTATUS_PROBLEM;
                return $statusObj;
            }
            //alive = false;
        } while ($alive);
        LogTest::log(INFO, "ATTACK", "Attack Figther Finished.");
        LogTest::log(INFO, "ATTACK", "Total Stamina used: " . $statusObj->totalStamina);
        LogTest::log(INFO, "ATTACK", "Total Attacks: " . $nrOfAttacks);
        GlobalSettings::$nrOfAttacks += $nrOfAttacks;
        return $statusObj;
    }

    function checkIfIced($iimHolder)
    {
        $iced = false;
        $retCode = playMacro($iimHolder, FIGHT_FOLDER, "31_Attack_Status.iim", MACRO_INFO_LOGGING);
        if ($retCode == SUCCESS) {
            $msg = strtoupper(getLastExtract($iimHolder, 1, "Ice Status", "Riki just Killed blabla. Your Kill Count is now 777"));
            LogTest::log(INFO, "FIGHT", "Check For Iced: " . $msg);
            if (contains($msg, 'YOUR KILL COUNT')) {
                $iced = true;
            } else if (contains($msg, "JUST KILLED")) {
                $iced = true;
            }
        } else {
            LogTest::log(INFO, "FIGHT", "Problem getting fight status: " . $retCode);
        }
        if ($iced) {
            LogTest::log(INFO, "FIGHT", "Total Ices: " . ++GlobalSettings::$iced);
        }
        return $iced;
    }


    function addFriend($fighter)
    {
        if (!$this->findFighter($this->friendObj->fighters, $fighter->id)) {
            $this->friendObj->fighters[] = $fighter;
            writeJSON($this->friendObj, MR_FRIENDS_FILE);
        }
    }

    function addStrongerOpponent($fighter)
    {
        if (!$this->findFighter($this->fightersToExclude->fighters, $fighter->id)) {
            $this->fightersToExclude->fighters[] = $fighter;
            writeJSON($this->fightersToExclude, MR_FIGHTERS_EXCLUDE_FILE);
        }
    }

    function addFighter($fighter)
    {
        if (!$this->findFighter($this->fighterObj->fighters, $fighter->id)) {
            $this->fighterObj->fighters[] = $fighter;
            writeJSON($this->fighterObj, MR_FIGHTERS_FILE);
        }
    }

    function evaluateAttackMessage($msg)
    {
        LogTest::log(INFO, "ATTACK", "Msg = " . $msg);
        if (isNullOrBlank($msg)) {
            return OPPONENT_UNKNOWN;
        }
        $msg = strtoupper($msg);
        if (startsWith($msg, "YOU LOST")) {
            return OPPONENT_LOST;
        } else if (startsWith($msg, "YOU WON")) {
            return OPPONENT_WON;
        } else if (startsWith($msg, "YOU CANNOT ATTACK YOUR FRIEND")) {
            return OPPONENT_FRIEND;
        } else if (startsWith($msg, "IT LOOKS LIKE")) {
            return OPPONENT_DEAD;
        } else {
            return OPPONENT_UNKNOWN;
        }
    }

    function getFightList($iimHolder)
    {
        LogTest::log(INFO, "FIGHTLIST", "Getting Fight List Info");
        $list = Array();
        $retCode = playMacro($iimHolder, FIGHT_FOLDER, "20_Extract_Start.iim", MACRO_INFO_LOGGING);
        LogTest::log(INFO, "FIGHTLIST", "Extract_Start Return Code: " . $retCode);
        if ($retCode == SUCCESS) {
            for ($i = 1; $i <= 15; $i++) {
                addMacroSetting($iimHolder, "pos", (string)$i);
                $retCode = playMacro($iimHolder, FIGHT_FOLDER, "21_Extract.iim", MACRO_INFO_LOGGING);
                if ($retCode == SUCCESS) {
                    $id = $this->extractIdFromString(getLastExtract($iimHolder, 1, "Fighter ID", "123456789"));
                    $name = getLastExtract($iimHolder, 2, "Fighter Name", "BlaBla");
                    $name = substr($name, 0, 100);
                    $level = $this->extractLevelFromString(getLastExtract($iimHolder, 3, "Fighter Level", "200"));
                    $object = new Fighter($id, $name, $level);
                    $list[] = $object;
                } else {
                    // ignore this line on the fight list
                    LogTest::log(INFO, "FIGHTLIST", "Last Line reached: " . $i);
                    break;
                }
            }
        } else {
            throw new Error("Problem With Extract Start");
        }
        return $list;
    }

    function filterFightList($fightList)
    {
        $filteredList = Array();
        if ($fightList != null && count($fightList) > 0) {
            foreach ($fightList as $fighter) {
                // lookup strong opponents list
                if (!$this->findFighter($this->fightersToExclude->fighters, $fighter->id)) {
                    // lookup friends list
                    if (!$this->findFighter($this->friendObj->fighters, $fighter->id)) {
                        $maxLevel = GlobalSettings::$currentLevel == 0 ? GlobalSettings::$maxLevel : (GlobalSettings::$currentLevel + 400);
                        if ($fighter->level <= $maxLevel) {
                            $filteredList[] = $fighter;
                        } else {
                            LogTest::log(INFO, "FIGHTLIST", "High Level: " . $fighter->id . " / Level: " . $fighter->level);
                        }
                    } else {
                        LogTest::log(INFO, "FIGHTLIST", "Friend Found: " . $fighter->id);
                    }
                } else {
                    LogTest::log(INFO, "FIGHTLIST", "Excluded Fighter Found: " . $fighter->id);
                }
            }
            LogTest::log(INFO, "FIGHTLIST", "Filtered Fightlist count: " . count($filteredList));
            return $filteredList;
        }
    }

    function findFighter($list, $id)
    {
        $found = false;
        forEach ($list as $arrayItem)
        {
            if ($arrayItem->id == $id) {
                $found = true;
                break;
            }
        }
    	return $found;
    }

    function extractLevelFromString($text){
        $text = removeComma($text);
        $regExp = "/Level (.*)$/";
        preg_match($regExp, $text, $matches);
        if ($matches != null && count($matches) > 0){
            $level = $matches[count($matches)-1];
            $level = intval($level);
            return $level;
        }
        return $text;
    }


    function extractIdFromString($text){
        $regExp = "/id=(.*)\">/";
        preg_match($regExp, $text, $matches);
        if ($matches != null && count($matches) > 0){
            return $matches[count($matches)-1];
        }
        return $text;
    }

    function addIcing($fighter){
        $foundFighter = $this->findFighter($this->fighterObj->fighters, $fighter->id);
        if ($foundFighter != null) {
            if (isset($foundFighter->iced) && is_int($foundFighter->iced)) {
                $foundFighter->iced++;
            }
            else {
                $foundFighter->iced = 1;
            }
            $date = new DateTime();
            $formattedDate = $date->format("YmdHis");
            $foundFighter->lastTimeIced = $formattedDate;
        }
        else {
            LogTest::log(INFO, "ADDICING", "Problem adding Ice for fighter " . $fighter->id);
        }

    }

    function removeItemFromArray($file, $id){
        $obj = readJSON(file);
        $index = -1;
        for ($i=0; $i < $obj->fighters->length; $i++){
            $item = $obj->fighters[i];
            if ($item->id == $id){
                $index = $i;
                break;
            }
        }
	if ($index >= 0){
        array_splice($obj->fighters, $index, 1);
        writeJSON($obj, $file);
    }
	return ($index > -1);
}

    function startProfileAttack($iimHolder){
        $status = CONSTANTS.ATTACKSTATUS.OK;
        $nr = count($this->fighterObj->fighters);
        $start = rand(0, $nr - 100);
        $end = min($start+100, $nr-1);
        logV2(INFO, "FIGHT", "Range Max:" . ($nr - 100));
        logV2(INFO, "FIGHT", "Max:" . $nr);
        logV2(INFO, "FIGHT", "Random Start Position: " . $start);
        logV2(INFO, "FIGHT", "Random End Position: " . $end);
        for ($i=$start; i < $end; $i++) {
            $arrayItem = $this->fighterObj->fighters[i];
            addMacroSetting($iimHolder,"ID", arrayItem.id);
            $retCode = playMacro($iimHolder,FIGHT_FOLDER, "80_Profile_Attack_Init.iim", MACRO_INFO_LOGGING);
            if ($retCode == SUCCESS) {
                if (!$arrayItem->skip) {
                    LogTest::log(INFO, "FIGHT", "Profile Fighting Player " + $arrayItem->id + " - " + $arrayItem->name);
                    $statusObj = $this->attack($iimHolder,$arrayItem, false, true);
                    switch ($statusObj->status) {
                        case ATTACKSTATUS_OK :
                            // do nothing, continue with next fighter
                        case ATTACKSTATUS_PROBLEM :
                            LogTest::log(INFO, "FIGHT", "Problem With Fighter. Skipping...");
                        case ATTACKSTATUS_NOSTAMINA :
                            LogTest::log(INFO, "FIGHT", "Out Of Stamina. Exiting Profile Fighters List");
                            $status = ATTACKSTATUS_NOSTAMINA;
                            break;
                    }
                }
                else {
                    LogTest::log(INFO, "FIGHT", "Skipping Stronger Opponent: " + arrayItem.id);
                }
            }
            else {
                LogTest::log(INFO, "FIGHT", "startProfileAttack Return Status: " + status);
            }
        }
    // reload fighters list
        LogTest::log(INFO, "FIGHT", "Reloading fighters list");
        $this->fighterObj = readJSON(MR_FIGHTERS_FILE);
        return $status;
    }

}

class Fighter {
    public $id;
    public $name;
    public $level;
    public $skip;
    public $iced;
    public $lastTimeIced;

    function __construct($id, $name, $level)
    {
        $this->id = $id;
        $this->name = $name;
        $this->level = $level;
    }
}

class FightStatus{
    public $status = null;
    public $totalStamina = 0;
    public $iced = 0;

    function __construct($status)
    {
        $this->status = $status;
    }
}

?>