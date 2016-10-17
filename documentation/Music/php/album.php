 <html>
<body>

<?php
include("config.php");
println ($json->album);
println($json->synchronizer->startDirectory);
println($json->rating->rating1);

$keyConst = 'HKEY_CURRENT_USER';

$oneDrive = getOneDrivePath();
println($oneDrive);
?>

<form action="albumSave.php" method="post">
Album: <input type="text" name="album" value="<?php print $json->album; ?>"><br>
E-mail: <input type="text" name="email"><br>
<input type="submit">
</form>

</body>
</html> 