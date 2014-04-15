package com.giovanetti.support;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.rules.ExternalResource;

import com.giovanetti.ExternalConfiguration.DataSourceType;
import com.giovanetti.ExternalConfiguration.DataSourcePropertyKeys;

public class BatchProperties extends ExternalResource {

	private final static String TARGET_FOLDER_PATH = "target" + File.separator
			+ "test-classes";

	private final static String BATCH_PROPERTIES_NAME = "batch.properties";

	private File file;

	List<String> lines = new ArrayList<String>();

	public File getFile() {
		return file;
	}

	@Override
	protected void before() throws Throwable {
		create();
	}

	@Override
	protected void after() {
		delete();
	}

	protected void create() throws IOException {
		file = new File(TARGET_FOLDER_PATH + File.separator
				+ BATCH_PROPERTIES_NAME);
	}

	protected void delete() {
		file.delete();
	}

	public BatchProperties addFunctionalHsql() {
		this.addHsql(DataSourceType.FUNCTIONAL);
		return this;
	}

	public BatchProperties addTechnicalHsql() {
		this.addHsql(DataSourceType.TECHNICAL);
		return this;
	}

	public BatchProperties addHsql(DataSourceType dataSourceType) {
		this.add(DataSourcePropertyKeys.DRIVER_CLASS.name(dataSourceType),
				"org.hsqldb.jdbcDriver")
				.add(DataSourcePropertyKeys.URL.name(dataSourceType),
						"jdbc:hsqldb:mem:" + dataSourceType)
				.add(DataSourcePropertyKeys.USERNAME.name(dataSourceType), "sa")
				.add(DataSourcePropertyKeys.PASSWORD.name(dataSourceType), "");
		return this;
	}

	public BatchProperties add(String key, String value) {
		lines.add(key + "=" + value);
		return this;
	}

	public void flush() throws IOException {
		FileUtils.writeLines(file, lines);
	}

}
