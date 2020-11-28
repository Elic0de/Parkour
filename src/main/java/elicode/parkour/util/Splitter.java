package elicode.parkour.util;

import java.util.Arrays;

public class Splitter {

	public static int[] splitToIntArguments(String text){
		return Arrays.stream(text.split(","))
						.mapToInt(Integer::parseInt)
						.toArray();
	}

}
