package be.home.common.model;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.model.json.FirefoxProfiles;
import be.home.common.model.json.UltratopConfig;
import be.home.common.utils.DateUtils;
import be.home.common.utils.JSONUtils;
import be.home.common.utils.MyFileWriter;
import be.home.model.M3uTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UltratopConfigBO {

    private static final String fileCode = Constants.JSON.ULTRATOP;
    private static UltratopConfigBO ultratopConfigBO = new UltratopConfigBO();
    private static UltratopConfig ultratopConfig;
    private static final Logger log = Logger.getLogger(UltratopConfigBO.class);

    private UltratopConfigBO() {
        ultratopConfig = (UltratopConfig) JSONUtils.openJSONWithCode(fileCode, UltratopConfig.class);
    }

    public void save() throws IOException {
        String file = Setup.getFullPath(fileCode) + ".NEW";
        JSONUtils.writeJsonFile(ultratopConfig, file);
        //JSONUtils.writeJsonFileWithCode(ultratopConfig, fileCode);
    }

    public static UltratopConfigBO getInstance() {
        return ultratopConfigBO;
    }

    public void saveUltratopList(String strDate, List<M3uTO> list) throws IOException {
        String filename = Setup.getFullPath(Constants.Path.OUTPUT) + File.separator + getDataFilename(strDate);
        MyFileWriter writer = new MyFileWriter(filename, MyFileWriter.NO_APPEND);
        for (M3uTO song : list){
            String line = StringUtils.leftPad(song.getTrack(), 2, "0") + " " + song.getArtist() + " - " + song.getSong();
            writer.append(line);
        }
        writer.close();
    }

    public String getDataFilename(String strDate){
        String filename = "Ultratop" + strDate + ".txt";
        return filename;
    }

    public static String getFormattedDate(Date date){
        String strDate = DateUtils.formatDate(date, DateUtils.YYYYMMDD);
        return strDate;
    }

    public String getDirName(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String frmDate = getFormattedDate(date);
        String path = "Ultratop 50 " + frmDate + " " + cal.get(Calendar.DAY_OF_MONTH) +  " " + DateUtils.getMonthName(cal) +
                " " + cal.get(Calendar.YEAR);
        return path;
    }

    public UltratopConfig.Month getNewMonth(Date date){
        UltratopConfig.Month monthTO = new UltratopConfig().new Month();
        monthTO.baseDir = getDirName(date);
        monthTO.enabled = true;
        monthTO.id = getFormattedDate(date);
        monthTO.inputFile = "data/" + getDataFilename(getFormattedDate(date));
        return monthTO;
    }

    public UltratopConfig.Year findYear(String year){
        for (UltratopConfig.Year yearTO : ultratopConfig.years){
            if (yearTO.year.equals(year)){
                return yearTO;
            }
        }
        return null;
    }

    public UltratopConfig.Month findUltratop(List <UltratopConfig.Month> list, String id){
        for (UltratopConfig.Month monthTO : list){
            if (monthTO.id.equals(id)){
                return monthTO;
            }
        }
        return null;
    }

    public boolean addUltratopConfigItem(Date date) throws IOException {
        boolean added = false;
        UltratopConfig.Month monthTO = getNewMonth(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        UltratopConfig.Year year = findYear(String.valueOf(cal.get(Calendar.YEAR)));
        if (year != null){
            UltratopConfig.Month month = findUltratop(year.m3uMonth, getFormattedDate(date));
            if (month == null){
                added = true;
                year.m3uMonth.add(getNewMonth(date));
                save();
            }
            else {

            }log.info("Ultratop config item already added: " + getFormattedDate(date));
        }
        else {
            log.warn("Year not found in config file: " + cal.get(Calendar.YEAR));
        }
        return added;
    }
}
