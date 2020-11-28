package elicode.effect;

public interface EmptyEffect<T> extends Effect<T> {

	@Override
	default void apply(T value) {

	}

}
