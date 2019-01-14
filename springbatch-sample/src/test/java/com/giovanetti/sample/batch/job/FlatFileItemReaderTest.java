package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.JobAlimentationTestConfiguration;
import com.giovanetti.sample.batch.item.User;
import com.giovanetti.support.batch.extension.BatchProperties;
import com.giovanetti.support.batch.template.ItemReaderTemplate;
import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import static com.giovanetti.sample.batch.item.ItemHelper.listOf2Users;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith({SpringExtension.class, TemporaryFolderExtension.class})
@ContextConfiguration(classes = {JobAlimentationTestConfiguration.class})
@TestExecutionListeners(
        {DependencyInjectionTestExecutionListener.class, StepScopeTestExecutionListener.class, DirtiesContextTestExecutionListener.class})
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class FlatFileItemReaderTest {

    public static StepExecution getStepExecution() throws IOException {
        inputFile = TEMPORARY_FOLDER.createFile("newFile");
        return MetaDataInstanceFactory.createStepExecution(
                new JobParametersBuilder().addString(JobAlimentationConfiguration.INPUT_FILE_PARAMETER,
                        inputFile.getPath()).toJobParameters());
    }

    private static File inputFile;

    public static TemporaryFolder TEMPORARY_FOLDER;

    @RegisterExtension
    public static BatchProperties batchProperties = BatchProperties.getDefault();

    @Inject
    private ItemReaderTemplate<User> itemReader;

    @BeforeAll
    public static void prepare(TemporaryFolder temporaryFolder) {
        TEMPORARY_FOLDER = temporaryFolder;
    }

    @Test
    public void read() throws IOException {

        Files.write(inputFile.toPath(), Arrays.asList("1,prenom1,nom1", "2,prenom2,nom2"));

        assertThat(itemReader.readAll())
                .hasSize(2)
                .usingFieldByFieldElementComparator()
                .containsAll(listOf2Users());
    }

}
