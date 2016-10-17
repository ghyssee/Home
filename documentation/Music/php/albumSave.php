<html>
<body>
<?php
include("config.php");
?>
Welcome <?php echo $_POST["album"]; ?><br>
Your email address is: <?php echo $_POST["email"]; ?><br>

<?php
$json->album = $_POST["album"];
if (empty($json->album)) {
    println ('Album is either empty, or not set at all');
}
else {
	$file = $mp3Settings;
	file_put_contents($file, json_encode($json, JSON_PRETTY_PRINT + JSON_UNESCAPED_SLASHES));
    println ('Contents saved to ' . $file);
}
?>
<a href="album.php">Config Album</a>
</body>
</html> 