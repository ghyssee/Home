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

    private static String[] OBJECTS1 = {"FAR_RECO_DATA", "FAR_RECO_INDX", "FAR_USER", "FAR_READ"};
    private static String OBJECT_DATA_MGR1 = "DATA_MGR";
    private static String BASE1 = "C:\\My Programs\\OneDrive\\Config\\Java\\Velocity\\Reconciliation\\GEN2\\";

    private static String OBJECT_DATA_MGR = "&1.";
    private static String[] OBJECTS = {"&1", "&2", "&3", "&4"};
    private static String BASE = "C:\\My Programs\\OneDrive\\Config\\Java\\Velocity\\Reconciliation\\GEN\\";
    private static List<OracleDriver> driverFile = new ArrayList();

    private static String DATA_MGR = "DATA_MGR";
    private static String META_DATA_MGR = "META_DATA_MGR";
    private static String FAR_USER = "FAR_USER";
    private static String FAR_READ = "FAR_READ";
    private static String FAR_RECO_DATA = "TBS_DATA_DATA_MGR";
    private static String FAR_RECO_INDX = "TBS_INDX_DATA_MGR";



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

        List<FileRuleSet> fileRuleSetList = Arrays.asList(
                new FileRuleSet("ALLREADY_LOADED_FILE")
        );

        List<Field> fieldsStream1 = Arrays.asList(
                new Field("CATEGORY", "VARCHAR2", "100", false, "Category"),
                new Field("CATEGORY_CODE", "VARCHAR2", "10", false, "Category Code"),
                new Field("DEST_DATE", "VARCHAR2", "50", false, "Destination Date"),
                new Field("CODE", "VARCHAR2", "100", true, "Flight Code"),
                new Field("WEIGHT", "NUMBER", null, false, "Weight"),
                new Field("LETTER", "NUMBER", null, false, "Letter"),
                new Field("EMS", "NUMBER", null, false, "EMS"),
                new Field("SV", "NUMBER", null, false, "SV"),
                new Field("ARRIVAL_CENTER", "VARCHAR2", "100", false, "Arrival Center"),
                new Field("DISP_OFFICE", "VARCHAR2", "20", true, "Disposit Office"),
                new Field("DEST_OFFICE", "VARCHAR2", "20", true, "Destination Office"),
                new Field("DISP_OFFICE_SHORT", "VARCHAR2", "20", true, "Disposit Office (Short)"),
                new Field("DEST_OFFICE_SHORT", "VARCHAR2", "20", true, "Destination Office (Short)"),
                new Field("MAIL_NO", "VARCHAR2", "20", false, "Mail No")
        );
        List<Field> fieldsStream2 = Arrays.asList(
                new Field("SUPPLIER", "VARCHAR2", "100", false, "Supplier"),
                new Field("CATEGORY", "VARCHAR2", "100", false, "Category"),
                new Field("CATEGORY_CODE", "VARCHAR2", "10", false, "Category Code"),
                new Field("CODE", "VARCHAR2", "100", false, "Flight Code"),
                new Field("MAIL_NO", "VARCHAR2", "20", false, "Mail No"),
                new Field("DISP_OFFICE", "VARCHAR2", "20", false, "Disposit Office"),
                new Field("DEST_OFFICE", "VARCHAR2", "20", false, "Destination Office"),
                new Field("FORWARD_FROM", "VARCHAR2", "20", false, "Forwarded From"),
                new Field("FORWARD_TO", "VARCHAR2", "20", false, "Forwarded To"),
                new Field("DEST_DATE", "VARCHAR2", "50", false, "Destination Date"),
                new Field("SORTE", "VARCHAR2", "50", false, "Sorte"),
                new Field("WEIGHT", "NUMBER", null, false, "Weight"),
                new Field("WEIGHT_LCAO", "NUMBER", null, false, "Wieght LCAO"),
                new Field("WEIGHT_EMS", "NUMBER", null, false, "Weight EMS"),
                new Field("WEIGHT_CP", "NUMBER", null, false, "Weight CP")
        );
        List<FileType> fileTypesStream1 = Arrays.asList(
                new FileType("ILPOST_FILE", "^ILPOST_.*\\.csv$", "ilpost", "ILPOST File", "DEFAULT", fileRuleSetList)
        );
        List<FileType> fileTypesStream2 = Arrays.asList(
                new FileType("ILPOST_SUPPLIER_FILE", "^SUPPLIER_.*\\.csv$", "ilpost-supplier", "SUPPLIER File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_AIR_CANADA_FILE", "^AIR_CANADA_.*\\.csv$", "ilpost-supplier", "AIR CANADA File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_AIR_CARGO_LOGISTICS_FILE", "^AIR_CARGO_LOGISTICS_.*\\.csv$", "ilpost-supplier", "AIR CARGO LOGISTICS File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_CROATIA_AIRWAYS_FILE", "^CROATIA_AIRWAYS_.*\\.csv$", "ilpost-supplier", "CROATIA AIRWAYS File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_CZECH_AIRLINES_FILE", "^CZECH_AIRLINES_.*\\.csv$", "ilpost-supplier", "CZECH AIRLINES File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_DHL_FILE", "^DHL_.*\\.csv$", "ilpost-supplier", "DHL File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_ETHIAD_FILE", "^ETHIAD_.*\\.csv$", "ilpost-supplier", "ETHIAD File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_GLOBAL_AIRLINE_FILE", "^GLOBAL_AIRLINE_.*\\.csv$", "ilpost-supplier", "GLOBAL AIRLINE SERVICES File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_KALES_FILE", "^KALES_.*\\.csv$", "ilpost-supplier", "KALES File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_NETWORK_AIRLINE_FILE", "^NETWORK_AIRLINE_.*\\.csv$", "ilpost-supplier", "NETWORK AIRLINE SERVICES File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_SWISSPORT_CARGO_FILE", "^SWISSPORT_CARGO_.*\\.csv$", "ilpost-supplier", "SWISSPORT CARGO SERVICES File", "DEFAULT", fileRuleSetList),
                new FileType("ILPOST_TURKISH_AIRLINES_FILE", "^TURKISH_AIRLINES_.*\\.csv$", "ilpost-supplier", "TURKISH AIRLINES File", "DEFAULT", fileRuleSetList)
        );

        List<FileType> fileTypes = new ArrayList<>();
        fileTypes.addAll(fileTypesStream1);
        fileTypes.addAll(fileTypesStream2);

        createCommonTables(DATA_MGR, MATCH_ENGINE, "01_DML_MATCHENGINE.sql");
        createMatchingTables(DATA_MGR, fieldsStream1, dataSource1, dataType, "02_DML_ILPOST.sql");
        createMatchingTables(DATA_MGR, fieldsStream2, dataSource2, dataType, "03_DML_ILPOST_SUP.sql");
        createSynonyms(FAR_USER, MATCH_ENGINE, dataSource1, dataSource2, dataType, "04_FU_SYNONYMS.sql");
        createSynonyms(FAR_READ, MATCH_ENGINE, dataSource1, dataSource2, dataType, "05_FR_SYNONYMS.sql");

        Datasource datasource1 = new Datasource(dataSource1, "ILPOST");
        Datasource datasource2 = new Datasource(dataSource2, "ILPOST SUPPLIER");

        Datatype datatype = new Datatype(dataType, "ILPOST Invoicing");
        Stream stream1 = new Stream("ILPost", "FILE", "N", datasource1, fileTypesStream1);
        Stream stream2 = new Stream("ILPost Supplier", "FILE", "N", datasource2, fileTypesStream2);
        List<Stream> streams = Arrays.asList(
                stream1,
                stream2
        );
        createGlobal(META_DATA_MGR, userId, datatype, streams, "06_MDM_SETUP_GLOBAL.sql");

        List<MatchPredicate> listMatchPredicateManual = Arrays.asList(
                new MatchPredicate("Dispatching Office Equals", "DISP_OFFICE", null, "DISP_OFFICE", "=", null, null, null, "N"),
                new MatchPredicate("Office Of Destination Equals", "DEST_OFFICE", null, "DEST_OFFICE", "=", null, null, null, "N"),
                new MatchPredicate("Mail No Equals", "MAIL_NO", null, "MAIL_NO", "=", null, null, null, "N"),
                new MatchPredicate("Mail Type Equals", "CATEGORY_CODE", null, "CATEGORY_CODE", "=", null, null, null, "N")
        );
        List<MatchPredicate> listMatchPredicateAutomatic = Arrays.asList(
                new MatchPredicate("Dispatching Office Equals", "DISP_OFFICE", null, "DISP_OFFICE", "=", null, null, null, "N"),
                new MatchPredicate("Office Of Destination Equals", "DEST_OFFICE", null, "DEST_OFFICE", "=", null, null, null, "N"),
                new MatchPredicate("Mail No Equals", "MAIL_NO", null, "MAIL_NO", "=", null, null, null, "N"),
                new MatchPredicate("Mail Type Equals", "CATEGORY_CODE", null, "CATEGORY_CODE", "=", null, null, null, "N")
        );
        List<MatchPredicate> listMatchPredicateManual2 = Arrays.asList(
                new MatchPredicate("Dispatching Office Equals", "DISP_OFFICE", null, "DISP_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Office Of Destination Equals", "DEST_OFFICE", null, "DEST_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Mail No Equals", "MAIL_NO", null, "MAIL_NO", "=", null, null, null, "N"),
                new MatchPredicate("Mail Type Equals", "CATEGORY_CODE", null, "CATEGORY_CODE", "=", null, null, null, "N")
        );
        List<MatchPredicate> listMatchPredicateAutomatic2 = Arrays.asList(
                new MatchPredicate("Dispatching Office Equals", "DISP_OFFICE", null, "DISP_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Office Of Destination Equals", "DEST_OFFICE", null, "DEST_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Mail No Equals", "MAIL_NO", null, "MAIL_NO", "=", null, null, null, "N"),
                new MatchPredicate("Mail Type Equals", "CATEGORY_CODE", null, "CATEGORY_CODE", "=", null, null, null, "N")
        );
        List<MatchPredicate> listMatchPredicateManual3 = Arrays.asList(
                new MatchPredicate("Flight Code", "CODE", null, "CODE", "=", null, null, null, "N")
        );
        List<MatchPredicate> listMatchPredicateAutomatic3 = Arrays.asList(
                new MatchPredicate("Flight Code", "CODE", null, "CODE", "=", null, null, null, "N")
        );
        Function makeNull = new Function("MakeNull", "Make Null", Function.DataType.NUMBER, Function.FunctionType.M);
        List<Function> newFunctions =  Arrays.asList(
                makeNull
        );
        Function isNull = new Function("IsNull", "Is Null");
        List<MatchPredicate> listMatchPredicateManual4 = Arrays.asList(
                new MatchPredicate("Dispatching Office Equals", "DISP_OFFICE", null, "DISP_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Office Of Destination Equals", "DEST_OFFICE", null, "DEST_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Mail No Equals", "MAIL_NO", null, "MAIL_NO", "=", null, null, null, "N"),
                new MatchPredicate("Categroy Code Is Null", "CATEGORY_CODE", null, "CATEGORY_CODE", "=", null, makeNull, isNull, "N")
        );
        List<MatchPredicate> listMatchPredicateAutomatic4 = Arrays.asList(
                new MatchPredicate("Dispatching Office Equals", "DISP_OFFICE", null, "DISP_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Office Of Destination Equals", "DEST_OFFICE", null, "DEST_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Mail No Equals", "MAIL_NO", null, "MAIL_NO", "=", null, null, null, "N"),
                new MatchPredicate("Categroy Code Is Null", "CATEGORY_CODE", null, "CATEGORY_CODE", "=", null, makeNull, isNull, "N")
        );
        List<MatchPredicate> listMatchPredicateManual5 = Arrays.asList(
                new MatchPredicate("Office Of Destination Equals", "DEST_OFFICE", null, "DEST_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Mail No Equals", "MAIL_NO", null, "MAIL_NO", "=", null, null, null, "N"),
                new MatchPredicate("Mail Type Equals", "CATEGORY_CODE", null, "CATEGORY_CODE", "=", null, null, null, "N"),
                new MatchPredicate("Disp Office Is Null", "DISP_OFFICE", null, "DISP_OFFICE", "=", null, makeNull, isNull, "N")
        );
        List<MatchPredicate> listMatchPredicateAutomatic5 = Arrays.asList(
                new MatchPredicate("Office Of Destination Equals", "DEST_OFFICE", null, "DEST_OFFICE_SHORT", "=", null, null, null, "N"),
                new MatchPredicate("Mail No Equals", "MAIL_NO", null, "MAIL_NO", "=", null, null, null, "N"),
                new MatchPredicate("Mail Type Equals", "CATEGORY_CODE", null, "CATEGORY_CODE", "=", null, null, null, "N"),
                new MatchPredicate("Disp Office Is Null", "DISP_OFFICE", null, "DISP_OFFICE", "=", null, makeNull, isNull, "N")
        );

        List<MatchAlgorithm> listMatchAlgorithm = Arrays.asList(
                new MatchAlgorithm("ILPOST_MAN", "Match on Flight code", "n", "n", "1", "MANUAL", listMatchPredicateManual3),
                new MatchAlgorithm("ILPOST_RUN1", "Match on Flight code", "n", "n", "1", "AUTOMATIC", listMatchPredicateAutomatic3),
                new MatchAlgorithm("ILPOST_MAN2", "Match on DISP / DEST / MAIL_NO", "n", "n", "2", "MANUAL", listMatchPredicateManual),
                new MatchAlgorithm("ILPOST_RUN2", "Match on DISP / DEST / MAIL_NO", "n", "n", "2", "AUTOMATIC", listMatchPredicateAutomatic),
                new MatchAlgorithm("ILPOST_MAN3", "Match on CATEGORY / SHORT DISP / DEST / MAIL_NO", "n", "n", "3", "MANUAL", listMatchPredicateManual2),
                new MatchAlgorithm("ILPOST_RUN3", "Match on CATEGORY / Match on SHORT DISP / DEST / MAIL_NO", "n", "n", "3", "AUTOMATIC", listMatchPredicateAutomatic2),
                new MatchAlgorithm("ILPOST_MAN4", "Match On SHORT DISP / DEST / MAIL_NO And On Empty Category Code", "n", "n", "4", "MANUAL", listMatchPredicateManual4),
                new MatchAlgorithm("ILPOST_RUN4", "Match On SHORT DISP / DEST / MAIL_NO And On Empty Category Code", "n", "n", "4", "AUTOMATIC", listMatchPredicateAutomatic4),
                new MatchAlgorithm("ILPOST_MAN5", "Match on CATEGORY / Match on SHORT DEST / MAIL_NO And On Empty Disp Office", "n", "n", "5", "MANUAL", listMatchPredicateManual5),
                new MatchAlgorithm("ILPOST_RUN5", "Match on CATEGORY / Match on SHORT DEST / MAIL_NO And On Empty Disp Office", "n", "n", "5", "AUTOMATIC", listMatchPredicateAutomatic5)
        );

        MatchEngine me = new MatchEngine("ILPOST", "ILPost Reconciliation", stream1, stream2, listMatchAlgorithm);
        createFunctions(META_DATA_MGR, userId, newFunctions, "07_MDM_SETUP_FUNCTIONS.sql");
        createMatchEngine(META_DATA_MGR, userId, me, "08_MDM_SETUP_MATCHENGINE.sql");

        createReport(META_DATA_MGR, userId, streamDescription1, streamDescription2, fieldsStream1, fieldsStream2, "09_MDM_SETUP_REPORT.sql"
        );
        createSecurity(META_DATA_MGR, MATCH_ENGINE, role, "10_MDM_INSERT_SECURITY_LEVELS.sql");
        createTempMatch(DATA_MGR, "11_DML_TEMP_MATCH.sql");
        createDropTables(me, dataSource1, dataSource2, datatype, streams, fileTypes, newFunctions, "99_DROP.sql");
        makeDriver();
    }

    public void setObjects(VelocityContext context) {
        context.put("object1", OBJECTS[0]);
        context.put("object2", OBJECTS[1]);
        context.put("object3", OBJECTS[2]);
        context.put("object4", OBJECTS[3]);
    }

    public String getOutputFile(String outputFile) {
        return BASE + outputFile;
    }

    public void createCommonTables(String scheme, String matchEngine, String outputFile) {

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
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        driverLine = driverLine
                .addScheme(FAR_RECO_DATA)
                .addScheme(FAR_RECO_INDX)
                .addScheme(FAR_USER)
                .addScheme(FAR_READ);
        this.driverFile.add(driverLine);

    }

    public void createMatchingTables(String scheme, List<Field> fields, String dataSource, String dataType,
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
        context.put("mu", new MyTools());
        context.put("su", new StringUtils());
        try {
            vu.makeFile("reconciliation/RECON_02_GEN_STREAM.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        driverLine = driverLine
                .addScheme(FAR_RECO_DATA)
                .addScheme(FAR_RECO_INDX)
                .addScheme(FAR_USER)
                .addScheme(FAR_READ);
        this.driverFile.add(driverLine);
    }

    public void createSynonyms(String scheme, String matchEngine, String dataSource1, String dataSource2, String dataType,
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
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        driverLine.addScheme(DATA_MGR);
        this.driverFile.add(driverLine);
    }

    public void createDropTables(MatchEngine matchEngine, String dataSource1, String dataSource2, Datatype dataType,
                                 List<Stream> streams,
                                 List<FileType> fileTypes,
                                 List<Function> newFunctions,
                                 String outputFile) {
        outputFile = getOutputFile(outputFile);


        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("matchEngine", matchEngine);
        context.put("dataSource1", dataSource1);
        context.put("dataSource2", dataSource2);
        context.put("dataType", dataType);
        context.put("fileTypes", fileTypes);
        context.put("streams", streams);
        context.put("functions", newFunctions);

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

    public void createSecurity(String scheme, String matchEngine, String role, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("matchEngine", matchEngine);
        context.put("role", role);

        try {
            vu.makeFile("reconciliation/RECON_10_GEN_MDM_INSERT_SECURITY_LEVELS.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        this.driverFile.add(driverLine);
    }

    public void createReport(String scheme, String userId, String streamDescription1, String streamDescription2,
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
            vu.makeFile("reconciliation/RECON_06_GEN_MDM_REPORT.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        this.driverFile.add(driverLine);
    }

    private void createGlobal(String scheme, String userId, Datatype datatype, List<Stream> streams, String outputFile) {
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
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        this.driverFile.add(driverLine);
    }

    private void createMatchEngine(String scheme, String userId, MatchEngine me, String outputFile) {
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
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        this.driverFile.add(driverLine);
    }

    private void createFunctions(String scheme, String userId, List<Function> functions, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("userId", userId);
        context.put("functions", functions);
        context.put("mu", new MyTools());

        try {
            vu.makeFile("reconciliation/RECON_08_GEN_FUNCTION.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        this.driverFile.add(driverLine);
    }

    private void createTempMatch(String scheme, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("mu", new MyTools());

        try {
            vu.makeFile("reconciliation/RECON_07_GEN_TEMP_MATCH.sql", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
        File script = new File(outputFile);
        OracleDriver driverLine = new OracleDriver(scheme, script.getName());
        this.driverFile.add(driverLine);
    }

    public void makeDriver(){
        String outputFile = getOutputFile("driver");

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("mu", new MyTools());
        context.put("scripts", this.driverFile);

        try {
            vu.makeFile("reconciliation/driver", outputFile, context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class MyTools {
        public String getOracleStringValue(String text) {
            String ret = "";
            if (text == null) {
                ret = "null";
            } else {
                ret = "'" + text + "'";
            }
            return ret;
        }
        public String getFunction(Function function) {
            String ret = "";
            if (function == null) {
                ret = "null";
            } else {
                ret = "(SELECT F_ID FROM FUNCTION WHERE CODE = '" + function.getCode() + "')";
            }
            return ret;
        }
        public String getColumnType(Field field) {
            String ret = "";
            if ("VARCHAR2".equals(field.getType())){
                ret = field.getType() + "(" + field.getLength() + " " + field.getType2() + ")";
            }
            else {
                ret = field.getType();
            }
            return ret;

        }
    }


}

