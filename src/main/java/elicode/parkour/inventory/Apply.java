package elicode.parkour.inventory;

import java.sql.SQLException;

public interface Apply<T> {

	default T apply(T value)  {
		define(value);
		return value;
	}

	void define(T value) ;
}
