package com.giovanetti.support.configuration;

import com.giovanetti.support.annotations.FunctionalDataSource;
import com.giovanetti.support.annotations.TechnicalDataSource;
import org.springframework.batch.test.JobLauncherTestUtils;
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
    public DataSourceInitializer technicalDataSourceInitializer(
            @TechnicalDataSource DataSource technicalDatasource) {
        return createDataSourceInitializer(
                technicalDatasource,
                createDatabasePopulator(
                        HSQL_SCRIPTS)
        );
    }

    @Bean
    public JobLauncherTestUtils jobLauncherTestUtils() {
        return new JobLauncherTestUtils();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(
            @FunctionalDataSource DataSource dataSource) {
        return new JdbcTemplate(dataSource);
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
