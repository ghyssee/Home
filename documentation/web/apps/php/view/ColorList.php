<html>
<head>
    <link rel="stylesheet" type="text/css" href="../../css/stylesheet.css">
</head>
<body style="background">

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
$_SESSION['form_location'] = basename($_SERVER['PHP_SELF']);
?>
<?php
goMenu();
?>
<h1>Colors</h1>
<div class="horizontalLine">.</div>
<?php
addLink();
echo build_table($htmlObj->colors);
addLink();
goMenu();
?>
<br>
</body>
</html>

<?php
function build_table($array){
    // start table
    $class = "displayTable";
    $html = addClassToElement("table", array($class, 'displayTable-zebra', 'displayTable-horizontal', 'centerTable'));
    // header row
    $html .= '<caption class="displayTable">Colors</caption>'. PHP_EOL;;
    $html .= "<thead>";
    $html .= '<tr>';
    $html .= '<th>' . '&nbsp;</th>';
    $html .= '<th>' . '&nbsp;</th>';
    //$html .= addClassToElement("th", $class) . '&nbsp;</th>';
    foreach($array[0] as $key=>$value){
        $html .= '<th>' . ucwords($key) . '</th>' . PHP_EOL;
    }
    $html .= '</tr>'. PHP_EOL;;
    $html .= "</thead>". PHP_EOL;;

    // data rows
    $html .= "<tbody>";
    foreach( $array as $key=>$value){
            //addClassToElement("tbody", "tabuler_data");
            //"<tbody>";
        $html .=  '<tr>';//addClassToElement("tr", $class);
        $html .= '<td>' . '&nbsp;<a href="/catalog/apps/php/view/Color.php?mode=U&id=' . $value->{'id'} .'"><img src="/catalog/apps/images/edit.gif" border="0" alt="Modify"></a></td>';
        $html .= '<td>' . '&nbsp;<a href="/catalog/apps/php/view/Color.php?mode=D&id=' . $value->{'id'} .'"><img src="/catalog/apps/images/delete.gif" border="0" alt="Delete"></a></td>';
        foreach($value as $key2=>$value2){
            $html .= '<td>' . $value2 . '</td>';
        }
        $html .= '</tr>' . PHP_EOL;
    }
    $html .= "</tbody>" . PHP_EOL;

    // finish table and return it

    $html .= '</table>'. PHP_EOL;
    return $html;
}

function addLink(){
   println('<br>');
   println( '<div class="centered"><a href="Color.php?mode=I">Add Color</a></div>');
   //println('<br>'. PHP_EOL);

}
