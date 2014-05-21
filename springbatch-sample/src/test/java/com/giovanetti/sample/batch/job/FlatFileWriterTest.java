package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.TestConfiguration;
import com.giovanetti.support.batch.function.FlatFileItemWriterConsumer;
import com.giovanetti.sample.batch.item.User;
import com.giovanetti.support.batch.rule.BatchProperties;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.inject.Inject;
import java.io.IOException;

import static com.giovanetti.sample.batch.item.ItemHelper.listOf2UsersMapFromDB;
import static java.nio.file.Files.readAllLines;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@TestExecutionListeners(
        {DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class FlatFileWriterTest {

    public static StepExecution getStepExecution() {
        return MetaDataInstanceFactory.createStepExecution(
                new JobParametersBuilder().addString(JobExtractionConfiguration.OUTPUT_FILE_PARAMETER,
                        outputFile.getRoot().getPath()).toJobParameters()
        );
    }

    @ClassRule
    public final static TemporaryFolder outputFile = new TemporaryFolder();

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @Inject
    private FlatFileItemWriter<User> itemWriter;

    @Test
    public void write() throws IOException {

        // Act
        FlatFileItemWriterConsumer.accept(itemWriter, listOf2UsersMapFromDB(), itemWriter::write);

        // Assert
        assertThat(readAllLines(outputFile.getRoot().toPath()))
                .hasSize(2)
                .contains("1,prenom1,nom1", "2,prenom2,nom2");

    }

}
