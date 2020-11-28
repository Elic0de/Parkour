package elicode.parkour.util.format;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class TimeFormat {

	private static final SimpleDateFormat FORMAT_FOR_TIMES_WITHIN_ONE_HOUR;
	private static final SimpleDateFormat FORMAT_FOR_TIMES_OF_ONE_HOUR_OR_MORE;

	static{
		FORMAT_FOR_TIMES_WITHIN_ONE_HOUR = new SimpleDateFormat("mm:ss.SSS");
		FORMAT_FOR_TIMES_WITHIN_ONE_HOUR.setTimeZone(TimeZone.getTimeZone("GMT"));

		FORMAT_FOR_TIMES_OF_ONE_HOUR_OR_MORE = new SimpleDateFormat("HH:mm:ss.SSS");
		FORMAT_FOR_TIMES_OF_ONE_HOUR_OR_MORE.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public static String format(long time){
		return time < 3600000 ? FORMAT_FOR_TIMES_WITHIN_ONE_HOUR.format(time) : FORMAT_FOR_TIMES_OF_ONE_HOUR_OR_MORE.format(time);
	}

}
