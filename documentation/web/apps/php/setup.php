<?php
include("config.php");
$oneDrive = getOneDrivePath();
$setupObj = readJSON($oneDrive . '/Config/Setup.json');


getFullPath($setupObj, "webMusicSongs");

function getFullPath($setupObj, $pathId){

$parent = true;
$path = "";
$id = $pathId;
do {
	if (isset($setupObj->{$id}->path)){
		$path = $setupObj->{$id}->path . $path;
		if (empty($setupObj->{$id}->parent)){
			println("parent is empty");
			$parent = false;
			break;
		}
		else {
			$id = $setupObj->{$id}->parent;
			println("parent found: " . $id);
		}
	}
	else {
		throw new Exception("Path With Id Not Found: " . $id);
	}
}
while ($parent);
echo $path;

}

?>