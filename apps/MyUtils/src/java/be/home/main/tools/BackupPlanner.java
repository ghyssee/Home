package be.home.main.tools;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.*;
import be.home.model.Backup;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

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
        BackupPlanner instance = new BackupPlanner();
        instance.start("Music");

    }

        @Override
    public void run() {

    }

    public void start(String schemeId){
        Backup backup = (Backup) JSONUtils.openJSONWithCode(Constants.JSON.BACKUP, Backup.class );
        for (Backup.Scheme scheme : backup.schemes){
            if (scheme.id.equals(schemeId)){
                log.info("Scheme found: " + schemeId);
                try {
                    startBackup(scheme);
                } catch (ZipException e) {
                    LogUtils.logError(log, e);
                }
                break;
            }
        }

    }

    public void startBackup(Backup.Scheme scheme) throws ZipException {
        ZipUtils zipUtils = new ZipUtils();
        String timeStamp = DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS);
        String zipFile = "C:/My Backups/Music." + timeStamp + ".zip";
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
                        zipUtils.zipFile(zipFile, item.zipPath +
                                (StringUtils.isBlank(relativePath.toString()) ? "" : "/")
                                + relativePath, file);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                log.info("Backing up folder " + realPath);
                zipUtils.zipFolder(zipFile, "/" + item.zipPath, realPath);
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
