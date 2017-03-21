package be.home.main.tools;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.*;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.model.ConfigTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class Reconciliation extends BatchJobV2 {

    private static final Logger log = getMainLog(Reconciliation.class);

    private static String[] OBJECTS = {"FAR_RECO_DATA", "FAR_RECO_INDX", "FAR_USER", "FAR_READ"};
    private static String OBJECT_DATA_MGR = "DATA_MGR";
    private static String BASE = "C:\\My Programs\\OneDrive\\Config\\Java\\Velocity\\Reconciliation\\GEN2\\";
    private static String OBJECT_DATA_MGR1 = "&1.";
    private static String[] OBJECTS1 = {"&1", "&2", "&3", "&4"};
    private static String BASE2 = "C:\\My Programs\\OneDrive\\Config\\Java\\Velocity\\Reconciliation\\GEN\\";

    public static void main(String args[]) {
        Reconciliation instance = new Reconciliation();
        try {
            instance.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {

    }

    public class Field {
        public String name;
        public String type;
        public String length;
        public boolean required;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLength() {
            return length;
        }

        public void setLength(String length) {
            this.length = length;
        }

        public String getType2(){
            if (this.type.equals("VARCHAR")){
                return "CHAR";
            }
            else if (this.type.equals("VARCHAR2")){
                return "CHAR";
            }
            return "";
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        Field(String name, String type, String length, boolean required){
            this.name = name;
            this.type = type;
            this.length = length;
            this.required = required;
        }
    }

    public void start() {
        String dataSource1 = "ILPOST";
        String dataSource2 = "ILPOST_SUP";
        String dataType = "ILPOST";
        String MATCH_ENGINE = "ILPOSTSUP";
        createCommonTables(MATCH_ENGINE,
                "01_DML_MATCHENGINE.sql"
        );
        Field f = new Field("CATEGORY", "VARCHAR2", "100", false);
        createMatchingTables(Arrays.asList(
                new Field("CATEGORY", "VARCHAR2", "100", false),
                new Field("DEST_DATE", "VARCHAR2", "50", false),
                new Field("CODE", "VARCHAR2", "100", true),
                new Field("WEIGHT", "VARCHAR2", "50", false),
                new Field("LETTER", "VARCHAR2", "50", false),
                new Field("EMS", "VARCHAR2", "50", false),
                new Field("SV", "VARCHAR2", "50", false),
                new Field("ARRIVAL_CENTER", "VARCHAR2", "100", false)
                ),
                dataSource1, dataType,
                "02_DML_ILPOST.sql"
        );

        createMatchingTables(Arrays.asList(
                new Field("CODE", "VARCHAR2", "100", false),
                new Field("MAIL_NO", "VARCHAR2", "20", false),
                new Field("DISP_OFFICE", "VARCHAR2", "20", false),
                new Field("DEST_OFFICE", "VARCHAR2", "20", false),
                new Field("DEST_DATE", "VARCHAR2", "50", false),
                new Field("CATEGORY", "VARCHAR2", "100", false),
                new Field("MAIL_LCAO", "VARCHAR2", "50", false),
                new Field("MAIL_CP", "VARCHAR2", "50", false),
                new Field("MAIL_EMS", "VARCHAR2", "50", false),
                new Field("SORTE", "VARCHAR2", "50", false),
                new Field("TYPE_POSTE", "VARCHAR2", "50", false),
                new Field("WEIGHT", "VARCHAR2", "100", false),
                new Field("FORMAT", "VARCHAR2", "100", false),
                new Field("WEIGHT_LCAO", "VARCHAR2", "20", false),
                new Field("WEIGHT_EMS", "VARCHAR2", "20", false),
                new Field("SERIAL_NBR", "VARCHAR2", "20", false),
                new Field("GROSS_WEIGHT", "VARCHAR2", "20", false)
                ),
                dataSource2, dataType,
                "03_DML_ILPOST_SUP.sql"
        );
        createSynonyms(MATCH_ENGINE, dataSource1, dataSource2, dataType,
                "04_FU_SYNONYMS.sql"
                );
        createSynonyms(MATCH_ENGINE, dataSource1, dataSource2, dataType,
                "05_FR_SYNONYMS.sql"
        );
        createSecurity(MATCH_ENGINE, "ILPost", "06_MDM_INSERT_SECURITY_LEVELS.sql"
        );
        createDropTables(MATCH_ENGINE, dataSource1, dataSource2, dataType,
                "09_DROP.sql"
        );
    }

    public void setObjects(VelocityContext context){
        context.put("object1", OBJECTS[0]);
        context.put("object2", OBJECTS[1]);
        context.put("object3", OBJECTS[2]);
        context.put("object4", OBJECTS[3]);
    }

    public String getOutputFile(String outputFile){
        return BASE + outputFile;
    }


    public void createCommonTables(String matchEngine, String outputFile) {

        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("matchEngine", matchEngine);

        context.put("date", new DateTool());
        context.put("esc", new EscapeTool());
        context.put("du", new DateUtils());
        context.put("su", new StringUtils());
        setObjects(context);
        try {
            vu.makeFile("reconciliation/RECON_1_GEN_COMMON.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMatchingTables(List<Field> fields, String dataSource, String dataType,
                                     String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        setObjects(context);
        context.put("dataType", dataType);
        context.put("dataSource", dataSource);
        context.put("columns", fields);

        context.put("date", new DateTool());
        context.put("esc", new EscapeTool());
        context.put("du", new DateUtils());
        context.put("su", new StringUtils());
        try {
            vu.makeFile("reconciliation/RECON_2_GEN_STREAM.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createMatchingTablesOld(List<String> fields, String dataSource, String dataType,
                                     String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        setObjects(context);
        context.put("dataType", dataType);
        context.put("dataSource", dataSource);
        context.put("columns", fields);

        context.put("date", new DateTool());
        context.put("esc", new EscapeTool());
        context.put("du", new DateUtils());
        context.put("su", new StringUtils());
        try {
            vu.makeFile("reconciliation/RECON_2_GEN_STREAM.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createSynonyms(String matchEngine, String dataSource1, String dataSource2, String dataType,
                               String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("object1", OBJECT_DATA_MGR);
        context.put("matchEngine", matchEngine);
        context.put("dataSource1", dataSource1);
        context.put("dataSource2", dataSource2);
        context.put("dataType", dataType);

        context.put("date", new DateTool());
        context.put("esc", new EscapeTool());
        context.put("du", new DateUtils());
        context.put("su", new StringUtils());
        try {
            vu.makeFile("reconciliation/RECON_3_GEN_FU__FR_SYNONYMS.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDropTables(String matchEngine, String dataSource1, String dataSource2, String dataType,
                               String outputFile) {
        outputFile = getOutputFile(outputFile);


        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("matchEngine", matchEngine);
        context.put("dataSource1", dataSource1);
        context.put("dataSource2", dataSource2);
        context.put("dataType", dataType);

        context.put("date", new DateTool());
        context.put("esc", new EscapeTool());
        context.put("du", new DateUtils());
        context.put("su", new StringUtils());
        try {
            vu.makeFile("reconciliation/RECON_9_DROP.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createSecurity(String matchEngine, String role, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("matchEngine", matchEngine);
        context.put("role", role);

        try {
            vu.makeFile("reconciliation/RECON_4_GEN_MDM_INSERT_SECURITY_LEVELS.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

