package be.home.mezzmo.domain.util.itext;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

/**
 * Created by ghyssee on 8/06/2016.
 */
public class PageEvent extends PdfPageEventHelper {

        /** The header text. */
        String header;
        /** The template with the total number of pages. */
        PdfTemplate total;

        /**
         * Allows us to change the content of the header.
         * @param header The new header String
         */
        public void setHeader(String header) {
            this.header = header;
        }

        /**
         * Creates the PdfTemplate that will hold the total number of pages.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */

        public float calculateFontHeight(String text){
            MezzmoFooter footer = new MezzmoFooter("T");
            Float size = footer.getFontSize();
            System.out.println("Font Size = " + footer.getFontSize());
            float ascent = footer.getFont().getCalculatedBaseFont(false).getAscentPoint(text, size);
            float descent = footer.getFont().getCalculatedBaseFont(false).getDescentPoint(text, size);
            System.out.println("ascent = " + ascent);
            System.out.println("descent = " + descent);
            return ascent-descent;
        }

        public void onOpenDocument(PdfWriter writer, Document document) {
            total = writer.getDirectContent().createTemplate(30, 17.5f -calculateFontHeight("Z"));
        }

        /**
         * Adds a header to every page
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onEndPage(PdfWriter writer, Document document) {
            PdfPTable table = new PdfPTable(3);
            try {
                table.setWidths(new int[]{24, 24, 2});
                table.setTotalWidth(527);
                table.setLockedWidth(true);
                table.getDefaultCell().setFixedHeight(20);
                table.getDefaultCell().setBorder(Rectangle.BOTTOM);
                table.addCell(new MezzmoFooter(header));
                table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
                table.addCell(new MezzmoFooter (String.format("Page %d of", writer.getPageNumber())));
                PdfPCell cell = new PdfPCell(Image.getInstance(total));
                cell.setBorder(Rectangle.BOTTOM);
                table.addCell(cell);
                table.writeSelectedRows(0, -1, 34, 50, writer.getDirectContent());
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }

        /**
         * Fills out the total number of pages before the document is closed.
         * @see com.itextpdf.text.pdf.PdfPageEventHelper#onCloseDocument(
         *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
         */
        public void onCloseDocument(PdfWriter writer, Document document) {
            ColumnText.showTextAligned(total, Element.ALIGN_LEFT,
                    new MezzmoFooter(String.valueOf(writer.getPageNumber() - 1)),
                    2, 2, 0);
        }


}
