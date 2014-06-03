package com.giovanetti.sample.batch.configuration;

import com.giovanetti.sample.batch.item.User;
import com.giovanetti.sample.batch.job.JobAlimentationConfiguration;
import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.giovanetti.support.batch.configuration.GenericTestConfiguration;
import com.giovanetti.support.batch.template.ItemReaderTemplate;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

import javax.sql.DataSource;

@Configuration
@Import({JobAlimentationConfiguration.class, GenericTestConfiguration.class})
public class JobAlimentationTestConfiguration {

    private final static String FUNCTIONAL_SCRIPT = "schema-functional.sql";

    @Bean
    ItemReaderTemplate<User> itemReaderTemplate(FlatFileItemReader<User> itemReader) {
        return new ItemReaderTemplate<>(itemReader);
    }

    @Bean
    DataSourceInitializer functionalDataSourceInitializer(@FunctionalDataSource DataSource functionalDataSource) {
        return GenericTestConfiguration.createDataSourceInitializer(functionalDataSource,
                GenericTestConfiguration.createDatabasePopulator(FUNCTIONAL_SCRIPT));
    }

}
