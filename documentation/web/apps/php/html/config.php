<?php

 function inputBox($name, $value, $size){
 echo '<input size="' . $size . '" type="text" name="' . $name . '"" value="' . $value . '">';
 }
 
 function button ($name, $value, $text){
  echo '<button name="' . $name . '" value="' . $value . '">' . $text . '</button>';
 }

 function errorCheck($group, $key){
	if (isset($_SESSION["errors"])){
		$array = $_SESSION["errors"];
		if (isset($array[$group][$key])){
			echo '<tr class="errorMessage"><td colspan=2>' . $array[$group][$key] . '</td></tr>';
		}
	}
 }
 
 function addError($group, $field, $message){
	if (!isset($_SESSION["errors"][$group])){
		$errors = array();
	}
	else {
		$errors = $_SESSION["errors"][$group];
	}
	$errors = $_SESSION["errors"][$group];
	$errors[$field] = $message;
	$_SESSION["errors"][$group] = $errors;
 }

 ?>

