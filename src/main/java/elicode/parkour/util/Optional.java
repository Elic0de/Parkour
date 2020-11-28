package elicode.parkour.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Optional<T> {

	public static final Optional<?> EMPTY = of(null);

	private final T reference;
	private Function<T, ?> action, emptyAction;

	public static <T> Optional<T> of(T reference){
		return new Optional<>(reference);
	}

	@SuppressWarnings("unchecked")
	public static <T> Optional<T> empty(){
		return (Optional<T>) EMPTY;
	}

	private Optional(T reference){
		this.reference = reference;
	}

	public boolean isPresent(){
		return reference != null;
	}

	public T forcedUnwrapping(){
		return reference;
	}

	public Optional<T> setPresentFunction(Function<T, ?> action){
		this.action = action;
		return this;
	}

	public Optional<T> setPresentProcedure(Consumer<T> action){
		this.action = ref -> {
			action.accept(ref);
			return null;
		};
		return this;
	}

	public Optional<T> setEmptyFunction(Supplier<?> emptyAction){
		this.emptyAction = ref -> emptyAction.get();
		return this;
	}

	public Optional<T> setEmptyProcedure(Runnable emptyAction){
		this.emptyAction = ref -> {
			emptyAction.run();
			return null;
		};
		return this;
	}

	@SuppressWarnings("unchecked")
	public <R> R apply(){
		if(isPresent()) return action != null ? (R) action.apply(reference) : null;
		else return emptyAction != null ? (R) emptyAction.apply(reference) : null;
	}

}
