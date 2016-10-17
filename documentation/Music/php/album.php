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
<table>
<tr><td>Album</td><td><input size="100" type="text" name="album" value="<?php print $json->album; ?>"></td></tr>
<tr><td>E-mail</td><td><input size="50" type="text" name="email"></td></tr>
</table>
<input type="submit">
</form>

</body>
</html> 