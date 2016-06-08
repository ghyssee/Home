package be.home.mezzmo.domain.util.itext;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

/**
 * Created by ghyssee on 8/06/2016.
 */
public class MezzmoFooter extends Paragraph {

    public MezzmoFooter(String text) {
        super(text, new Font(Font.FontFamily.HELVETICA, 8L, Font.NORMAL));
    }

    public MezzmoFooter(String text, Font font) {
        super(text, font);
    }

    public float getFontSize(){
        return this.font.getSize();
    }
}