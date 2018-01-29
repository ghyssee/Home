<?php
/**
 * Created by PhpStorm.
 * User: ghyssee
 * Date: 29/01/2018
 * Time: 13:29
 */

    class DateUtils {
        public static function convertStringToDate($strDate){
            $date = DateTime::createFromFormat('YmdHis', $strDate);
            return $date;
        }
    }