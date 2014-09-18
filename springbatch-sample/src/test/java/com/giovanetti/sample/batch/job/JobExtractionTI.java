package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.JobExtractionTestConfiguration;
import com.giovanetti.support.batch.rule.BatchProperties;
import com.giovanetti.support.batch.rule.DBUnitRule;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TemporaryFolder;
import org.springframework.batch.core.launch.support.CommandLineJobRunner;
import org.springframework.batch.core.launch.support.ExitCodeMapper;

import javax.inject.Inject;

public class JobExtractionTI {


    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * @see <a href="http://www.stefan-birkner.de/system-rules/index.html">System Rules</a>
     */
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule
    @Inject
    public DBUnitRule dbUnitRule;

    @Test
    public void run() throws Exception {

        exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_COMPLETED);

        CommandLineJobRunner.main(
                new String[]{JobExtractionTestConfiguration.class.getName(), JobExtractionConfiguration.JOB_NAME.toString(), JobExtractionConfiguration.OUTPUT_FILE_PARAMETER + "=" + temporaryFolder
                        .newFile()
                        .getPath()});

    }

    @Test
    public void run_SiParametreInvalide_AlorsExitWithError() throws Exception {

        exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR);

        CommandLineJobRunner.main(
                new String[]{JobExtractionTestConfiguration.class.getName(), JobExtractionConfiguration.JOB_NAME.toString()});

    }
}
