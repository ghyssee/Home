 <html>
<body>

<?php
include("../config.php");
include("../html/config.php");
$mp3SettingsObj = readJSON($oneDrivePath . '/Config/Java/MP3Settings.json');
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');

$oneDrive = getOneDrivePath();
println($oneDrive);
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

<form action="albumSave.php" method="post">
<?php
	goMenu();
?>
<h1>Album Configuration</h1>
<div class="horizontalLine">.</div>
<table style="width:60%" class="inlineTable">
<tr><td>Album</td><td><input size="100" type="text" name="album" value="<?php print $mp3SettingsObj->album; ?>"></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Settings" value="saveAlbum">Save</button></td></tr>
</table>
<div class="emptySpace"></div>
</form>

<form action="albumSave.php" method="post">
<h1>Mezzmo Configuration</h1>
<div class="horizontalLine">.</div>
<table style="width:60%" class="inlineTable">
<tr><td>Base</td><td><input size="100" type="text" name="mezzmoBase" value="<?php print $mp3SettingsObj->mezzmo->base; ?>"></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Settings" value="saveMezzmo">Save</button></td></tr>
</table>
<div class="emptySpace"></div>
</form>

<form action="albumSave.php" method="post">
<h1>iPod Configuration</h1>
<div class="horizontalLine">.</div>

<table style="width:60%" class="inlineTable">
<tr><td>Update Rating<input type="checkbox" name="updateRating" 
                            value="<?php print $mp3SettingsObj->synchronizer->updateRating; ?>" <?php setCheckbox($mp3SettingsObj->synchronizer->updateRating) ?>>
</td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Settings" value="saveiPod">Save</button></td></tr>
</table>
</form>
<div class="emptySpace"></div>

<form action="albumSave.php" method="post">
<h1>MediaMonkey Configuration</h1>
<div class="horizontalLine">.</div>

<table style="width:60%" class="inlineTable">
<tr><td>Base</td><td><input size="100" type="text" name="mediaMonkeyBase" value="<?php print $mp3SettingsObj->mediaMonkey->base; ?>"></td></tr>
<tr><td>Playlist Path</td><td><input size="100" type="text" name="mediaMonkeyPlaylistPath" value="<?php print $mp3SettingsObj->mediaMonkey->playlist->path; ?>"></td></tr>
<tr><td>Top 20 Name</td><td><input size="100" type="text" name="mediaMonkeyTop20" value="<?php print $mp3SettingsObj->mediaMonkey->playlist->top20; ?>"></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Settings" value="saveMM">Save</button></td></tr>
</table>
</form>
<div class="emptySpace"></div>

<form action="albumSave.php" method="post">
<h1>MP3Preprocessor Configuration</h1>
<div class="horizontalLine">.</div>

<table style="width:60%" class="inlineTable">
<tr><td>AlbumTag</td><td><input size="50" type="text" name="albumTag" value="<?php print $mp3PreprocessorObj->albumTag; ?>"></td></tr>
<tr><td>CdTag</td><td><input size="50" type="text" name="cdTag" value="<?php print $mp3PreprocessorObj->cdTag; ?>"></td></tr>
<tr><td>Prefix</td><td><input size="50" type="text" name="prefix" value="<?php print $mp3PreprocessorObj->prefix; ?>"></td></tr>
<tr><td>Suffix</td><td><input size="50" type="text" name="suffix" value="<?php print $mp3PreprocessorObj->suffix; ?>"></td></tr>
<tr><td>Active Configuration</td><td><?php generateSelectFromArray($mp3PreprocessorObj->configurations, $mp3PreprocessorObj->activeConfiguration); ?></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Preprocessor" value="save">Save</button></td></tr>
</table>
<div class="emptySpace"></div>
<br><br>
</form>
<div class="emptySpace"></div>

<form action="albumSave.php" method="post">
<h1>LastPlayed Configuration</h1>
<div class="horizontalLine">.</div>

<table style="width:60%" class="inlineTable">
<tr><td>Number</td><td><input size="50" type="text" name="number" value="<?php print $mp3SettingsObj->lastPlayedSong->number; ?>"></td></tr>
<tr><td>Scroll Color</td><td><?php
		generateSelectFromArray2($htmlObj->colors, "scrollColor", $mp3SettingsObj->lastPlayedSong->scrollColor);
		//comboBox($htmlObj->colors, new Input(array('name'=>"scrollBackgroundColor",
		//	                                       'value'=>$mp3SettingsObj->lastPlayedSong->scrollBackgroundColor)));
		?>
	</td></tr>
<tr><td>Scroll Background Color</td><td><?php generateSelectFromArray2($htmlObj->colors, "scrollBackgroundColor",
			$mp3SettingsObj->lastPlayedSong->scrollBackgroundColor); ?></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Settings" value="saveLastPlayed">Save</button></td></tr>
</table>
<div class="emptySpace"></div>
<br><br>
</form>
<div class="emptySpace"></div>

<form action="albumSave.php" method="post">
<h1>MP3Prettifier Configuration</h1>
<hr>
<h3>General</h3>
<table style="width:60%" class="inlineTable">
<tr><td class="descriptionColumn">Old Word</td><td><input size="50" type="text" name="oldWord" value=""></td></tr>
<tr><td style="width:10%">New Word</td><td><input size="50" type="text" name="newWord" value=""></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Prettifier" value="SaveGlobalWord">Save Word</button></td></tr>
</table>
<div class="emptySpace"></div>

<h3>Artist Word</h3>
<table style="width:60%" class="inlineTable">
<tr><td class="descriptionColumn">Old Word</td><td><input size="50" type="text" name="artistOldWord" value=""></td></tr>
<tr><td class="descriptionColumn">New Word</td><td><input size="50" type="text" name="artistNewWord" value=""></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Prettifier" value="saveArtistWord">Save Word</button></td></tr>
</table>
<div class="emptySpace"></div>

<h3>Artist Name</h3>
<table style="width:60%" class="inlineTable">
<tr><td class="descriptionColumn">Old Artist Name</td><td><input size="50" type="text" name="artistNameOldWord" value=""></td></tr>
<tr><td class="descriptionColumn">New Aritst Name</td><td><input size="50" type="text" name="artistNameNewWord" value=""></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Prettifier" value="saveArtistName">Save Name</button></td></tr>
</table>
<div class="emptySpace"></div>

<h3>Song Title</h3>
<table style="width:60%" class="inlineTable">
<tr><td class="descriptionColumn">Old Song Title</td><td><input size="50" type="text" name="songTitleOldWord" value=""></td></tr>
<tr><td class="descriptionColumn">New Song Title</td><td><input size="50" type="text" name="songTitleNewWord" value=""></td></tr>
</table>
<table style="width:30%" class="inlineTable">
<tr><td><button name="mp3Prettifier" value="saveSongTitle">Save Title</button></td></tr>
</table>
<div class="emptySpace"></div>

<br><br>

<?php
	goMenu();
?>

</form>
</body>
</html> 

<?php 
 foreach($mp3PreprocessorObj->configurations as $key => $configuration) {
	/*println($configuration->id);
	println("Count: " . count($configuration->config));
	*/
    /*println("<option value='1000'>1000</option>");*/
	
	foreach($configuration->config as $key2 => $config) {
	   if (isset($config->splitter)){
		//println($config->splitter);
	   }
   }
 }
 
 function generateSelectFromArray($array, $default){
  echo "<select name=\"activeConfiguration\">";
  
  foreach($array as $key => $value) {
    
		$selected = "";
		if ($value->id == $default){
			$selected = " selected";
		}
		echo "<option value=\"$value->id\"" . $selected . ">" . getConfigurationText($value->config) . "</option>";
   }
    echo "</select>";
}

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

function getConfigurationText($array){

	$desc = "";
	foreach($array as $key2 => $config) {
	   if (isset($config->type)){
		$desc = $desc . (empty($desc) ? '' : ' ') . $config->type;
	   }
	   if (isset($config->splitter)){
		$desc = $desc . " " . $config->splitter;
	   }
	   if (!empty($config->duration)){
		   $desc = $desc . " (duration TRUE)";
	   }
   }
   return $desc;
}
 
?>
