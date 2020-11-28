package elicode.beta.parkour.serialize;

import elicode.function.BiRecursable;

public class Serializer {

	public static final BiRecursable<Object[], Integer, String> SERIALIZER = BiRecursable.define((objs, index, self) -> {
		if(objs.length - 1 == index) return objs[index].toString();
		else return objs[index].toString() + "," + self.apply(objs, index + 1);
	});

	public static String serialize(Object... objects){
		return SERIALIZER.apply(objects, 0);
	}

}
