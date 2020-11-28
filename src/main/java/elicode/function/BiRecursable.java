package elicode.function;

import java.util.function.BiFunction;

@FunctionalInterface
public interface BiRecursable<T, U, R> extends BiFunction<T, U, R> {

	static <T, U, R> BiRecursable<T, U, R> define(BiRecursable<T, U, R> recursable){
		return recursable;
	}

	R apply(T t, U u, BiRecursable<T, U, R> self);

	default R apply(T t, U u){
		return apply(t, u, this);
	}

}