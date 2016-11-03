<?php

class Input
{
    public $name;
    public $id;
    public $value;
    public $default;
    public $size;
    public $text;
    public $required;

    public function __construct($array)
    {
        $keys = array_keys($array);
        foreach($keys as $key => $tst) {
            $this->{$tst} = $array[$tst];
        }
    }
}

function inputBox(Input $input){
    echo '<input size="' . $input->size . '" type="text" name="' . $input->name . '" value="' . $input->value . '">';
    echo PHP_EOL;
}

/* $id    = fieldname that contains the id
   $value = fieldname that contains the value that will be displayed on the screen
   $input = a Class Object that contains the HTML Settings, like name of the select, the default value
*/
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
    echo PHP_EOL;
}

function checkBox(Input $input){
    echo '<input type="checkbox" name="' . $input->name . '"';
    echo ' value="' . $input->value . '"';
    echo ($input->value ? " checked" : "");
    echo '>';
    echo PHP_EOL;
}

function button(Input $input){
    echo '<button name="' . $input->name . '"';
    echo ' value="' . $input->value . '"';
    echo '>';
    echo $input->text;
    echo '</button>';
    echo PHP_EOL;
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
    echo PHP_EOL;
}
 
 function button2 ($name, $value, $text){
  echo '<button name="' . $name . '" value="' . $value . '">' . $text . '</button>';
     echo PHP_EOL;
 }

 function errorCheck($key){
	if (isset($_SESSION["errors"])){
		$array = $_SESSION["errors"];
		if (isset($array[$key])){
			echo '<tr class="errorMessage"><td colspan=2>' . $array[$key] . '</td></tr>';
            echo PHP_EOL;
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
	//$errors = $_SESSION["errors"];
	$errors[$field] = $message;
	$_SESSION["errors"] = $errors;
 }

 ?>
