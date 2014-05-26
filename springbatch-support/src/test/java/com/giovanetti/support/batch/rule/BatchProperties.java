package com.giovanetti.support.batch.rule;

import com.giovanetti.support.batch.ExternalConfiguration;
import com.giovanetti.support.batch.ExternalConfiguration.DataSourcePropertyKeys;
import com.giovanetti.support.batch.ExternalConfiguration.DataSourceType;
import org.apache.commons.io.FileUtils;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Rule Junit pour fournir un fichier de propriétés temporaire pendant les tests.
 */
public class BatchProperties extends TemporaryFolder {

    private final BatchPropertiesPathSystemProperty pathSystemProperty = new BatchPropertiesPathSystemProperty();

    private File batchPropertiesFile;

    final private List<String> lines = new ArrayList<>();

    public static BatchProperties getDefault() {
        return new BatchProperties().addTechnicalHsql()
                .addFunctionalHsql()
                .add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL.toString(), "1");

    }

    @Override
    protected void before() throws Throwable {
        super.before();
        batchPropertiesFile = newFile();
        flush();
        pathSystemProperty.set(batchPropertiesFile.getPath());
        pathSystemProperty.before();
    }

    public BatchProperties addFunctionalHsql() {
        this.addHsql(DataSourceType.FUNCTIONAL);
        return this;
    }

    public BatchProperties addTechnicalHsql() {
        return this.addHsql(DataSourceType.TECHNICAL);
    }

    BatchProperties addHsql(DataSourceType dataSourceType) {
        return this.add(DataSourcePropertyKeys.DRIVER_CLASS.name(dataSourceType), "org.hsqldb.jdbcDriver")
                .add(DataSourcePropertyKeys.URL.name(dataSourceType), "jdbc:hsqldb:mem:" + dataSourceType)
                .add(DataSourcePropertyKeys.USERNAME.name(dataSourceType), "sa")
                .add(DataSourcePropertyKeys.PASSWORD.name(dataSourceType), "");
    }

    public BatchProperties add(String key, String value) {
        lines.add(key + "=" + value);
        return this;
    }

    private void flush() {
        try {
            FileUtils.writeLines(batchPropertiesFile, lines);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
