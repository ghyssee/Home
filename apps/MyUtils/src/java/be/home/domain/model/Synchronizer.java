package be.home.domain.model;

import be.home.common.utils.CSVUtils;
import be.home.common.utils.DateUtils;
import be.home.mezzmo.domain.bo.IPodBO;
import be.home.mezzmo.domain.bo.MediaMonkeyBO;
import be.home.mezzmo.domain.bo.MezzmoBO;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 22/11/2016.
 */
public class Synchronizer {

    List<MGOFileAlbumCompositeTO> iPodList;
    private static final Logger log = Logger.getLogger(Synchronizer.class);
    Map<String, MGOFileAlbumCompositeTO> discMap;
    MezzmoBO mezzmoBO;

    private Synchronizer(){
    }

    public Synchronizer(List<MGOFileAlbumCompositeTO> iPodList, Map<String, MGOFileAlbumCompositeTO> discMap){
        this.iPodList = iPodList;
        this.discMap = discMap;
        this.mezzmoBO = new MezzmoBO();
    }

    public void synchronizeIPodWithMezzmo(String base, String filename) throws Exception {

        if (iPodList == null || iPodList.size() == 0){
            log.warn("Nothing To Synchronize!!!");
        }
        File syncedFile = new File(base + File.separator + filename + "." + DateUtils.formatDate(new Date(), DateUtils.YYYYMMDDHHMMSS) + ".csv");
        CSVUtils csvUtils = new CSVUtils();
        CSVPrinter csvFilePrinter = null;
        Exception exception = null;
        try {
            String[] fields = {"FileTitle",
                               "File",
                               "OldPlayCount",
                               "OldDateLastPlayed",
                               "NewPlayCount",
                               "NewDateLastPlayed",
                               "Album"
                              };
            csvFilePrinter = csvUtils.initialize(syncedFile, fields);
            List <String> errors = startSynchronisation(iPodList, csvFilePrinter);
            MediaMonkeyBO mmBO = new MediaMonkeyBO();
            int mmReset = mmBO.resetPlayCount();
            log.info("Number of MediaMonkey Records updated: " + mmReset);
            if (errors.size() > 0){
                log.error("Number of errors found: " + errors.size());
                for (String errorMsg : errors){
                    log.error(errorMsg);
                }
            }
        } catch (Exception e) {
            exception = e;
        } finally {
            csvUtils.close(csvFilePrinter);
            if (exception != null){
                throw exception;
            }
        }
    }

    private List<String> startSynchronisation(List <MGOFileAlbumCompositeTO> list, CSVPrinter csvPrinter) throws SQLException {
        List <String> errors = new ArrayList<>();
        MezzmoBO mezzmoBO = new MezzmoBO();
        IPodBO iPodBO = new IPodBO();
        for (MGOFileAlbumCompositeTO comp : list) {
            MGOFileTO fileTO = comp.getFileTO();
            MGOFileTO foundFileTO = null;
            try {
                foundFileTO = mezzmoBO.findByTitleAndAlbum(comp);
                int playCount = foundFileTO.getPlayCount() + fileTO.getPlayCount();
                log.info("FileTitle: " + mezzmoBO.constructFileTitle(this.discMap, comp));
                log.info("Playcount: " + foundFileTO.getPlayCount() + " => " + playCount);
                Date lastUpdatedDate = DateUtils.max(foundFileTO.getDateLastPlayed(), comp.getFileTO().getDateLastPlayed());
                try {
                    int count = mezzmoBO.synchronizePlayCount(foundFileTO.getId(), playCount, lastUpdatedDate);
                    //int count = 1;
                    if (count != 1) {
                        errors.add("Problem updating file " + mezzmoBO.constructFileTitle(this.discMap, comp) + " with playcount " + playCount);
                    } else {
                        count = iPodBO.resetPlayCount(new Long(comp.getFileTO().getId()), 0);
                        //count = 1;
                        if (count != 1) {
                            errors.add("Problem resetting playcount for DB iPod And File " + mezzmoBO.constructFileTitle(this.discMap, comp) + " with playcount " + playCount);
                        } else {
                            // everything ok
                            if (lastUpdatedDate.equals(foundFileTO.getDateLastPlayed())){
                                lastUpdatedDate = null;
                            }
                            writeResult( foundFileTO, comp, playCount, lastUpdatedDate, csvPrinter);
                        }
                    }
                } catch (SQLException e) {
                    log.error(e);
                    errors.add("SQL Problem updating file " + mezzmoBO.constructFileTitle(this.discMap, comp) + " with playcount " + playCount);
                } catch (IOException e) {
                    log.error(e);
                    errors.add("IO Problem updating file " + mezzmoBO.constructFileTitle(this.discMap, comp) + " with playcount " + playCount);
                }
            }
            catch (EmptyResultDataAccessException ex){
                errors.add("No Result Found When updating file " + mezzmoBO.constructFileTitle(this.discMap, comp));
            }
        }
        return errors;
    }

    private void writeResult( MGOFileTO originalFile, MGOFileAlbumCompositeTO iPodFile, int newPlayCount, Date newDate, CSVPrinter csvFilePrinter) throws IOException {
        List record = new ArrayList();
        record.add(mezzmoBO.constructFileTitle(this.discMap, iPodFile));
        record.add(originalFile.getFile());
        record.add(originalFile.getPlayCount());
        record.add(DateUtils.formatDate(originalFile.getDateLastPlayed(), DateUtils.DD_MM_YYYY_HH_MM_SS));
        record.add(newPlayCount);
        record.add(DateUtils.formatDate(newDate, DateUtils.DD_MM_YYYY_HH_MM_SS));
        record.add(iPodFile.getFileAlbumTO().getName());
        csvFilePrinter.printRecord(record);
    }



}
