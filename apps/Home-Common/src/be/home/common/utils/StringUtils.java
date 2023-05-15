package be.home.common.utils;

import java.math.BigInteger;

public class StringUtils {

    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    public static boolean isBlank(String text){
        return org.apache.commons.lang3.StringUtils.isBlank(text.replaceAll("\0", ""));
    }

    public static String removeNull(String text){
        return text.replaceAll("\0", "");
    }
}
