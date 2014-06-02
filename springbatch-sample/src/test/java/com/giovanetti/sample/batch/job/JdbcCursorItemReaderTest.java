package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.TestConfiguration;
import com.giovanetti.sample.batch.item.User;
import com.giovanetti.support.batch.rule.BatchProperties;
import com.giovanetti.support.batch.rule.DBUnitRule;
import com.giovanetti.support.batch.template.ItemReaderTemplate;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static com.giovanetti.sample.batch.item.ItemHelper.listOf2UsersMapFromDB;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JdbcCursorItemReaderTest {

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @Rule
    @Inject
    public DBUnitRule dbUnitRule;

    @Inject
    private ItemReaderTemplate<User> itemReader;

    @Test
    public void read() {

        assertThat(itemReader.readAll()).isNotEmpty()
                .hasSize(2)
                .usingFieldByFieldElementComparator()
                .containsAll(listOf2UsersMapFromDB());
    }

}
