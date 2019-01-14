package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.JobExtractionTestConfiguration;
import com.giovanetti.support.batch.ExternalConfiguration;
import com.giovanetti.support.batch.extension.BatchProperties;
import com.giovanetti.support.batch.extension.DBUnitExtension;
import com.google.common.collect.Iterables;
import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({SpringExtension.class, TemporaryFolderExtension.class})
@ContextConfiguration(classes = {JobExtractionTestConfiguration.class, JobLauncherTestUtils.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class JobExtractionTest {


    @RegisterExtension
    public final static BatchProperties batchProperties = new BatchProperties().addTechnicalHsql()
            .addFunctionalHsql()
            .add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL.toString(), "1");

    public TemporaryFolder temporaryFolder;

    @RegisterExtension
    @Inject
    public DBUnitExtension dbUnitExtension;

    @Inject
    private JobLauncherTestUtils jobLauncherTestUtils;

    @BeforeEach
    public void prepare(TemporaryFolder temporaryFolder) {
        this.temporaryFolder = temporaryFolder;
    }

    @Test
    public void jobExtraction() throws Exception {

        // Act
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(
                new JobParametersBuilder().addString(JobExtractionConfiguration.OUTPUT_FILE_PARAMETER,
                        temporaryFolder.getRoot().getPath()).toJobParameters());

        // Assert
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        StepExecution stepExecution = Iterables.getOnlyElement(jobExecution.getStepExecutions());
        assertThat(stepExecution.getReadCount()).isEqualTo(2);
        assertThat(stepExecution.getWriteCount()).isEqualTo(2);

    }

    @Test
    public void jobExtraction_SiParametreInvalide_AlorsException() {
        assertThrows(JobParametersInvalidException.class, () -> jobLauncherTestUtils.launchJob());
    }

}
