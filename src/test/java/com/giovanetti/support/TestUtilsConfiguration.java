package com.giovanetti.support;

import javax.sql.DataSource;

import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import com.giovanetti.JobConfiguration;
import com.giovanetti.support.annotations.FunctionalDataSource;
import com.giovanetti.support.annotations.TechnicalDataSource;

@Configuration
@Import({ JobConfiguration.class })
public class TestUtilsConfiguration {

	//TODO : externalize literals
	@Bean
	public DataSourceInitializer technicalDataSourceInitializer(
			@TechnicalDataSource DataSource technicalDatasource) {
		// EmbeddedDatabaseBuilder is cool but build two things : dataSource and
		// init scripts !
		return createDataSourceInitializer(
				technicalDatasource,
				createDatabasePopulator(
						"org/springframework/batch/core/schema-drop-hsqldb.sql",
						"org/springframework/batch/core/schema-hsqldb.sql"));
	}

	@Bean
	public DataSourceInitializer functionalDataSourceInitializer(
			@FunctionalDataSource DataSource functionalDatasource) {
		return createDataSourceInitializer(functionalDatasource,
				createDatabasePopulator("schema-functional.sql", "users.sql"));
	}

	// TODO : move database util generic
	private ResourceDatabasePopulator createDatabasePopulator(String... paths) {
		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
		for (String path : paths) {
			databasePopulator.addScript(new ClassPathResource(path));
		}
		return databasePopulator;

	}

	private DataSourceInitializer createDataSourceInitializer(
			DataSource dataSource,
			ResourceDatabasePopulator resourceDatabasePopulator) {
		DataSourceInitializer dataSourceInitializer = new DataSourceInitializer();
		dataSourceInitializer.setDataSource(dataSource);
		dataSourceInitializer.setDatabasePopulator(resourceDatabasePopulator);
		return dataSourceInitializer;
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

}
