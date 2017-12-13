<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 13/12/2017
 * Time: 13:45
 */

class FightSettingsTO {
    public $autoHeal;
    public $minLengthOfFightList;

    public function __construct()
    {
        $this->autoHeal = false;
        $this->minLengthOfFightList = 0;
    }
}

class SettingsBO
{

}