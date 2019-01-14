package com.giovanetti.sample.batch.job;

import com.ginsberg.junit.exit.ExpectSystemExitWithStatus;
import com.giovanetti.support.batch.configuration.GenericTestConfiguration;
import com.giovanetti.support.batch.extension.BatchProperties;
import io.github.glytching.junit.extension.folder.TemporaryFolder;
import io.github.glytching.junit.extension.folder.TemporaryFolderExtension;
import io.github.glytching.junit.extension.system.SystemProperty;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.batch.core.launch.support.ExitCodeMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith({SpringExtension.class, TemporaryFolderExtension.class})
@SystemProperty(name = "job.name", value = JobExtractionConfiguration.JOB_NAME)
public class BatchApplicationExtractionIT {

    private final static String FUNCTIONAL_SCRIPT = "schema-functional.sql";

    @RegisterExtension
    public static BatchProperties batchProperties = BatchProperties.getDefault();

    private TemporaryFolder temporaryFolder;

    @BeforeEach
    public void prepare(TemporaryFolder temporaryFolder) {
        this.temporaryFolder = temporaryFolder;
        GenericTestConfiguration.buildFunctionalDataSource(FUNCTIONAL_SCRIPT);
        GenericTestConfiguration.buildTechnicalDataSource();
    }

    @Test
    @ExpectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_COMPLETED)
    public void run() throws IOException {
        BatchApplication.main(
                new String[]{JobExtractionConfiguration.OUTPUT_FILE_PARAMETER + "=" + temporaryFolder.createFile("newFile")
                        .getPath()});

    }

    @Test
    public void run_SiParametreInvalide_AlorsExitWithError() {
        assertThrows(IllegalStateException.class, () -> BatchApplication.main(new String[]{}));
    }

}
