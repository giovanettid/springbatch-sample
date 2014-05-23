package com.giovanetti.support.batch.template;

import org.springframework.batch.item.*;

import java.util.List;

public class ItemWriterTemplate<T> {

    private final ItemStreamWriter<T> itemWriter;

    public ItemWriterTemplate(ItemStreamWriter<T> itemWriter) {
        this.itemWriter = itemWriter;
    }

    public void write(List<T> items) {
        try {
            itemWriter.open(new ExecutionContext());
            itemWriter.write(items);
            itemWriter.close();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
