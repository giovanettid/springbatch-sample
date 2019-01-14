package com.giovanetti.support.batch.extension;

import com.giovanetti.support.batch.ExternalConfiguration;
import com.giovanetti.support.batch.ExternalConfiguration.DataSourcePropertyKeys;
import com.giovanetti.support.batch.ExternalConfiguration.DataSourceType;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extension Junit pour fournir un fichier de propriétés temporaire pendant les tests.
 */
public class BatchProperties implements BeforeAllCallback, AfterAllCallback {

    private File batchPropertiesFile;

    final private List<String> lines = new ArrayList<>();

    public static BatchProperties getDefault() {
        return new BatchProperties().addTechnicalHsql()
                .addFunctionalHsql()
                .add(ExternalConfiguration.StepPropertyKeys.COMMIT_INTERVAL.toString(), "1");

    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        create();
    }

    @Override
    public void afterAll(ExtensionContext context) {
        System.clearProperty(ExternalConfiguration.BATCH_PROPERTIES_PATH);
    }

    public void create() {
        try {
            batchPropertiesFile = newFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        flush();
        System.setProperty(ExternalConfiguration.BATCH_PROPERTIES_PATH, "file:" + batchPropertiesFile.getPath());
    }

    private File newFile() throws IOException {
        return File.createTempFile("junit", null, createTemporaryFolder());
    }

    private File createTemporaryFolder() throws IOException {
        File createdFolder = File.createTempFile("junit", "");
        createdFolder.delete();
        createdFolder.mkdir();
        return createdFolder;
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
