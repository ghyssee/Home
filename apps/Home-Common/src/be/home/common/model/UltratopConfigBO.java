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
        //String file = Setup.getFullPath(Constants.JSON.MP3PRETTIFIER) + ".NEW";
        //JSONUtils.writeJsonFile(mp3Prettifier, file);
        JSONUtils.writeJsonFileWithCode(ultratopConfig, fileCode);
    }

    public static UltratopConfigBO getInstance() {
        return ultratopConfigBO;
    }

    public void saveUltratopList(String strDate, List<M3uTO> list) throws IOException {
        String filename = Setup.getFullPath(Constants.Path.OUTPUT) + File.separator +
                "Ultratop" + strDate + ".txt";
        MyFileWriter writer = new MyFileWriter(filename, MyFileWriter.NO_APPEND);
        for (M3uTO song : list){
            String line = StringUtils.leftPad(song.getTrack(), 2, "0") + " " + song.getArtist() + " - " + song.getSong();
            writer.append(line);
        }
        writer.close();
    }

    public void updateUltratop(){

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

}
