package com.giovanetti.support.function;

@FunctionalInterface
public interface Function<T,R> {

    R apply(T t) throws Exception;

    public static <T,R> R applyAndAvoidRawExceptionType(T t, Function<T,R> function) {
        try {
            return function.apply(t);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
