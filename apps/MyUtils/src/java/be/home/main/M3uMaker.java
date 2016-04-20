package be.home.main;

import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.utils.FileUtils;
import be.home.model.ConfigTO;
import be.home.model.M3uTO;
import be.home.model.UltratopConfig;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ghyssee on 20/02/2015.
 */
public class M3uMaker extends BatchJobV2 {

    public static Log4GE log4GE;
    public static ConfigTO.Config config;
    public static UltratopConfig.Config ultratopConfig;
    private static String SPLIT_SONG = " - ";


    public static void main(String args[])  {
        M3uMaker instance = new M3uMaker();
        boolean fileRenamed = false;
        try {
            config = instance.init();
            System.out.println("Full Path Config Dir = " + config.getFullPathConfigDir());
            ultratopConfig = instance.init(config.getFullPathConfigDir() + "/UltratopConfig.json");
            System.out.println(config.toString());


            for (UltratopConfig.Year year : ultratopConfig.years){
                System.out.println(StringUtils.repeat("*", 200));
                System.out.println("YEAR    : " + year.year);
                System.out.println("LISTFILE: " + year.listFile);
                System.out.println("ENABLED : " + year.enabled);
                System.out.println(StringUtils.repeat("*", 200));
                if (year.enabled) {
                    for (UltratopConfig.Month m3uMonth : year.m3uMonth) {
                        System.out.println("MONTH ID       : " + m3uMonth.id);
                        System.out.println("MONTH BASEDIR  : " + m3uMonth.baseDir);
                        System.out.println("MONTH INPUTFILE: " + m3uMonth.inputFile);
                        System.out.println("MONTH ENABLED  : " + m3uMonth.enabled);
                        System.out.println(StringUtils.repeat("=", 200));
                        if (m3uMonth.enabled){
                            fileRenamed = fileRenamed || instance.run(ultratopConfig, year, m3uMonth);
                        }
                    }
                }
                System.out.println();
                if (fileRenamed){
                    System.out.println("File(s) were renamed. You should run Mp3ListMaker to update the year list for " + year.year);
                    System.out.println();
                }
            }

            /*
            for (ConfigTO.Parts part : config.parts){
                System.out.println(part.name);
            }*/
            //instance.run();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void run(){

    }


    public boolean run(UltratopConfig.Config m3u, UltratopConfig.Year year, UltratopConfig.Month m3uMonth) {

        final String batchJob = "M3u";
        log4GE = new Log4GE(config.wiki.resultLog);
        log4GE.clear();
        log4GE.start("Wiki Make CSV file");
        //log.info("Batchjob " + batchJob + " started on " + new Date());
        String baseFolder = m3u.baseDir + File.separator + m3uMonth.baseDir + File.separator;
        String inputFile =  baseFolder + m3uMonth.inputFile;
        List <M3uTO> ultratopList = null;
        List <M3uTO> renameList = new ArrayList <M3uTO> ();
        List <M3uTO> errorBaseList = new ArrayList <M3uTO> ();
        boolean fileRenamed = false;
        try {
            ultratopList = processUltratopList(inputFile);
            validateM3uList(ultratopList, inputFile);
            List <M3uTO> listOfMP3Files = processYearList(getFullPathYearListFile(year.listFile));
            validateM3uList(listOfMP3Files, getFullPathYearListFile(year.listFile));
            lookupMP3Songs(ultratopList, listOfMP3Files, m3uMonth.inputFile, baseFolder);
            List <M3uTO> baseDirList = lookupMP3InBaseDirectory(baseFolder, ultratopList,listOfMP3Files );
            int trackNr = 51;
            for (M3uTO line : baseDirList){
                switch (line.getStatus()) {
                    case M3uTO.STATUS_BASE_OK:
                        //System.out.println("BASE OK: " + line.getOriginalLine());
                        String track = StringUtils.leftPad(String.valueOf(trackNr), 2, "0");
                        if (track.equals(line.getTrack())){
                            //System.out.println("NO RENAME necessary for " + line.getOriginalLine());
                        }
                        else {
                            //System.out.println("Rename " + line.getOriginalLine() + " TO " + trackNr + " " + line.getArtist() + " - " + line.getSong());
                            line.setTrack(track);
                            renameList.add(line);
                        }
                        trackNr++;
                        break;
                    case M3uTO.MATCH_FOUND:
                        //System.out.println("OK: " + line.getOriginalLine());
                        break;
                    case M3uTO.STATUS_OK:
                        //System.out.println("OK: " + line.getOriginalLine());
                        break;
                    case M3uTO.ERROR_BASE_SONG_INFO:
                        errorBaseList.add(line);
                        break;
                    case M3uTO.RENAME:
                        //System.out.println(line.getErrorMessage());
                        renameList.add(line);
                        break;
                    default:
                        System.out.println(line.getErrorMessage());
                        break;
                }
            }
            fileRenamed = printResult(ultratopList, renameList, errorBaseList);
            makeM3uFile(ultratopList, baseFolder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //log.info("Batchjob " + batchJob + " ended on " + new Date());
        log4GE.end();
        return fileRenamed;

    }

    private String getFullPathYearListFile(String listFile) {
        return config.oneDriveDir + File.separator + listFile;
    }

    private boolean printResult(List <M3uTO> ultratopList, List <M3uTO> renameList, List <M3uTO> errorBaseList){
        System.out.println();
        System.out.println("RESULT");
        System.out.println(StringUtils.repeat("=", 200));
        boolean printHeader = true;
        boolean errorFound = false;
        boolean fileRenamed = false;
        for (M3uTO ultratopSong : ultratopList){
            switch (ultratopSong.getStatus()) {
                case M3uTO.STATUS_OK:
                    //System.out.println("ULTRATOP OK: " + ultratopSong.getOriginalLine());
                    break;
                case M3uTO.STATUS_ERROR:
                    errorFound = true;
                    if (printHeader){
                        printHeader = false;
                        System.out.println();
                        System.out.println("ULTRATOP ERRORS");
                        System.out.println("===============");
                    }
                    System.out.println("ERROR: " + ultratopSong.getOriginalLine());
                    break;
                case M3uTO.NO_MATCH_FOUND:
                    errorFound = true;
                    if (printHeader){
                        printHeader = false;
                        System.out.println();
                        System.out.println("ULTRATOP ERRORS");
                        System.out.println("===============");
                    }
                    System.out.println("NO MATCH FOUND: " + ultratopSong.getOriginalLine());
                    break;
            }
        }
        if (renameList.size() > 0){
            errorFound = true;
            System.out.println();
            System.out.println("RENAMING THE FOLLOWING FILES");
            System.out.println("============================");
            if (ultratopConfig.rename){
                System.out.println("STATUS: RENAMING ENABLED");
            }
            else {
                System.out.println("STATUS: RENAMING DISABLED");
            }
            for (M3uTO renameSong : renameList){
                System.out.println("ORIGINAL NAME: " + renameSong.getMp3File().getAbsoluteFile());
                File newFile = new File(renameSong.getMp3File().getParent() + File.separator + renameSong.getTrack() + " " +
                        renameSong.getArtist() + SPLIT_SONG + renameSong.getSong() + "." + FilenameUtils.getExtension(renameSong.getMp3File().getName()));
                System.out.println("NEW NAME     : " + newFile.getAbsolutePath());
                if (ultratopConfig.rename){
                    if (renameSong.getMp3File().renameTo(newFile)) {
                        System.out.println("STATUS: Renamed");
                        fileRenamed = true;
                    }
                    else {
                        System.err.println("STATUS: ERROR");
                    }
                }
                System.out.println(StringUtils.repeat("=",200));
            }
        }
        if (errorBaseList.size() > 0) {
            errorFound = true;
            System.out.println();
            System.out.println("BASELIST ERRORLIST");
            System.out.println("==================");
            for (M3uTO errorSong : errorBaseList) {
                System.out.println("ERROR Getting info from " + errorSong.getMp3File().getAbsolutePath());
                System.out.println(StringUtils.repeat("=", 200));
            }
        }
        if (!errorFound){
            System.out.println("NO PROBLEMS FOUND");
        }
        System.out.println(StringUtils.repeat("=", 200));
        return (fileRenamed);
    }

    private List  <M3uTO> lookupMP3InBaseDirectory(String baseDir, List <M3uTO> ultratopList, List <M3uTO> yearList){
        File folder = new File(baseDir);
        File[] listOfFiles = folder.listFiles();
        List <M3uTO> baseDirList = new ArrayList <M3uTO> ();
        for (int i=0; i < listOfFiles.length; i++){
            File file = listOfFiles[i];
            if (file.getName().toUpperCase().endsWith(".MP3")) {
                String strippedLine = file.getName();
                strippedLine = FilenameUtils.removeExtension(strippedLine);
                M3uTO info = evaluateSong(strippedLine, "..\\" + folder.getName() + "\\" + file.getName());
                info.setMp3File(listOfFiles[i]);
                if (info.getStatus() == M3uTO.STATUS_OK){

                    M3uTO lookupMP3 = lookupMP3Song(info, yearList);
                    if (lookupMP3 != null){
                        // found the base dir song in the year list, so no rename necessary
                        info.setStatus(M3uTO.STATUS_OK);
                    }
                    else {
                        info.setStatus(M3uTO.STATUS_BASE_OK);
                    }
                    baseDirList.add(info);
                }
                else {
                    //info.setMp3File(listOfFiles[i]);
                    info.setStatus(M3uTO.ERROR_BASE_SONG_INFO);
                    info.setErrorMessage("lookupMP3InBaseDirectory: Problem getting song info for " + file.getName());
                    baseDirList.add(info);
                }
            }
        }
        for (M3uTO ultratopSong: ultratopList){
            // no matching song found yet in the year list file
            // look it up in the base Dir
            if (ultratopSong.getM3uSong() == null){
                M3uTO lookupMP3 = lookupMP3Song(ultratopSong, baseDirList);
                if (lookupMP3 != null){
                    // we found a match, now check track number
                    if (ultratopSong.getTrack().equals(lookupMP3.getTrack())){
                        ultratopSong.setM3uSong(FilenameUtils.getName(lookupMP3.getOriginalLine()));
                        lookupMP3.setStatus(M3uTO.STATUS_OK);
                    }
                    else {
                        //System.out.println("Wrong track number " + ultratopSong.getLine());
                        lookupMP3.setStatus(M3uTO.RENAME);
                        lookupMP3.setTrack(ultratopSong.getTrack());
                    }
                }
                else {
                    ultratopSong.setStatus(M3uTO.NO_MATCH_FOUND);
                }
            }
        }
        return baseDirList;


    }

    private void makeM3uFile(List <M3uTO> ultratopList, String baseDir){
        PrintWriter writer = null;
        //OutputStreamWriter writer = null;
        File listFile = new File(baseDir);
        File m3uFile = new File(baseDir + File.separator + listFile.getName() + ".m3u");
        System.out.println("Making M3u File " + m3uFile);
        int counter = 0;
        try {
            Charset charset = Charset.forName("UTF-8");
            FileOutputStream stream = null;
            try {
                stream = new FileOutputStream(m3uFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            writer = new PrintWriter(new java.io.BufferedWriter(
                    new java.io.OutputStreamWriter(stream, charset)), true);
            /* add the UTF-8 BOM to the beginning of the file
               some players have trouble with special characters without this entry
             */
            writer.print('\ufeff');
            for (M3uTO m3uTO : ultratopList) {
                if (m3uTO.getM3uSong() != null) {
                    //System.out.println(m3uTO.getM3uSong());
                    writer.println(convertSongNameToM3uSongName(m3uTO.getM3uSong()));
                    counter++;
                }
                else {
                    //System.out.println("ERROR: " + m3uTO.getOriginalLine());
                }
            }
        } finally{
            if (writer != null){
                writer.close();
            }
        }
        System.out.println("FINISHED: " + counter + " songs added" );
        System.out.println(StringUtils.repeat("*", 200));
        System.out.println();

    }

    private void lookupMP3Songs(List <M3uTO> ultratopList, List <M3uTO> ListOfMP3Files, String inputFile, String baseDir){

        System.out.println("Matching MP3 songs");

        boolean success = true;
        File listFile = new File(baseDir);
        String parent = listFile.getName();
        for (M3uTO m3uTO: ultratopList){
            M3uTO m3u = lookupMP3Song(m3uTO, ListOfMP3Files);
            if (m3u == null){
                //System.out.println("NO Match found for " + m3uTO.getLine());
                success = false;
            }
            else {
                File tmpFile = new File(m3u.getOriginalLine());
                String parent2 = tmpFile.getParentFile().getName();
                //System.out.println("Parent 1 = " + parent);
                //System.out.println("Parent 2 = " + parent2);
                if (parent.equalsIgnoreCase(parent2)){
                    m3uTO.setM3uSong(FilenameUtils.getName(m3u.getOriginalLine()));
                    //System.out.println(FilenameUtils.getName(m3u.getOriginalLine()));
                }
                else {
                    m3uTO.setM3uSong(m3u.getOriginalLine());
                }
            }
        }
        if (success){
            System.out.println("LOOKUP SUCCESSFULL: " + inputFile);
        }
        else {
            System.err.println("LOOKUP ERRORS : " + inputFile);
        }
        System.out.println("Finished Matching MP3 songs");

    }

    private M3uTO lookupMP3Song(M3uTO song, List <M3uTO> listOfMP3Files ){

        if (song.getStatus() == M3uTO.STATUS_OK){
            String uniqueSong = getUniqueSong(song.getSong());
            String uniqueArtist = getUniqueSong(song.getArtist());
            for (M3uTO m3uTO: listOfMP3Files) {
                if (m3uTO.getStatus() == M3uTO.STATUS_OK || m3uTO.getStatus() == M3uTO.STATUS_BASE_OK){
                    //System.out.println("Comparing 1: " + getUniqueSong(song.getSong()));
                    //System.out.println("Comparing 2: " + getUniqueSong(m3uTO.getSong()));
                    if (uniqueSong.equals(getUniqueSong(m3uTO.getSong()))) {
                        //System.out.println("Comparing 1: " + getUniqueSong(song.getArtist()));
                        //System.out.println("Comparing 2: " + getUniqueSong(m3uTO.getArtist()));
                        if (uniqueArtist.equals(getUniqueSong(m3uTO.getArtist()))) {
                            return m3uTO;
                        }
                    }
                }
            }
        }
        return null;

    }

    private String getUniqueSong(String song){
        String tmpSong = song.toUpperCase();
        tmpSong = tmpSong.replace(" FEAT. ", " & ").replace(" FEAT ", " & ").replace(" FT. ", " & ").replace(" FT ", " & ");
        tmpSong = tmpSong.replace("[BE]", "");
        tmpSong = tmpSong.replace(" VS. ", " VS ");
        tmpSong = tmpSong.replace("MR. ", "MR ");
        tmpSong = tmpSong.replace("?", "");
        tmpSong = tmpSong.replace("/", "&");
        tmpSong = tmpSong.replace("<3", "LOVE");
        tmpSong = tmpSong.replace("**", "UC");
        tmpSong = tmpSong.replace("* ", "");
        tmpSong = tmpSong.replace("*", "");
        tmpSong = tmpSong.replace("+", "&");
        //tmpSong = tmpSong.replace("''", "\"");
        tmpSong = tmpSong.replace("\"", "''");
        tmpSong = tmpSong.replace("\"", "");
        tmpSong = tmpSong.replace("[", "(");
        tmpSong = tmpSong.replace("]", ")");
        tmpSong = tmpSong.replace("S.O.S", "SOS");
        tmpSong = tmpSong.replace("Å", "A");
        tmpSong = tmpSong.replace("å", "a");
        tmpSong = tmpSong.replace("é", "e");
        tmpSong = tmpSong.replace("è", "e");
        tmpSong = tmpSong.replace("ë", "e");
        tmpSong = tmpSong.replace("ê", "e");
        tmpSong = tmpSong.replace("á", "a");
        tmpSong = tmpSong.replace("à", "a");
        tmpSong = tmpSong.replace("ä", "a");
        tmpSong = tmpSong.replace("â", "a");
        tmpSong = tmpSong.replace("ó", "o");
        tmpSong = tmpSong.replace("ò", "o");
        tmpSong = tmpSong.replace("ö", "o");
        tmpSong = tmpSong.replace("ô", "o");
        tmpSong = tmpSong.replace("ú", "u");
        tmpSong = tmpSong.replace("ù", "u");
        tmpSong = tmpSong.replace("ü", "u");
        tmpSong = tmpSong.replace("û", "u");
        tmpSong = tmpSong.replace("í", "i");
        tmpSong = tmpSong.replace("ì", "i");
        tmpSong = tmpSong.replace("ï", "i");
        tmpSong = tmpSong.replace("î", "i");
        tmpSong = tmpSong.replace("ý", "y");
        tmpSong = tmpSong.replace("ÿ", "y");

        tmpSong = tmpSong.replace("É", "E");
        tmpSong = tmpSong.replace("È", "E");
        tmpSong = tmpSong.replace("Ë", "E");
        tmpSong = tmpSong.replace("Ê", "E");
        tmpSong = tmpSong.replace("Á", "A");
        tmpSong = tmpSong.replace("À", "A");
        tmpSong = tmpSong.replace("Ä", "A");
        tmpSong = tmpSong.replace("Â", "A");
        tmpSong = tmpSong.replace("Ó", "O");
        tmpSong = tmpSong.replace("Ò", "O");
        tmpSong = tmpSong.replace("Ö", "O");
        tmpSong = tmpSong.replace("Ô", "O");
        tmpSong = tmpSong.replace("Ú", "U");
        tmpSong = tmpSong.replace("Ù", "U");
        tmpSong = tmpSong.replace("Ü", "U");
        tmpSong = tmpSong.replace("Û", "U");
        tmpSong = tmpSong.replace("Í", "I");
        tmpSong = tmpSong.replace("Ì", "I");
        tmpSong = tmpSong.replace("Ï", "I");
        tmpSong = tmpSong.replace("Î", "I");
        tmpSong = tmpSong.replace("Ý", "Y");
        tmpSong = tmpSong.replace(" (DEEPEND REMIX)", "");
        tmpSong = tmpSong.replace("Ø", "O");
        tmpSong = tmpSong.replace("$IGN", "SIGN");

        return tmpSong.trim();
    }

    private String convertSongNameToM3uSongName(String song){
        String tmpSong = song.replace("\"", "''");
        return tmpSong;
    }

    private void validateM3uList(List <M3uTO> lines, String file){
        boolean errorFound = false;
        for (M3uTO m3uTO: lines){
            if (m3uTO.getStatus() == M3uTO.STATUS_OK){
                //System.out.println("     " + "Track  : " + m3uTO.getTrack());
                //System.out.println("     " + "Artist : " + m3uTO.getArtist());
                //System.out.println("     " + "Song   : " + m3uTO.getSong());
            }
            else {
                System.err.println("m3u: Problem with line " + m3uTO.getOriginalLine());
                errorFound = true;
            }
        }
        if (!errorFound){
            System.out.println(file + ": Processed OK (" + lines.size() + " songs found)");
        }
    }

    private M3uTO evaluateSong(String strippedSongLine, String songLine){
        String[] song = strippedSongLine.substring(3).split(SPLIT_SONG);
        M3uTO m3u = new M3uTO();
        m3u.setLine(strippedSongLine);
        m3u.setOriginalLine(songLine);
        String track = "";
        switch (song.length) {
            case 0:
                m3u.setStatus(M3uTO.STATUS_ERROR);
                m3u.setErrorMessage("No Artist or Song Title Found");
                break;
            case 1:
                m3u.setStatus(M3uTO.STATUS_ERROR);
                m3u.setErrorMessage("No Artist or Song Title Found");
                break;
            case 2:
                m3u.setStatus(M3uTO.STATUS_OK);
                track = strippedSongLine.substring(0,2);
                m3u.setArtist(song[0].trim());
                m3u.setSong(song[1].trim());
                m3u.setTrack(track);
                break;
            default:
                m3u.setStatus(M3uTO.STATUS_OK);
                track = strippedSongLine.substring(0,2);
                m3u.setTrack(track);
                m3u.setArtist(song[0].trim());
                m3u.setSong(song[1].trim());
                for (int i=2; i < song.length; i++){
                    m3u.setSong(m3u.getSong()+song[i]);
                }
                break;
        }
        return m3u;
    }



    private List<M3uTO> processYearList(String inputFile) throws IOException {
        System.out.println("Processing Year List " + inputFile);
        List <String> lines = new ArrayList<String>();
        File myFile = new File(inputFile);
        if (myFile.isFile()) {
            lines = FileUtils.getContents(new File(inputFile), "UTF-8");
        }
        List <M3uTO> newLines = new ArrayList();
        //System.out.println(line);
        for(String line: lines) {
            if (StringUtils.isBlank(line)) {
            } else {
                File tmpFile = new File(line);
                String strippedLine = tmpFile.getName();
                strippedLine = FilenameUtils.removeExtension(strippedLine);
                newLines.add(evaluateSong(strippedLine, line));
            }
        }
        System.out.println("Finished Year List " + inputFile);
        return newLines;
    }

    private List<M3uTO> processUltratopList(String inputFile) throws IOException {
        System.out.println("Processing Ultratop List " + inputFile);
        List <String> lines = FileUtils.getContents(new File(inputFile), "UTF-8");
        List <M3uTO> newLines = new ArrayList();
        for(String line: lines) {
            if (StringUtils.isBlank(line)) {
            } else {
                newLines.add(evaluateSong(line, line));
            }
        }
        System.out.println("Finished Ultratop List " + inputFile);
        return newLines;
    }

}
