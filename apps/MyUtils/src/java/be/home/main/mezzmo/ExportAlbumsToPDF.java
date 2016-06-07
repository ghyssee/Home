package be.home.main.mezzmo;

import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.dao.jdbc.SQLiteUtils;
import be.home.common.logging.Log4GE;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.DateUtils;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.model.ConfigTO;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class ExportAlbumsToPDF extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;
    public static ConfigTO.Config config;
    private static final Logger log = Logger.getLogger(ExportAlbumsToPDF.class);

    public static void main(String args[]) {

        ExportAlbumsToPDF instance = new ExportAlbumsToPDF();
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

        export(base, "MezzmoDB.PlayCount.V12.csv");

    }

    public void export(String base, String fileName){
        Document document = new Document();

        try {
            PdfWriter.getInstance(document,
                    new FileOutputStream("HelloWorld.pdf"));

            document.open();

            PdfPTable table = new PdfPTable(2);

            PdfPCell cell1 = new PdfPCell(new Paragraph("Album"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Artist"));
            table.addCell(cell1);
            table.addCell(cell2);
            table.setHeaderRows(1);
            document.add(table);

            document.close(); // no need to close PDFwriter?

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

