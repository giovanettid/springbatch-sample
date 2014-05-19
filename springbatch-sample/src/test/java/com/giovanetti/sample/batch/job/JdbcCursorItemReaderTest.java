package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.TestConfiguration;
import com.giovanetti.support.batch.function.JdbcCursorItemReaderSupplier;
import com.giovanetti.support.batch.rule.BatchProperties;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JdbcCursorItemReaderTest {

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @Inject
    private JdbcCursorItemReader<String> itemReader;

    @Test
    public void read() {

        assertThat(JdbcCursorItemReaderSupplier.get(itemReader, itemReader::read))
                .isNotEmpty()
                .hasSize(2)
                .containsExactly("1", "2");
    }

}
