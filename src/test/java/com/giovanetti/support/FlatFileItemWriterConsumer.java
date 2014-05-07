package com.giovanetti.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;

public interface FlatFileItemWriterConsumer<T> {

    void accept(T t) throws Exception;

    public static <R> void accept(FlatFileItemWriter<R> t, FlatFileItemWriterConsumer<FlatFileItemWriter<R>> consumer) {
        try {
            t.open(new ExecutionContext());
            consumer.accept(t);
            t.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
