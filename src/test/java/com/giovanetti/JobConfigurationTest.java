package com.giovanetti;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collection;

import javax.inject.Inject;

import com.giovanetti.support.ExternalConfiguration;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.giovanetti.support.BatchProperties;
import com.giovanetti.support.TestUtilsConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestUtilsConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobConfigurationTest {

    private static final String OUTPUT_FILE_PATH = "target/out/output"
            + System.currentTimeMillis() + ".txt";

    @ClassRule
    public final static BatchProperties batchProperties = new BatchProperties();

    @BeforeClass
    public static void setupClass() throws IOException {
        batchProperties
                .addTechnicalHsql()
                .addFunctionalHsql()
                .add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL
                                .toString(),
                        "1"
                ).flush();
    }

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

        StepExecution stepExecution = stepExecutions.iterator().next();
        assertThat(stepExecution.getReadCount()).isEqualTo(2);
        assertThat(stepExecution.getWriteCount()).isEqualTo(2);

    }

    @Test(expected = JobParametersInvalidException.class)
    public void jobExtraction_ParametreInvalide_KO() throws Exception {
        jobLauncherTestUtils.launchJob();
    }

}
