<html>
<body>
<?php
include("../config.php");
?>
<style>
.emptySpace {
	height:100px
}
.errorMessage {
   color: red;
   font-weight: bolder;
   font-size: 20px;
   
}
</style>
<h1>Save Settings</h1>

<?php
if(isset($_POST['mp3Preprocessor'])){
	$button = $_POST['mp3Preprocessor'];
	$file = $oneDrivePath . '/Config/Java/MP3Preprocessor.json';
	if ($button == "addSplitter"){
		addSplitter($file);
	}
	if ($button == "addConfig"){
		addConfig($file);
	}
}

function addSplitter($file){

	println("<h1>MP3Preprocessor Splitter</h1>");
	$mp3PreprocesorObj = initSave($file);

	assignField($id, "splitterId");
	assignField($splitter, "splitter");
	$save = true;
	if (empty($id)) {
		printErrorMessage ("Splitter Id can't be empty", "errorMessage");
		$save = false;
	}
	if (empty($splitter)) {
		printErrorMessage ("Splitter can't be empty", "errorMessage");
		$save = false;
	}
	else if (objectExist($mp3PreprocesorObj->splitters, "pattern", $splitter, false)){
			printErrorMessage ("Splitter already exist: " . $splitter, "errorMessage");
			$save = false;
	}
	if ($save) {
		array_push ($mp3PreprocesorObj->splitters, new Splitter($id, $splitter));
		println ('Contents saved to ' . $file);
		writeJSON($mp3PreprocesorObj, $file);
	}
}

class Splitter
{
  public $id;
  public $pattern;

  public function __construct($id, $pattern)
  {
    $this->id = $id;    
    $this->pattern = $pattern;
  }
}

class Config
{
  public $type;
  public $splitter;
  public $duration;

  public function __construct($type, $splitter, $duration)
  {
    $this->type = $type;    
    $this->splitter = $splitter;
	$this->duration = $duration;
  }
}

?>
<div class="emptySpace"></div>

<script>
    document.write('<a href="' + document.referrer + '">Go Back</a>');
</script>
</body>
</html> 