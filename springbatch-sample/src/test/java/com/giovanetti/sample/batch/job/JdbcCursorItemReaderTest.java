package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.JobExtractionTestConfiguration;
import com.giovanetti.sample.batch.item.User;
import com.giovanetti.support.batch.extension.BatchProperties;
import com.giovanetti.support.batch.extension.DBUnitExtension;
import com.giovanetti.support.batch.template.ItemReaderTemplate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static com.giovanetti.sample.batch.item.ItemHelper.listOf2Users;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {JobExtractionTestConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JdbcCursorItemReaderTest {

    @RegisterExtension
    public static BatchProperties batchProperties = BatchProperties.getDefault();

    @RegisterExtension
    @Inject
    public DBUnitExtension dbUnitExtension;

    @Inject
    private ItemReaderTemplate<User> itemReader;

    @Test
    public void databaseInitialisationOK() {
        assertThat(dbUnitExtension.rowCountFrom("USER")).isEqualTo(2);
    }

    @Test
    public void read() {

        assertThat(itemReader.readAll())
                .hasSize(2)
                .usingFieldByFieldElementComparator()
                .containsAll(listOf2Users());
    }
}
