package com.giovanetti.sample.batch.job;

import com.giovanetti.support.batch.CustomBatchConfigurer;
import com.giovanetti.support.batch.annotations.CommitInterval;
import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.giovanetti.support.batch.ExternalConfiguration;
import com.giovanetti.support.batch.item.User;
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
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import javax.inject.Inject;
import javax.sql.DataSource;

//TODO : configure logs : switch to logback ?
//TODO : javadoc, changes.xml
//TODO : check violations
//TODO : check coverage
//TODO : check pom.xml dependencies

@Configuration
@EnableBatchProcessing
@Import({ExternalConfiguration.class, CustomBatchConfigurer.class})
public class JobExtractionConfiguration {

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
        return new DefaultJobParametersValidator(new String[]{OUTPUT_FILE_PARAMETER}, new String[]{});
    }

    @Bean(name = JOB_NAME)
    Job job() {
        return jobBuilders.get(JOB_NAME).validator(jobParametersValidator()).start(step()).build();
    }

    @Bean(name = STEP_NAME)
    Step step() {
        return stepBuilders.get(STEP_NAME)
                .transactionManager(new ResourcelessTransactionManager())
                .<User, User>chunk(commitInterval)
                .reader(reader())
                .writer(writer(null))
                .build();
    }

    @Bean
    JdbcCursorItemReader<User> reader() {
        JdbcCursorItemReader<User> jdbcCursorItemReader = new JdbcCursorItemReader<>();
        jdbcCursorItemReader.setDataSource(dataSource);
        jdbcCursorItemReader.setSql("select ID,PRENOM,NOM from USER");
        ParameterizedBeanPropertyRowMapper<User> rowMapper = ParameterizedBeanPropertyRowMapper.newInstance(User.class);
        jdbcCursorItemReader.setRowMapper(rowMapper);
        return jdbcCursorItemReader;
    }

    @Bean
    @StepScope
    FlatFileItemWriter<User> writer(@Value("#{jobParameters['" + OUTPUT_FILE_PARAMETER + "']}") String path) {
        FlatFileItemWriter<User> flatFileItemWriter = new FlatFileItemWriter<>();
        DelimitedLineAggregator<User> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        BeanWrapperFieldExtractor<User> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"id", "prenom", "nom"});
        lineAggregator.setFieldExtractor(fieldExtractor);
        flatFileItemWriter.setLineAggregator(lineAggregator);
        flatFileItemWriter.setResource(new FileSystemResource(path));
        return flatFileItemWriter;
    }

}
