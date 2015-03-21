package com.giovanetti.support.batch.template;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamWriter;

import java.util.List;

/**
 * Facilite les tests des composants writer d'une step.
 * Masque les appels de méthodes à effectuer pour effectuer des écritures
 * sans passer par une step.
 *
 * @param <T> type generique pour un item writer
 */
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
