package nl.cerios.demo;

import java.util.function.Supplier;

public class CF_Utils {
	public static <T> Supplier<T> transportException(Supplier_Ex<T> func) {
		return new Supplier<T>() {
			@Override
			public T get() {
				try {
					return func.get();
				} catch (Exception e) {
					throw LaunderThrowable.launderThrowable(e);
				}
			}
		};
	}
}
