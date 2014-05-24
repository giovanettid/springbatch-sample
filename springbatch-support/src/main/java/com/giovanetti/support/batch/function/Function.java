package com.giovanetti.support.batch.function;

@FunctionalInterface
public interface Function<T,R> {

    R apply(T t) throws Exception;

    public static <T,R> R applyWithRawException(T t, Function<T, R> function) {
        try {
            return function.apply(t);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
