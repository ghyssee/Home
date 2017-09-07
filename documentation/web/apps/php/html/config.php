<?php
$tabIndex = 1;
$focus = false;


class Input
{
    public $name;
    public $id;
    public $disabled;
    public $fieldId;
    public $value;
    public $default;
    public $size;
    public $label;
    public $text;
    public $required;
    public $method;
    public $col = 1;
    public $cols;
    public $rows;
    public $colspan = 1;
    public $labelClass;
    public $min;
    public $max;
    // used by displayTable
    public $title;
    public $update;
    public $delete;
    // inputBox type=text Or Number
    public $type;
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

class ToolBar extends FormLayout {
    function button(Input $input){
        $html = '<span class="toolbarButton2">';
        $html .= '<button ' . 'class="formButton toolbarButton"' . ' name="' . $input->name . '"';
        $html .= ' value="' . $input->value . '"';
        $html .=  '>';
        $html .= $input->text;
        $html .= '</button>';
        $html .= '</span>';
        $html .= PHP_EOL;
        $this->addElement($html);
    }

    function resetButton(){
        $html = '<span class="toolbarButton2">';
        $html .= '<input class="formButton toolbarButton" type="reset" value="Reset">';
        $html .= '</span>';
        $this->addElement($html);
}

    function addElement($html){
        array_push ($this->elements, $html);
    }


    public function close()
    {
        echo '<div>';
        foreach($this->elements as $value) {
            echo $value .PHP_EOL;
        }
        echo '</div>';
    }

}

abstract class FormLayout
{
    public $numCols = 1;
    public $elements = array();
    public function __construct($array)
    {
        $this->key = getUniqueId();
        $keys = array_keys($array);
        foreach($keys as $key => $tst) {
            $this->{$tst} = $array[$tst];
        }
    }
}

class Layout extends FormLayout
{
    public $rows = 1;
    public $maxRows = 1;
    public $previousCol = 0;
    public $errors = [];
    public $errorClass = "errorMessage";
    public $key;

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

    function checkMinMax(Input $input){
        $html = '';
        if (!empty($input->min)){
            $html .= ' min="' . $input->min . '"';
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

    
    function setAttribute($attribute, $value){
        $html = '';
        if (isset($value)){
            $html .= ' ' . $attribute . '="' . $value . '"';
        }
        return $html;
    }

    function setCheckAttribute($attribute, $value){
        $html = '';
        if (isset($value) || $value){
            $html .= ' ' . $attribute;
        }
        return $html;
    }


    function textArea(Input $input){

        if (!isset($input->labelClass)){
            $input->labelClass = "formTextAreaLabelAlignment";
        }
        $hasError = errorCheck($input->name, $this->errors);
        $html = '';
        $html .= "<td" . $this->getClasses($input, $hasError) . ">" . $input->label;
        $html .= $hasError ? " *" : "";
        $html .= "</td>" . PHP_EOL;
        $html .= "<td>";
        $html .= '<textarea ' . $this->setAttribute("class", "inputField") . $this->setAttribute("cols", $input->cols) . $this->setAttribute("rows", $input->rows);
        $html .= $this->setAttribute("name", $input->name) . $this->setCheckAttribute("required", $input->required);
        $html .= $this->checkTabIndex() . $this->checkAutofocus($hasError);
        $html .= '>';
        $html .= $input->value;
        $html .= '</textarea>';
        $html .= "</td>" . PHP_EOL;
        if (!empty($error)){
            $html .= '</tr></table></td>';
        }
        $this->addElement($input, $html);
    }

    function hiddenField(Input $input){

        $html = '<input type="hidden" name="' . $input->name . '" value="' . $input->value . '">';
        echo $html . PHP_EOL;
    }

    function inputBox(Input $input){

        $type = empty($input->type) ? 'text' : $input->type;
        $hasError = errorCheck($input->name, $this->errors);
        $html = '';
        $html .= "<td" . $this->getClasses($input, $hasError) . ">" . $input->label;
        $html .= $hasError ? " *" : "";
        $html .= "</td>" . PHP_EOL;
        $html .= "<td>";
        $html .= '<input class="inputField" size="' . $input->size .  '"' . $this->checkMinMax($input) . ' type="' . $type . '"';
        $html.= ' name="' . $input->name . '" value="' . $input->value . '"';
        $html .= $this->checkTabIndex() . $this->checkAutofocus($hasError) . $this->setCheckAttribute("disabled", $input->disabled);
        $html .= '>';
        $html .= "</td>" . PHP_EOL;
        if (!empty($error)){
            $html .= '</tr></table></td>';
        }
        $this->addElement($input, $html);
    }

    function checkBox(Input $input){
        $html = "<tr " . $this->setAttribute("class", "spaceUnder") . ">" . PHP_EOL;
        $html .= "<td" . $this->setColSpan($input->colspan) . '>' . $input->label;
        if ($input->colspan == 1){
            $html .= '</td><td>';
        }
        $html .= '<input class="inputField" type="checkbox" name="' . $input->name . '"';
        $html .= ' value="' . $input->value . '"';
        $html .= ($input->value ? " checked" : "");
        $html .=  $this->checkTabIndex() .'>';
        $html .= "</td>";
        $html .= PHP_EOL;
        $this->addElement($input, $html);
    }

    function setColSpan($span){
        if (!empty($span)){
            return " colspan='" . $span . "'";
        }
        return '';
    }

    /* $id    = fieldname that contains the id
       $value = fieldname that contains the value that will be displayed on the screen
       $input = a Class Object that contains the HTML Settings, like name of the select, the default value
    */
    function comboBox($array, $id, $value, Input $input){
        $html = "<tr" . $this->setAttribute("class", "spaceUnder") . ">" . PHP_EOL;
        $html .= "<td>" . $input->label . "</td>" . PHP_EOL;
        $html .= "<td>";
        //$html .= '<select class="inputField" name="' . $input->name . '"' .  $this->checkTabIndex() . '>';
        $html .= '<select';
        $html .=  $this->setAttribute("class", "inputField");
        $html .=  $this->setAttribute("name", $input->name);
        $html .=  $this->setAttribute("id", $input->fieldId);
        $html .= $this->checkTabIndex();
        $html .= '>';

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
        //$html .= "</tr>" . PHP_EOL;;
        $this->addElement($input, $html);
    }

    function label(Input $input){
        $html = '<td' . ($input->colspan > 1 ? ' colspan="' . $input->colspan . '"' : '') . '>' . $input->label . "</td>" . PHP_EOL;
        $this->addElement($input, $html);
    }

    function button(Input $input){
        $html = '<td class="buttonCell"' . ($input->colspan > 1 ? ' colspan="' . $input->colspan . '"' : '') . '>';
        $html .= '<button ' . 'class="formButton"' . ' name="' . $input->name . '"';
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
            unset($_SESSION["errors"]);
        }
    }

    public function printInfo()
    {
        if (!empty($_SESSION["info"])) {
            echo '<div class="infoMessage">';
            echo '<h3>Info</h3>' . PHP_EOL;
            echo '<ul>' . PHP_EOL;
            foreach ($_SESSION["info"] as $key => $value) {
                echo "<li>" . $key . ": " . $value . "</li>" . PHP_EOL;
            }
            echo "</ul>" . PHP_EOL;
            echo '</div>' . PHP_EOL;
            echo '<br>'. PHP_EOL;;
            unset($_SESSION["info"]);
        }
    }

    public function close()
    {
        echo '<input type="hidden" name="formKey" value="' . $this->key . '">';
        $this->printInfo();
        $this->printErrors();
        //echo var_dump($this->elements);
        echo "<table>" . PHP_EOL;
        for ($x = 1; $x <= $this->rows; $x++) {
            echo "<tr class='spaceUnder'>" . PHP_EOL ;
            for ($y = 1; $y <= $this->numCols; $y++) {
                if (isset($this->elements[$x][$y])) {
                    echo $this->elements[$x][$y];
                }
            }
            echo "</tr>" . PHP_EOL;
        }
        echo "</table>" . PHP_EOL;
    }

    public function displayTable($array, Input $input){
        // start table
        if (count($array) > 0) {
            $class = "displayTable";
            $html = addClassToElement("table", array($class, 'displayTable-zebra', 'displayTable-horizontal', 'centerTable'));
            // header row
            $html .= '<caption class="displayTable">' . $input->title . '</caption>'. PHP_EOL;;
            $html .= "<thead>";
            $html .= '<tr>';
            $html .= '<th>' . '&nbsp;</th>';
            $html .= '<th>' . '&nbsp;</th>';
            foreach ($array[0] as $key => $value) {
                $html .= '<th>' . ucwords($key) . '</th>' . PHP_EOL;
            }
            $html .= '</tr>' . PHP_EOL;
            $html .= "</thead>" . PHP_EOL;;

            // data rows
            $html .= "<tbody>";
            foreach ($array as $key => $value) {
                $html .= '<tr>';
                if (!empty($input->update)) {
                    $html .= '<td>' . '&nbsp;<a href="' . $input->update . 'mode=U&id=' . $value->{$input->id} . '"><img src="/catalog/apps/images/edit.gif" border="0" alt="Modify"></a></td>';
                }
                if (!empty($input->delete)) {
                    $html .= '<td>' . '&nbsp;<a class="confirmLink" href="' . $input->delete . 'mode=D&id=' . $value->{$input->id} . '"><img src="/catalog/apps/images/delete.gif" border="0" alt="Delete"></a></td>';
                }
                foreach ($value as $key2 => $value2) {
                    $html .= '<td>' . $value2 . '</td>';
                }
                $html .= '</tr>' . PHP_EOL;
            }
            $html .= "</tbody>" . PHP_EOL;

            // finish table and return it
            $html .= '</table>'. PHP_EOL;
            $this->addElement($input, $html);
        }

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

function addInfo($field, $message){
    if (!isset($_SESSION["info"])){
        $info = array();
    }
    else {
        $info = $_SESSION["info"];
    }
    $info[$field] = $message;
    $_SESSION["info"] = $info;
}

function addClassToElement($element, $class)
{
    $html = '<' . $element . ' class="' ;
    if (is_array($class)) {
        $html .= implode(' ', $class);
    } else {
        $html .= $class;
    }
    $html .= '"' . '>';
    return $html;
}

function checkSave($save, $key, $obj, $file, $returnObj = null)
{
    if ($save) {
        echo '<h1>Album Configuration Saved Status</h1>' . PHP_EOL;
        writeJSON($obj, $file);
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


function checkSave2($save, $key, $obj, $file, $forward, $returnObj = null)
{
    if ($save) {
        writeJSON($obj, $file);
        if (isset($_POST['formKey'])) {
            $formKey = $_POST['formKey'];
        }
        else {
            $formKey = '';
        }
        addInfo($key, "Contents saved to ' . $file");
        header("Location: " . $forward);
    }
    else {
        if (isset($returnObj)){
            $_SESSION[$key] = $returnObj;
        }
        else {
            $_SESSION[$key] = $obj;
        }
        header("Location: " . $_SESSION["previous_location"]);
    }
    exit();
}

function checkForErrors($save, $key, $obj){
    if (!$save) {
        $_SESSION[$key] = $obj;
    }
}

function radioButton($value,$default){
    if ($value == $default){
        return "checked";
    }
    return "";

}

function isSetFilterRule($filterRules = null){
    $isSet = false;
    if (!isset($filterRules) && isset($_POST["filterRules"])) {
        $filterRules = json_decode($_POST["filterRules"]);
    }
    if (isset($filterRules)){
        if(count($filterRules) > 0){
            $isSet = true;
        }
    }
    return $isSet;
}

?>
