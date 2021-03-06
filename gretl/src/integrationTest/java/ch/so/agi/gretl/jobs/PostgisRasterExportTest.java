package ch.so.agi.gretl.jobs;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.testcontainers.containers.PostgisContainerProvider;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import ch.so.agi.gretl.util.GradleVariable;
import ch.so.agi.gretl.util.IntegrationTestUtil;
import ch.so.agi.gretl.util.IntegrationTestUtilSql;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;

import static org.junit.Assert.assertEquals;

public class PostgisRasterExportTest {
    static String WAIT_PATTERN = ".*database system is ready to accept connections.*\\s";
    
    @ClassRule
    public static PostgreSQLContainer postgres = 
        (PostgreSQLContainer) new PostgisContainerProvider()
        .newInstance().withDatabaseName("gretl")
        .withUsername(IntegrationTestUtilSql.PG_CON_DDLUSER)
        .withInitScript("init_postgresql.sql")
        .waitingFor(Wait.forLogMessage(WAIT_PATTERN, 2));

    @Test
    public void exportTiff() throws Exception {
        String jobPath = "src/integrationTest/jobs/PostgisRasterTiffExport/";
        String exportFileName = "export.tif";
        String targetFileName = "target.tif";
        
        // Delete existing file from previous test runs.
        File file = new File(jobPath, exportFileName);
        Files.deleteIfExists(file.toPath());

        GradleVariable[] gvs = {GradleVariable.newGradleProperty(IntegrationTestUtilSql.VARNAME_PG_CON_URI, postgres.getJdbcUrl())};
        IntegrationTestUtil.runJob(jobPath, gvs);
        
        long targetFileSize = new File(jobPath, targetFileName).length();
        long exportFileSize = new File(jobPath, exportFileName).length();

        assertEquals(targetFileSize, exportFileSize);
    }

    // At the moment this is more a test if GDAL drivers are enabled
    // in PostGIS.
    @Test
    public void exportGeoTiff() throws Exception {
        String jobPath = "src/integrationTest/jobs/PostgisRasterGeotiffExport/";
        String exportFileName = "export.tif";
        String targetFileName = "target.tif";

        // Delete existing file from previous test runs.
        File file = new File(jobPath, exportFileName);
        Files.deleteIfExists(file.toPath());
        
        GradleVariable[] gvs = {GradleVariable.newGradleProperty(IntegrationTestUtilSql.VARNAME_PG_CON_URI, postgres.getJdbcUrl())};
        IntegrationTestUtil.runJob(jobPath, gvs);

        long targetFileSize = new File(jobPath, targetFileName).length();
        long exportFileSize = new File(jobPath, exportFileName).length();
        
        assertEquals(targetFileSize, exportFileSize);
    }
}
