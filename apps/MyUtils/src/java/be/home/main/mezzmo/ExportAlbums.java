package be.home.main.mezzmo;

import be.home.common.configuration.Setup;
import be.home.common.constants.Constants;
import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.model.MGOFileTO;
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
    private static final Logger log = Logger.getLogger(ExportAlbums.class);

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

        String jrxmlFileName = "config/MezzmoJavaBean.jrxml";
        String jasperFileName = "config/MezzmoJavaBean.jasper";
        String pdfFileName = "C:/reports/ListOfAlbums.pdf";
        String htmlFile = "C:/reports/Albums.html";
        Map hm = new HashMap();
        //hm.put("ID", "123");
        //hm.put("DATENAME", "April 2006");
        List<MGOFileAlbumCompositeTO> list = getMezzmoService().getAlbums(new TransferObject());
        //List<MGOFileAlbumCompositeTO> listTracks = getMezzmoService().getAlbumTracks(new TransferObject());
        log.info("Getting cover arts of the albums");
        for (MGOFileAlbumCompositeTO comp : list){
            //comp.getFileAlbumTO().setCoverArt(Setup.getInstance().getFullPath(Constants.Path.RESOURCES) + "folder.jpg");
            //comp.getFileAlbumTO().setCoverArt("C:/My Programs/OneDrive/Config/Java/Resources/folder.jpg");
            MGOFileTO fileTO = getMezzmoService().findCoverArt(comp.getFileAlbumTO().getId());
            comp.getFileAlbumTO().setCoverArt(getCoverArtFile(fileTO.getFile()));
            log.debug("Album: " + comp.getFileAlbumTO().getName());
            log.debug("CoverArt: " + comp.getFileAlbumTO().getCoverArt());
        }
        JRDataSource dataSource = new JRBeanCollectionDataSource(list);
        JRFileVirtualizer virtualizer = new JRFileVirtualizer (100, Setup.getInstance().getFullPath(Constants.Path.TMP));
        virtualizer.setReadOnly(false);
        hm.put(JRParameter.REPORT_VIRTUALIZER, virtualizer);
        log.info("Compiling report " + jrxmlFileName);
        JasperCompileManager.compileReportToFile(jrxmlFileName, jasperFileName);
        log.info("Preparing report " + pdfFileName);
        JasperPrint jprint = (JasperPrint) JasperFillManager.fillReport(jasperFileName, hm, dataSource);
        log.info("Writing report " + pdfFileName);
        JasperExportManager.exportReportToPdfFile(jprint, pdfFileName);
        //JasperExportManager.exportReportToHtmlFile(jprint,htmlFile);

    }

    private String getCoverArtFile(String filename){
        File file = new File(filename);
        String path = file.getParentFile().getPath();
        File coverArt = new File(path + File.separator + "folder.jpg");
        if (!coverArt.exists()){
            //coverArt = new File("C:/My Programs/OneDrive/Config/Java/Resources/folder.jpg");
            coverArt = new File(Setup.getInstance().getFullPath(Constants.Path.RESOURCES) + File.separator + "folder.jpg");
        }
        return coverArt.toString();
    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

