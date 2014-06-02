package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.TestConfiguration;
import com.giovanetti.support.batch.ExternalConfiguration;
import com.giovanetti.support.batch.rule.BatchProperties;
import com.giovanetti.support.batch.rule.DBUnitRule;
import com.google.common.collect.Iterables;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobExtractionTest {

    @ClassRule
    public final static TemporaryFolder outputFile = new TemporaryFolder();

    @ClassRule
    public final static BatchProperties batchProperties = new BatchProperties().addTechnicalHsql()
            .addFunctionalHsql()
            .add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL.toString(), "1");

    @Rule
    @Inject
    public DBUnitRule dbUnitRule;

    @Inject
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void databaseInitialisationOK() {
        assertThat(dbUnitRule.rowCountFrom("USER")).isEqualTo(2);
    }

    @Test
    public void jobExtraction() throws Exception {

        // Act
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder().addString(JobExtractionConfiguration.OUTPUT_FILE_PARAMETER,
                        outputFile.getRoot().getPath()).toJobParameters()
        );

        // Assert
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        StepExecution stepExecution = Iterables.getOnlyElement(jobExecution.getStepExecutions());
        assertThat(stepExecution.getReadCount()).isEqualTo(2);
        assertThat(stepExecution.getWriteCount()).isEqualTo(2);

    }

    @Test(expected = JobParametersInvalidException.class)
    public void jobExtraction_SiParametreInvalide_AlorsException() throws Exception {
        jobLauncherTestUtils.launchJob();
    }

}
