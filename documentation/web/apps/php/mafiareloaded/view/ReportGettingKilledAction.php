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
    public $profile;
    public $message;
    function __construct() {
    }
}


if (isset($_REQUEST['report'])) {
    $reportTO = new ReportTO();
    assignField($reportTO->gangId, "gangId");
    assignField($reportTO->profile, "profile");
    $reportTO->message = "";
    $homefeedBO = new HomeFeedBO($reportTO->profile, false);
    $kills = $homefeedBO->getKills($reportTO->gangId);
    foreach($kills as $item){
        $test = convertStringYYYYMMDDHHMMSSToDate($item->timeStamp);
        if (!$test){
            $reportTO->message .= 'Error in timestamp: ' . $item->feedMsg . "<BR>";
        }
        else {
            $currDate = new DateTime();
            $interval = date_diff($currDate, $test);
            $tst = convertIntervalToString($interval);
            $reportTO->message .= $tst . ' ago: ' . $item->feedMsg . "<BR>";
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
