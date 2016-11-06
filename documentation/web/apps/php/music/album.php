<html>
<body>

<?php
include("../config.php");
include("../html/config.php");
$mp3SettingsObj = readJSON($oneDrivePath . '/Config/Java/MP3Settings.json');
$mp3PreprocessorObj = readJSON($oneDrivePath . '/Config/Java/MP3Preprocessor.json');
$htmlObj = readJSON($oneDrivePath . '/Config/Java/HTML.json');
$oneDrive = getOneDrivePath();
session_start();
$_SESSION['previous_location'] = basename($_SERVER['PHP_SELF']);
?>

<style>
    .inlineTable {
        float: left;
    }

    .emptySpace {
        height: 80px;
    }

    .buttonDiv {
        height: 80px;
        display: flex;
        align-items: center;
        text-align: right;
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

</style>

<form action="albumSave.php" method="post">
    <?php
    goMenu();
    ?>
    <h1>Album Configuration</h1>
    <div class="horizontalLine">.</div>
    <table>
        <tr>
            <td>Album</td>
            <td>
                <?php inputBox(new Input(array('name' => "album",
                    'size' => 100,
                    'value' => $mp3SettingsObj->album)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Settings",
            'value' => 'saveAlbum',
            'text' => 'Save')));
        ?>
    </div>
</form>

<form action="albumSave.php" method="post">
    <h1>Mezzmo Configuration</h1>
    <div class="horizontalLine">.</div>
    <table>
        <tr>
            <td>Base</td>
            <td>
                <?php inputBox(new Input(array('name' => "mezzmoBase",
                    'size' => 100,
                    'value' => $mp3SettingsObj->mezzmo->base)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Settings",
            'value' => 'saveMezzmo',
            'text' => 'Save')));
        ?>
    </div>
</form>

<form action="albumSave.php" method="post">
    <h1>iPod Configuration</h1>
    <div class="horizontalLine">.</div>

    <table>
        <tr>
            <td>Update Rating
                <?php checkBox(new Input(array('name' => "updateRating",
                    'value' => $mp3SettingsObj->synchronizer->updateRating)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Settings",
            'value' => 'saveiPod',
            'text' => 'Save')));
        ?>
    </div>
</form>

<form action="albumSave.php" method="post">
    <h1>MediaMonkey Configuration</h1>
    <div class="horizontalLine">.</div>

    <table>
        <tr>
            <td>Base</td>
            <td>
                <?php inputBox(new Input(array('name' => "mediaMonkeyBase",
                    'size' => 100,
                    'value' => $mp3SettingsObj->mediaMonkey->base)));
                ?>
            </td>
        </tr>
        <tr>
            <td>Playlist Path</td>
            <td>
                <?php inputBox(new Input(array('name' => "mediaMonkeyPlaylistPath",
                    'size' => 100,
                    'value' => $mp3SettingsObj->mediaMonkey->playlist->path)));
                ?>
            </td>
        </tr>
        <tr>
            <td>Top 20 Name</td>
            <td>
                <?php inputBox(new Input(array('name' => "mediaMonkeyTop20",
                    'size' => 100,
                    'value' => $mp3SettingsObj->mediaMonkey->playlist->top20)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Settings",
            'value' => 'saveMM',
            'text' => 'Save')));
        ?>
    </div>
</form>

<form action="albumSave.php" method="post">
    <h1>MP3Preprocessor Configuration</h1>
    <div class="horizontalLine">.</div>

    <table>
        <tr>
            <td>AlbumTag</td>
            <td>
                <?php inputBox(new Input(array('name' => "albumTag",
                    'size' => 50,
                    'value' => $mp3PreprocessorObj->albumTag)));
                ?>
            </td>
        </tr>
        <tr>
            <td>CdTag</td>
            <td>
                <?php inputBox(new Input(array('name' => "cdTag",
                    'size' => 50,
                    'value' => $mp3PreprocessorObj->cdTag)));
                ?>
            </td>
        </tr>
        <tr>
            <td>Prefix</td>
            <td>
                <?php inputBox(new Input(array('name' => "prefix",
                    'size' => 50,
                    'value' => $mp3PreprocessorObj->prefix)));
                ?>
            </td>
        </tr>
        <tr>
            <td>Suffix</td>
            <td>
                <?php inputBox(new Input(array('name' => "suffix",
                    'size' => 50,
                    'value' => $mp3PreprocessorObj->suffix)));
                ?>
            </td>
        </tr>
        <tr>
            <td>Active Configuration</td>
            <td>
                <?php
                //generateSelectFromArray($mp3PreprocessorObj->configurations, $mp3PreprocessorObj->activeConfiguration);

                comboBox($mp3PreprocessorObj->configurations, "id", "id",
                    new Input(array('name' => "activeConfiguration",
                        'method' => 'getConfigurationText',
                        'methodArg' => 'config',
                        'default' => $mp3PreprocessorObj->activeConfiguration)));

                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Preprocessor",
            'value' => 'save',
            'text' => 'Save')));
        ?>
    </div>
</form>

<form action="albumSave.php" method="post">
    <h1>LastPlayed Configuration</h1>
    <div class="horizontalLine">.</div>

    <table style="width:60%" class="inlineTable">
        <tr>
            <td>Number</td>
            <td>
                <?php inputBox(new Input(array('name' => "number",
                    'size' => 5,
                    'value' => $mp3SettingsObj->lastPlayedSong->number)));
                ?>
            </td>
        </tr>
        <tr>
            <td>Scroll Color</td>
            <td>
                <?php
                comboBox($htmlObj->colors, "code", "description",
                    new Input(array('name' => "scrollColor",
                        'default' => $mp3SettingsObj->lastPlayedSong->scrollColor)));
                ?>
            </td>
        </tr>
        <tr>
            <td>Scroll Background Color</td>
            <td>
                <?php
                comboBox($htmlObj->colors, "code", "description",
                    new Input(array('name' => "scrollBackgroundColor",
                        'default' => $mp3SettingsObj->lastPlayedSong->scrollBackgroundColor)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Settings",
            'value' => 'saveLastPlayed',
            'text' => 'Save')));
        ?>
    </div>
</form>

<form action="albumSave.php" method="post">
    <h1>MP3Prettifier Configuration</h1>
    <hr>
    <h3>General</h3>
    <table>
        <tr>
            <td class="descriptionColumn">Old Word</td>
            <td>
                <?php inputBox(new Input(array('name' => "oldWord",
                    'size' => 50)));
                ?>
        <tr>
            <td style="width:10%">New Word</td>
            <td>
                <?php inputBox(new Input(array('name' => "newWord",
                    'size' => 50)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Prettifier",
            'value' => 'SaveGlobalWord',
            'text' => 'Save Word')));
        ?>
    </div>

    <h3>Artist Word</h3>

    <table>
        <tr>
            <td class="descriptionColumn">Old Word</td>
            <td>
                <?php inputBox(new Input(array('name' => "artistOldWord",
                    'size' => 50)));
                ?>
        <tr>
            <td style="width:10%">New Word</td>
            <td>
                <?php inputBox(new Input(array('name' => "artistNewWord",
                    'size' => 50)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Prettifier",
            'value' => 'saveArtistWord',
            'text' => 'Save Word')));
        ?>
    </div>

    <h3>Artist Name</h3>

    <table>
        <tr>
            <td class="descriptionColumn">Old Artist Name</td>
            <td>
                <?php inputBox(new Input(array('name' => "artistNameOldWord",
                    'size' => 50)));
                ?>
        <tr>
            <td style="width:10%">New Aritst Name</td>
            <td>
                <?php inputBox(new Input(array('name' => "artistNameNewWord",
                    'size' => 50)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Prettifier",
            'value' => 'saveArtistName',
            'text' => 'Save Name')));
        ?>
    </div>

    <h3>Song Title</h3>

    <table>
        <tr>
            <td class="descriptionColumn">Old Song Title</td>
            <td>
                <?php inputBox(new Input(array('name' => "songTitleOldWord",
                    'size' => 50)));
                ?>
        <tr>
            <td style="width:10%">New Song Title</td>
            <td>
                <?php inputBox(new Input(array('name' => "songTitleNewWord",
                    'size' => 50)));
                ?>
            </td>
        </tr>
    </table>
    <div class="buttonDiv">
        <?php button(new Input(array('name' => "mp3Prettifier",
            'value' => 'saveSongTitle',
            'text' => 'Save Title')));
        ?>
    </div>

    <?php
    goMenu();
    ?>

</form>
</body>
</html>

<?php

function generateSelectFromArray($array, $default)
{
    echo "<select name=\"activeConfiguration\">";

    foreach ($array as $key => $value) {

        $selected = "";
        if ($value->id == $default) {
            $selected = " selected";
        }
        echo "<option value=\"$value->id\"" . $selected . ">" . getConfigurationText($value->config) . "</option>";
    }
    echo "</select>";
}

function getConfigurationText($array)
{
    $desc = "";
    foreach ($array as $key => $config) {
        if (isset($config->type)) {
            $desc = $desc . (empty($desc) ? '' : ' ') . $config->type;
        }
        if (isset($config->splitter)) {
            $desc = $desc . " " . $config->splitter;
        }
        if (!empty($config->duration)) {
            $desc = $desc . " (duration TRUE)";
        }
    }
    return $desc;
}

?>
