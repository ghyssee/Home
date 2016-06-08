package be.home.mezzmo.domain.util.itext;

import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;

/**
 * Created by ghyssee on 8/06/2016.
 */
public class MezzmoParagraph extends Paragraph {

    public MezzmoParagraph(String text) {
        super(text, new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL));
    }

    public MezzmoParagraph(String text, Font font) {
        super(text, font);
    }
    }
