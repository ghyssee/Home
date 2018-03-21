<!-- SmartMenus jQuery plugin -->
<script type="text/javascript" src="/catalog/apps/js/jquery.smartmenus.js"></script>

<!-- SmartMenus jQuery init -->
<script type="text/javascript">
    $(function() {
        $('#main-menu').smartmenus({
            subMenusSubOffsetX: 1,
            subMenusSubOffsetY: -8
        });
    });
</script>

<!-- SmartMenus core CSS (required) -->
<link href="/catalog/apps/themes/sm/sm-core-css.css" rel="stylesheet" type="text/css" />

<!-- "sm-blue" menu theme (optional, you can use your own CSS, too) -->
<link href="/catalog/apps/themes/sm/sm-blue/sm-blue.css" rel="stylesheet" type="text/css" />

<!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
<!--[if lt IE 9]>
<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
<![endif]-->
<?php
include_once documentPath (ROOT_PHP, "setup.php");
include_once documentPath (ROOT_PHP, "config.php");
$menuPath = getFullPath(PATH_CONFIG);
$menuObj = readJSON($menuPath . "/" . "menu.json");
$mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
?>

<nav id="main-nav" role="navigation">
    <!-- Sample menu definition -->
    <ul id="main-menu" class="sm sm-blue">
        <?php
        $user = $_SESSION['user'];
        foreach($menuObj->menu->menuItems as $key => $menuItem) {
            showMainMenutem($user, $menuItem);
        }
        ?>
    </ul>
</nav>

<?php
function addRoot($menuItem){
    $root = '';
    if (isset($menuItem->root) && $menuItem->root){
        $root = $GLOBALS['menuObj']->root->href . '/';
    }
    return $root;
}

function showMainMenutem($user, $menuItem){
    echo '<li><a href="' . $menuItem->href . addRoot($menuItem) . '">' . getDescription($menuItem) . '</a>';
    if (isset($menuItem->menuItems)){
        showSubMenuItem($user, $menuItem->menuItems);
    }
    printh ("</li>" . PHP_EOL);
}

function showSubMenuItem($user, $menuItems){
    printh( "<ul>");;
    foreach($menuItems as $key => $menuItem) {
        printh( '<li><a href="' . addRoot($menuItem) . $menuItem->href . '"' . checkNewPage($menuItem) . '>' . getDescription($menuItem) . '</a>');
        if (isset($menuItem->menuItems)){
            showSubMenuItem($user, $menuItem->menuItems);
        }
        printh('</li>');
    }
    printh("</ul>");


}

function checkNewPage($menuItem){
    $html = "";
    if (isset($menuItem->newPage) && $menuItem->newPage) {
        $html = ' target="_blank"';
    }
    return $html;
}

function getDescription($menuItem){
    $description = $menuItem->description;
    $description = str_replace("%NumberOfSongs%", $GLOBALS['mp3SettingsObj']->lastPlayedSong->number, $description);
    return $description;
}
