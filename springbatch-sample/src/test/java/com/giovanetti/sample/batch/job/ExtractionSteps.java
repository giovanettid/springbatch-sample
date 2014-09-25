package com.giovanetti.sample.batch.job;


import com.giovanetti.sample.batch.item.User;
import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.DeleteAll;
import com.ninja_squad.dbsetup.operation.Insert;
import org.apache.commons.io.FileUtils;
import org.jbehave.core.annotations.*;
import org.jbehave.core.model.ExamplesTable;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.assertj.core.api.Assertions.assertThat;

public class ExtractionSteps {

    public static final String USER_TABLE = "USER";
    public static final String[] USER_COLUMNS = new String[]{"ID", "NOM", "PRENOM"};

    public static final DeleteAll DELETE_ALL_USER = deleteAllFrom(USER_TABLE);

    private File outputFile;

    private DataSourceDestination destination;

    @Inject
    @FunctionalDataSource
    private void setDataSourceDestination(DataSource dataSource) {
        destination = new DataSourceDestination((dataSource));
    }

    @Inject
    private JobLauncherTestUtils jobLauncherTestUtils;

    private List<User> users;

    @BeforeScenario
    public void createOutputFile() throws IOException {
        outputFile = createTempFile();
    }

    private File createTempFile() throws IOException {
        File tempFolder = File.createTempFile("junit", "");
        tempFolder.delete();
        tempFolder.mkdir();
        return File.createTempFile("junit", null, tempFolder);
    }

    @AfterScenario
    public void deleteOutputFile() throws IOException {
        FileUtils.deleteDirectory(outputFile.getParentFile());
    }

    @BeforeScenario
    public void setupDB() {
        new DbSetup(destination, DELETE_ALL_USER).launch();
    }

    @Given("les utilisateurs $users")
    public void setupUsers(ExamplesTable table) {
        users = table.getRows().stream()
                .map(row -> {
                    User user = new User();
                    user.setId(row.get("Id"));
                    user.setPrenom(row.get("Prenom"));
                    user.setNom(row.get("Nom"));
                    return user;
                })
                .collect(Collectors.toList());
    }

    @When("je charge les utilisateurs en base de données")
    public void loadUsers() {
        Insert.Builder insertBuilder = insertInto(USER_TABLE).columns(USER_COLUMNS);
        users.forEach(user -> insertBuilder.values(user.getId(), user.getNom(), user.getPrenom()));
        new DbSetup(destination, insertBuilder.build()).launch();
    }

    @When("j'execute le job d'extraction")
    public void executeExtractionJob() throws Exception {
        jobLauncherTestUtils.launchJob(
                new JobParametersBuilder().addString(JobExtractionConfiguration.OUTPUT_FILE_PARAMETER,
                        outputFile.getPath()).toJobParameters());
    }

    @Then("mon fichier de sortie contient les lignes $lines")
    public void checkUsers(List<String> lines) throws IOException {
        assertThat(Files.readAllLines(outputFile.toPath())).hasSize(lines.size()).containsAll(lines);
    }

}
