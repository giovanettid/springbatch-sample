package com.giovanetti.sample.batch.job;

import com.giovanetti.sample.batch.item.User;
import com.giovanetti.support.batch.CustomBatchConfigurer;
import com.giovanetti.support.batch.ExternalConfiguration;
import com.giovanetti.support.batch.annotations.CommitInterval;
import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.giovanetti.support.batch.function.Consumer;
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

@Configuration
@EnableBatchProcessing
@Import({ExternalConfiguration.class, CustomBatchConfigurer.class})
public class JobExtractionConfiguration {

    final static String JOB_NAME = "extractionJob";
    private final static String STEP_NAME = "jdbcToFileStep";

    public final static String OUTPUT_FILE_PARAMETER = "output.file.path";
    private static final String PATH_OVERRIDE_BY_LATE_BINDING = null;

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
                .writer(writer(PATH_OVERRIDE_BY_LATE_BINDING))
                .build();
    }

    @Bean
    JdbcCursorItemReader<User> reader() {
        ParameterizedBeanPropertyRowMapper<User> mapper = ParameterizedBeanPropertyRowMapper.newInstance(User.class);

        JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select ID,PRENOM,NOM from USER");
        reader.setRowMapper(mapper);
        Consumer.acceptWithRawException(reader, JdbcCursorItemReader::afterPropertiesSet);
        return reader;
    }

    @Bean
    @StepScope
    FlatFileItemWriter<User> writer(@Value("#{jobParameters['" + OUTPUT_FILE_PARAMETER + "']}") String path) {
        BeanWrapperFieldExtractor<User> extractor = new BeanWrapperFieldExtractor<>();
        extractor.setNames(new String[]{"id", "prenom", "nom"});
        extractor.afterPropertiesSet();

        DelimitedLineAggregator<User> aggregator = new DelimitedLineAggregator<>();
        aggregator.setDelimiter(",");
        aggregator.setFieldExtractor(extractor);

        FlatFileItemWriter<User> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(path));
        writer.setLineAggregator(aggregator);
        Consumer.acceptWithRawException(writer, FlatFileItemWriter::afterPropertiesSet);
        return writer;
    }

}
