package be.home.main.tools;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.*;
import be.home.model.json.Backup;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import static java.nio.file.FileVisitOption.FOLLOW_LINKS;

/**
 * Created by Gebruiker on 30/12/2016.
 */
public class BackupPlanner extends BatchJobV2{

    private static final Logger log = getMainLog(BackupPlanner.class);

    public static void main(String args[]) {
        Backup backup = (Backup) JSONUtils.openJSONWithCode(Constants.JSON.BACKUP, Backup.class );
        BackupPlanner instance = new BackupPlanner();
        instance.start(backup, args.length > 0 ? args[0] : null);

    }

        @Override
    public void run() {

    }

    public String getDefaultSchemeId(Backup backup){
        String host = NetUtils.getHostName().toUpperCase();
        String defaultId = null;
        for (Backup.DefaultScheme defaultScheme : backup.defaultSchemes){
            if (host.equals(defaultScheme.host.toUpperCase())){
                defaultId = defaultScheme.id;
                break;
            }
        }
        return defaultId;
    }


    public Backup.SchemeItem getSchemeItem(List<Backup.SchemeItem> schemes, String schemeId){
        for (Backup.SchemeItem schemeItem : schemes){
            if (schemeItem.id.equals(schemeId)){
                return schemeItem;
            }
        }
        return null;
    }

    public Backup.Scheme getScheme(List<Backup.Scheme> schemes, String schemeId){
        for (Backup.Scheme scheme : schemes){
            if (scheme.id.equals(schemeId)){
                return scheme;
            }
        }
        return null;
    }

    public void start(Backup backup, String schemeId) {
        String schemeLog = "";
        List <String> errors = new ArrayList();
        if (schemeId == null) {
            schemeId = getDefaultSchemeId(backup);
            schemeLog = "Scheme Origin: Default Host";
        }
        else {
            schemeLog = "Scheme Origin: Parameter";
        }
        if (schemeId != null) {
            Backup.SchemeItem schemeItem = getSchemeItem(backup.schemes, schemeId);
            if (schemeItem != null) {
                for (Backup.SchemeId id : schemeItem.list) {
                    Backup.Scheme scheme = getScheme(backup.schemeList, id.id);
                    if (scheme != null) {
                        log.info("Scheme found: " + schemeId + " / " + schemeLog);
                        log.info("Backup File: " + scheme.backupFile);
                        try {
                            startBackup(scheme);
                        } catch (ZipException e) {
                            LogUtils.logError(log, e);
                        }
                    } else {
                        errors.add("Backup Plan:" + schemeId + " / Scheme Not Found: " + id.id);
                    }
                }
            }
            else {
                errors.add("No Backup Scheme found for id: " + schemeId);
            }
        }
        else {
            errors.add("No Backup Scheme found for id: " + schemeId);
        }
        if (errors.size() > 0){
            for (String msg : errors){
                log.warn(msg);
            }
        }
    }

    public void startBackup(Backup.Scheme scheme) throws ZipException {
        String timeStamp = DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS);
        String zipFile = scheme.backupFile.replaceAll("<TIMESTAMP>", timeStamp);
        for (Backup.Scheme.Item item : scheme.items){
            String realPath = Setup.replaceEnvironmentVariables(item.path);
            log.info("Item Path: " + realPath);
            log.info("Item ZipPath: " + item.zipPath);
            log.info("Item File: " + item.file);
            if (item.file != null){
                String glob = "glob:**/" + item.file;
                int depth = (item.subFolder ? Integer.MAX_VALUE : 1);
                try {
                    List<Path> files = match(glob, realPath, depth);
                    for (Path file : files){
                        log.info("Backing up file " + file.toString());
                        Path basePath = Paths.get(realPath);
                        Path relativePath = FileUtils.relativize(basePath, file.getParent());
                        ZipUtils.zip(zipFile, item.zipPath +
                                (StringUtils.isBlank(relativePath.toString()) ? "" : "/")
                                + relativePath, file.toString(), null);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                log.info("Backing up folder " + realPath);
                ZipUtils.zip(zipFile, "/" + item.zipPath, realPath, null);
            }
            log.info(StringUtils.repeat('*', 200));

        }

    }

    public static List<Path> match(String glob, String location, int depth) throws IOException {

        List <Path> files = new ArrayList<>();
        final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher(
                glob);


        EnumSet<FileVisitOption> opts = EnumSet.of(FOLLOW_LINKS);
        Files.walkFileTree(Paths.get(location), opts, depth, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path path,
                                             BasicFileAttributes attrs) throws IOException {
                if (pathMatcher.matches(path)) {
                    files.add(path);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc)
                    throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        return files;
    }
}
