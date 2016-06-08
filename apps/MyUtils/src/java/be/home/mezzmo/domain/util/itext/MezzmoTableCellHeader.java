package be.home.mezzmo.domain.util.itext;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;

/**
 * Created by ghyssee on 8/06/2016.
 */
public class MezzmoTableCellHeader extends PdfPCell{
    public MezzmoTableCellHeader(Phrase phrase) {
        super(phrase);
        this.setBackgroundColor(BaseColor.LIGHT_GRAY);
    }

}
