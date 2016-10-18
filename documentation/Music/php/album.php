 <html>
<body>

<?php
include("config.php");
$mp3SettingsObj = readJSON($oneDrivePath . '/Config/Java/MP3Settings.json');
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');

$oneDrive = getOneDrivePath();
println($oneDrive);
?>

<style>
.inlineTable {
   float: left;
}
.emptySpace {
	height:100px
}
</style>

<form action="albumSave.php" method="post">
<h1>Album Configuration</h1>
<hr>
<table style="width:60%" class="inlineTable">
<tr><td>Album</td><td><input size="100" type="text" name="album" value="<?php print $mp3SettingsObj->album; ?>"></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><input type="submit" name="mp3Settings" value="save"></td></tr>
</table>
</form>
<div class="emptySpace"></div>

<form action="albumSave.php" method="post">
<h1>MP3Preprocessor Configuration</h1>
<hr>
<table style="width:60%" class="inlineTable">
<tr><td>AlbumTag</td><td><input size="50" type="text" name="albumTag" value="<?php print $mp3PreprocessorObj->albumTag; ?>"></td></tr>
<tr><td>CdTag</td><td><input size="50" type="text" name="cdTag" value="<?php print $mp3PreprocessorObj->cdTag; ?>"></td></tr>
<tr><td>Prefix</td><td><input size="50" type="text" name="prefix" value="<?php print $mp3PreprocessorObj->prefix; ?>"></td></tr>
<tr><td>Suffix</td><td><input size="50" type="text" name="suffix" value="<?php print $mp3PreprocessorObj->suffix; ?>"></td></tr>
<tr><td>Active Configuration</td><td><?php generateSelectFromArray($mp3PreprocessorObj->configurations, $mp3PreprocessorObj->activeConfiguration); ?></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><input type="submit" name="mp3Preprocessor" value="save"></td></tr>
</table>
<div class="emptySpace"></div>
<br><br>
<?php 
 foreach($mp3PreprocessorObj->configurations as $key => $configuration) {
	/*println($configuration->id);
	println("Count: " . count($configuration->config));
	*/
    /*println("<option value='1000'>1000</option>");*/
	
	foreach($configuration->config as $key2 => $config) {
	   if (isset($config->splitter)){
		/*println($config->splitter);*/
	   }
   }
 }
 
 function generateSelectFromArray($array, $default){
  // echo your opening Select
  echo "<select name=\"activeConfiguration\">";
  // Use simple foreach to generate the options
  
  foreach($array as $key => $value) {
    if ($value->id == $default){
		println( "<option value=\"$value->id selected\">$value->id</option>");
	}
	else {
		println( "<option value=\"$value->id\">$value->id</option>");
	}
   }
   echo "</select>";
}
 
?>
</form>

</body>
</html> 