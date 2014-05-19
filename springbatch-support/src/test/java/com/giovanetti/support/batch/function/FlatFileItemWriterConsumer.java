package com.giovanetti.support.batch.function;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;

import java.util.List;

public interface FlatFileItemWriterConsumer<T> {

    void accept(T t) throws Exception;

    public static <T> void accept(FlatFileItemWriter<T> t, List<T> items, FlatFileItemWriterConsumer<List<T>> consumer) {
        try {
            t.open(new ExecutionContext());
            consumer.accept(items);
            t.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
