package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.JobAlimentationTestConfiguration;
import com.giovanetti.sample.batch.item.User;
import com.giovanetti.support.batch.rule.BatchProperties;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.inject.Inject;

import static com.giovanetti.sample.batch.item.ItemHelper.listOf2Users;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JobAlimentationTestConfiguration.class})
@TestExecutionListeners(
        {DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JdbcBatchItemWriterTest {

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @Inject
    private JdbcBatchItemWriter<User> itemWriter;

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Test
    public void write() throws Exception {

        // Act
        itemWriter.write(listOf2Users());

        // Assert
        assertThat(jdbcTemplate.query("select id,prenom,nom from USER",
                ParameterizedBeanPropertyRowMapper.newInstance(User.class)))
                .hasSize(2)
                .usingFieldByFieldElementComparator()
                .containsAll(listOf2Users());
    }

}
