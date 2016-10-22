<html>
<body>

<?php
include("config.php");
$jobsObj = readJSON($oneDrivePath . '/Config/Java/Jobs.json');
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
   font-size: 20px;
   
}

</style>

<h1>Start Job</h1>
<div class="horizontalLine">.</div>

<?php 
if(isset($_POST['start'])){
	$button = $_POST['start'];
	$job = findJob ($jobsObj->list, $button);
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
		printErrorMessage("Job Not Found: " . $button, "errorMessage");
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
</body>

</html>