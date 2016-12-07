package be.home.main.mezzmo;

import be.home.common.dao.jdbc.SQLiteJDBC;
import be.home.common.main.BatchJobV2;
import be.home.common.model.TransferObject;
import be.home.common.utils.WinUtils;
import be.home.mezzmo.domain.model.MGOFileAlbumCompositeTO;
import be.home.mezzmo.domain.service.MezzmoServiceImpl;
import be.home.mezzmo.domain.util.itext.MezzmoParagraph;
import be.home.mezzmo.domain.util.itext.MezzmoTableCellHeader;
import be.home.mezzmo.domain.util.itext.PageEvent;
import be.home.model.ConfigTO;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;

/**
 * Created by ghyssee on 9/02/2016.
 */
public class ExportAlbumsToPDF extends BatchJobV2{

    public static MezzmoServiceImpl mezzmoService = null;
    public static ConfigTO.Config config;
    private static final Logger log = getMainLog(ExportAlbumsToPDF.class);

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

    class MyFooter extends PdfPageEventHelper {
        Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);

        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Phrase header = new Phrase("this is a header", ffont);
            Phrase footer = new Phrase("this is a footer", ffont);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    header,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.top() + 10, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER,
                    footer,
                    (document.right() - document.left()) / 2 + document.leftMargin(),
                    document.bottom() - 10, 0);
        }
    }

    public void export(String base, String fileName){

        TransferObject to = new TransferObject();
        List <MGOFileAlbumCompositeTO> list = getMezzmoService().getAlbums(null, to);

        log.info("Number Of Albums Retrieved: " + list.size());

        Document document = new Document();

        try {
            PdfWriter writer = PdfWriter.getInstance(document,
                    new FileOutputStream("HelloWorld.pdf"));
            Font small = new Font(Font.FontFamily.HELVETICA, 10,
                    Font.NORMAL);


            PageEvent event = new PageEvent();
            writer.setPageEvent(event);
            event.setHeader("ALBUMS");
            document.setMargins(document.leftMargin(), document.rightMargin(), document.topMargin(), document.bottomMargin()+30F);
            document.open();

            PdfPTable table = new PdfPTable(3);


            PdfPCell cellHeader1 = new MezzmoTableCellHeader(new Paragraph("Album"));
            //cellHeader1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            PdfPCell cellHeader2 = new MezzmoTableCellHeader(new Paragraph("Artist"));
            cellHeader2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell (new MezzmoTableCellHeader(new Paragraph("")));
            table.addCell(cellHeader1);
            table.addCell(cellHeader2);
            table.setHeaderRows(1);
            float[] columnWidths = new float[] {2f, 30f, 10f};
            table.setWidths(columnWidths);

            for (MGOFileAlbumCompositeTO comp : list){
                fillTableRow(comp, table, small);
            }

            document.add(table);
            document.close(); // no need to close PDFwriter?

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void fillTableRow(MGOFileAlbumCompositeTO comp, PdfPTable table, Font font) throws IOException, DocumentException {
        PdfPCell cell1 = new PdfPCell(new MezzmoParagraph(comp.getFileAlbumTO().getName()));
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell1.setFixedHeight(15);
        String albumArtist = comp.getAlbumArtistTO().getName();
        if (albumArtist != null && "VARIOUS ARTISTS".equals(albumArtist.toUpperCase())){
            albumArtist = "";
        }
        PdfPCell cell2 = new PdfPCell(new MezzmoParagraph(albumArtist));
        cell2.setFixedHeight(15);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        table.addCell(createImageCell("C:\\Projects\\GitHub\\Home\\config\\folder.jpg"));
        table.addCell(cell1);
        table.addCell(cell2);

    }

    private PdfPCell createImageCell(String path) throws DocumentException, IOException {

        PdfPCell cell = null;

        if (StringUtils.isNotBlank(path)){
            File file = new File(path);
            if (file.exists()){
                Image img = Image.getInstance(path);
                img.scaleToFit(15,15);
                cell = new PdfPCell(img, true);
            }
            else {
                cell = new PdfPCell(new Paragraph());
            }
        }
        else {
            cell = new PdfPCell(new Paragraph());
        }

        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setFixedHeight(15);
        cell.setPaddingBottom(1);
        cell.setPaddingTop(1);
        cell.setPaddingLeft(1);
        cell.setPaddingRight(1);
        return cell;
    }


    public static MezzmoServiceImpl getMezzmoService(){

        if (mezzmoService == null) {
            return MezzmoServiceImpl.getInstance();
        }
        return mezzmoService;
    }
}

