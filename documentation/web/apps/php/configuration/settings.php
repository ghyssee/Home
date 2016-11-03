<html>
<body>

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$mp3SettingsObj = readJSON($oneDrivePath . '/Config/Java/MP3Settings.json');
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
?>

<style>
    .buttonDiv {
        height: 80px;
        display: flex;
        align-items: center;
        text-align: right;
    }

    .inlineTable {
        float: left;
    }

    .emptySpace {
        height: 80px
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
        text-align: left;
    }

    .descriptionColumn {
        width: 20%;
    }

    .errorMessage {
        color: red;
        font-weight: bolder;
        font-size: 20px;

    }
</style>

<?php
goMenu();
if (isset($_SESSION["color"])) {
    $color = $_SESSION["color"];
} else {
    $color = new Color();
}
?>
<h1>Settings</h1>
<div class="horizontalLine">.</div>
<form action="settingsSave.php" method="post">
    <table>
        <?php errorCheck("description"); ?>
        <tr>
            <td>Omschrijving</td>
            <td>
                <?php inputBox(new Input(array('name' => "colorDescription",
                    'size' => 50,
                    "value" => $color->description)));
                ?>
            </td>
        </tr>
        <?php errorCheck("code"); ?>
        <tr>
            <td>Kleur Code</td>
            <td>
                <?php inputBox(new Input(array('name' => "colorCode",
                    'size' => 30,
                    '"value' => $color->code)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "htmlSettings",
            'value' => 'addColor',
            'text' => 'add')));
        ?>
    </div>
</form>

<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["color"]);
?>
</body>
</html> 
