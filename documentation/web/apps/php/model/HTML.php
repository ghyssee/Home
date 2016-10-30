<?php

class Color
{
  public $description;
  public $code;


  public function __construct()
  {
    $this->description = '';    
    $this->code = '';
  }
  public function __construct_2($description, $code)
  {
    $this->description = $description;    
    $this->code = $code;
  }

}

?>

