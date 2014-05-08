package com.giovanetti.support;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.database.JdbcCursorItemReader;

import java.util.ArrayList;
import java.util.List;

public interface JdbcCursorItemReaderSupplier<T> {

    T get() throws Exception;

    public static <T> List<T> get(JdbcCursorItemReader<T> t, JdbcCursorItemReaderSupplier<T> supplier) {
        try {
            t.open(new ExecutionContext());
            List<T> listResult = new ArrayList<>();
            T result;
            while ((result = supplier.get())!=null) {
                listResult.add(result);
            }
            t.close();
            return listResult;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
