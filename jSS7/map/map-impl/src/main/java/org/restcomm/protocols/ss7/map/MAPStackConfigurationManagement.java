package org.restcomm.protocols.ss7.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 * read/write MAP layer configuration *.xml file
 */
public class MAPStackConfigurationManagement {

    private static final String PERSIST_FILE_NAME = "management.xml";
    private static final String MAP_MANAGEMENT_PERSIST_DIR_KEY = "mapmanagement.persist.dir";
    private static final String USER_DIR_KEY = "user.dir";
    private static final String DEFAULT_CONFIG_FILE_NAME = "MapStack";

    private static final XmlMapper xmlMapper = MAPJacksonHelper.getXmlMapper();
    private static MAPStackConfigurationManagement instance = new MAPStackConfigurationManagement();

    private String persistFile;
    private String configFileName = DEFAULT_CONFIG_FILE_NAME;
    private String persistDir = null;

    private int shortTimer = 10000;
    private int mediumTimer = 30000;
    private int longTimer = 600000;

    private MAPStackConfigurationManagement() {
    }

    public static MAPStackConfigurationManagement getInstance() {
        return instance;
    }

    public void setPersistDir(String persistDir) {
        this.persistDir = persistDir;
        this.setPersistFile();
    }

    private void setPersistFile() {
        StringBuilder persistFileBuilder = new StringBuilder();

        if (persistDir != null) {
            persistFileBuilder.append(persistDir).append(File.separator).append(this.configFileName).append("_").append(PERSIST_FILE_NAME);
        } else {
            persistFileBuilder.append(System.getProperty(MAP_MANAGEMENT_PERSIST_DIR_KEY, System.getProperty(USER_DIR_KEY))).append(File.separator).append(this.configFileName)
                    .append("_").append(PERSIST_FILE_NAME);
        }
        this.persistFile = persistFileBuilder.toString();
    }

    /**
     * Persist
     */
    public void store() {
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(persistFile), StandardCharsets.UTF_8)) {
            xmlMapper.writeValue(writer, this);
        } catch (Exception e) {
            System.err.println(String.format("Error while persisting the MAP Resource state in file=%s", persistFile));
            e.printStackTrace();
        }
    }

    /**
     * Load and create LinkSets and Link from persisted file
     * <p>
     * load() is called from MAPStackImpl
     */
    public void load() {
        try {
            setPersistFile();
            File file = new File(persistFile);
            if (!file.exists()) {
                // File doesn't exist, use defaults
                return;
            }
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                MAPStackConfigurationManagement loaded = xmlMapper.readValue(reader, MAPStackConfigurationManagement.class);
                this.shortTimer = loaded.shortTimer;
                this.mediumTimer = loaded.mediumTimer;
                this.longTimer = loaded.longTimer;
            }
        } catch (FileNotFoundException e) {
            // File not found, use defaults
            System.err.println(String.format("MAP Resource state file not found=%s, using defaults", persistFile));
        } catch (Exception e) {
            System.err.println(String.format("Error while loading the MAP Resource state from file=%s", persistFile));
            e.printStackTrace();
        }
    }

    public void setConfigFileName(String configFileName) {
        this.configFileName = configFileName;
    }

    public int getShortTimer() {
        return shortTimer;
    }

    public int getMediumTimer() {
        return mediumTimer;
    }

    public int getLongTimer() {
        return longTimer;
    }

    public void setShortTimer(int shortTimer) {
        this.shortTimer = shortTimer;
        this.store();
    }

    public void setMediumTimer(int mediumTimer) {
        this.mediumTimer = mediumTimer;
        this.store();
    }

    public void setLongTimer(int longTimer) {
        this.longTimer = longTimer;
        this.store();
    }
}
