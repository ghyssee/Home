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
.columnDescription {
   width:80%;
}
</style>

<form action="jobStart.php" method="post">
<h1>List Of Jobs</h1>
<div class="horizontalLine">.</div>

<?php 
 foreach($jobsObj->list as $key => $groups) {
	echo "<h3>" . $groups->title . "</h3>";
	echo "<table>";
	foreach($groups->jobs as $key => $job) {
		echo "<tr>";
		echo '<td class="columnDescription">' . $job->description . "</td>";
			echo "\n";
		echo '<td><button name="' . $groups->id . '" value="' . $job->id . '">Start</button></td>';
			echo "\n";
		if (isset($job->stop)){
		    echo '<td><button name="' . "DELETE" . '" value="' . $job->stop . '">Stop</button></td>';
			echo "\n";
		}
		echo "</tr>";
    }
	echo "</table>\n";
 }
?>
</body>
</form>

</html>