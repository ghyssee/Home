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

class ActiveJobTO extends Castable {
    public $id;
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

class Castable
{
    public function __construct($object = null)
    {
        $this->cast($object);
    }

    public function cast($object)
    {
        if (is_array($object) || is_object($object)) {
            foreach ($object as $key => $value) {
                if (property_exists($this, $key)) {
                    $this->$key = $value;
                }
                $var = "blbb";
            }
        }
    }
}


class ChapterTO {
    public $id;
    public $name;
    public $star;
}

class DistrictTO extends Castable {
    public $id;
    public $description;
    public $event;
}

class DistrictCompositeTO extends DistrictTO {
    public $chapters = Array();
    public $jobs = Array();
}

class ActiveJobBO{
    public $jobManagerObj;
    public $fileCode = JSON_MR_JOBS;

    function __construct() {
        $this->jobManagerObj = readJSONWithCode($this->fileCode);
    }

    function getDistrictsForComboBox(){
        $districts = Array();
        foreach($this->jobManagerObj->districts as $key => $item){
            $districtTO = new DistrictTO();
            $districtTO->id = $item->id;
            $districtTO->description = $item->id . " - " . $item->description;
            $districtTO->event = $item->event;
            $districts[] = $districtTO;
        }
        return $districts;
    }

    function getJobs($districtId, $chapter){
        $district = $this->findDistrict($districtId);
        $jobs = Array();
        if ($district != null) {
            if ($chapter != null){
                foreach($district->jobs as $key => $item) {
                    if ($item->chapter == $chapter){
                        $jobs[] = $item;
                    }
                }
            }
            else {
                return $district->jobs;
            }
        }
        return $jobs;
    }

    function getChapters($districtId){
        $district = $this->findDistrict($districtId);
        $chapters = Array();
        if ($district != null) {
            return $district->chapters;
        }
        return $chapters;
    }

    function getJobTypes(){
        return $this->jobManagerObj->types;
    }

    function convertToDistrictTO($district){
        $districtTO = new DistrictTO();
        $districtTO2 = new DistrictTO();
        foreach ($districtTO as $key=>$value) {
            $districtTO2->{$key} = $district->{$key};
        }
        return $districtTO2;
    }

    function findDistrict($districtId){
        $district = null;
        foreach($this->jobManagerObj->districts as $key => $item){
            if ($item->id == $districtId){
                $district = $item;
                break;
            }
        }
        return $district;
    }

    function findJob($district, $jobId){
        $foundJob = new JobTO();
        foreach($district->jobs as $key => $job){
            if ($job->id == $jobId){
                $foundJob = $job;
                break;
            }
        }
        return $foundJob;
    }

    function getScheduledJobs(){
        $array = [];
        foreach($this->jobManagerObj->activeJobs as $key => $value){
            $district = $this->findDistrict($value->districtId);
            $strippedDistrict = $this->convertToDistrictTO($district);
            $value->district = $strippedDistrict;
            $job = $this->findJob($district, $value->jobId);
            $value->job = $job;
            $array[] = $value;
        }
        return $array;
    }

    function saveJobList($activeJobs){
        $this->jobManagerObj->activeJobs = $activeJobs;
        $this->save();
    }

    function addScheduledJob(ActiveJobTO $activeJobTO, $insertBeforeId){
        $activeJobTO->id = getUniqueId();
        $newArray = Array();
        $found = false;
        if (!isset($insertBeforeId)){
            $this->jobManagerObj->activeJobs[] = $activeJobTO;
        }
        else {
            foreach ($this->jobManagerObj->activeJobs as $key => $value) {
                if (!$found && $value->id == $insertBeforeId){
                    $newArray[] = $activeJobTO;
                    $found = true;
                }
                $newArray[] = $value;
            }
            if (!$found){
                $newArray[] = $activeJobTO;
            }
            $this->jobManagerObj->activeJobs = $newArray;
        }
        $this->save();
    }

    function updateScheduledJob(ActiveJobTO $activeJobTO){
        $counter = 0;
        $feedBackTO = new FeedBackTO();
        $feedBackTO->success = false;
        foreach ($this->jobManagerObj->activeJobs as $key => $value) {
            if (strcmp($value->id, $activeJobTO->id) == 0) {
                $this->jobManagerObj->activeJobs[$counter] = $activeJobTO;
                //$this->save();
                $feedBackTO->success = true;
                break;
            }
            $counter++;
        }
        if (!$feedBackTO->success){
            $feedBackTO->errorMsg = "Problem updating the scheduled job with Id " . $activeJobTO->id;
        }
        return $feedBackTO;
    }

    function deleteScheduledJob($id){
        $feedbackTO = new FeedBackTO();
        $key = array_search($id, array_column($this->jobManagerObj->activeJobs, "id"));
        if ($key === false) {
            $feedbackTO->success = false;
            $feedbackTO->errorMsg = 'There was a problem finding the scheduled job wiht ID ' . $id;
            return $feedbackTO;

        } else {
            unset($this->jobManagerObj->activeJobs[$key]);
            $array = array_values($this->jobManagerObj->activeJobs);
            $this->jobManagerObj->activeJobs = $array;
            $this->save();
            $feedbackTO->success = true;
        }
        return $feedbackTO;

    }

    function save(){
        writeJSONWithCode($this->jobManagerObj, $this->fileCode);
    }


}