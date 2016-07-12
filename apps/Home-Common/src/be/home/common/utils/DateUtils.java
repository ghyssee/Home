package be.home.common.utils;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;

public class DateUtils {

	public static final String YYYYMMDD = "yyyyMMdd";
	private static final Locale CURRENT_LOCALE = new Locale("nl", "BE");
	public static final String YYYYMMDDHHMMSS = "yyyyMMdd.HHmmss";

	private static final SimpleDateFormat YYYYMMDD_FORMATTER = new SimpleDateFormat(
			YYYYMMDD, CURRENT_LOCALE);

	static {
		YYYYMMDD_FORMATTER.setLenient(false);
	}

	public static String formatYYYYMMDD(Date date) {
		return YYYYMMDD_FORMATTER.format(date);
	}

	public static String formatDate(Date date, String dateFormat) {
		if (dateFormat != null){
			final SimpleDateFormat FORMATTER = new SimpleDateFormat(
					dateFormat, CURRENT_LOCALE);
			return FORMATTER.format(date);
		}
		return "";
	}

	public static boolean isWeekend(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setLenient(false);
		cal.setTime(date);
		return (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal
				.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY);

	}

}