package be.home.common.utils;

import org.apache.commons.lang3.StringUtils;

public class SortUtils {
    public static String stripAccentsIgnoreCase(String sortString) {
        return StringUtils.stripAccents(sortString).toUpperCase();
    }
}
