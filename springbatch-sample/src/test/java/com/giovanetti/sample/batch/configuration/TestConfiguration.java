package com.giovanetti.sample.batch.configuration;

import com.giovanetti.sample.batch.job.JobExtractionConfiguration;
import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.giovanetti.support.batch.configuration.GenericTestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

import javax.sql.DataSource;

@Configuration
@Import({JobExtractionConfiguration.class, GenericTestConfiguration.class})
public class TestConfiguration {

    public final static String[] SQL_SCRIPTS = {"schema-functional.sql", "users.sql"};

    @Bean
    public DataSourceInitializer functionalDataSourceInitializer(
            @FunctionalDataSource DataSource functionalDataSource) {
        return GenericTestConfiguration.createDataSourceInitializer(functionalDataSource,
                GenericTestConfiguration.createDatabasePopulator(SQL_SCRIPTS));
    }

}
