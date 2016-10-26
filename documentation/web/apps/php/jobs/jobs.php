<html>
<body>

<?php
include("../config.php");
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
	printh( "<h3>" . $groups->title . "</h3>");
	printh("<table>");
	foreach($groups->jobs as $key => $job) {
		printh("<tr>");
		printh('<td class="columnDescription">' . $job->description . "</td>");
		printh('<td><button name="' . $groups->id . '" value="' . $job->id . '">Start</button></td>');
		if (isset($job->stop)){
		    printh('<td><button name="' . "DELETE" . '" value="' . $job->stop . '">Stop</button></td>');
		}
		printh("</tr>");
    }
	printh("</table>");
 }
?>
<br>
<?php
	goMenu();
?>
</body>
</form>

</html>