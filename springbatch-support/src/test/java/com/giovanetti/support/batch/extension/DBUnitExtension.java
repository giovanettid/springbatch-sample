package com.giovanetti.support.batch.extension;

import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBUnitExtension implements BeforeEachCallback, AfterEachCallback {

    private final static Logger LOG = LoggerFactory.getLogger(DBUnitExtension.class);

    @Inject
    @FunctionalDataSource
    private DataSource dataSource;

    @Inject
    private JdbcTemplate jdbcTemplate;

    private final String dataSetResource;

    public DBUnitExtension(String dataSetResource) {
        this.dataSetResource = dataSetResource;
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        executeInsertOperation();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        executeDeleteOperation();
    }

    public int rowCountFrom(String tableName) {
        return jdbcTemplate.queryForObject("select count(*) from "+tableName, Integer.class);
    }

    void executeInsertOperation() {
        executeDBUnitOperation(DatabaseOperation.TRANSACTION(DatabaseOperation.INSERT));
        LOG.info("execute insert operation with {} dataSet", dataSetResource);
    }

    void executeDeleteOperation() {
        executeDBUnitOperation(DatabaseOperation.TRANSACTION(DatabaseOperation.DELETE));
        LOG.info("execute delete operation with {} dataSet", dataSetResource);
    }

    private void executeDBUnitOperation(final DatabaseOperation databaseOperation) {
        Connection connection = null;
        try {
            connection = DataSourceUtils.getConnection(dataSource);
            DatabaseConnection dbUnitConnection = new DatabaseConnection(connection);
            dbUnitConnection.getConfig()
                    .setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqldbDataTypeFactory());
            databaseOperation.execute(dbUnitConnection, new FlatXmlDataSetBuilder().build(
                    DBUnitExtension.class.getClassLoader().getResourceAsStream(dataSetResource)));
        } catch (DatabaseUnitException | SQLException e) {
            throw new IllegalStateException(e);
        } finally {
            DataSourceUtils.releaseConnection(connection, dataSource);
        }
    }

}
