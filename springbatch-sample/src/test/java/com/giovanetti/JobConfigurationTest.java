package com.giovanetti;

import com.giovanetti.support.ExternalConfiguration;
import com.giovanetti.support.rule.BatchProperties;
import com.google.common.collect.Iterables;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobConfigurationTest {

    private static final String OUTPUT_FILE_PATH = "target/out/output"
            + System.currentTimeMillis() + ".txt";

    @ClassRule
    public final static BatchProperties batchProperties = new BatchProperties()
            .addTechnicalHsql()
            .addFunctionalHsql()
            .add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL
                            .toString(),
                    "1"
            );

    @Inject
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Test
    public void databaseInitialisationOK() {

        // Act & Assert
        assertThat(
                jdbcTemplate.queryForObject("select count(*) from USER",
                        Integer.class)
        ).isEqualTo(2);
    }

    @Test
    public void jobExtractionOK() throws Exception {

        // Act
        JobExecution jobExecution = jobLauncherTestUtils
                .launchJob(new JobParametersBuilder().addString(JobConfiguration.OUTPUT_FILE_PARAMETER,
                        OUTPUT_FILE_PATH).toJobParameters());

        // Assert
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Collection<StepExecution> stepExecutions = jobExecution
                .getStepExecutions();

        assertThat(stepExecutions).hasSize(1);

        StepExecution stepExecution = Iterables.getOnlyElement(stepExecutions);
        assertThat(stepExecution.getReadCount()).isEqualTo(2);
        assertThat(stepExecution.getWriteCount()).isEqualTo(2);

    }

    @Test(expected = JobParametersInvalidException.class)
    public void jobExtraction_ParametreInvalide_KO() throws Exception {
        jobLauncherTestUtils.launchJob();
    }

}
