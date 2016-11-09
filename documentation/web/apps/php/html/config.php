<?php
$tabIndex = 1;
$focus = false;

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
    public $errors = [];
    public $errorClass = "errorMessage";

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

    function getClasses(Input $input, $hasError){
        $class = ' class="';
        $counter = 0;
        if (!empty($input->labelClass)){
            $class .= $input->labelClass;
            $counter++;
        }
        if ($hasError){
            $class .= ($counter > 0 ? ' ' : '') . $this->errorClass;
            $counter++;
        }
        return $counter > 0 ? $class . '"' : '';
    }

    function checkTabIndex(){
        $html = '';
        if ($this->numCols > 1){
            $html .= ' tabindex="' . $GLOBALS['tabIndex'] . '"';
            $GLOBALS['tabIndex']++;
        }
        return $html;
    }

    function checkAutofocus($hasError){
        $html = '';
        if (isset($_SESSION["errors"])){
            if ($hasError && !$GLOBALS['focus']){
                $html .= " autofocus";
                $GLOBALS['focus'] = true;
            }
        }
        else {
            if (!$GLOBALS['focus']){
                $html .= " autofocus";
                $GLOBALS['focus'] = true;
            }
        }
        return $html;
    }

    function inputBox(Input $input){

        $hasError = errorCheck($input->name, $this->errors);
        $html = '';
        $html .= "<td" . $this->getClasses($input, $hasError) . ">" . $input->label;
        $html .= $hasError ? " *" : "";
        $html .= "</td>" . PHP_EOL;
        $html .= "<td>";
        $html .= '<input size="' . $input->size . '" type="text" name="' . $input->name . '" value="' . $input->value . '"';
        $html .= $this->checkTabIndex() . $this->checkAutofocus($hasError);
        $html .= '>';
        $html .= "</td>" . PHP_EOL;
        if (!empty($error)){
            $html .= '</tr></table></td>';
        }
        $this->addElement($input, $html);
    }

    function checkBox(Input $input){
        $html = "<tr>" . PHP_EOL;
        $html .= "<td colspan='2'>" . $input->label;
        $html .= '<input type="checkbox" name="' . $input->name . '"';
        $html .= ' value="' . $input->value . '"';
        $html .= ($input->value ? " checked" : "");
        $html .=  $this->checkTabIndex() .'>';
        $html .= "</td></tr>";
        $html .= PHP_EOL;
        $this->addElement($input, $html);
    }

    /* $id    = fieldname that contains the id
       $value = fieldname that contains the value that will be displayed on the screen
       $input = a Class Object that contains the HTML Settings, like name of the select, the default value
    */
    function comboBox($array, $id, $value, Input $input){
        $html = "<tr>" . PHP_EOL;
        $html .= "<td>" . $input->label . "</td>" . PHP_EOL;
        $html .= "<td>";
        $html .= '<select name="' . $input->name . '"' .  $this->checkTabIndex() . '>';

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

    function button(Input $input){
        $html = '<td class="buttonCell"' . ($input->colspan > 1 ? ' colspan="' . $input->colspan . '"' : '') . '>';
        $html .= '<button name="' . $input->name . '"';
        $html .= ' value="' . $input->value . '"';
        $html .=  $this->checkTabIndex() . '>';
        $html .= $input->text;
        $html .= '</button>';
        $html .= "</td>" . PHP_EOL;
        $this->addElement($input, $html);
    }

    public function printErrors()
    {
        if (!empty($this->errors)) {
            echo '<h3 class="errorMessage">Errors Found</h3>' . PHP_EOL;
            echo '<ul class="errorMessage">' . PHP_EOL;
            foreach ($this->errors as $key => $value) {
                echo "<li>" . $key . ": " . $value . "</li>" . PHP_EOL;
            }
            echo "</ul>" . PHP_EOL;
        }
    }

    public function close()
    {
        $this->printErrors();
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

function inputBoxOld(Input $input){
    echo '<input size="' . $input->size . '" type="text" name="' . $input->name . '" value="' . $input->value . '">';
    echo PHP_EOL;
}

function comboBoxOld($array, $id, $value, Input $input){
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

function checkBoxOld(Input $input){
    echo '<input type="checkbox" name="' . $input->name . '"';
    echo ' value="' . $input->value . '"';
    echo ($input->value ? " checked" : "");
    echo '>';
    echo PHP_EOL;
}

function buttonOld(Input $input){
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
            $errors[$key] = $html;
            return true;
            //array_push($errors, $html);
            //$html .= PHP_EOL;
		}
	}
     return false;
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

function checkSave($save, $key, $obj, $file, $returnObj = null)
{
    if ($save) {
        echo '<h1>Album Configuration Saved Status</h1>' . PHP_EOL;
        //writeJSON($obj, $file);
        println('Contents saved to ' . $file);
    } else {
        if (isset($returnObj)){
            $_SESSION[$key] = $returnObj;
        }
        else {
            $_SESSION[$key] = $obj;
        }
        header("Location: " . $_SESSION["previous_location"]);
        exit();
    }
}

?>
