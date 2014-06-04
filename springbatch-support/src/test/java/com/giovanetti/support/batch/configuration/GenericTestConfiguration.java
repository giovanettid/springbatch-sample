package com.giovanetti.support.batch.configuration;

import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.giovanetti.support.batch.annotations.TechnicalDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class GenericTestConfiguration {

    private final static String[] HSQL_SCRIPTS = new String[]{"org/springframework/batch/core/schema-drop-hsqldb.sql",
            "org/springframework/batch/core/schema-hsqldb.sql"};

    @Bean
    public JdbcTemplate jdbcTemplate(
            @FunctionalDataSource DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSourceInitializer technicalDataSourceInitializer(
            @TechnicalDataSource DataSource technicalDatasource) {
        return createDataSourceInitializer(
                technicalDatasource,
                createDatabasePopulator(
                        HSQL_SCRIPTS)
        );
    }

    public static ResourceDatabasePopulator createDatabasePopulator(String... paths) {
        ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
        for (String path : paths) {
            databasePopulator.addScript(new ClassPathResource(path));
        }
        return databasePopulator;
    }

    public static DataSourceInitializer createDataSourceInitializer(
            DataSource dataSource,
            ResourceDatabasePopulator resourceDatabasePopulator) {
        DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
        dataSourceInitializer.setDataSource(dataSource);
        dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
        return dataSourceInitializer;
    }

}
