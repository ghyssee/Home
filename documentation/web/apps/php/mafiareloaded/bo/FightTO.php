<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 16/02/2018
 * Time: 10:30
 */
require_once documentPath (ROOT_PHP_BO, "MyClasses.php");

class GangTO extends Castable {
    public $gangId;
    public $gangName;
}

class FightTO extends GangTO {
    public $id;
    public $name;
    public $level;
    public $bigHealth;
    public $lastAttacked;
    public $lastIced;
    public $iced;
    public $alive;
    public $dead;
}

class AssassinTO extends FightTO {
    public $fighterId;
    public $active;
}

class AllyTO {
    public $id;
    public $name;
    public $active;
}