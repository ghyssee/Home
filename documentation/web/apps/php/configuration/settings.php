 <html>
<body>

<?php
include("../config.php");
$mp3SettingsObj = readJSON($oneDrivePath . '/Config/Java/MP3Settings.json');
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
?>

<style>
.inlineTable {
   float: left;
}
.emptySpace {
	height:80px
}
.horizontalLine {
    width: 95%
    font-size: 1px;
    color: rgba(0, 0, 0, 0);
    line-height: 1px;

    background-color: grey;
    margin-top: -6px;
    margin-bottom: 10px;
}
th {
	text-align:left;
}
.descriptionColumn {
   width:20%;
}

</style>

<?php
	goMenu();
?>
<h1>Settings</h1>
<div class="horizontalLine">.</div>
<form action="settingsSave.php" method="post">
<table style="width:60%" class="inlineTable">
<tr><td>Omschrijving</td><td><input size="50" type="text" name="colorDescription" value=""></td></tr>
<tr><td>Kleur Code</td><td><input size="30" type="text" name="colorCode" value=""></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="htmlSettings" value="addColor">Add</button></td></tr>
</table>
<div class="emptySpace"></div>
</form>

<div class="emptySpace"></div>
<br><br>
<?php
	goMenu();
?>
</form>
</body>
</html> 

<?php 
 
 function generateSelectFromArray2($array, $field, $default){
  echo '<select name="' . $field . '">';
  
  foreach($array as $key => $value) {
    
		$selected = "";
		if ($value->code == $default){
			$selected = " selected";
		}
		echo "<option value=\"$value->code\"" . $selected . ">" . $value->description . "</option>";
   }
    echo "</select>";
}

?>
