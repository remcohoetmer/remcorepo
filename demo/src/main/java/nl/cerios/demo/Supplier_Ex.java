package nl.cerios.demo;


@FunctionalInterface
public interface Supplier_Ex<T> {

    /**
     * Gets a result.
     *
     * @return a result
     */
    T get() throws Exception;
}