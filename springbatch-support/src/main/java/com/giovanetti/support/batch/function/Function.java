package com.giovanetti.support.batch.function;

/**
 * Interface créée pour palier aux signatures de méthode spring déclarant l'exception brute java.lang.Exception.
 * Permet de renvoyer une exception runtime à la place.
 *
 * @see {@link org.springframework.beans.factory.FactoryBean#getObject()}
 */
@FunctionalInterface
public interface Function<T, R> {

    R apply(T t) throws Exception;

    public static <T, R> R applyWithRawException(T t, Function<T, R> function) {
        try {
            return function.apply(t);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
