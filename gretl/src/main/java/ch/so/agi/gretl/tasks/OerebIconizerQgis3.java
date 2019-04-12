package ch.so.agi.gretl.tasks;

import java.awt.image.BufferedImage;
import java.net.URLDecoder;
import java.util.Map;

import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import ch.so.agi.gretl.api.Connector;
import ch.so.agi.gretl.logging.GretlLogger;
import ch.so.agi.gretl.logging.LogEnvironment;
import ch.so.agi.gretl.util.TaskUtil;
import ch.so.agi.oereb.OerebIconizer;

public class OerebIconizerQgis3 extends DefaultTask {
    protected GretlLogger log;

    @Input
    public String sldUrl = null;
    
    @Input 
    public String legendGraphicUrl = null;
    
    @Input
    public Connector database = null;
    
    @Input
    public String dbQTable = null;
    
    @Input
    public String typeCodeAttrName = null;
    
    @Input 
    public String symbolAttrName = null;

    @TaskAction
    public void createAndSaveSymbols() {
        log = LogEnvironment.getLogger(IliRepositorizer.class);

        if (sldUrl == null) {
            throw new IllegalArgumentException("sldUrl must not be null");
        }
        if (legendGraphicUrl == null) {
            throw new IllegalArgumentException("legendGraphicUrl must not be null");
        }
        if (database == null) {
            throw new IllegalArgumentException("database must not be null");
        }
        if (dbQTable == null) {
            throw new IllegalArgumentException("dbQTable must not be null");
        }
        if (typeCodeAttrName == null) {
            throw new IllegalArgumentException("typeCodeAttrName must not be null");
        }
        if (symbolAttrName == null) {
            throw new IllegalArgumentException("symbolAttrName must not be null");
        }
               
        try {
            OerebIconizer iconizer = new OerebIconizer();
            Map<String,BufferedImage> typeCodeSymbols =  iconizer.getSymbolsQgis3(sldUrl, legendGraphicUrl);
            int count = iconizer.updateSymbols(typeCodeSymbols, database.getDbUri(), database.getDbUser(), database.getDbPassword(), dbQTable, typeCodeAttrName, symbolAttrName);    
            log.info("Updated " + String.valueOf(count) + " column(s).");
        } catch (Exception e) {
            log.error("Exception in OerebIconizerQgis3 task.", e);
            GradleException ge = TaskUtil.toGradleException(e);
            throw ge;
        }
    }
}