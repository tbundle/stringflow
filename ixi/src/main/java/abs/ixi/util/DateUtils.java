package abs.ixi.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtils {
    public static final String DEFAULT_DATE_FORMAT = "ddMMyyyy";

    public static final int WEEK_DAYS = 7;

    public static final long MILS_IN_A_HOUR = 3600000;
    public static final long MILS_IN_8_HOUR = 28800000;
    public static final long MILS_IN_A_DAY = 86400000;

    public static Calendar setStartOfDayTime(Calendar cal) {
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.clear(Calendar.MINUTE);
	cal.clear(Calendar.SECOND);
	cal.clear(Calendar.MILLISECOND);

	return cal;
    }

    public static Calendar setEndOfDayTime(Calendar cal) {
	cal.set(Calendar.HOUR_OF_DAY, cal.getActualMaximum(Calendar.HOUR_OF_DAY));
	cal.set(Calendar.MINUTE, cal.getActualMaximum(Calendar.MINUTE));
	cal.set(Calendar.SECOND, cal.getActualMaximum(Calendar.SECOND));
	cal.set(Calendar.MILLISECOND, cal.getActualMaximum(Calendar.MILLISECOND));

	return cal;
    }

    public static Calendar calendarWithStartOfDayTime() {
	Calendar cal = Calendar.getInstance();
	return setStartOfDayTime(cal);
    }

    public static Calendar calendarWithEndOfDayTime() {
	Calendar cal = Calendar.getInstance();
	return setEndOfDayTime(cal);
    }

    public static int calendarDay(String day) {
	String dayUpper = StringUtils.toUpper(day);

	switch (dayUpper) {
	case "SUNDAY":
	    return Calendar.SUNDAY;
	case "MONDAY":
	    return Calendar.MONDAY;
	case "TUESDAY":
	    return Calendar.TUESDAY;
	case "WEDNESDAY":
	    return Calendar.WEDNESDAY;
	case "THURSDAY":
	    return Calendar.THURSDAY;
	case "FRIDAY":
	    return Calendar.FRIDAY;
	case "SATURDAY":
	    return Calendar.SATURDAY;
	}

	throw new IllegalArgumentException("Illegal day string :" + day);
    }

    public static final Date now() {
	return new Date();
    }

    public static final Date today() {
	return new Date();
    }

    public static final Calendar currentTime() {
	return Calendar.getInstance();
    }

    /**
     * Returns current date minus one day
     */
    public static final Date yesterday() {
	Calendar cal = Calendar.getInstance();
	cal.add(Calendar.DAY_OF_MONTH, -1);

	return cal.getTime();
    }

    public static final Date date(String date) throws ParseException {
	DateFormat formatter = new SimpleDateFormat(DEFAULT_DATE_FORMAT);

	return formatter.parse(date);
    }

    public static final Date date(String date, String format) throws ParseException {
	DateFormat formatter = new SimpleDateFormat(format);

	return formatter.parse(date);
    }

    /**
     * returns messenger style display time. The input mills must be positive
     * number
     */
    public static final String displayTime(long mills) {
	long current = Calendar.getInstance().getTimeInMillis();

	if (mills <= 0) {
	    return "invalid time";
	}

	if ((current - mills) < MILS_IN_A_HOUR) {
	    return (current - mills) / (1000 * 60) + " minutes ago";

	} else if ((current - mills) < MILS_IN_8_HOUR) {
	    return (current - mills) / (1000 * 60 * 60) + " hours ago";

	} else if ((current - mills) < MILS_IN_A_DAY) {
	    return "today";

	} else {
	    Calendar cal = Calendar.getInstance();
	    cal.setTimeInMillis(mills);

	    return cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
	}

    }

    public static final Timestamp timestamp(Calendar cal) {
	return new Timestamp(cal.getTimeInMillis());
    }

    public static final Timestamp currentTimestamp() {
	Calendar cal = Calendar.getInstance();
	return new Timestamp(cal.getTimeInMillis());
    }

    public static final Calendar calendar(Timestamp t) {
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(t.getTime());

	return c;
    }

    public static final Timestamp roll(int field, int amount, Timestamp t) {
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(t.getTime());
	Calendar cal = roll(field, amount, c);

	return new Timestamp(cal.getTimeInMillis());
    }

    public static final Calendar roll(int field, int amount, Calendar c) {
	c.add(field, amount);
	return c;
    }

    public static String shortMonthAndDate(Calendar c) {
	return c.get(Calendar.DAY_OF_MONTH) + " " + c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
    }

    public static String shortMonthAndDate(long millis) {
	Calendar c = Calendar.getInstance();
	c.setTimeInMillis(millis);

	return c.get(Calendar.DAY_OF_MONTH) + " " + c.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US);
    }

    public static String monthAndYear(Calendar c) {
	return c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US) + " " + c.get(Calendar.YEAR);
    }

    public static String getCurrentTimeString() {

	return new SimpleDateFormat("dd-MM-YYYY-HH-MM-ss").format(new Date());
    }

    public static String getTimeString(Date date, TimeZone timeZone, String pattern) {
	SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
	dateFormat.setTimeZone(timeZone);

	return dateFormat.format(date);
    }

    public static Date getDateTime(String dateTimeString, String pattern, TimeZone timeZone) throws ParseException {
	SimpleDateFormat sdf = new SimpleDateFormat();
	sdf.setTimeZone(timeZone);

	return sdf.parse(dateTimeString);
    }
}
