<?php
    function getFormMode(){
        if (isset($_GET['mode'])){
            $formMode = htmlspecialchars($_GET['mode']);
            return $formMode;
        }
        else {
           exit('Form Mode Not Set!!!');
        }
        
    }
?>