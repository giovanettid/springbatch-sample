package com.giovanetti.sample.batch.configuration;

import com.giovanetti.sample.batch.item.User;
import com.giovanetti.sample.batch.job.JobExtractionConfiguration;
import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.giovanetti.support.batch.configuration.GenericTestConfiguration;
import com.giovanetti.support.batch.extension.DBUnitExtension;
import com.giovanetti.support.batch.template.ItemReaderTemplate;
import com.giovanetti.support.batch.template.ItemWriterTemplate;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;

import javax.sql.DataSource;

@Configuration
@Import({JobExtractionConfiguration.class, GenericTestConfiguration.class})
public class JobExtractionTestConfiguration {

    private final static String FUNCTIONAL_SCRIPT = "schema-functional.sql";

    private final static String XML_DATASET = "users.xml";

    @Bean
    public DBUnitExtension dbUnitRule() {
        return new DBUnitExtension(XML_DATASET);
    }

    @Bean
    ItemReaderTemplate<User> itemReaderTemplate(JdbcCursorItemReader<User> itemReader) {
        return new ItemReaderTemplate<>(itemReader);
    }

    @Bean
    ItemWriterTemplate<User> itemWriterTemplate(FlatFileItemWriter<User> itemWriter) {
        return new ItemWriterTemplate<>(itemWriter);
    }

    @Bean
    DataSourceInitializer functionalDataSourceInitializer(@FunctionalDataSource DataSource functionalDataSource) {
        return GenericTestConfiguration.createDataSourceInitializer(functionalDataSource,
                GenericTestConfiguration.createDatabasePopulator(FUNCTIONAL_SCRIPT));
    }

}
