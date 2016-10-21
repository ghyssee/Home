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

</style>

<form action="jobStart.php" method="post">
<h1>List Of Jobs</h1>
<div class="horizontalLine">.</div>

 <table>
<?php 
 foreach($jobsObj->list as $key => $job) {
	echo "<tr>";
	echo "<td>" . $job->description . "</td>";
	echo '<td><button name="start" value="' . $job->id . '">Start</button></td>';
	echo "</tr>";
 }
?>
</table>
</body>
</form>

</html>