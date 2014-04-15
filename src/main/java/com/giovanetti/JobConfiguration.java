package com.giovanetti;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.giovanetti.annotations.FunctionalDataSource;

//TODO : configure logs : switch logback ?
//TODO : push to github
//TODO : re package
//TODO : javadoc, changes.xml
//TODO : checkstyle PMD
//TODO : coverage
//TODO : check pom.xml

@Configuration
@EnableBatchProcessing
@Import({ ExternalConfiguration.class, BatchConfigurerImpl.class })
public class JobConfiguration {

	public final static String JOB_NAME = "extractionJob";
	public final static String STEP_NAME = "jdbcToFileStep";

	public final static String OUTPUT_FILE_PARAMETER = "output.file.path";

	@Inject
	private Environment environment;

	@Inject
	private JobBuilderFactory jobBuilders;

	@Inject
	private StepBuilderFactory stepBuilders;

	@Inject
	@FunctionalDataSource
	private DataSource dataSource;

	@Bean
	public JobParametersValidator jobParametersValidator() {
		return new DefaultJobParametersValidator(
				new String[] { OUTPUT_FILE_PARAMETER }, new String[] {});
	}

	@Bean(name = JOB_NAME)
	public Job job() {
		return jobBuilders.get(JOB_NAME).validator(jobParametersValidator())
				.start(step()).build();
	}

	@Bean(name = STEP_NAME)
	public Step step() {
		return stepBuilders
				.get(STEP_NAME)
				.transactionManager(new ResourcelessTransactionManager())
				.<String, String> chunk(
						Integer.parseInt(environment
								.getProperty(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL
										.toString()))).reader(reader())
				.writer(writer(null)).build();
	}

	@Bean
	public JdbcCursorItemReader<String> reader() {
		JdbcCursorItemReader<String> jdbcCursorItemReader = new JdbcCursorItemReader<String>();
		jdbcCursorItemReader.setDataSource(dataSource);
		// TODO extract all columns => bean User
		jdbcCursorItemReader.setSql("select ID from USER");
		jdbcCursorItemReader.setRowMapper(new SingleColumnRowMapper<String>());
		return jdbcCursorItemReader;
	}

	@Bean
	@StepScope
	public FlatFileItemWriter<String> writer(@Value("#{jobParameters['"
			+ OUTPUT_FILE_PARAMETER + "']}") String path) {
		FlatFileItemWriter<String> flatFileItemWriter = new FlatFileItemWriter<String>();
		DelimitedLineAggregator<String> lineAggregator = new DelimitedLineAggregator<String>();
		lineAggregator.setDelimiter(",");
		flatFileItemWriter.setLineAggregator(lineAggregator);
		flatFileItemWriter.setResource(new FileSystemResource(path));
		return flatFileItemWriter;
	}

}
