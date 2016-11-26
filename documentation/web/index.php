<?php 
session_start();
if (isset($_GET['user'])){
    $_SESSION["user"] = $_GET['user'];
}
else {
    $_SESSION["user"] = "guest";
}
?>
<!doctype html>
<html>
<head>
<META http-equiv="content-type" content="text/html; charset=ISO-8859-1">
<META http-equiv="expires" content="0">	 
<META http-equiv="Pragma" content="no-cache">   	 

<title>Eric's Menu</title>
<style>
* {
  margin: 0;
  padding: 0;
  -webkit-box-sizing: border-box;
  -moz-box-sizing: border-box;
  -ms-box-sizing: border-box;
  box-sizing: border-box;
}
.centered {
    text-align: center;
}
.title {
 font-family:Verdana;
 font-size:40px; 
 font-weight: bold; 
 color:black;
}
.heightHeader {
   height: 60px;
   
}

body {
  background: #F7F7F7;
  font-size: 15px;
  color: #777777;
  font-family: 'Roboto', sans-serif;
}

a { text-decoration: none; }

#main {
  width: 100%;
  display: block;
  float: left;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  width: 100%;
}

.navigation {
  display: block;
  margin: 20px 0;
  background: #009788;
  box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.26);
  border-radius: 3px;
}

.navigation ul {
  list-style-type: none;
  margin: 0;
  padding: 0;
  display: block;
}

.navigation li {
  list-style-type: none;
  margin: 0;
  padding: 0;
  display: inline-block;
  position: relative;
  color: #def1f0;
}

.navigation li a {
  padding: 10px 15px;
  font-size: 25px;
  color: #def1f0;
  display: inline-block;
  outline: 0;
  font-weight: 400;
  font-size: 40px;
}

.navigation li:hover ul.dropdown { display: block; }

.navigation li ul.dropdown {
  position: absolute;
  display: none;
  width: 400px;
  background: #00695b;
  box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.26);
  padding-top: 0;
}

.navigation li ul.dropdown li {
  display: block;
  list-style-type: none;
}

.navigation li ul.dropdown li a {
  padding: 10px;
  font-size: 15px;
  color: #fff;
  display: block;
  border-bottom: 1px solid #005c4d;
  font-weight: 400;
  font-size: 20px;
}

.navigation li ul.dropdown li:last-child a { border-bottom: none; }

.navigation li:hover a {
  background: #00695b;
  color: #fff !important;
}

.navigation li:first-child:hover a { border-radius: 3px 0 0 3px; }

.navigation li ul.dropdown li:hover a { background: #56b5ae; }

.navigation li ul.dropdown li:first-child:hover a { border-radius: 0; }

.navigation li:hover .arrow-down { border-top: 5px solid #fff; }

.arrow-down {
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #def1f0;
  position: relative;
  top: 15px;
  right: -5px;
  content: '';
}
 @media only screen and (max-width:767px) {

.navigation {
  background: #fff;
  width: 200px;
  height: 100%;
  display: block;
  position: fixed;
  left: -200px;
  top: 0px;
  transition: left 0.3s linear;
  margin: 0;
  border: 0;
  border-radius: 0;
  overflow-y: auto;
  overflow-x: hidden;
  height: 100%;
}

.navigation.visible {
  left: 0px;
  transition: left 0.3s linear;
}

.nav_bg {
  display: inline-block;
  vertical-align: middle;
  width: 100%;
  height: 50px;
  margin: 0;
  position: absolute;
  top: 0px;
  left: 0px;
  background: #009788;
  padding: 12px 0 0 10px;
  box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.26);
}

.nav_bar {
  display: inline-block;
  vertical-align: middle;
  height: 50px;
  cursor: pointer;
  margin: 0;
}

.nav_bar span {
  height: 2px;
  background: #fff;
  margin: 5px;
  display: block;
  width: 20px;
}

.nav_bar span:nth-child(2) { width: 20px; }

.nav_bar span:nth-child(3) { width: 20px; }

.navigation ul { padding-top: 50px; }

.navigation li { display: block; }

.navigation li a {
  display: block;
  color: #505050;
  font-weight: 500;
}

.navigation li:first-child:hover a { border-radius: 0; }

.navigation li ul.dropdown { position: relative; }

.navigation li ul.dropdown li a {
  background: #00695b !important;
  border-bottom: none;
  color: #fff !important;
}

.navigation li:hover a {
  background: #009788;
  color: #fff !important;
}

.navigation li ul.dropdown li:hover a {
  background: #56b5ae !important;
  color: #fff !important;
}

.navigation li ul.dropdown li a { padding: 10px 10px 10px 30px; }

.navigation li:hover .arrow-down { border-top: 5px solid #fff; }

.arrow-down {
  border-top: 5px solid #505050;
  position: absolute;
  top: 20px;
  right: 10px;
}

.opacity {
  background: rgba(0,0,0,0.7);
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
}
}
 @media only screen and (max-width:1199px) {

.container { width: 96%; }
}
</style>
</head>

<body>

<?php
include("apps/php/config.php");
$menuObj = readJSONWithCode(JSON_MENU);
$mp3SettingsObj = readJSONWithCode(JSON_MP3SETTINGS);
?>

<div class="title centered heightHeader">
Eric's Menu
</div>
<div id="main">
  <div class="container">
    <nav>
      <div class="navigation">
        <ul>
			<?php
			$user = $_SESSION['user'];
			foreach($menuObj->menu->menuItems as $key => $menuItem) {
                showMenuItems($user, $menuItem);
			}
			?>
       </ul>
    </div>
    <div class="nav_bg">
      <div class="nav_bar"> <span></span> <span></span> <span></span> </div>
    </div>
  </nav>
<script src="Music/js/jquery-3.1.1.js"></script> 
<script>
$(document).ready(function(){
		$('.nav_bar').click(function(){
			$('.navigation').toggleClass('visible');
			$('body').toggleClass('opacity');
		});
	});
</script>
</body>
</html>
<?php
function showMenuItems($user, $menuItem){
  if (isset($menuItem->menuItems)){
    printh ('<li><a href="#" tabindex="1">' . $menuItem->description . '<span class="arrow-down"></span></a>');
    printh( '<ul class="dropdown">');
    foreach($menuItem->menuItems as $key => $menuItem) {
    //    if (isset($menuItem->menuItems)) {
     //     showMenuItems($user, $menuItem);
     //   }
        //else {
            $hasAccess = true;
            if (isset($menuItem->user)) {
              if ($menuItem->user != $user) {
                $hasAccess = true;
              }
            }
            if ($hasAccess) {
              printh('<li><a href="' . $menuItem->href . '">' . getDescription($menuItem) . '</a></li>');
            }
        }
    //}
    printh('</ul>');
  }
  else {
    printh( '<li><a href="' . $menuItem->href . '">' . getDescription($menuItem) . '</a></li>');
  }
}

function getDescription($menuItem){
		$description = $menuItem->description;
		$description = str_replace("%NumberOfSongs%", $GLOBALS['mp3SettingsObj']->lastPlayedSong->number, $description);
		return $description;
	}
?>
