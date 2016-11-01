 <html>
<body>

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');
session_start(); 
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
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
.errorMessage {
   color: red;
   font-weight: bolder;
   font-size: 17px;
   
}
</style>


<?php
	goMenu();
	$HTML_SETTINGS = 'mp3Preprocessor';
	if (isset($_SESSION["splitter"])){
		$splitter = $_SESSION["splitter"];
	}
	else {
		$splitter = new Splitter();
	}
?>
<h1>MP3Preprocessor Settings</h1>
<h3>Splitters</h3>
<div class="horizontalLine">.</div>
<form action="mp3preprocessorSave.php" method="post">
<table style="width:60%" class="inlineTable">
<?php errorCheck("id"); ?>
<tr><td>Id</td><td><?php inputBox("splitterId", $splitter->id, 30); ?></td></tr>
<?php errorCheck("pattern"); ?>
<tr><td>Splitter</td><td><?php inputBox("pattern", $splitter->pattern, 30); ?></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Preprocessor" value="addSplitter">Add</button></td></tr>
</table>
<div class="emptySpace"></div>
</form>
<div class="emptySpace"></div>
<h3>MP3Preprocessor Configuration</h3>
<div class="horizontalLine">.</div>
<form action="mp3preprocessorSave.php" method="post">
<table style="width:60%" class="inlineTable">
<tr><td>Type</td><td><?php generateSelectFromArray2($mp3PreprocessorObj->fields, "configType", "id", "description"); ?></td></tr>
<tr><td>Splitter</td><td><input size="30" type="text" name="configSplitter" value=""></td></tr>
<tr><td>Duration</td><td><input size="30" type="text" name="configDuration" value=""></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Preprocessor" value="addConfig">Add</button></td></tr>
</table>
<div class="emptySpace"></div>
</form>

<div class="emptySpace"></div>
<br><br>
<?php
	goMenu();
	unset($_SESSION["errors"]);
	unset($_SESSION["splitter"]);
?>
</form>
</body>
</html> 

<?php 
 
 function generateSelectFromArray2($array, $field, $id, $value, $default = ""){
  echo '<select name="' . $field . '">';
  
  foreach($array as $key => $item) {
    
		$selected = "";
		if ($item->{$id} == $default){
			$selected = " selected";
		}
		echo '<option value="' . $item->{$id} . '"' . $selected . ">" . $item->{$value} . "</option>";
   }
    echo "</select>";
}

?>
