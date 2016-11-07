<?php
//$dbHandle = sqlite_open("c:/My Data/Mezzmo.db");
//_test(array('item'=>"test", 'css'=>"default"));
if (isset($_GET['user'])){
    echo $_GET['user'];
}
else {
    echo 'unknown user';
}

function _test($array){
    $obj = (object) $array;
    var_dump($array);
    echo $obj->item;
}

class MyTest
{
    public $id;
    public $pattern;
    public $name;
    public $field;
    public $value;


    public function __construct($array)
    {
        $keys = array_keys($array);
        foreach($keys as $key => $tst) {
            $this->{$tst} = $array[$tst];
        }
    }
}


function sqlite_open($location)
{
    $handle = new SQLite3($location);
    return $handle;
}
function sqlite_query($dbhandle,$query)
{
    $array['dbhandle'] = $dbhandle;
    $array['query'] = $query;
    $result = $dbhandle->query($query);
    return $result;
}
function sqlite_fetch_array(&$result,$type)
{
    #Get Columns
    $i = 0;
    while ($result->columnName($i))
    {
        $columns[ ] = $result->columnName($i);
        $i++;
    }
   
    $resx = $result->fetchArray(SQLITE3_ASSOC);
    return $resx;
} 

?>