package com.giovanetti;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestExecutionListener;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;

import com.giovanetti.support.BatchProperties;
import com.giovanetti.support.TestUtilsConfiguration;

//TODO : classe support de test ? factoriser job param builder avec autres classes de test
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestUtilsConfiguration.class })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		StepScopeTestExecutionListener.class })
// TODO : check DirtyContextListener
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class FlatFileWriterTest {

	public StepExecution getStepExecution() {

		JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
		jobParametersBuilder.addString(JobConfiguration.OUTPUT_FILE_PARAMETER,
				outputFilePath);

		StepExecution execution = MetaDataInstanceFactory
				.createStepExecution(jobParametersBuilder.toJobParameters());

		return execution;
	}

	@ClassRule
	public static BatchProperties batchProperties = new BatchProperties();

	@Inject
	private FlatFileItemWriter<String> itemWriter;

	private String outputFilePath = "target/out/output"
			+ System.currentTimeMillis() + ".txt";

	@BeforeClass
	public static void setupClass() throws IOException {
		batchProperties
				.addTechnicalHsql()
				.addFunctionalHsql()
				.add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL
						.toString(),
						"1").flush();
	}

	@Before
	public void setup() {
		itemWriter.open(new ExecutionContext());
	}

	@After
	public void tearDown() {
		itemWriter.close();
	}

	@Test
	public void flatFileWriterOK() throws Exception {

		// Arrange
		List<String> items = new ArrayList<String>();
		items.add("1");
		items.add("2");

		// Act
		itemWriter.write(items);

		// Assert
		List<String> lines = FileUtils.readLines(new File(outputFilePath));
		assertThat(lines).hasSize(2).contains("1", "2");
	}

}
