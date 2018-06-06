package be.home.common.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils  {

	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String DD_MM_YYYYY = "dd/MM/yyyy";
	private static final Locale CURRENT_LOCALE = new Locale("nl", "BE");
	public static final String YYYYMMDDHHMMSS = "yyyyMMdd.HHmmss";
    public static final String DD_MM_YYYY_HH_MM_SS = "dd/MM/yyyy HH:mm:ss";

	private static final SimpleDateFormat YYYYMMDD_FORMATTER = new SimpleDateFormat(
			YYYYMMDD, CURRENT_LOCALE);

	static {
		YYYYMMDD_FORMATTER.setLenient(false);
	}

	public static String formatYYYYMMDD(Date date) {
		date = date == null ? new Date() : date;
		return YYYYMMDD_FORMATTER.format(date);
	}

	public static String formatYYYYMMDD() {
		return YYYYMMDD_FORMATTER.format(new Date());
	}

	public static String formatDate(Date date, String dateFormat) {
		if (date != null && dateFormat != null){
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

	public String convertSecToMin (Object t) {
		Integer i = (Integer) t;
        int nSecondTime = i.intValue();
        if (nSecondTime < 3600){
			return LocalTime.MIN.plusMinutes(nSecondTime).toString();
		}
		else {
			return LocalTime.MIN.plusSeconds(nSecondTime).toString();
		}
	}

	public static Long getSecondsBetween(Date first, Date second){
		return (first.getTime() - second.getTime())/ 1000;
	}

	public static Date max(Date d1, Date d2) {
		if (d1 == null && d2 == null) return null;
		if (d1 == null) return d2;
		if (d2 == null) return d1;
		return (d1.after(d2)) ? d1 : d2;
	}

	public static LocalDate convertCalendarToLocalDate(Calendar cal){
		LocalDate localDate = cal.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		return localDate;
	}

	public static Calendar getFirstSaturdayOfMonth() {
		Calendar cal = Calendar.getInstance();
		return getFirstSaturdayOfMonth(cal);
	}

	public static Calendar getFirstSaturdayOfMonth(Calendar cal) {
		if (cal == null){
			cal = Calendar.getInstance();
		}
		cal.set(Calendar.DAY_OF_MONTH, 1);
		int day = cal.get(Calendar.DAY_OF_WEEK);
		while (day != Calendar.SATURDAY) {
			cal.add(Calendar.DAY_OF_MONTH, 1);
			day = cal.get(Calendar.DAY_OF_WEEK);
		}
		return cal;
	}

	public static String getMonthName(Calendar cal){
		String name = "Unknown";
		int month = cal.get(Calendar.MONTH);
		switch (month){
			case Calendar.JANUARY:
				name = "Januari";
				break;
			case Calendar.FEBRUARY:
				name = "Februari";
				break;
			case Calendar.MARCH:
				name = "Maart";
				break;
			case Calendar.APRIL:
				name = "April";
				break;
			case Calendar.MAY:
				name = "Mei";
				break;
			case Calendar.JUNE:
				name = "Juni";
				break;
			case Calendar.JULY:
				name = "Juli";
				break;
			case Calendar.AUGUST:
				name = "Augustus";
				break;
			case Calendar.SEPTEMBER:
				name = "September";
				break;
			case Calendar.OCTOBER:
				name = "Oktober";
				break;
			case Calendar.NOVEMBER:
				name = "November";
				break;
			case Calendar.DECEMBER:
				name = "DECEMBER";
				break;

		}
		return name;
	}

	public static String formattedDay(Calendar cal){
		String day = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));
		return day;

	}
}
