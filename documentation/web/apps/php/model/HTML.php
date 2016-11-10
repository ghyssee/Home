<?php

class Word
{
  public $oldWord;
  public $newWord;

  public function __construct()
  {
    $this->oldWord = '';
    $this->newWord = '';
  }
  public function __construct_2($oldWord, $newWord)
  {
    $this->oldWord = $oldWord;
    $this->newWord = $newWord;
  }
}

class Color
{
  public $id;
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

class Splitter
{
  public $id;
  public $pattern;


  public function __construct()
  {
    $this->id = '';    
    $this->pattern = '';
  }
  public function __construct_2($id, $pattern)
  {
    $this->id = $id;    
    $this->pattern = $pattern;
  }
}

class Config
{
  public $type;
  public $splitter;
  public $duration;

  public function __construct()
  {
    $this->type = '';    
    $this->splitter = '';
	$this->duration = false;
  }
  public function __construct_2($type, $splitter, $duration)
  {
    $this->type = $type;    
    $this->splitter = $splitter;
	$this->duration = $duration;
  }
}


?>

