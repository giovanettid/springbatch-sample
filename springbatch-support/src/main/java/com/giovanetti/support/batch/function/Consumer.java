package com.giovanetti.support.batch.function;

import org.springframework.beans.factory.InitializingBean;

/**
 * Interface créée pour palier aux signatures de méthode spring déclarant l'exception brute java.lang.Exception.
 * Permet de renvoyer Une exception runtime à la place.
 *
 * @see {@link org.springframework.beans.factory.InitializingBean#afterPropertiesSet()}
 */
public interface Consumer<T> {

    void accept(T t) throws Exception;

    public static <T> void acceptWithRawException(T t, Consumer<T> consumer) {
        try {
            consumer.accept(t);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
