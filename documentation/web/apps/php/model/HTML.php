<?php

class Word
{
  public $oldWord;
  public $newWord;
  public $id;

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
    public function __construct3($id, $oldWord, $newWord)
    {
        $this->oldWord = $oldWord;
        $this->newWord = $newWord;
    }
}

class ExtWord extends Word {
  public $parenthesis;
  public $exactMatch;
  public function __construct4($oldWord, $newWord, $parenthesis, $exactMatch)
  {
    $this->oldWord = $oldWord;
    $this->newWord = $newWord;
    $this->parenthesis = $parenthesis;
    $this->exactMatch = $exactMatch;
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

class PreprocessorConfiguration
{
  public $id;
  public $config;

  public function __construct()
  {
    $this->id = '';
    $this->config = Array();
  }
}


class PreprocessorConfigurationItem
{
  public $id;
  public $type;
  public $splitter;
  public $duration;

  public function __construct()
  {
    $this->type = '';    
    $this->splitter = '';
	$this->duration = false;
    $this->id = '';
  }
  public function __construct_2($id, $type, $splitter, $duration)
  {
    $this->id = $id;
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

