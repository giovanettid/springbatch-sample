package com.giovanetti.support.batch.template;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Facilite les tests des composants reader d'une step.
 * Masque les appels de méthodes à effectuer pour effectuer des lectures
 * sans passer par une step.
 *
 * @param <T> type generique pour un item reader
 */
public class ItemReaderTemplate<T> {

    private final ItemStreamReader<T> itemReader;

    public ItemReaderTemplate(ItemStreamReader<T> itemReader) {
        this.itemReader = itemReader;
    }

    public List<T> readAll() {
        try {
            itemReader.open(new ExecutionContext());
            List<T> listResult = new ArrayList<>();
            T result;
            while ((result = itemReader.read()) != null) {
                listResult.add(result);
            }
            itemReader.close();
            return listResult;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
