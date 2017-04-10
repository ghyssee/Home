<?php
/**
 * Created by PhpStorm.
 * User: Gebruiker
 * Date: 8/04/2017
 * Time: 22:24
 */

function sessionStart()
{
    session_start();
    $expiry = 1800;//session expiry required after 30 mins
    if (isset($_SESSION['LAST']) && (time() - $_SESSION['LAST'] > $expiry)) {
        logInfo("Session Timed out");
        session_unset();
        session_destroy();
        session_start();
    }
    $_SESSION['LAST'] = time();
}
?>