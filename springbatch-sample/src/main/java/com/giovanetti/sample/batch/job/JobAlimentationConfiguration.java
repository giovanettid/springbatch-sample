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
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.inject.Inject;
import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@Import({ExternalConfiguration.class, CustomBatchConfigurer.class})
public class JobAlimentationConfiguration {

    final static String INPUT_FILE_PARAMETER = "input.file.path";
    private static final String PATH_OVERRIDE_BY_LATE_BINDING = null;

    final static String JOB_NAME = "alimentationJob";
    private final static String STEP_NAME = "fileToJdbcStep";

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
    JobParametersValidator jobAlimentationParametersValidator() {
        return new DefaultJobParametersValidator(new String[]{INPUT_FILE_PARAMETER}, new String[]{});
    }

    @Bean(name = JOB_NAME)
    Job job() {
        return jobBuilders.get(JOB_NAME).validator(jobAlimentationParametersValidator()).start(step()).build();
    }

    @Bean(name = STEP_NAME)
    Step step() {
        return stepBuilders.get(STEP_NAME)
                .transactionManager(new DataSourceTransactionManager(dataSource))
                .<User, User>chunk(commitInterval)
                .reader(fileReader(PATH_OVERRIDE_BY_LATE_BINDING))
                .writer(jdbcWriter())
                .build();
    }

    @Bean
    @StepScope
    FlatFileItemReader<User> fileReader(@Value("#{jobParameters['" + INPUT_FILE_PARAMETER + "']}") String path) {
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer(",");
        tokenizer.setNames(new String[]{"id", "prenom", "nom"});

        BeanWrapperFieldSetMapper<User> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(User.class);
        Consumer.acceptWithRawException(fieldSetMapper, BeanWrapperFieldSetMapper::afterPropertiesSet);

        DefaultLineMapper<User> mapper = new DefaultLineMapper<>();
        mapper.setLineTokenizer(tokenizer);
        mapper.setFieldSetMapper(fieldSetMapper);
        mapper.afterPropertiesSet();

        FlatFileItemReader<User> reader = new FlatFileItemReader<>();
        reader.setResource(new FileSystemResource(path));
        reader.setLineMapper(mapper);
        Consumer.acceptWithRawException(reader, FlatFileItemReader::afterPropertiesSet);
        return reader;
    }

    @Bean
    JdbcBatchItemWriter<User> jdbcWriter() {
        JdbcBatchItemWriter<User> writer = new JdbcBatchItemWriter<>();
        writer.setDataSource(dataSource);
        writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        writer.setSql("insert into USER(ID,PRENOM,NOM) values (:id,:prenom,:nom)");
        writer.afterPropertiesSet();
        return writer;
    }


}
