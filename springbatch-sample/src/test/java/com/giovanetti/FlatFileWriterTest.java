package com.giovanetti;

import com.giovanetti.support.function.FlatFileItemWriterConsumer;
import com.giovanetti.support.rule.BatchProperties;
import org.assertj.core.util.Lists;
import org.junit.ClassRule;
import org.junit.Test;
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

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;

import static java.nio.file.Files.readAllLines;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfiguration.class})
@TestExecutionListeners({DependencyInjectionTestExecutionListener.class,
        StepScopeTestExecutionListener.class})
// TODO : check DirtyContextListener
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class FlatFileWriterTest {

    private static final String OUTPUT_FILE_PATH = "target/out/output"
            + System.currentTimeMillis() + ".txt";

    public static StepExecution getStepExecution() {
        return MetaDataInstanceFactory
                .createStepExecution(new JobParametersBuilder()
                        .addString(JobConfiguration.OUTPUT_FILE_PARAMETER, OUTPUT_FILE_PATH)
                        .toJobParameters());
    }

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @Inject
    private FlatFileItemWriter<String> itemWriter;

    @Test
    public void write() throws IOException {

        // Act
        FlatFileItemWriterConsumer.accept(itemWriter, Lists.newArrayList("1", "2"), itemWriter::write);

        // Assert
        assertThat(readAllLines(Paths.get(OUTPUT_FILE_PATH)))
                .hasSize(2)
                .containsExactly("1", "2");

    }

}
