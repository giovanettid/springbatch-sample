package com.giovanetti.sample.batch.job.cucumber;


import com.giovanetti.sample.batch.configuration.JobExtractionTestConfiguration;
import com.giovanetti.sample.batch.item.User;
import com.giovanetti.sample.batch.job.JobExtractionConfiguration;
import com.giovanetti.support.batch.annotations.FunctionalDataSource;
import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.DeleteAll;
import com.ninja_squad.dbsetup.operation.Insert;
import cucumber.api.DataTable;
import cucumber.api.PendingException;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.io.FileUtils;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static com.ninja_squad.dbsetup.Operations.deleteAllFrom;
import static com.ninja_squad.dbsetup.Operations.insertInto;
import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = {JobExtractionTestConfiguration.class, JobLauncherTestUtils.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
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

    @Before
    public void setupDB() {
        new DbSetup(destination, DELETE_ALL_USER).launch();
    }

    @Before
    public void createOutputFile() throws IOException {
        outputFile = createTempFile();
    }

    private File createTempFile() throws IOException {
        File tempFolder = File.createTempFile("junit", "");
        tempFolder.delete();
        tempFolder.mkdir();
        return File.createTempFile("junit", null, tempFolder);
    }

    @After
    public void deleteOutputFile() throws IOException {
        FileUtils.deleteDirectory(outputFile.getParentFile());
    }

    @Given("^les utilisateurs$")
    public void les_utilisateurs(DataTable table) {
        users = table.asList(User.class);
    }

    @When("^je charge les utilisateurs en base de données$")
    public void je_charge_les_utilisateurs_en_base_de_données() {
        Insert.Builder insertBuilder = insertInto(USER_TABLE).columns(USER_COLUMNS);
        users.forEach(user -> insertBuilder.values(user.getId(), user.getNom(), user.getPrenom()));
        new DbSetup(destination, insertBuilder.build()).launch();
    }

    @When("^j'execute le job d'extraction$")
    public void j_execute_le_job_d_extraction() throws Exception {
        jobLauncherTestUtils.launchJob(
                new JobParametersBuilder().addString(JobExtractionConfiguration.OUTPUT_FILE_PARAMETER,
                        outputFile.getPath()).toJobParameters());
    }

    @Then("^mon fichier de sortie contient les lignes$")
    public void mon_fichier_de_sortie_contient_les_lignes(List<String> lines) throws IOException {
        assertThat(Files.readAllLines(outputFile.toPath())).hasSize(lines.size()).containsAll(lines);
    }

}
