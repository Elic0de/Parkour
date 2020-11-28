package elicode.beta.parkour.serialize;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.IntStream;

public class Deserializer {

	private final String[] data;
	private final Function<String, ?>[] mappers;
	private final Class<?>[] types;

	public static Deserializer stream(String text){
		return new Deserializer(text);
	}

	@SuppressWarnings("unchecked")
	public Deserializer(String text){
		data = text.split(",");
		mappers = new Function[data.length];
		types = new Class<?>[data.length];
	}

	public Deserializer map(Function<String, ?> mapper, Class<?> type, int... indexes){
		Arrays.stream(indexes).forEach(index -> {
			mappers[index] = mapper;
			types[index] = type;
		});
		return this;
	}

	@SuppressWarnings("unchecked")
	public <T> T deserializeTo(Class<?> type){
		Object[] arguments = IntStream.range(0, data.length)
				.mapToObj(i -> mappers[i].apply(data[i]))
				.toArray();

		try{
			return (T) type.getConstructor(types).newInstance(arguments);
		}catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e){
			e.printStackTrace();
		}

		return null;
	}

}
