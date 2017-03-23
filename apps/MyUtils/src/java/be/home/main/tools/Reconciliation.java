package be.home.main.tools;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.*;
import be.home.domain.model.reconciliation.*;
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



    public void start() {
        String dataSource1 = "ILPOST";
        String dataSource2 = "ILPOST_SUP";
        String dataType = "ILPOST";
        String MATCH_ENGINE = "ILPOST";
        String streamDescription1 = "ILPost";
        String streamDescription2 = "ILPost Supplier";
        String role = "ILPost";
        String userId = "363";

        createCommonTables(MATCH_ENGINE,
                "01_DML_MATCHENGINE.sql"
        );
        List<FileRuleSet> fileRuleSetList = Arrays.asList(
                new FileRuleSet("ALLREADY_LOADED_FILE")
        );

        List<Field> fieldsStream1 = Arrays.asList(
                new Field("CATEGORY", "VARCHAR2", "100", false, "Category"),
                new Field("DEST_DATE", "VARCHAR2", "50", false, "Destination Date"),
                new Field("CODE", "VARCHAR2", "100", true, "Flight Code"),
                new Field("WEIGHT", "VARCHAR2", "50", false, "Weight"),
                new Field("LETTER", "VARCHAR2", "50", false, "Letter"),
                new Field("EMS", "VARCHAR2", "50", false, "EMS"),
                new Field("SV", "VARCHAR2", "50", false, "CV"),
                new Field("ARRIVAL_CENTER", "VARCHAR2", "100", false, "Arrival Center")
        );
        List<Field> fieldsStream2 = Arrays.asList(
                new Field("SUPPLIER", "VARCHAR2", "100", false, "Supplier"),
                new Field("CODE", "VARCHAR2", "100", false, "Flight Code"),
                new Field("MAIL_NO", "VARCHAR2", "20", false, "Mail No"),
                new Field("DISP_OFFICE", "VARCHAR2", "20", false, "Disposit Office"),
                new Field("DEST_OFFICE", "VARCHAR2", "20", false, "Destination Office"),
                new Field("DEST_DATE", "VARCHAR2", "50", false, "Destination Date"),
                new Field("CATEGORY", "VARCHAR2", "100", false, "Category"),
                new Field("MAIL_LCAO", "VARCHAR2", "50", false, "Mail LCAO"),
                new Field("MAIL_CP", "VARCHAR2", "50", false, "Mail CP"),
                new Field("MAIL_EMS", "VARCHAR2", "50", false, "Mail EMS"),
                new Field("SORTE", "VARCHAR2", "50", false, "Sorte"),
                new Field("TYPE_POSTE", "VARCHAR2", "50", false, "Type Post"),
                new Field("WEIGHT", "VARCHAR2", "100", false, "Weight"),
                new Field("FORMAT", "VARCHAR2", "100", false, "Format"),
                new Field("WEIGHT_LCAO", "VARCHAR2", "20", false, "Wieght LCAO"),
                new Field("WEIGHT_EMS", "VARCHAR2", "20", false, "Weight EMS"),
                new Field("SERIAL_NBR", "VARCHAR2", "20", false, "Wieght NBR"),
                new Field("GROSS_WEIGHT", "VARCHAR2", "20", false, "Gross Weight")
        );
        List<FileType> fileTypesStream1 = Arrays.asList(
                new FileType("ILPOST_FILE","^ILPOST_.*\\.csv$","ilpost","ILPOST File", "DEFAULT", fileRuleSetList)
        );
        List<FileType> fileTypesStream2 = Arrays.asList(
                new FileType("ILPOST_AIR_CANADA_FILE","^AIR_CANADA_.*\\.csv$","ilpost-supplier","AIR CANADA File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_CROATIA_AIRWAYS_FILE","^CROATIA_AIRWAYS_.*\\.csv$","ilpost-supplier","CROATIA AIRWAYS File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_CZECH_AIRLINES_FILE","^CZECH_AIRLINES_.*\\.csv$","ilpost-supplier","CZECH AIRLINES File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_DHL_FILE","^DHL_.*\\.csv$","ilpost-supplier","DHL File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_ETHIAD_FILE","^ETHIAD_.*\\.csv$","ilpost-supplier","ETHIAD File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_GLOBAL_AIRLINE_FILE","^GLOBAL_AIRLINE_.*\\.csv$","ilpost-supplier","GLOBAL AIRLINE SERVICES File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_KALES_FILE","^KALES_.*\\.csv$","ilpost-supplier","KALES File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_NETWORK_AIRLINE_FILE","^NETWORK_AIRLINE_.*\\.csv$","ilpost-supplier","NETWORK AIRLINE SERVICES File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_SWISSPORT_CARGO_FILE","^SWISSPORT_CARGO_.*\\.csv$","ilpost-supplier","SWISSPORT CARGO SERVICES File","DEFAULT", fileRuleSetList),
                new FileType("ILPOST_TURKISH_AIRLINES_FILE","^TURKISH_AIRLINES_.*\\.csv$","ilpost-supplier","TURKISH AIRLINES File","DEFAULT", fileRuleSetList)
        );

        List<FileType> fileTypes = new ArrayList<>();
        fileTypes.addAll(fileTypesStream1);
        fileTypes.addAll(fileTypesStream2);

        createMatchingTables(fieldsStream1, dataSource1, dataType, "02_DML_ILPOST.sql");
        createMatchingTables(fieldsStream2,dataSource2, dataType,"03_DML_ILPOST_SUP.sql");
        createSynonyms(MATCH_ENGINE, dataSource1, dataSource2, dataType,"04_FU_SYNONYMS.sql");
        createSynonyms(MATCH_ENGINE, dataSource1, dataSource2, dataType,"05_FR_SYNONYMS.sql");

        Datasource datasource1 = new Datasource(dataSource1, "ILPOST");
        Datasource datasource2 = new Datasource(dataSource2, "ILPOST SUPPLIER");
        List<Datasource> datasources = Arrays.asList(
                datasource1,
                datasource2
        );
        Datatype datatype = new Datatype(dataType, "ILPOST Invoicing");
        Stream stream1 = new Stream("ILPost", "FILE", "N", datasource1, fileTypesStream1);
        Stream stream2 = new Stream("ILPost Supplier", "FILE", "N", datasource2, fileTypesStream2);
        List<Stream> streams = Arrays.asList(
                stream1,
                stream2
        );

        createGlobal(userId,
                datatype,
                streams,
                "06_MDM_SETUP_GLOBAL.sql");

        //createFileType(userId, datasources, fileTypes, "07_MDM_SETUP_FILETYPE.sql");

        List<MatchPredicate> listMatchPredicateManual =  Arrays.asList(
                new MatchPredicate("Flight Code Equals", "CODE", null, "CODE", "=", null, null, null, "N")
        );
        List<MatchPredicate> listMatchPredicateAutomatic =  Arrays.asList(
                new MatchPredicate("Flight Code Equals", "CODE", null, "CODE", "=", null, null, null, "N")
        );

        List<MatchAlgorithm> listMatchAlgorithm =  Arrays.asList(
                new MatchAlgorithm("ILPOST_MAN", "Match on Flight Code", "n", "n", "1", "MANUAL", listMatchPredicateManual),
                new MatchAlgorithm("ILPOST_RUN1", "Match on Flight Code", "n", "n", "1", "AUTOMATIC", listMatchPredicateAutomatic)
        );

        MatchEngine me = new MatchEngine("ILPOST", "ILPost Reconciliation", stream1, stream2, listMatchAlgorithm);
        createMatchEngine(userId, me, "08_MDM_SETUP_MATCHENGINE.sql");

        createReport(userId, streamDescription1, streamDescription2, fieldsStream1, fieldsStream2, "09_MDM_SETUP_REPORT.sql"
        );
        createSecurity(MATCH_ENGINE, role, "90_MDM_INSERT_SECURITY_LEVELS.sql");
        createDropTables(me, dataSource1, dataSource2, datatype, streams, fileTypes, "99_DROP.sql");
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
            vu.makeFile("reconciliation/RECON_01_GEN_COMMON.sql", outputFile, context);
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
            vu.makeFile("reconciliation/RECON_02_GEN_STREAM.sql", outputFile, context);
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
            vu.makeFile("reconciliation/RECON_03_GEN_FU__FR_SYNONYMS.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDropTables(MatchEngine matchEngine, String dataSource1, String dataSource2, Datatype dataType,
                                 List<Stream> streams,
                               List<FileType> fileTypes, String outputFile) {
        outputFile = getOutputFile(outputFile);


        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("matchEngine", matchEngine);
        context.put("dataSource1", dataSource1);
        context.put("dataSource2", dataSource2);
        context.put("dataType", dataType);
        context.put("fileTypes", fileTypes);
        context.put("streams", streams);

        context.put("date", new DateTool());
        context.put("esc", new EscapeTool());
        context.put("du", new DateUtils());
        context.put("su", new StringUtils());
        try {
            vu.makeFile("reconciliation/RECON_99_DROP.sql", outputFile, context);
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
            vu.makeFile("reconciliation/RECON_90_GEN_MDM_INSERT_SECURITY_LEVELS.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createReport(String userId, String streamDescription1, String streamDescription2,
                             List<Field> fields1, List<Field> fields2, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("streamDescription1", streamDescription1);
        context.put("streamDescription2", streamDescription2);
        context.put("columns1", fields1);
        context.put("columns2", fields2);
        context.put("userId", userId);

        try {
            vu.makeFile("reconciliation/RECON_07_GEN_MDM_REPORT.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGlobal(String userId, Datatype datatype, List<Stream> streams, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("userId", userId);
        context.put("datatype", datatype);
        context.put("streams", streams);
        context.put("mu", new MyTools());

        try {
            vu.makeFile("reconciliation/RECON_04_GEN_MDM_GLOBAL.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createMatchEngine(String userId, MatchEngine me, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("userId", userId);
        context.put("matchEngine", me);
        context.put("mu", new MyTools());

        try {
            vu.makeFile("reconciliation/RECON_05_GEN_MDM_MATCHENGINE.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createFileType(String userId, List<Datasource> datasources, List<FileType> list, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("fileTypes", list);
        context.put("datasources", datasources);
        context.put("userId", userId);

        try {
            vu.makeFile("reconciliation/RECON_06_GEN_FILE_TYPE.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyTools {
        public String getOracleStringValue(String text){
            String ret = "";
            if (text == null){
                ret = "null";
            }
            else {
                ret = "'" + text + "'";
            }
            return ret;
        }
    }



}

