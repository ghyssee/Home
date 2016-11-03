<html>
<body>

<?php
include("../config.php");
include("../model/HTML.php");
include("../html/config.php");
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');
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
        font-size: 17px;

    }
</style>


<?php
goMenu();
if (isset($_SESSION["splitter"])) {
    $splitter = $_SESSION["splitter"];
} else {
    $splitter = new Splitter();
}
?>
<h1>MP3Preprocessor Settings</h1>
<h3>Splitters</h3>
<div class="horizontalLine">.</div>
<form action="mp3preprocessorSave.php" method="post">
    <table>
        <?php errorCheck("id"); ?>
        <tr>
            <td>Id</td>
            <td>
                <?php inputBox(new Input(array('name' => "splitterId",
                    'size' => 30,
                    "value" => $splitter->id)));
                ?>
            </td>
        </tr>
        <?php errorCheck("pattern"); ?>
        <tr>
            <td>Splitter</td>
            <td>
                <?php inputBox(new Input(array('name' => "pattern",
                    'size' => 30,
                    "value" => $splitter->pattern)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Preprocessor",
            'value' => 'addSplitter',
            'text' => 'Add')));
        ?>
    </div>
</form>
<h3>MP3Preprocessor Configuration</h3>
<div class="horizontalLine">.</div>
<form action="mp3preprocessorSave.php" method="post">
    <table>
        <tr>
            <td>Type</td>
            <td>
                <?php
                comboBox($mp3PreprocessorObj->fields, "id", "description",
                    new Input(array('name' => "scrollColor",
                        'default' => '')));
                ?>


            </td>
        </tr>
        <tr>
            <td>Splitter</td>
            <td><input size="30" type="text" name="configSplitter" value=""></td>
        </tr>
        <tr>
            <td>Duration</td>
            <td><input size="30" type="text" name="configDuration" value=""></td>
        </tr>
    </table>
    <table style="width:30%" class="inlineTable">
        <tr>
            <td>
                <button name="mp3Preprocessor" value="addConfig">Add</button>
            </td>
        </tr>
    </table>
    <div class="emptySpace"></div>
</form>

<div class="emptySpace"></div>
<br><br>
<?php
goMenu();
unset($_SESSION["errors"]);
unset($_SESSION["splitter"]);
?>
</form>
</body>
</html> 

