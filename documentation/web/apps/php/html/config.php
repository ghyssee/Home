<?php

class Input
{
    public $name;
    public $id;
    public $value;
    public $default;
    public $size;
    public $label;
    public $text;
    public $required;
    public $method;
    public $col = 1;
    public $colspan = 1;
    public $labelClass;
    // $methodArg = property name of an element from the comboBox $array Argument
    public $methodArg;

    public function __construct($array)
    {
        $keys = array_keys($array);
        foreach($keys as $key => $tst) {
            $this->{$tst} = $array[$tst];
        }
    }
}

class Layout
{
    public $numCols = 1;
    public $elements;
    public $rows = 1;
    public $maxRows = 1;
    public $previousCol = 0;
    public $errors;

    public function __construct($array)
    {
        $keys = array_keys($array);
        foreach($keys as $key => $tst) {
            $this->{$tst} = $array[$tst];
        }
    }

    function addElement(Input $input, $html){
        if ($this->previousCol < $input->col){
            $this->rows = 1;
        }
        else {
            $this->rows++;
        }
        $this->previousCol = $input->col;
        $this->elements[$this->rows][$input->col] = $html;
        $this->maxRows = max($this->rows, $this->maxRows);
    }

    function inputBox3(Input $input){

        errorCheck($input->name, $this->errors);
        $html = '';
        $html .= "<td" . (empty($input->labelClass) ? '' : ' class="' . $input->labelClass . '"') . ">" . $input->label . "</td>" . PHP_EOL;
        $html .= "<td>";
        $html .= '<input size="' . $input->size . '" type="text" name="' . $input->name . '" value="' . $input->value . '">';
        $html .= "</td>" . PHP_EOL;
        if (!empty($error)){
            $html .= '</tr></table></td>';
        }
        $this->addElement($input, $html);
    }

    function checkBox3(Input $input){
        $html = "<tr>" . PHP_EOL;
        $html .= "<td colspan='2'>" . $input->label;
        $html .= '<input type="checkbox" name="' . $input->name . '"';
        $html .= ' value="' . $input->value . '"';
        $html .= ($input->value ? " checked" : "");
        $html .= '>';
        $html .= "</td></tr>";
        $html .= PHP_EOL;
        $this->addElement($input, $html);
    }

    /* $id    = fieldname that contains the id
       $value = fieldname that contains the value that will be displayed on the screen
       $input = a Class Object that contains the HTML Settings, like name of the select, the default value
    */
    function comboBox3($array, $id, $value, Input $input){
        $html = "<tr>" . PHP_EOL;
        $html .= "<td>" . $input->label . "</td>" . PHP_EOL;
        $html .= "<td>";
        $html .= '<select name="' . $input->name . '">';

        foreach($array as $key => $item) {

            $selected = "";
            if ($item->{$id} ==  $input->default){
                $selected = " selected";
            }
            $html .= '<option value="' . $item->{$id} . '"' . $selected . ">";
            if (!empty($input->method)){
                $function = new ReflectionFunction($input->method);
                $html .= $function->invoke($item->{$input->methodArg});
            }
            else {
                $html .= $item->{$value};
            }
            $html .= '</option>' . PHP_EOL;
        }
        $html .= "</select>";
        $html .= "</td>" . PHP_EOL;;
        $html .= "</tr>" . PHP_EOL;;
        $this->addElement($input, $html);
    }

    function label(Input $input){
        $html = '<td' . ($input->colspan > 1 ? ' colspan="' . $input->colspan . '"' : '') . '>' . $input->label . "</td>" . PHP_EOL;
        $this->addElement($input, $html);
    }

    function button2(Input $input){
        $html = '<td class="buttonCell"' . ($input->colspan > 1 ? ' colspan="' . $input->colspan . '"' : '') . '>';
        $html .= '<button name="' . $input->name . '"';
        $html .= ' value="' . $input->value . '"';
        $html .= '>';
        $html .= $input->text;
        $html .= '</button>';
        $html .= "</td>" . PHP_EOL;
        $this->addElement($input, $html);
    }

    public function close()
    {
        //echo var_dump($this->elements);
        echo "<table>" . PHP_EOL;
        for ($x = 1; $x <= $this->rows; $x++) {
            echo "<tr>" . PHP_EOL ;
            for ($y = 1; $y <= $this->numCols; $y++) {
                if (isset($this->elements[$x][$y])) {
                    echo $this->elements[$x][$y];
                }
            }
            echo "</tr>" . PHP_EOL;
        }
        echo "</table>" . PHP_EOL;
    }
}

function inputBox(Input $input){
    echo '<input size="' . $input->size . '" type="text" name="' . $input->name . '" value="' . $input->value . '">';
    echo PHP_EOL;
}

function inputBox2(Input $input){
    echo "<tr>" . PHP_EOL;
    echo "<td>" . $input->label . "</td>" . PHP_EOL;
    echo "<td>";
    echo '<input size="' . $input->size . '" type="text" name="' . $input->name . '" value="' . $input->value . '">';
    echo "</td>" . PHP_EOL;;
    echo "</tr>" . PHP_EOL;;
}

/* $id    = fieldname that contains the id
   $value = fieldname that contains the value that will be displayed on the screen
   $input = a Class Object that contains the HTML Settings, like name of the select, the default value
*/
function comboBox2($array, $id, $value, Input $input){
    echo "<tr>" . PHP_EOL;
    echo "<td>" . $input->label . "</td>" . PHP_EOL;
    echo "<td>";
    echo '<select name="' . $input->name . '">';

    foreach($array as $key => $item) {

        $selected = "";
        if ($item->{$id} ==  $input->default){
            $selected = " selected";
        }
        echo '<option value="' . $item->{$id} . '"' . $selected . ">";
        if (!empty($input->method)){
            $function = new ReflectionFunction($input->method);
            echo $function->invoke($item->{$input->methodArg});
        }
        else {
            echo $item->{$value};
        }
        echo '</option>' . PHP_EOL;
    }
    echo "</select>";
    echo "</td>" . PHP_EOL;;
    echo "</tr>" . PHP_EOL;;
}

function comboBox($array, $id, $value, Input $input){
    echo '<select name="' . $input->name . '">';

    foreach($array as $key => $item) {

        $selected = "";
        if ($item->{$id} ==  $input->default){
            $selected = " selected";
        }
        echo '<option value="' . $item->{$id} . '"' . $selected . ">";
        if (!empty($input->method)){
            $function = new ReflectionFunction($input->method);
            echo $function->invoke($item->{$input->methodArg});
        }
        else {
            echo $item->{$value};
        }
        echo '</option>' . PHP_EOL;
    }
    echo "</select>";
    echo PHP_EOL;
}

function checkBox2(Input $input){
    echo "<tr>" . PHP_EOL;
    echo "<td colspan='2'>" . $input->label;
    echo '<input type="checkbox" name="' . $input->name . '"';
    echo ' value="' . $input->value . '"';
    echo ($input->value ? " checked" : "");
    echo '>';
    echo "</td></tr>";
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

 function errorCheck($key, &$errors){
	$html = '';
     if (isset($_SESSION["errors"])){
		$array = $_SESSION["errors"];
		if (isset($array[$key])){
            //$html = '<td colspan="2"><table><tr>';
            //$html .= '<td class="errorMessage" colspan=2>' . $array[$key] . "</td></tr><tr>";
            $html = $array[$key];
            array_push($errors, $html);
            //$html .= PHP_EOL;
		}
	}
     //return $html;
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
