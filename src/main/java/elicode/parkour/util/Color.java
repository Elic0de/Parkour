package elicode.parkour.util;

import java.util.Random;

import elicode.parkour.util.text.Text;

public class Color {

	private static final Random random = new Random();

	public static Color deserialize(String text){
		int[] values = Splitter.splitToIntArguments(text);
		return new Color(values[0], values[1], values[2]);
	}

	public final int red, green, blue;

	public Color(int red, int green, int blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public int adjustRed(int width){
		return adjust(red, width);
	}

	public int adjustGreen(int width){
		return adjust(green, width);
	}

	public int adjustBlue(int width){
		return adjust(blue, width);
	}

	private int adjust(int value, int width){
		return Math.max(Math.min(value + random.nextInt(width) - (width / 2 - 1), 255), 0);
	}

	public String serialize(){
		return Text.stream("$red,$green,$blue")
				.setAttribute("$red", red)
				.setAttribute("$green", green)
				.setAttribute("$blue", blue)
				.toString();
	}

}
