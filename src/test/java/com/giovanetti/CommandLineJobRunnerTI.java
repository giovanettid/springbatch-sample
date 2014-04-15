package com.giovanetti;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.springframework.batch.core.launch.support.CommandLineJobRunner;
import org.springframework.batch.core.launch.support.ExitCodeMapper;

import com.giovanetti.support.BatchProperties;
import com.giovanetti.support.TestUtilsConfiguration;

public class CommandLineJobRunnerTI {

	@ClassRule
	public static BatchProperties batchProperties = new BatchProperties();

	@BeforeClass
	public static void setupClass() throws IOException {
		batchProperties
				.addTechnicalHsql()
				.addFunctionalHsql()
				.add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL
						.toString(),
						"1").flush();
	}

	// TODO : special thanks
	// http://www.stefan-birkner.de/system-rules/index.html
	@Rule
	public final ExpectedSystemExit exit = ExpectedSystemExit.none();

	@Test
	public void run() throws Exception {

		exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_COMPLETED);

		CommandLineJobRunner.main(new String[] {
				TestUtilsConfiguration.class.getName(),
				JobConfiguration.JOB_NAME.toString(),
				JobConfiguration.OUTPUT_FILE_PARAMETER + "=target/out/output"
						+ System.currentTimeMillis() + ".txt" });

	}

	@Test
	public void runKO() throws Exception {

		exit.expectSystemExitWithStatus(ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR);

		CommandLineJobRunner.main(new String[] {
				TestUtilsConfiguration.class.getName(),
				JobConfiguration.JOB_NAME.toString() });

	}
}
