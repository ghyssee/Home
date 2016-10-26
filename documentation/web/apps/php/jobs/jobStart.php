<html>
<body>

<?php
include("../config.php");
$jobsObj = initSave($oneDrivePath . '/Config/Java/Jobs.json');
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
.errorMessage {
   color: red;
   font-weight: bolder;
   font-size: 20px;
   
}

</style>

<h1>Start Job</h1>
<div class="horizontalLine">.</div>

<?php
 foreach($jobsObj->list as $key => $groups) {
    if(isset($_POST[$groups->id])){
		println("Group Found: " . $groups->id);
		$button = $_POST[$groups->id];
		processJob($jobsObj, $oneDrivePath, $groups->jobs, $button);
		break;
	}
 }
	if (isset($_POST["DELETE"])){
		$file = $_POST["DELETE"];
		deleteFile($file);
	}
 
 function deleteFile($file){
	$tmpFile = "c:/My Data/tmp/java/" . $file;
	if (file_exists($tmpFile)){
		println("Deleting file " . $tmpFile);
		unlink ($tmpFile);
	}
	else {
		println("Job Not Running: " . $file);
	}
 }
 
 function processJob($jobsObj, $oneDrivePath, $group, $id){
	$job = findJob ($group, $id);
	if (!empty($job)){
		println("Starting job " . $job->description);
		$cmd = $job->cmd;
		if ($job->type == "MYUTILS"){
			$cmd = $oneDrivePath . $jobsObj->myUtils->path . $cmd;
		}
		if ($job->type == "SERVICE"){
			$cmd = $oneDrivePath . "/scripts/desktopi5/lnk/" . $cmd . ".lnk";
		}
		println("Cmd: " . $cmd);
		execInBackground2($cmd);
		println("Job started in background");
	}
	else {
		printErrorMessage("Job Not Found: " . $id, "errorMessage");
	}
 }
 
function findJob ($array, $jobId){
   foreach($array as $key => $job) {
		if ($job->id == $jobId) {
			return $job;
		}
   }	
}

?>
<br>
<script>
    document.write('<a href="' + document.referrer + '">Go Back</a>');
</script>

</body>

</html>