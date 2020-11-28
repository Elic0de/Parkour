package elicode.function;

import java.util.function.Function;

@FunctionalInterface
public interface Recursable<T, R> extends Function<T, R> {

	static <T, R> Recursable<T, R> define(Recursable<T, R> recursable){
		return recursable;
	}

	R apply(T t, Recursable<T, R> self);

	default R apply(T t){
		return apply(t, this);
	}

}
