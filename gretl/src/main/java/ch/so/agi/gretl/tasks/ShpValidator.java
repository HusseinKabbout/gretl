package ch.so.agi.gretl.tasks;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ioxwkf.shp.ShapeReader;
import ch.so.agi.gretl.logging.GretlLogger;
import ch.so.agi.gretl.logging.LogEnvironment;
import ch.so.agi.gretl.tasks.impl.AbstractValidatorTask;
import ch.so.agi.gretl.tasks.impl.ShpValidatorImpl;

import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.util.ArrayList;
import java.util.List;

public class ShpValidator extends AbstractValidatorTask {
    private GretlLogger log;
    @Input
    @Optional
    public String encoding = null;

    @TaskAction
    public void validate() {
        log = LogEnvironment.getLogger(ShpValidator.class);

        if (dataFiles == null) {
            return;
        }
        FileCollection dataFilesCollection=null;
        if(dataFiles instanceof FileCollection) {
            dataFilesCollection=(FileCollection)dataFiles;
        }else {
            dataFilesCollection=getProject().files(dataFiles);
        }
        if (dataFilesCollection == null || dataFilesCollection.isEmpty()) {
            return;
        }
        List<String> files = new ArrayList<String>();
        for (java.io.File fileObj : dataFilesCollection) {
            String fileName = fileObj.getPath();
            files.add(fileName);
        }

        Settings settings = new Settings();
        initSettings(settings);
        if (encoding != null) {
            settings.setValue(ShapeReader.ENCODING, encoding);
        }

        validationOk = new ShpValidatorImpl().validate(files.toArray(new String[files.size()]), settings);
        if (!validationOk && failOnError) {
            throw new TaskExecutionException(this, new Exception("validation failed"));
        }
    }
}
