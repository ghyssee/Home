<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 16/02/2018
 * Time: 10:30
 */

class FightTO {
    public $id;
    public $name;
    public $level;
    public $gangId;
    public $gangName;
    public $bigHealth;
    public $lastAttacked;
    public $lastIced;
    public $iced;
    public $alive;
    public $dead;
    public $active;
}

class AssassinTO extends FightTO {
    public $active;
}

class FightBO
{

}