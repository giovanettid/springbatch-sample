package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.JobAlimentationTestConfiguration;
import com.giovanetti.support.batch.ExternalConfiguration;
import com.giovanetti.support.batch.rule.BatchProperties;
import com.google.common.collect.Iterables;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {JobAlimentationTestConfiguration.class, JobLauncherTestUtils.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobAlimentationTest {


    @ClassRule
    public final static BatchProperties batchProperties = new BatchProperties().addTechnicalHsql()
            .addFunctionalHsql()
            .add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL.toString(), "1");

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Inject
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Inject
    private JdbcTemplate jdbcTemplate;

    @Test
    public void jobAlimentation() throws Exception {

        // Arrange
        File inputFile = temporaryFolder.newFile();
        Files.write(inputFile.toPath(), Arrays.asList("1,prenom1,nom1", "2,prenom2,nom2"));

        // Act
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder().addString(JobAlimentationConfiguration.INPUT_FILE_PARAMETER,
                        inputFile.getPath()).toJobParameters());

        // Assert
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        StepExecution stepExecution = Iterables.getOnlyElement(jobExecution.getStepExecutions());
        assertThat(stepExecution.getReadCount()).isEqualTo(2);
        assertThat(stepExecution.getWriteCount()).isEqualTo(2);

        assertThat(jdbcTemplate.queryForObject("select count(*) from USER", Integer.class)).isEqualTo(2);
    }

    @Test(expected = JobParametersInvalidException.class)
    public void jobAlimentation_SiParametreInvalide_AlorsException() throws Exception {
        jobLauncherTestUtils.launchJob();
    }

}
