package be.home.main.mezzmo;

import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.fill.JRFileVirtualizer;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class ExportAlbums extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ExportAlbumsToPDF.class);

    public static void main(String args[]) {

        ExportAlbums instance = new ExportAlbums();
        try {
            config = instance.init();
            SQLiteJDBC.initialize(workingDir);
            instance.run();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        final String batchJob = "Export Albums";
        //log4GE = new Log4GE(config.logDir, config.movies.log);
        //log4GE.start(batchJob);
        //log4GE.info("test");
        //log4GE.addColumn("Status", 20);
        //log4GE.printHeaders();

        String base = WinUtils.getOneDrivePath();
        log.info("OneDrive Path: " + base);
        base += "\\Muziek\\Export\\";

        try {
            export(base, "MezzmoDB.PlayCount.V12.csv");
        } catch (JRException e) {
            e.printStackTrace();
        }

    }

    public void export(String base, String fileName) throws JRException {

        String jrxmlFileName = "C:/Projects/GitHub/Home/apps/MyUtils/src/resources/MezzmoJavaBean.jrxml";
        String jasperFileName = "C:/reports/MezzmoJavaBean2.jasper";
        String pdfFileName = "C:/reports/ListOfAlbums.pdf";
        String htmlFile = "C:/reports/Albums.html";
        Map hm = new HashMap();
        //hm.put("ID", "123");
        //hm.put("DATENAME", "April 2006");
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getAlbums(new TransferObject());
        for (MGOFileAlbumCompositeTO comp : list){
            comp.getFileAlbumTO().setCoverArt("config/folder.jpg");
        }
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        JRFileVirtualizer virtualizer = new JRFileVirtualizer (100, "temp");
        virtualizer.setReadOnly(false);
        hm.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        JasperCompileManager.compileReportToFile(jrxmlFileName, jasperFileName);
        JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(jasperFileName, hm, dataSource);
        JasperExportManager.exportReportToPdfFile(jprint, pdfFileName);
        //JasperExportManager.exportReportToHtmlFile(jprint,htmlFile);

    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

