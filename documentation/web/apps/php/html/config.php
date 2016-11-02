<?php

 function inputBox2($name, $value, $size){
 echo '<input size="' . $size . '" type="text" name="' . $name . '"" value="' . $value . '">';
 }

class Input
{
    public $name;
    public $id;
    public $value;
    public $default;
    public $size;

    public function __construct($array)
    {
        $keys = array_keys($array);
        foreach($keys as $key => $tst) {
            $this->{$tst} = $array[$tst];
        }
    }
}

function inputBox(Input $input){
    //<input size="50" type="text" name="albumTag" value="<?php print $mp3PreprocessorObj->albumTag;
    echo '<input size="' . $input->size . '" type="text" name="' . $input->name . '" value="' . $input->value . '">';
}

function comboBox($array, $id, $value, Input $input){
    echo '<select name="' . $input->name . '">';

    foreach($array as $key => $item) {

        $selected = "";
        if ($item->{$id} ==  $input->default){
            $selected = " selected";
        }
        echo '<option value="' . $item->{$id} . '"' . $selected . ">" . $item->{$value} . "</option>";
    }
    echo "</select>";
}

function comboBox2($array, $name, $id, $value, $default = ""){
	echo '<select name="' . $name . '">';

	foreach($array as $key => $item) {

		$selected = "";
		if ($item->{$id} == $default){
			$selected = " selected";
		}
		echo '<option value="' . $item->{$id} . '"' . $selected . ">" . $item->{$value} . "</option>";
	}
	echo "</select>";
}
 
 function button ($name, $value, $text){
  echo '<button name="' . $name . '" value="' . $value . '">' . $text . '</button>';
 }

 function errorCheck($key){
	if (isset($_SESSION["errors"])){
		$array = $_SESSION["errors"];
		if (isset($array[$key])){
			echo '<tr class="errorMessage"><td colspan=2>' . $array[$key] . '</td></tr>';
		}
	}
 }
 
 function addError($field, $message){
	if (!isset($_SESSION["errors"])){
		$errors = array();
	}
	else {
		$errors = $_SESSION["errors"];
	}
	$errors = $_SESSION["errors"];
	$errors[$field] = $message;
	$_SESSION["errors"] = $errors;
 }

 ?>

