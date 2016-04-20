package be.home.common.constants;

import org.apache.commons.lang3.*;

public final class BooleanConstants {

	public static final String TRUE = "yes";
	public static final String FALSE = "no";

	private BooleanConstants() {
	}

	public static Boolean decode(String value) {
		Boolean result = null;
		if (StringUtils.isNotEmpty(value)) {
			result = value.equalsIgnoreCase(TRUE) ? Boolean.TRUE
					: Boolean.FALSE;
		}
		return result;
	}

	public static boolean getBoolean(Boolean bool) {
		return bool == null ? false : bool.booleanValue();
	}

	public static String encode(Boolean value) {
		if (value == null) {
			return null;
		}
		if (value.booleanValue()) {
			return TRUE;
		} else {
			return FALSE;
		}
	}

}
