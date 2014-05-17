package com.giovanetti;

import com.giovanetti.support.CustomBatchConfigurer;
import com.giovanetti.support.annotations.CommitInterval;
import com.giovanetti.support.annotations.FunctionalDataSource;
import com.giovanetti.support.ExternalConfiguration;
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
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import javax.inject.Inject;
import javax.sql.DataSource;

//TODO : configure logs : switch to logback ?
//TODO : reorganize package
//TODO : javadoc, changes.xml
//TODO : check violations
//TODO : check coverage
//TODO : check pom.xml dependencies

@Configuration
@EnableBatchProcessing
@Import({ExternalConfiguration.class, CustomBatchConfigurer.class})
public class JobConfiguration {

    public final static String JOB_NAME = "extractionJob";
    public final static String STEP_NAME = "jdbcToFileStep";

    public final static String OUTPUT_FILE_PARAMETER = "output.file.path";

    @Inject
    private JobBuilderFactory jobBuilders;

    @Inject
    private StepBuilderFactory stepBuilders;

    @Inject
    @FunctionalDataSource
    private DataSource dataSource;

    @Inject
    @CommitInterval
    private Integer commitInterval;

    @Bean
    JobParametersValidator jobParametersValidator() {
        return new DefaultJobParametersValidator(
                new String[]{OUTPUT_FILE_PARAMETER}, new String[]{});
    }

    @Bean(name = JOB_NAME)
    Job job() {
        return jobBuilders.get(JOB_NAME).validator(jobParametersValidator())
                .start(step()).build();
    }

    @Bean(name = STEP_NAME)
    Step step() {
        return stepBuilders
                .get(STEP_NAME)
                .transactionManager(new ResourcelessTransactionManager())
                .<String, String>chunk(commitInterval).reader(reader())
                .writer(writer(null)).build();
    }

    @Bean
    JdbcCursorItemReader<String> reader() {
        JdbcCursorItemReader<String> jdbcCursorItemReader = new JdbcCursorItemReader<>();
        jdbcCursorItemReader.setDataSource(dataSource);
        // TODO extract all columns => bean User
        jdbcCursorItemReader.setSql("select ID from USER");
        SingleColumnRowMapper<String> rowMapper = new SingleColumnRowMapper<>();
        rowMapper.setRequiredType(String.class);
        jdbcCursorItemReader.setRowMapper(rowMapper);
        return jdbcCursorItemReader;
    }

    @Bean
    @StepScope
    FlatFileItemWriter<String> writer(@Value("#{jobParameters['"
            + OUTPUT_FILE_PARAMETER + "']}") String path) {
        FlatFileItemWriter<String> flatFileItemWriter = new FlatFileItemWriter<>();
        DelimitedLineAggregator<String> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        flatFileItemWriter.setLineAggregator(lineAggregator);
        flatFileItemWriter.setResource(new FileSystemResource(path));
        return flatFileItemWriter;
    }

}
