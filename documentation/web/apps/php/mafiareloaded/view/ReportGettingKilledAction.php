<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 16/03/2018
 * Time: 10:56
 */
chdir("..");
include_once "../setup.php";

include_once documentPath (ROOT_PHP, "config.php");
include_once documentPath (ROOT_PHP_MR_BO, "HomeFeedBO.php");

class ReportTO {
    public $gangId;
    public $fighterId;
    public $history;
    public $profile;
    public $message;
    function __construct() {
    }
}

if (isset($_REQUEST['report'])) {
    $reportTO = new ReportTO();
    assignField($reportTO->gangId, "gangId");
    assignField($reportTO->fighterId, "fighterId");
    assignField($reportTO->profile, "profile");
    assignCheckbox($reportTO->history, "history");
    $reportTO->message = "";
    $homefeedBO = new HomeFeedBO($reportTO->profile, false);
    $kills = $homefeedBO->getKills($reportTO);
    foreach($kills as $item){
        $test = convertStringYYYYMMDDHHMMSSToDate($item->timeStamp);
        $newline = "<br>";
        if (!$test){
            $reportTO->message .= 'Error in timestamp: ' . htmlentities($item->feedMsg) . $newline;
        }
        else {
            /*
            $currDate = new DateTime();
            $interval = date_diff($currDate, $test);
            $tst = convertIntervalToString($interval);
            $reportTO->message .= $tst . ' ago: ' . htmlentities($item->feedMsg) . $newline;
            */
            $reportTO->message .= convertDateToString(convertStringYYYYMMDDHHMMSSToDate($item->currentTime)) . ': ';
            $reportTO->message .= $item->timeMsg . " " . $item->feedMsg  . $newline;
        }
    }
    echo json_encode($reportTO);
}

function convertTimeUnit(&$first,$val, $unit, $plural){
    $str = '';
    if ($val > 0 || !$first) {
        $str = ($first ? '' : ', ') . $val . ' ' . $unit . ($val > 1 ? $plural : '');
        $first = false;
    }
    return $str;
}

function convertIntervalToString(DateInterval $interval){
    $str = '';

    $first = true;
    $str .= convertTimeUnit($first, $interval->y, 'year', 's');
    $str .= convertTimeUnit($first, $interval->m, 'month','s');
    $str .= convertTimeUnit($first, $interval->d, 'day','s');
    $str .= convertTimeUnit($first, $interval->h, 'hour','s');
    $str .= convertTimeUnit($first, $interval->i, 'minute','s');
    $str .= convertTimeUnit($first, $interval->s, 'second','s');
    return $str;
}

?>
