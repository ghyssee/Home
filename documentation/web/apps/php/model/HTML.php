<?php

class Word
{
  public $oldWord;
  public $newWord;

  public function __construct()
  {
        $get_arguments       = func_get_args();
        $number_of_arguments = func_num_args();
        $method_name         = '__construct'.$number_of_arguments;

        if (method_exists($this, $method_name)) {
          call_user_func_array(array($this, $method_name), $get_arguments);
        }
  }

  public function __construct1()
  {
    $this->oldWord = '';
    $this->newWord = '';
  }
  public function __construct2($oldWord, $newWord)
  {
    $this->oldWord = $oldWord;
    $this->newWord = $newWord;
  }
}

class ExtWord extends Word {
  public $parenthesis;
  public function __construct3($oldWord, $newWord, $parenthesis)
  {
    $this->oldWord = $oldWord;
    $this->newWord = $newWord;
    $this->parenthesis = $parenthesis;
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
  public $description;


  public function __construct()
  {
    $this->id = '';    
    $this->pattern = '';
    $this->description = '';
  }
  public function __construct_2($id, $pattern, $description)
  {
    $this->id = $id;    
    $this->pattern = $pattern;
    $this->description = $description;
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

class TableGrid
{
  public $title;
  public $url;
  public $newUrl;
  public $updateUrl;

  public function __construct()
  {
  }
}

?>

