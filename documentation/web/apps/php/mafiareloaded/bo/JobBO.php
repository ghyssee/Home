<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 24/11/2017
 * Time: 13:32
 */
require_once documentPath (ROOT_PHP, "config.php");
require_once documentPath (ROOT_PHP_MODEL, "HTML.php");
require_once documentPath (ROOT_PHP_BO, "CacheBO.php");

class ActiveJobTO {
    public $districtId;
    public $jobId;
    public $chapter;
    public $type;
    public $total;
    public $description;
    public $enabled;
}

class JobTO {
    public $id;
    public $chapter;
    public $type;
    public $description;
    public $loot;
    public $consumableCost;
    public $consumable;
    public $money;
    public $energy;
    public $exp;
}

class ChapterTO {
    public $id;
    public $name;
    public $star;
}

class DistrictTO {
    public $id;
    public $description;
    public $event;
    public $chapters = Array();
    public $jobs = Array();
}

class ActiveJobBO{
    public $jobManagerObj;
    public $file;

    function __construct() {
        $this->file = getFullPath(JSON_MR_JOBS);
        $this->jobManagerObj = readJSON( $this->file);
    }

    function getScheduledJobs($filterRules = null){
        $array = [];
        if ($filterRules != null){
            foreach($this->jobManagerObj->activeJobs as $key => $value){
                foreach($filterRules as $item){
                    $test = $item;
                    if ($test->field == "name"){
                        if (strpos(strtoupper($value->name), strtoupper($item->value)) !== false) {
                            $array[] = $value;
                        }
                    }
                    else if ($test->field == "stageName"){
                        if (strpos(strtoupper($value->stageName), strtoupper($item->value)) !== false) {
                            $array[] = $value;
                        }
                    }
                }

            }
        }
        else {
            $array = $this->jobManagerObj->activeJobs;
        }
        return $array;
    }
}