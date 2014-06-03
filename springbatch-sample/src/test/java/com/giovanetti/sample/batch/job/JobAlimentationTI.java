package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.configuration.JobAlimentationTestConfiguration;
import com.giovanetti.support.batch.rule.BatchProperties;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.rules.TemporaryFolder;
import org.springframework.batch.core.launch.support.CommandLineJobRunner;
import org.springframework.batch.core.launch.support.ExitCodeMapper;

public class JobAlimentationTI {

    @ClassRule
    public final static TemporaryFolder inputRule = new TemporaryFolder();

    @ClassRule
    public final static BatchProperties batchProperties = BatchProperties.getDefault();

    /**
     * @see <a href="http://www.stefan-birkner.de/system-rules/index.html">System Rules</a>
     */
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Test
    public void run() throws Exception {

        exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_COMPLETED);

        CommandLineJobRunner.main(
                new String[]{JobAlimentationTestConfiguration.class.getName(), JobAlimentationConfiguration.JOB_NAME.toString(), JobAlimentationConfiguration.INPUT_FILE_PARAMETER + "=" + inputRule
                        .newFile()
                        .getPath()});

    }

    @Test
    public void run_SiParametreInvalide_AlorsExitWithError() throws Exception {

        exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR);

        CommandLineJobRunner.main(
                new String[]{JobAlimentationTestConfiguration.class.getName(), JobAlimentationConfiguration.JOB_NAME.toString()});

    }
}
