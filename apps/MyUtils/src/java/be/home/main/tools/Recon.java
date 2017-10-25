package be.home.main.tools;

import be.home.common.utils.JSONUtils;
import be.home.domain.model.reconciliation.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.EscapeTool;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class Recon  {

    private static String[] OBJECTS = {"FAR_RECO_DATA", "FAR_RECO_INDX", "FAR_USER", "FAR_READ"};
    private static String OBJECT_DATA_MGR = "DATA_MGR";
    private static String BASE= "C:\\My Programs\\OneDrive\\Config\\Java\\Velocity\\Reconciliation\\GEN2\\";

    private static String OBJECT_DATA_MGR1 = "&1.";
    private static String[] OBJECTS1 = {"&1", "&2", "&3", "&4"};
    private static String BASE1 = "C:\\My Programs\\OneDrive\\Config\\Java\\Velocity\\Reconciliation\\GEN\\";
    private static List<OracleDriver> driverFile = new ArrayList();

    private static String DATA_MGR = "DATA_MGR";
    private static String META_DATA_MGR = "META_DATA_MGR";
    private static String FAR_USER = "FAR_USER";
    private static String FAR_READ = "FAR_READ";
    private static String FAR_RECO_DATA = "TBS_DATA_DATA_MGR";
    private static String FAR_RECO_INDX = "TBS_INDX_DATA_MGR";
    private static int fileCounter = 1;
    private static int fileCounterDrop = 990;

    public static void main(String args[]) {
        Recon instance = new Recon();
        try {
            instance.start("C:\\Projects\\far\\DBUtil\\01_SetupEvoucher.json");
            instance.start("C:\\Projects\\far\\DBUtil\\02_SetupPosa.json");
            instance.start("C:\\Projects\\far\\DBUtil\\03_SetupTCS.json");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getFile(String file){
        String index = StringUtils.leftPad(String.valueOf(fileCounter++), 2, '0');
        file = index + "_" + file + ".SQL";
        return file;
    }

    private String getDropFile(String file){
        String index = StringUtils.leftPad(String.valueOf(fileCounterDrop++), 3, '0');
        file = index + "_" + file + ".SQL";
        return file;
    }

    public void start(String configurationFile) {

        Configuration config = (Configuration) JSONUtils.openJSON(configurationFile, Configuration.class);

        List<FileRuleSet> fileRuleSetList = Arrays.asList(
                new FileRuleSet("ALLREADY_LOADED_FILE")
        );


        //createCommonTables(DATA_MGR, config.description, getFile("DML_MATCHENGINE"));
        createCommonTables(DATA_MGR, config.code, getFile("DML_MATCHENGINE"));
        //createMatchingTables(DATA_MGR, fieldsStream1, dataSource1, dataType, getFile("DML_ILPOST"));
        //createMatchingTables(DATA_MGR, fieldsStream2, dataSource2, dataType, getFile("DML_ILPOST_SUPl"));
        createMatchingTables2(DATA_MGR, config.leftStream, config.datatype, getFile("DML_ALVADIS"));
        // clear the datasource list, because it's already created with the previous line
        createMatchingTables2(DATA_MGR, config.rightStream, config.datatype, getFile("DML_PST"));
        createSynonyms(FAR_USER, config.code, config.leftStream.datasourceCode, config.rightStream.datasourceCode,
                config.datatype.code, getFile("FU_SYNONYMS"));
        createSynonyms(FAR_READ, config.code, config.leftStream.datasourceCode, config.rightStream.getDatasourceCode(),
                config.datatype.code, getFile("FR_SYNONYMS"));


        List<Stream> streams = Arrays.asList(
                config.leftStream,
                config.rightStream
        );

        createGlobal(META_DATA_MGR, config.userId, config.datatype, streams, config.datasources, getFile("MDM_SETUP_GLOBAL"));

        createMatchEngine(META_DATA_MGR, config.userId, config, getFile("MDM_SETUP_MATCHENGINE"));

        createReport(META_DATA_MGR, config.userId, config.leftStream.getDescription(), config.rightStream.getDescription(),
                config.leftStream.getFields(), config.rightStream.getFields(), getFile("MDM_SETUP_REPORT")
        );
        createSecurity(META_DATA_MGR, config.code, config.role, getFile("MDM_INSERT_SECURITY_LEVELS"));
        createTempMatch(config, DATA_MGR, getFile("DML_TEMP_MATCH"));
        //createDropTables(me, dataSource1, dataSource2, datatype, streams, fileTypes, newFunctions, "99_DROP.sql");
        createDropTables(config, getDropFile("DROP"));
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

    public void createMatchingTables2(String scheme, Stream stream, Datatype datatype, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        setObjects(context);
        context.put("dataType", datatype.getCode());
        context.put("columns", stream.getFields());
        context.put("dataSource", stream.getDatasourceCode());

        context.put("date", new DateTool());
        context.put("esc", new EscapeTool());
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

    public void createDropTables(Configuration config,  String outputFile) {
        outputFile = getOutputFile(outputFile);


        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("matchEngine", config);
        context.put("dataSource1", config.getLeftStream().getDatasourceCode());
        context.put("dataSource2", config.getRightStream().getDatasourceCode());
        context.put("dataType", config.getDatatype());
        List<FileType> fileTypes = new ArrayList();
        fileTypes.addAll(config.getLeftStream().fileTypes);
        fileTypes.addAll(config.getRightStream().fileTypes);
        context.put("fileTypes", fileTypes);
        List<Stream> streams = new ArrayList();
        streams.add(config.getLeftStream());
        streams.add(config.getRightStream());
        context.put("streams", streams);
        List<Function> newFunctions = new ArrayList();
        context.put("functions", newFunctions);

        context.put("date", new DateTool());
        context.put("esc", new EscapeTool());
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

    private void createGlobal(String scheme, String userId, Datatype datatype, List<Stream> streams, List<Datasource> datasources, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("userId", userId);
        context.put("datatype", datatype);
        context.put("datasources", datasources);
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

    private void createMatchEngine(String scheme, String userId, Configuration config, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("userId", userId);
        context.put("matchEngine", config);
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

    private boolean checkIfFieldExist(List<Field> fields, Field searchField){
        for (Field field : fields){
            if (field.name.equals(searchField.name)){
                return true;
            }
        }
        return false;
    }

    private void createTempMatch(Configuration config, String scheme, String outputFile) {
        outputFile = getOutputFile(outputFile);

        VelocityUtils vu = new VelocityUtils();
        VelocityContext context = new VelocityContext();
        context.put("mu", new MyTools());
        context.put("su", new StringUtils());
        context.put("matchEngine", config.code);
        List<Field> matchFields = new ArrayList<>();

        for (Field field :config.leftStream.fields){
            if (field.match && !checkIfFieldExist(matchFields, field)){
                matchFields.add(field);
            }
        }
        for (Field field :config.rightStream.fields){
            if (field.match && !checkIfFieldExist(matchFields, field)){
                matchFields.add(field);
            }
        }
        context.put("columns", matchFields);

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

    public class VelocityUtils {

        public void makeFile(String template, String outputFile, VelocityContext context) throws IOException {
            Properties p = new Properties();
            p.setProperty("file.resource.loader.path", "C:\\Projects\\far\\DBUtil\\");
            p.setProperty("input.encoding", "UTF-8");
            p.setProperty("output.encoding", "UTF-8");
            p.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.Log4JLogChute");
            p.setProperty("runtime.log.logsystem.log4j.logger","velocity");

            VelocityEngine ve = new VelocityEngine();
            ve.init(p);
        /*  next, get the Template  */
            Template t = ve.getTemplate( template );
        /*  create a context and add data */
            Path file = Paths.get(outputFile);
            BufferedWriter writer = null;
            try {
                writer = Files.newBufferedWriter(file, Charset.forName("UTF-8"));
                t.merge(context, writer);
            } finally {
                if (writer != null){
                    writer.flush();
                    writer.close();
                }
            }

        }
    }
}

