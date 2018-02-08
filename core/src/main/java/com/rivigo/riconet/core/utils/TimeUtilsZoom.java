package com.rivigo.riconet.core.utils;


import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TimeUtilsZoom {

	private TimeUtilsZoom() {throw new IllegalStateException("Utility class");}

	public static final long MILLIS_IN_DAY = 24 * 3600 * 1000l;
	public static final long MILLIS_IN_HOUR = 3600 * 1000l;
	public static final long MILLIS_IN_MINUTE = 60 * 1000l;
	public static final long SECONDS_IN_MINUTE = 60l;

	public static final DateTimeZone IST=DateTimeZone.forID("Asia/Kolkata");
	public static final DateTimeFormatter IST_DATE_TIME_FORMATTER = DateTimeFormat.forPattern("dd-MM-yyyy").withZone(IST);

}
