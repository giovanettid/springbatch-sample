package com.giovanetti.support.function;

public interface Consumer<T> {

    void accept(T t) throws Exception;

    public static <T> void acceptAndAvoidRawExceptionType(T t, Consumer<T> consumer) {
        try {
            consumer.accept(t);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
