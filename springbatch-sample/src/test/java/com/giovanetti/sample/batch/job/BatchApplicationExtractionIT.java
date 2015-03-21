package com.giovanetti.sample.batch.job;

import com.giovanetti.support.batch.configuration.GenericTestConfiguration;
import com.giovanetti.support.batch.rule.BatchProperties;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.ProvideSystemProperty;
import org.junit.rules.TemporaryFolder;
import org.springframework.batch.core.launch.support.ExitCodeMapper;

import java.io.IOException;

public class BatchApplicationExtractionIT {

    private final static String FUNCTIONAL_SCRIPT = "schema-functional.sql";

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @ClassRule
    public static ProvideSystemProperty systemProperty = new ProvideSystemProperty("job.name", JobExtractionConfiguration.JOB_NAME);

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * @see <a href="http://www.stefan-birkner.de/system-rules/index.html">System Rules</a>
     */
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void before() {
        GenericTestConfiguration.buildFunctionalDataSource(FUNCTIONAL_SCRIPT);
        GenericTestConfiguration.buildTechnicalDataSource();
    }

    @Test
    public void run() throws IOException {

        exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_COMPLETED);

        BatchApplication.main(
                new String[]{JobExtractionConfiguration.OUTPUT_FILE_PARAMETER + "=" + temporaryFolder.newFile()
                        .getPath()});

    }

    @Test(expected = IllegalStateException.class)
    public void run_SiParametreInvalide_AlorsExitWithError() {
        BatchApplication.main(new String[]{});
    }

}
